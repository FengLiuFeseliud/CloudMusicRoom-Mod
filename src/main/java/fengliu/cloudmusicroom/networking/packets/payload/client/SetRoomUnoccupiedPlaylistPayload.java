package fengliu.cloudmusicroom.networking.packets.payload.client;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SetRoomUnoccupiedPlaylistPayload(NbtCompound playlistInfoNbt, long roomId) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, SetRoomUnoccupiedPlaylistPayload> CODE = PacketCodec.tuple(
            PacketCodecs.NBT_COMPOUND,
            SetRoomUnoccupiedPlaylistPayload::playlistInfoNbt,
            PacketCodecs.VAR_LONG,
            SetRoomUnoccupiedPlaylistPayload::roomId,
            SetRoomUnoccupiedPlaylistPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ModC2SPacketsId.SET_ROOM_UNOCCUPIED_PLAYLIST;
    }
}
