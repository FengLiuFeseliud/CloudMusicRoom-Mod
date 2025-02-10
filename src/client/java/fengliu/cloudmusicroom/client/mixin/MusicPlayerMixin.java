package fengliu.cloudmusicroom.client.mixin;

import fengliu.cloudmusic.util.MusicPlayer;
import fengliu.cloudmusicroom.client.networking.MusicRoomClient;
import fengliu.cloudmusicroom.networking.packets.payload.client.RoomMusicPlayEndPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * 修改 MusicPlayer 以兼容房间内使用
 */
@Mixin(value = MusicPlayer.class, remap = false)
public class MusicPlayerMixin {

    @Unique private boolean inUpDateNoPlay = false;
    @Unique private boolean inNotNext = false;

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
        if (!MusicRoomClient.inMusicRoom()){
            return;
        }

        if (MusicRoomClient.inJoinRoomOldPlayer){
            MusicRoomClient.inJoinRoomOldPlayer = false;
            return;
        }

        if (this.inNotNext){
            this.inNotNext = false;
            return;
        }

        this.inUpDateNoPlay = true;
        ClientPlayNetworking.send(new RoomMusicPlayEndPayload(MusicRoomClient.musicRoom.getId()));
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
     * 拦截 next 发送更新包
     */
    @Inject(method = "next", at = @At("HEAD"))
    public void switchMusic(CallbackInfo ci){
        if (!MusicRoomClient.inMusicRoom()){
            return;
        }

        this.inNotNext = true;
    }

    /**
     * 在房间退出房间
     */
    @Inject(method = "exit", at = @At("RETURN"))
    public void exitRoom(CallbackInfo ci){
        if (!MusicRoomClient.inMusicRoom()){
            return;
        }

        if (MusicRoomClient.inUpdateMusic){
            MusicRoomClient.inUpdateMusic = false;
            return;
        }

        MusicRoomClient.musicRoom.exit(MinecraftClient.getInstance().player);
    }
}
