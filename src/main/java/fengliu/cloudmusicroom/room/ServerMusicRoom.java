package fengliu.cloudmusicroom.room;

import fengliu.cloudmusicroom.CloudMusicRoom;
import fengliu.cloudmusicroom.command.MusicRoomCommand;
import fengliu.cloudmusicroom.networking.packets.payload.JoinRoomPayload;
import fengliu.cloudmusicroom.networking.packets.payload.RoomDeletePayload;
import fengliu.cloudmusicroom.networking.packets.payload.RoomPlayMusicPayload;
import fengliu.cloudmusicroom.networking.packets.payload.RoomUpdatePayload;
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

public class ServerMusicRoom extends MusicRoom{
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

    public ServerMusicRoom(MinecraftServer server, String roomName, ServerPlayerEntity owner) {
        super(roomName, owner);
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

    /**
     * 加入房间
     */
    public void join(PlayerEntity player) {
        for (IMusicRoom iMusicRoom : MusicRoomCommand.musicRoomList) {
            if (!iMusicRoom.inJoinRoom(player)){
                continue;
            }

            player.sendMessage(Text.translatable(IdUtil.error("in.join.room")), false);
            return;
        }

        joinUserList.add(player);
        newJoinUserList.add(player);
        ServerPlayNetworking.send((ServerPlayerEntity) player, new JoinRoomPayload(this.toNbtCompound()));
        joinUserList.forEach(playerEntity -> {
            if (playerEntity.equals(player)){
                return;
            }
            playerEntity.sendMessage(Text.translatable(IdUtil.info("play.join.room"), player.getName(), this.getName(), this.joinUserList.size()), false);
        });
    }

    /**
     * 向房间所有用户更新歌曲
     */
    public void updateMusic() {
        joinUserList.forEach(player -> ServerPlayNetworking.send((ServerPlayerEntity) player,
                new RoomPlayMusicPayload(this.musicQueue.isUnoccupied() ? new NbtCompound(): this.musicQueue.getPlayingMusicInfo().toNbtCompound(), this.musicQueue.isUnoccupied())));
        this.switchVoteCount = 0;
        this.switchVoteUserList.clear();
    }

    /**
     * 点歌
     */
    public void addMusic(MusicInfo musicInfo, PlayerEntity addMusicPlayer) {
        if (!this.joinUserList.contains(addMusicPlayer)){
            addMusicPlayer.sendMessage(Text.translatable(IdUtil.error("not.join.room.add.music")), false);
            return;
        }

        if (this.musicQueue.isUnoccupied()){
            this.musicQueue.addMusic(musicInfo);
            this.updateMusic();
        } else {
            this.musicQueue.addMusic(musicInfo);
        }

        joinUserList.forEach(player -> player.sendMessage(Text.translatable(IdUtil.info("room.add.music"), addMusicPlayer.getName(), musicInfo.musicName()), false));
    }

    /**
     * 取消点歌
     */
    public void deleteMusic(long musicId, PlayerEntity player) {
        MusicInfo musicInfo = this.musicQueue.getMusicInfo(musicId);
        if (musicInfo == null){
            player.sendMessage(Text.translatable(IdUtil.error("not.get.delete.room.music"), musicId), false);
            return;
        }

        if (!player.getUuid().equals(musicInfo.addMusicPlayerUuid()) && !this.owner.getUuid().equals(player.getUuid())){
            player.sendMessage(Text.translatable(IdUtil.error("not.delete.room.music"), musicInfo.musicName()), false);
            return;
        }

        joinUserList.forEach(playerEntity -> playerEntity.sendMessage(
                Text.translatable(IdUtil.info("delete.room.music"), player.getName(), this.getName(), musicInfo.musicName(), musicInfo.addMusicPlayerName()), false));
        this.musicQueue.deleteMusic(musicInfo);
    }

    /**
     * 下一首
     */
    public void nextMusic() {
        this.musicQueue.nextMusic();
        this.updateMusic();
    }

    /**
     * 判断所有用户是否都播放完毕
     * @return true 用户都播放完毕
     */
    public boolean isAllClientPlayEnd(){
        this.clientPlayEndCount ++;

        if (this.clientPlayEndCount < this.joinUserList.size() - this.newJoinUserList.size()){
            return false;
        }

        this.clientPlayEndCount = 0;
        this.newJoinUserList.clear();
        return true;
    }

    /**
     * 向房间所有用户更新房间信息
     */
    public void updateRoomInfo(Text updateInfo) {
        joinUserList.forEach(player -> {
            player.sendMessage(updateInfo, false);
            ServerPlayNetworking.send((ServerPlayerEntity) player, new RoomUpdatePayload(this.toNbtCompound()));
        });
    }

    public void setUnoccupiedPlaylist(PlaylistInfo playlistInfo, ServerPlayerEntity player){
        if (!this.isOwner(player)){
            player.sendMessage(Text.translatable(IdUtil.error("not.set.unoccupied.playlist"), this.getName()));
            return;
        }

        if (playlistInfo.playlistId() == 0){
            this.unoccupiedPlaylist = null;
            this.updateRoomInfo(Text.translatable(IdUtil.info("set.null.unoccupied.playlist"), player.getName()));
            return;
        }

        this.unoccupiedPlaylist = playlistInfo;
        this.updateRoomInfo(Text.translatable(IdUtil.info("set.unoccupied.playlist"), player.getName(), playlistInfo.playlistName(), playlistInfo.playlistId()));
    }

    @Override
    public void delete(PlayerEntity player) {
        if (!this.isOwner(player)){
            player.sendMessage(Text.translatable(IdUtil.error("not.delete.room"), this.getName()), false);
            return;
        }

        joinUserList.forEach(playerEntity -> ServerPlayNetworking.send((ServerPlayerEntity) playerEntity, new RoomDeletePayload(player.getName().getString())));
        MusicRoomCommand.musicRoomList.remove(this);
    }

    @Override
    public void exit(PlayerEntity player) {
        this.joinUserList.remove(player);
        if (newJoinUserList.contains(player)){
            this.newJoinUserList.remove(player);
        }

        joinUserList.forEach(playerEntity -> playerEntity.sendMessage(Text.translatable(IdUtil.info("play.exit.room"), player.getName(), this.getName(), this.joinUserList.size()), false));
    }

    /**
     * 切歌投票
     * @param player 玩家
     * @return 是否成功切歌
     */
    public boolean switchVote(PlayerEntity player){
        if (switchVoteUserList.contains(player)){
            player.sendMessage(Text.translatable(IdUtil.error("not.add.switch.vote")), false);
            return false;
        }

        switchVoteUserList.add(player);
        this.switchVoteCount ++;
        this.joinUserList.forEach(joinPlayer -> joinPlayer.sendMessage(
                Text.translatable(IdUtil.info("switch.vote.music"), this.musicQueue.getPlayingMusicInfo().musicName(),
                        this.switchVoteCount, this.joinUserList.size(), player.getName().getString()), false));
        return this.switchVoteCount > this.joinUserList.size() / 2;
    }

    @Override
    public void switchMusic(PlayerEntity player) {
        if (this.musicQueue.isUnoccupied()){
            player.sendMessage(Text.translatable(IdUtil.error("not.switch.music.unoccupied")), false);
            return;
        }

        if (!this.isOwner(player) && !this.switchVote(player)){
            //   player.sendMessage(Text.translatable(IdUtil.error("not.switch.music"), this.getName()));
            return;
        }

        this.joinUserList.forEach(joinPlayer -> joinPlayer.sendMessage(
                Text.translatable(IdUtil.info("switch.music"), player.getName().getString(), this.musicQueue.getPlayingMusicInfo().musicName()), false));
        this.nextMusic();
    }
}
