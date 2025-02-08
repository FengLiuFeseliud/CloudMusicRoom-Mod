package fengliu.cloudmusicroom.room;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record MusicInfo(long musicId, String musicName, UUID addMusicPlayerUuid, String addMusicPlayerName, long addMusicTime) {
    public static final String MUSIC_ID_KEY = "musicId";
    public static final String MUSIC_NAME_KEY = "musicName";
    public static final String ADD_MUSIC_PLAYER_UUID_KEY = "addMusicPlayerUuid";
    public static final String ADD_MUSIC_PLAYER_NAME_KEY = "addMusicPlayerName";
    public static final String ADD_MUSIC_TIME_KEY = "addMusicTime";

    /**
     *
     * @param musicInfoNbtCompound 歌曲信息 nbt
     * @param player 用于更新点歌人名称
     * @return 歌曲信息对象
     */
    public static MusicInfo fromNbtCompound(NbtCompound musicInfoNbtCompound, @Nullable ServerPlayerEntity player){
        if (musicInfoNbtCompound == null || musicInfoNbtCompound.getLong(MUSIC_ID_KEY) == 0){
            return null;

        }
        return new MusicInfo(
                musicInfoNbtCompound.getLong(MUSIC_ID_KEY),
                musicInfoNbtCompound.getString(MUSIC_NAME_KEY),
                musicInfoNbtCompound.getUuid(ADD_MUSIC_PLAYER_UUID_KEY),
                player != null ? player.getName().getString(): musicInfoNbtCompound.getString(ADD_MUSIC_PLAYER_NAME_KEY),
                musicInfoNbtCompound.getLong(MUSIC_ID_KEY)
        );
    }

    public static MusicInfo fromNbtCompound(NbtCompound musicInfoNbtCompound){
        return MusicInfo.fromNbtCompound(musicInfoNbtCompound, null);
    }

    public NbtCompound toNbtCompound(){
        NbtCompound compound = new NbtCompound();
        compound.putLong(MUSIC_ID_KEY, musicId);
        compound.putString(MUSIC_NAME_KEY, musicName);
        compound.putUuid(ADD_MUSIC_PLAYER_UUID_KEY, addMusicPlayerUuid);
        compound.putString(ADD_MUSIC_PLAYER_NAME_KEY, addMusicPlayerName);
        compound.putLong(ADD_MUSIC_TIME_KEY, addMusicTime);
        return compound;
    }
}
