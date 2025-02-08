package fengliu.cloudmusicroom.client;

import fengliu.cloudmusicroom.client.command.MusicRoomClientCommand;
import fengliu.cloudmusicroom.client.config.Configs;
import fengliu.cloudmusicroom.client.networking.packets.ModS2CPackets;
import net.fabricmc.api.ClientModInitializer;

public class CloudMusicRoomClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Configs.INSTANCE.load();

        ModS2CPackets.registerS2CPackets();
        MusicRoomClientCommand.registerAll();
    }
}
