package fengliu.cloudmusicroom.client.mixin;

import fengliu.cloudmusic.music163.data.PlayList;
import fengliu.cloudmusic.util.TextClickItem;
import fengliu.cloudmusicroom.client.networking.MusicRoomClient;
import fengliu.cloudmusicroom.utils.IdUtil;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayList.class, remap = false)
public class PlayListMixin {

    @Shadow @Final public long id;

    @Inject(
            method = "printToChatHud",
            at = @At("RETURN")
    )
    public void printSetUnoccupiedPlaylist(FabricClientCommandSource source, CallbackInfo ci){
        if (!MusicRoomClient.inMusicRoom()){
            return;
        }

        source.sendFeedback(TextClickItem.combine(
                new TextClickItem(IdUtil.option("set.unoccupied.playlist"), IdUtil.optionShow("set.unoccupied.playlist"),
                        "/cloudmusic-room-client unoccupied %s %s".formatted(MusicRoomClient.musicRoom.getId(), this.id))
        ));
    }
}
