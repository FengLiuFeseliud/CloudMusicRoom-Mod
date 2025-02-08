package fengliu.cloudmusicroom;

import fengliu.cloudmusicroom.command.MusicRoomCommand;
import fengliu.cloudmusicroom.networking.packets.payload.ModS2CPacketsId;
import fengliu.cloudmusicroom.networking.packets.payload.client.ModC2SPacketsId;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudMusicRoom implements ModInitializer {
    public static final String MOD_ID = "cloudmusicroom";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        MusicRoomCommand.registerAll();
        ModS2CPacketsId.registerS2CPackets();
        ModC2SPacketsId.registerC2SPackets();
    }
}
