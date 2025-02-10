package fengliu.cloudmusicroom.client.event;


import fengliu.cloudmusicroom.client.config.ConfigGui;
import fengliu.cloudmusicroom.client.config.Configs;
import fengliu.cloudmusicroom.client.networking.MusicRoomClient;
import fengliu.cloudmusicroom.networking.packets.payload.client.SwitchRoomMusicPayload;
import fengliu.cloudmusicroom.room.MusicRoom;
import fengliu.cloudmusicroom.utils.IdUtil;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class HotkeysCallback implements IHotkeyCallback {
    private final MinecraftClient client = MinecraftClient.getInstance();

    public static void init(){
        HotkeysCallback hotkeysCallback = new HotkeysCallback();

        for(ConfigHotkey hotkey: Configs.HOTKEY.HOTKEY_LIST){
            hotkey.getKeybind().setCallback(hotkeysCallback);
        }
    }

    @Override
    public boolean onKeyAction(KeyAction action, IKeybind key) {
        if (this.client.player == null){
            return false;
        }

        if (key == Configs.HOTKEY.OPEN_CONFIG_GUI.getKeybind() && action == KeyAction.PRESS){
            this.client.setScreen(new ConfigGui());
            return true;
        }

        if (key == Configs.HOTKEY.SWITCH_MUSIC.getKeybind() && action == KeyAction.PRESS){
            if (MusicRoomClient.getRoomInfo() == null){
                this.client.player.sendMessage(Text.translatable(IdUtil.error("not.join.room.switch.music")), false);
                return true;
            }
            ClientPlayNetworking.send(new SwitchRoomMusicPayload(MusicRoomClient.getRoomInfo().getLong(MusicRoom.ROOM_ID_KEY)));
            return true;
        }

        return false;
    }
}
