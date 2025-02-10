package fengliu.cloudmusicroom.room;

import fengliu.cloudmusicroom.CloudMusicRoom;
import fengliu.cloudmusicroom.command.MusicRoomCommand;
import fengliu.cloudmusicroom.networking.packets.payload.JoinRoomPayload;
import fengliu.cloudmusicroom.networking.packets.payload.RoomDeletePayload;
import fengliu.cloudmusicroom.networking.packets.payload.RoomPlayMusicPayload;
import fengliu.cloudmusicroom.sql.SqlConnection;
import fengliu.cloudmusicroom.utils.IdUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MusicRoom implements IMusicRoom{
    public static final String ROOM_ID_KEY = "roomId";
    public static final String ROOM_NAME_KEY = "roomName";
    public static final String ROOM_OWNER_NAME_KEY = "roomOwnerName";
    public static final String ROOM_PLAYING_MUSIC_KEY = "roomPlayingMusic";

    private final long roomId;
    private final String name;
    private final PlayerEntity owner;
    private final List<ServerPlayerEntity> joinUserList = new ArrayList<>();
    private final List<ServerPlayerEntity> newJoinUserList = new ArrayList<>();
    private MusicQueue musicQueue = new MusicQueue();
    private long playMusicPlaylistId = 0;
    private long clientPlayEndCount = 0;
    private long switchVoteCount = 0;
    private final List<ServerPlayerEntity> switchVoteUserList = new ArrayList<>();

    public final MinecraftServer server;
    public final SqlConnection connection = new SqlConnection() {
        @Override
        public String getDBUrl() {
            return "jdbc:sqlite:%s/%s.db".formatted(server.getSavePath(WorldSavePath.ROOT).getParent(), CloudMusicRoom.MOD_ID);
        }

        @Override
        public String getTableName() {
            return "table_music_room";
        }

        @Override
        public String getCreateTableSql() {
            return """
                    	CREATE TABLE "%s" (
                            "roomId"	INTEGER NOT NULL UNIQUE,
                            "roomName"	TEXT NOT NULL,
                            "ownerUuid"	TEXT NOT NULL,
                            "creationTime"	INTEGER NOT NULL,
                            "playMusicPlaylistId"	INTEGER,
                            "playMusicListNbt"	TEXT,
                            PRIMARY KEY("roomId" AUTOINCREMENT)
                    )""".formatted(this.getTableName());
        }
    };


    public MusicRoom(MinecraftServer server, String roomName, ServerPlayerEntity owner){
        this.name = roomName;
        this.owner = owner;
        this.server = server;

        this.connection.createTable();
        this.roomId = this.connection.executeSpl(statement -> {
            try {
                statement.execute("INSERT INTO %s (roomName, ownerUuid, creationTime) VALUES ('%s', '%s', %s)"
                        .formatted(this.connection.getTableName(), this.getName(), this.owner.getUuid(), System.currentTimeMillis()));
                return statement.executeQuery("SELECT roomId FROM %s WHERE ownerUuid IN ('%s') ORDER BY roomId DESC".formatted(this.connection.getTableName(), this.owner.getUuid())).getLong(1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public long getId() {
        return this.roomId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public MusicQueue getQueue() {
        return this.musicQueue;
    }

    @Override
    public void delete(ServerPlayerEntity player) {
        if (!this.isOwner(player)){
            player.sendMessage(Text.translatable(IdUtil.error("not.delete.room"), this.getName()));
            return;
        }

        joinUserList.forEach(playerEntity -> ServerPlayNetworking.send(playerEntity, new RoomDeletePayload(player.getName().getString())));
        MusicRoomCommand.musicRoomList.remove(this);
    }

    @Override
    public void join(ServerPlayerEntity player) {
        for (IMusicRoom iMusicRoom : MusicRoomCommand.musicRoomList) {
            if (!iMusicRoom.inJoinRoom(player)){
                continue;
            }

            player.sendMessage(Text.translatable(IdUtil.error("in.join.room")));
            return;
        }

        joinUserList.forEach(playerEntity -> playerEntity.sendMessage(Text.translatable(IdUtil.info("play.join.room"), player.getName(), this.getName()), false));

        joinUserList.add(player);
        newJoinUserList.add(player);
        ServerPlayNetworking.send(player, new JoinRoomPayload(this.toNbtCompound()));
    }

    @Override
    public void exit(ServerPlayerEntity player) {
        this.joinUserList.remove(player);
        if (newJoinUserList.contains(player)){
            this.newJoinUserList.remove(player);
        }

        joinUserList.forEach(playerEntity -> playerEntity.sendMessage(Text.translatable(IdUtil.info("play.exit.room"), player.getName(), this.getName()), false));
    }

    @Override
    public void updateMusic() {
        joinUserList.forEach(player -> ServerPlayNetworking.send(player,
                new RoomPlayMusicPayload(this.musicQueue.isUnoccupied() ? new NbtCompound(): this.musicQueue.getPlayingMusicInfo().toNbtCompound(), this.musicQueue.isUnoccupied())));
        this.switchVoteCount = 0;
        this.switchVoteUserList.clear();
    }

    @Override
    public void nextMusic() {
        this.musicQueue.nextMusic();
        this.updateMusic();
    }

    /**
     * 切歌投票
     * @param player 玩家
     * @return 是否成功切歌
     */
    public boolean switchVote(ServerPlayerEntity player){
        if (switchVoteUserList.contains(player)){
            player.sendMessage(Text.translatable(IdUtil.error("not.add.switch.vote")));
            return false;
        }

        switchVoteUserList.add(player);
        this.switchVoteCount ++;
        this.joinUserList.forEach(joinPlayer -> joinPlayer.sendMessage(
                Text.translatable(IdUtil.info("switch.vote.music"), this.musicQueue.getPlayingMusicInfo().musicName(),
                        this.switchVoteCount, this.joinUserList.size(), player.getName().getString())));
        return this.switchVoteCount > this.joinUserList.size() / 2;
    }

    @Override
    public void switchMusic(ServerPlayerEntity player) {
        if (this.musicQueue.isUnoccupied()){
            player.sendMessage(Text.translatable(IdUtil.error("not.switch.music.unoccupied")));
            return;
        }

        if (!this.isOwner(player) && !this.switchVote(player)){
            //   player.sendMessage(Text.translatable(IdUtil.error("not.switch.music"), this.getName()));
            return;
        }

        this.joinUserList.forEach(joinPlayer -> joinPlayer.sendMessage(
                Text.translatable(IdUtil.info("switch.music"), player.getName().getString(), this.musicQueue.getPlayingMusicInfo().musicName())));
        this.nextMusic();
    }

    @Override
    public void addMusic(MusicInfo musicInfo, ServerPlayerEntity addMusicPlayer) {
        if (!this.joinUserList.contains(addMusicPlayer)){
            addMusicPlayer.sendMessage(Text.translatable(IdUtil.error("not.join.room.add.music")));
            return;
        }

        if (this.musicQueue.isUnoccupied()){
            this.musicQueue.addMusic(musicInfo);
            this.updateMusic();
        } else {
            this.musicQueue.addMusic(musicInfo);
        }

        joinUserList.forEach(player -> player.sendMessage(Text.translatable(IdUtil.info("room.add.music"), addMusicPlayer.getName(), musicInfo.musicName())));
    }

    @Override
    public void deleteMusic(long musicId, ServerPlayerEntity player) {
        MusicInfo musicInfo = this.musicQueue.getMusicInfo(musicId);
        if (musicInfo == null){
            player.sendMessage(Text.translatable(IdUtil.error("not.get.delete.room.music"), musicId));
            return;
        }

        if (!player.getUuid().equals(musicInfo.addMusicPlayerUuid()) && !this.owner.getUuid().equals(player.getUuid())){
            player.sendMessage(Text.translatable(IdUtil.error("not.delete.room.music"), musicInfo.musicName()));
            return;
        }

        joinUserList.forEach(playerEntity -> playerEntity.sendMessage(
                Text.translatable(IdUtil.info("delete.room.music"), player.getName(), this.getName(), musicInfo.musicName(), musicInfo.addMusicPlayerName())));
        this.musicQueue.deleteMusic(musicInfo);
    }

    @Override
    public boolean isAllClientPlayEnd(){
        this.clientPlayEndCount ++;

        if (this.clientPlayEndCount < this.joinUserList.size() - this.newJoinUserList.size()){
            return false;
        }

        this.clientPlayEndCount = 0;
        this.newJoinUserList.clear();
        return true;
    }

    @Override
    public boolean inJoinRoom(ServerPlayerEntity player) {
        return this.joinUserList.contains(player);
    }

    @Override
    public boolean isOwner(ServerPlayerEntity player) {
        return player.getUuid().equals(this.owner.getUuid());
    }

    public NbtCompound toNbtCompound(){
        NbtCompound musicRoomNbt = new NbtCompound();
        musicRoomNbt.putLong(ROOM_ID_KEY, this.roomId);
        musicRoomNbt.putString(ROOM_NAME_KEY, this.getName());
        musicRoomNbt.putString(ROOM_OWNER_NAME_KEY, String.valueOf(this.owner.getName().getString()));
        if (!this.musicQueue.isUnoccupied()){
            musicRoomNbt.put(ROOM_PLAYING_MUSIC_KEY, this.musicQueue.getPlayingMusicInfo().toNbtCompound());
        }
        return musicRoomNbt;
    }
}
