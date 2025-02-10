package fengliu.cloudmusicroom.room;

import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 点歌队列
 */
public class MusicQueue {
    private MusicInfo playingMusicInfo = null;
    private final List<MusicInfo> musicList = new ArrayList<>();

    public MusicInfo getPlayingMusicInfo(){
        return this.playingMusicInfo;
    }

    public boolean isUnoccupied(){
        return musicList.isEmpty() && playingMusicInfo == null;
    }

    public @Nullable MusicInfo getMusicInfo(long musicId){
        for (MusicInfo musicInfo : musicList) {
            if (musicInfo.musicId() != musicId){
                continue;
            }

            return musicInfo;
        }
        return null;
    }

    public void addMusic(MusicInfo music){
        if (!this.isUnoccupied()){
            this.musicList.add(music);
            return;
        }

        this.playingMusicInfo = music;
    }

    public void deleteMusic(MusicInfo musicInfo){
        this.musicList.remove(musicInfo);
    }

    public void nextMusic(){
        if (musicList.isEmpty()){
            this.playingMusicInfo = null;
            return;
        }

        this.playingMusicInfo = musicList.getFirst();
        this.musicList.removeFirst();
    }

    public NbtList toNbtList(){
        NbtList playingList = new NbtList();
        if (this.isUnoccupied()){
            return playingList;
        }

        playingList.add(this.playingMusicInfo.toNbtCompound());
        musicList.forEach(musicInfo -> playingList.add(musicInfo.toNbtCompound()));
        return playingList;
    }
}
