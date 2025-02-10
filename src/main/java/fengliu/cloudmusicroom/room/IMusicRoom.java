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
     * 获取闲置时播放的歌单
     */
    PlaylistInfo getUnoccupiedPlaylist();

    /**
     * 删除房间
     */
    void delete(PlayerEntity player);

    /**
     * 离开房间
     */
    void exit(PlayerEntity player);

    /**
     * 切歌
     */
    void switchMusic(PlayerEntity player);

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

    boolean canPlayUnoccupiedPlaylist();
}
