package fengliu.cloudmusicroom.networking.packets.payload.client;

import fengliu.cloudmusicroom.CloudMusicRoom;
import fengliu.cloudmusicroom.networking.packets.MusicRoomServer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class ModC2SPacketsId {
    public static final CustomPayload.Id<AddRoomMusicPayload> ADD_ROOM_MUSIC = new CustomPayload.Id<>(Identifier.of(CloudMusicRoom.MOD_ID, "add_room_music"));
    public static final CustomPayload.Id<RoomMusicPlayEndPayload> ROOM_MUSIC_PLAY_END = new CustomPayload.Id<>(Identifier.of(CloudMusicRoom.MOD_ID, "room_music_play_end"));
    public static final CustomPayload.Id<RoomExitPayload> ROOM_EXIT = new CustomPayload.Id<>(Identifier.of(CloudMusicRoom.MOD_ID, "room_exit"));

    public static void registerC2SPackets(){
        PayloadTypeRegistry.playC2S().register(ADD_ROOM_MUSIC, AddRoomMusicPayload.CODE);
        PayloadTypeRegistry.playC2S().register(ROOM_MUSIC_PLAY_END, RoomMusicPlayEndPayload.CODE);
        PayloadTypeRegistry.playC2S().register(ROOM_EXIT, RoomExitPayload.CODE);

        ServerPlayNetworking.registerGlobalReceiver(ADD_ROOM_MUSIC, MusicRoomServer::addRoomMusic);
        ServerPlayNetworking.registerGlobalReceiver(ROOM_MUSIC_PLAY_END, MusicRoomServer::roomMusicPlayEnd);
        ServerPlayNetworking.registerGlobalReceiver(ROOM_EXIT, MusicRoomServer::roomExit);
    }
}
