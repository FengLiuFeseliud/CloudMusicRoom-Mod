package fengliu.cloudmusicroom.client;

import fengliu.cloudmusicroom.client.command.MusicRoomClientCommand;
import fengliu.cloudmusicroom.client.config.Configs;
import fengliu.cloudmusicroom.client.event.HotkeysCallback;
import fengliu.cloudmusicroom.client.event.InputHandler;
import fengliu.cloudmusicroom.client.networking.packets.ModS2CPackets;
import fi.dy.masa.malilib.event.InputEventHandler;
import net.fabricmc.api.ClientModInitializer;

public class CloudMusicRoomClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Configs.INSTANCE.load();

        ModS2CPackets.registerS2CPackets();
        MusicRoomClientCommand.registerAll();

        HotkeysCallback.init();
        InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());
        InputEventHandler.getInputManager().registerKeyboardInputHandler(InputHandler.getInstance());
        InputEventHandler.getInputManager().registerMouseInputHandler(InputHandler.getInstance());
    }
}
