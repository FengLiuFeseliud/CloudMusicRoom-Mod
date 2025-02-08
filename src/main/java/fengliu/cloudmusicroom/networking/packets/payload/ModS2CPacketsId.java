package fengliu.cloudmusicroom.networking.packets.payload;

import fengliu.cloudmusicroom.CloudMusicRoom;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class ModS2CPacketsId {
    public static final CustomPayload.Id<JoinRoomPayload> JOIN_ROOM = new CustomPayload.Id<>(Identifier.of(CloudMusicRoom.MOD_ID, "join_room"));
    public static final CustomPayload.Id<RoomPlayMusicPayload> ROOM_PLAY_MUSIC = new CustomPayload.Id<>(Identifier.of(CloudMusicRoom.MOD_ID, "room_play_music"));
    public static final CustomPayload.Id<RoomListPayload> ROOM_LIST = new CustomPayload.Id<>(Identifier.of(CloudMusicRoom.MOD_ID, "room_list"));

    public static void registerS2CPackets(){
        PayloadTypeRegistry.playS2C().register(JOIN_ROOM, JoinRoomPayload.CODE);
        PayloadTypeRegistry.playS2C().register(ROOM_PLAY_MUSIC, RoomPlayMusicPayload.CODE);
        PayloadTypeRegistry.playS2C().register(ROOM_LIST, RoomListPayload.CODE);
    }
}
