package fengliu.cloudmusicroom.client.networking.packets;

import fengliu.cloudmusicroom.client.networking.MusicRoomClient;
import fengliu.cloudmusicroom.networking.packets.payload.ModS2CPacketsId;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ModS2CPackets {
    public static void registerS2CPackets(){
        ClientPlayNetworking.registerGlobalReceiver(ModS2CPacketsId.JOIN_ROOM, MusicRoomClient::joinRoom);
        ClientPlayNetworking.registerGlobalReceiver(ModS2CPacketsId.ROOM_PLAY_MUSIC, MusicRoomClient::RoomPlayMusic);
        ClientPlayNetworking.registerGlobalReceiver(ModS2CPacketsId.ROOM_LIST, MusicRoomClient::RoomList);
        ClientPlayNetworking.registerGlobalReceiver(ModS2CPacketsId.ROOM_PLAYING_LIST, MusicRoomClient::roomPlayingList);
    }
}
