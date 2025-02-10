package fengliu.cloudmusicroom.networking.packets.payload.client;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record DeleteRoomPayload(long roomId, long musicId) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, DeleteRoomPayload> CODE = PacketCodec.tuple(
            PacketCodecs.VAR_LONG,
            DeleteRoomPayload::roomId,
            PacketCodecs.VAR_LONG,
            DeleteRoomPayload::musicId,
            DeleteRoomPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ModC2SPacketsId.DELETE_ROOM_MUSIC;
    }
}
