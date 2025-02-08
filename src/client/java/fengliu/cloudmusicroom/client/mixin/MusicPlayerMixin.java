package fengliu.cloudmusicroom.client.mixin;

import fengliu.cloudmusic.util.MusicPlayer;
import fengliu.cloudmusicroom.client.networking.MusicRoomClient;
import fengliu.cloudmusicroom.networking.packets.payload.client.RoomMusicPlayEndPayload;
import fengliu.cloudmusicroom.room.MusicRoom;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = MusicPlayer.class, remap = false)
public class MusicPlayerMixin {

    @Unique private boolean inUpDateNoPlay = false;

    /**
     * 客户端完成播放后向服务端发送更新包
     */
    @Inject(
            method = "play(Ljavax/sound/sampled/AudioInputStream;)V",
            at = @At(
                    value = "RETURN"
            )
    )
    public void playEndSendC2S(CallbackInfo ci){
        if (!MusicRoomClient.isInMusicRoom()){
            return;
        }

        if (MusicRoomClient.inJoinRoomOldPlayer){
            MusicRoomClient.inJoinRoomOldPlayer = false;
            return;
        }

        this.inUpDateNoPlay = true;
        ClientPlayNetworking.send(new RoomMusicPlayEndPayload(MusicRoomClient.getRoomInfo().getLong(MusicRoom.ROOM_ID_KEY)));
    }

    /**
     * 发送更新包后关闭播放线程等待更新
     */
    @Inject(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/config/options/ConfigBooleanHotkeyed;getBooleanValue()Z"
            ),
            cancellable = true
    )
    public void upDateNoPlay(CallbackInfo ci){
        if (!this.inUpDateNoPlay){
            return;
        }

        this.inUpDateNoPlay = false;
        ci.cancel();
    }

    /**
     * 在房间退出房间
     */
    @Inject(method = "exit", at = @At("RETURN"))
    public void exitRoom(CallbackInfo ci){
        if (!MusicRoomClient.isInMusicRoom()){
            return;
        }

        if (MusicRoomClient.inUpdateMusic){
            MusicRoomClient.inUpdateMusic = false;
            return;
        }

        MusicRoomClient.exitRoom();
    }
}
