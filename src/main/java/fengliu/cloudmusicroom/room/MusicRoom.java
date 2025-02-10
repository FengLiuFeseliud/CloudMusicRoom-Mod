package fengliu.cloudmusicroom.room;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public abstract class MusicRoom implements IMusicRoom{
    public static final String ROOM_ID_KEY = "roomId";
    public static final String ROOM_NAME_KEY = "roomName";
    public static final String ROOM_OWNER_NAME_KEY = "ownerName";
    public static final String ROOM_OWNER_UUID_KEY = "ownerUuid";
    public static final String ROOM_PLAYING_MUSIC_KEY = "roomPlayingMusic";
    public static final String ROOM_UNOCCUPIED_PLAYING_PLAYLIST_ID_KEY = "unoccupiedPlayingPlaylistId";

    protected long roomId = 0;
    protected final String name;
    protected final PlayerEntity owner;
    protected final String ownerName;
    protected final UUID ownerUuid;
    protected final List<PlayerEntity> joinUserList = new ArrayList<>();
    protected final List<PlayerEntity> newJoinUserList = new ArrayList<>();
    protected final MusicQueue musicQueue = new MusicQueue();
    protected long clientPlayEndCount = 0;
    protected long switchVoteCount = 0;
    protected final List<PlayerEntity> switchVoteUserList = new ArrayList<>();
    protected long unoccupiedPlayingPlaylist = 0;


    public MusicRoom(String roomName, PlayerEntity owner){
        this.name = roomName;
        this.owner = owner;
        this.ownerName = this.owner.getName().getString();
        this.ownerUuid = this.owner.getUuid();
    }

    public MusicRoom(NbtCompound roomInfo){
        this.name = roomInfo.getString(ROOM_NAME_KEY);
        this.roomId = roomInfo.getLong(ROOM_ID_KEY);
        this.owner = null;

        this.ownerName = roomInfo.getString(ROOM_OWNER_NAME_KEY);
        this.ownerUuid = roomInfo.getUuid(ROOM_OWNER_UUID_KEY);
        this.unoccupiedPlayingPlaylist = roomInfo.getLong(ROOM_UNOCCUPIED_PLAYING_PLAYLIST_ID_KEY);
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
    public boolean inJoinRoom(PlayerEntity player) {
        return this.joinUserList.contains(player);
    }

    @Override
    public boolean isOwner(PlayerEntity player) {
        return player.getUuid().equals(this.owner.getUuid());
    }

    public NbtCompound toNbtCompound(){
        NbtCompound musicRoomNbt = new NbtCompound();
        musicRoomNbt.putLong(ROOM_ID_KEY, this.getId());
        musicRoomNbt.putString(ROOM_NAME_KEY, this.getName());
        musicRoomNbt.putString(ROOM_OWNER_NAME_KEY, this.ownerName);
        musicRoomNbt.putUuid(ROOM_OWNER_UUID_KEY, this.ownerUuid);
        if (!this.musicQueue.isUnoccupied()){
            musicRoomNbt.put(ROOM_PLAYING_MUSIC_KEY, this.musicQueue.getPlayingMusicInfo().toNbtCompound());
        }
        musicRoomNbt.putLong(ROOM_UNOCCUPIED_PLAYING_PLAYLIST_ID_KEY, this.unoccupiedPlayingPlaylist);
        return musicRoomNbt;
    }
}
