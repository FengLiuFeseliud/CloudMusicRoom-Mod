package fengliu.cloudmusicroom.room;

import net.minecraft.nbt.NbtCompound;

public record PlaylistInfo(long playlistId, String playlistName, String tagNames) {
    public static final PlaylistInfo EMPTY = new PlaylistInfo(0, "", "");
    public static final String PLAYLIST_ID_KEY = "playlistId";
    public static final String PLAYLIST_NAME_KEY = "playlistName";
    public static final String TAG_NAMES_KEY = "tagNames";

    public static PlaylistInfo fromNbtCompound(NbtCompound playlistInfoNbt){
        return new PlaylistInfo(
                playlistInfoNbt.getLong(PLAYLIST_ID_KEY),
                playlistInfoNbt.getString(PLAYLIST_NAME_KEY),
                playlistInfoNbt.getString(TAG_NAMES_KEY)
        );
    }

    public NbtCompound toNbtCompound(){
        NbtCompound compound = new NbtCompound();
        compound.putLong(PLAYLIST_ID_KEY, playlistId);
        compound.putString(PLAYLIST_NAME_KEY, playlistName);
        compound.putString(TAG_NAMES_KEY, tagNames);
        return compound;
    }
}
