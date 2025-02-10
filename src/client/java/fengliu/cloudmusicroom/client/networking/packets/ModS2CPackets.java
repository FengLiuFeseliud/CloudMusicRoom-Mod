package fengliu.cloudmusicroom.client.networking.packets;

import fengliu.cloudmusicroom.client.networking.MusicRoomClient;
import fengliu.cloudmusicroom.networking.packets.payload.ModS2CPacketsId;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ModS2CPackets {
    public static void registerS2CPackets(){
        ClientPlayNetworking.registerGlobalReceiver(ModS2CPacketsId.JOIN_ROOM, MusicRoomClient::joinRoom);
        ClientPlayNetworking.registerGlobalReceiver(ModS2CPacketsId.ROOM_PLAY_MUSIC, MusicRoomClient::roomPlayMusic);
        ClientPlayNetworking.registerGlobalReceiver(ModS2CPacketsId.ROOM_LIST, MusicRoomClient::roomList);
        ClientPlayNetworking.registerGlobalReceiver(ModS2CPacketsId.ROOM_PLAYING_LIST, MusicRoomClient::roomPlayingList);
        ClientPlayNetworking.registerGlobalReceiver(ModS2CPacketsId.ROOM_DELETE, MusicRoomClient::roomDelete);
        ClientPlayNetworking.registerGlobalReceiver(ModS2CPacketsId.ROOM_UPDATE, MusicRoomClient::roomUpdate);
    }
}
