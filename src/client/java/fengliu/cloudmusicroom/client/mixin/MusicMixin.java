package fengliu.cloudmusicroom.client.mixin;

import fengliu.cloudmusic.music163.data.Music;
import fengliu.cloudmusic.util.TextClickItem;
import fengliu.cloudmusicroom.client.networking.MusicRoomClient;
import fengliu.cloudmusicroom.room.MusicRoom;
import fengliu.cloudmusicroom.utils.IdUtil;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 客户端在房间时显示歌曲信息时添加选项
 */
@Mixin(value = Music.class, remap = false)
public class MusicMixin {

    @Shadow @Final public long id;

    @Inject(
            method = "printToChatHud",
            at = @At("RETURN")
    )
    public void printAddMusic(FabricClientCommandSource source, CallbackInfo ci){
        if (!MusicRoomClient.isInMusicRoom()){
            return;
        }

        source.sendFeedback(TextClickItem.combine(
            new TextClickItem(IdUtil.option("add.music"), IdUtil.optionShow("add.music"),
                    "/cloudmusic-room-client add %s %s".formatted(MusicRoomClient.getRoomInfo().getLong(MusicRoom.ROOM_ID_KEY), this.id)),
            new TextClickItem(IdUtil.option("delete.music"), IdUtil.optionShow("delete.music"),
                    "/cloudmusic-room-client delete %s %s".formatted(MusicRoomClient.getRoomInfo().getLong(MusicRoom.ROOM_ID_KEY), this.id))
        ));
    }
}
