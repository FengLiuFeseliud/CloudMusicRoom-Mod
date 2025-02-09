package fengliu.cloudmusicroom.networking.packets.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record RoomDeletePayload(String deletePlayerName) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, RoomDeletePayload> CODE = PacketCodec.tuple(
            PacketCodecs.STRING,
            RoomDeletePayload::deletePlayerName,
            RoomDeletePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ModS2CPacketsId.ROOM_DELETE;
    }
}
