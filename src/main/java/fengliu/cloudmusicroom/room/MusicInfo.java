package fengliu.cloudmusicroom.room;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public record MusicInfo(long musicId, String musicName, UUID addMusicPlayerUuid, long addMusicTime) {
    public static final String MUSIC_ID_KEY = "musicId";
    public static final String MUSIC_NAME_KEY = "musicName";
    public static final String ADD_MUSIC_PLAYER_UUID_KEY = "addMusicPlayerUuid";
    public static final String ADD_MUSIC_TIME_KEY = "addMusicTime";

    public static MusicInfo fromNbtCompound(NbtCompound musicInfoNbtCompound){
        if (musicInfoNbtCompound == null || musicInfoNbtCompound.getLong(MUSIC_ID_KEY) == 0){
            return null;

        }
        return new MusicInfo(
                musicInfoNbtCompound.getLong(MUSIC_ID_KEY),
                musicInfoNbtCompound.getString(MUSIC_NAME_KEY),
                musicInfoNbtCompound.getUuid(ADD_MUSIC_PLAYER_UUID_KEY),
                musicInfoNbtCompound.getLong(MUSIC_ID_KEY)
        );
    }

    public NbtCompound toNbtCompound(){
        NbtCompound compound = new NbtCompound();
        compound.putLong(MUSIC_ID_KEY, musicId);
        compound.putString(MUSIC_NAME_KEY, musicName);
        compound.putUuid(ADD_MUSIC_PLAYER_UUID_KEY, addMusicPlayerUuid);
        compound.putLong(ADD_MUSIC_TIME_KEY, addMusicTime);
        return compound;
    }
}
