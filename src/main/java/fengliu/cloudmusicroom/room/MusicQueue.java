package fengliu.cloudmusicroom.room;

import java.util.ArrayList;
import java.util.List;

/**
 * 点歌队列
 */
public class MusicQueue {
    private MusicInfo playingMusicInfo = null;
    private List<MusicInfo> musicList = new ArrayList<>();

    public MusicInfo getPlayingMusicInfo(){
        return this.playingMusicInfo;
    }

    public boolean isUnoccupied(){
        return musicList.isEmpty() && playingMusicInfo == null;
    }

    public void addMusic(MusicInfo music){
        if (!this.isUnoccupied()){
            this.musicList.add(music);
            return;
        }

        this.playingMusicInfo = music;
    }

    public void nextMusic(){
        if (musicList.isEmpty()){
            this.playingMusicInfo = null;
            return;
        }

        this.playingMusicInfo = musicList.getFirst();
        this.musicList.removeFirst();
    }
}
