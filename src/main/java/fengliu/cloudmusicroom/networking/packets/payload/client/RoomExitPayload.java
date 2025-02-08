package fengliu.cloudmusicroom.networking.packets.payload.client;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record RoomExitPayload(long roomId) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, RoomExitPayload> CODE = PacketCodec.tuple(
            PacketCodecs.VAR_LONG,
            RoomExitPayload::roomId,
            RoomExitPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ModC2SPacketsId.ROOM_EXIT;
    }
}
