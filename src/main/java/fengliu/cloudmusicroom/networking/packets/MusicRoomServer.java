package fengliu.cloudmusicroom.networking.packets;

import fengliu.cloudmusicroom.command.MusicRoomCommand;
import fengliu.cloudmusicroom.networking.packets.payload.client.AddRoomMusicPayload;
import fengliu.cloudmusicroom.networking.packets.payload.client.RoomExitPayload;
import fengliu.cloudmusicroom.networking.packets.payload.client.RoomMusicPlayEndPayload;
import fengliu.cloudmusicroom.room.MusicInfo;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;


/**
 * C2S 服务端处理
 */
public class MusicRoomServer {

    /**
     * 客户端播放完成
     */
    public static void roomMusicPlayEnd(RoomMusicPlayEndPayload payload, ServerPlayNetworking.Context context) {
        MusicRoomCommand.musicRoomList.forEach(musicRoom -> {
            if (musicRoom.getId() != payload.roomId() || !musicRoom.isAllClientPlayEnd()){
                return;
            }

            musicRoom.nextMusic();
        });
    }

    /**
     * 客户端点歌
     */
    public static void addRoomMusic(AddRoomMusicPayload payload, ServerPlayNetworking.Context context) {
        MusicRoomCommand.musicRoomList.forEach(musicRoom -> {
            if (musicRoom.getId() != payload.roomId()){
                return;
            }

            musicRoom.addMusic(MusicInfo.fromNbtCompound(payload.musicInfo(), context.player()), context.player());
        });
    }

    /**
     * 退出房间
     */
    public static void roomExit(RoomExitPayload payload, ServerPlayNetworking.Context context) {
        MusicRoomCommand.musicRoomList.forEach(musicRoom -> {
            if (musicRoom.getId() != payload.roomId()){
                return;
            }

            musicRoom.exit(context.player());
        });
    }
}
