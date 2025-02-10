package fengliu.cloudmusicroom.room;

import net.minecraft.entity.player.PlayerEntity;

/**
 * 云音乐房间
 */
public interface IMusicRoom {

    long getId();

    String getName();

    MusicQueue getQueue();

    /**
     * 删除房间
     */
    void delete(PlayerEntity player);

    /**
     * 加入房间
     */
    void join(PlayerEntity player);

    /**
     * 离开房间
     */
    void exit(PlayerEntity player);

    /**
     * 向房间所有用户更新歌曲
     */
    void updateMusic();

    /**
     * 向房间所有用户更新房间信息
     */
    void updateRoomInfo();

    /**
     * 下一首
     */
    void nextMusic();

    /**
     * 切歌
     */
    void switchMusic(PlayerEntity player);

    /**
     * 点歌
     */
    void addMusic(MusicInfo musicInfo, PlayerEntity player);

    /**
     * 取消点歌
     */
    void deleteMusic(long musicId, PlayerEntity player);

    /**
     * 判断用户是否在房间
     * @param player 玩家
     * @return true 玩家在房间
     */
    boolean inJoinRoom(PlayerEntity player);

    /**
     * 判断用户是房间否为房间创建者
     * @param player 玩家
     * @return true 玩家是房间创建者
     */
    boolean isOwner(PlayerEntity player);
}
