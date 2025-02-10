package fengliu.cloudmusicroom.networking.packets.payload;

import fengliu.cloudmusicroom.CloudMusicRoom;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class ModS2CPacketsId {
    public static final CustomPayload.Id<JoinRoomPayload> JOIN_ROOM = new CustomPayload.Id<>(Identifier.of(CloudMusicRoom.MOD_ID, "join_room"));
    public static final CustomPayload.Id<RoomPlayMusicPayload> ROOM_PLAY_MUSIC = new CustomPayload.Id<>(Identifier.of(CloudMusicRoom.MOD_ID, "room_play_music"));
    public static final CustomPayload.Id<RoomListPayload> ROOM_LIST = new CustomPayload.Id<>(Identifier.of(CloudMusicRoom.MOD_ID, "room_list"));
    public static final CustomPayload.Id<RoomPlayingListPayload> ROOM_PLAYING_LIST = new CustomPayload.Id<>(Identifier.of(CloudMusicRoom.MOD_ID, "room_playing_list"));
    public static final CustomPayload.Id<RoomDeletePayload> ROOM_DELETE = new CustomPayload.Id<>(Identifier.of(CloudMusicRoom.MOD_ID, "room_delete"));
    public static final CustomPayload.Id<RoomUpdatePayload> ROOM_UPDATE = new CustomPayload.Id<>(Identifier.of(CloudMusicRoom.MOD_ID, "room_update"));

    public static void registerS2CPackets(){
        PayloadTypeRegistry.playS2C().register(JOIN_ROOM, JoinRoomPayload.CODE);
        PayloadTypeRegistry.playS2C().register(ROOM_PLAY_MUSIC, RoomPlayMusicPayload.CODE);
        PayloadTypeRegistry.playS2C().register(ROOM_LIST, RoomListPayload.CODE);
        PayloadTypeRegistry.playS2C().register(ROOM_PLAYING_LIST, RoomPlayingListPayload.CODE);
        PayloadTypeRegistry.playS2C().register(ROOM_DELETE, RoomDeletePayload.CODE);
        PayloadTypeRegistry.playS2C().register(ROOM_UPDATE, RoomUpdatePayload.CODE);
    }
}
