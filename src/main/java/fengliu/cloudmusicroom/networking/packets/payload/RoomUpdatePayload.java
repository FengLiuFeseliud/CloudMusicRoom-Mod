package fengliu.cloudmusicroom.networking.packets.payload;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record RoomUpdatePayload(NbtCompound roomInfoNbt) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, RoomUpdatePayload> CODE = PacketCodec.tuple(
            PacketCodecs.NBT_COMPOUND,
            RoomUpdatePayload::roomInfoNbt,
            RoomUpdatePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ModS2CPacketsId.ROOM_UPDATE;
    }
}
