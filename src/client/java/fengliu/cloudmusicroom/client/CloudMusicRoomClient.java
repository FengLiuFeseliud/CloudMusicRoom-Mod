package fengliu.cloudmusicroom.client;

import fengliu.cloudmusicroom.client.config.Configs;
import net.fabricmc.api.ClientModInitializer;

public class CloudMusicRoomClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Configs.INSTANCE.load();
    }
}
