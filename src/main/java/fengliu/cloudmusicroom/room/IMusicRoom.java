package fengliu.cloudmusicroom.room;

import net.minecraft.server.network.ServerPlayerEntity;

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
    void delete(ServerPlayerEntity player);

    /**
     * 加入房间
     */
    void join(ServerPlayerEntity player);

    /**
     * 离开房间
     */
    void exit(ServerPlayerEntity player);

    /**
     * 向房间所有用户更新歌曲
     */
    void updateMusic();

    /**
     * 下一首
     */
    void nextMusic();

    /**
     * 切歌
     */
    void switchMusic(ServerPlayerEntity player);

    /**
     * 点歌
     */
    void addMusic(MusicInfo musicInfo, ServerPlayerEntity player);

    /**
     * 取消点歌
     */
    void deleteMusic(long musicId, ServerPlayerEntity player);

    /**
     * 判断所有用户是否都播放完毕
     * @return true 用户都播放完毕
     */
    boolean isAllClientPlayEnd();

    /**
     * 判断用户是否在房间
     * @param player 玩家
     * @return true 玩家在房间
     */
    boolean inJoinRoom(ServerPlayerEntity player);

    /**
     * 判断用户是房间否为房间创建者
     * @param player 玩家
     * @return true 玩家是房间创建者
     */
    boolean isOwner(ServerPlayerEntity player);
}
