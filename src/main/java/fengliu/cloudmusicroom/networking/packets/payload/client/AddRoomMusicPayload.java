package fengliu.cloudmusicroom.networking.packets.payload.client;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record AddRoomMusicPayload(long roomId, NbtCompound musicInfo) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, AddRoomMusicPayload> CODE = PacketCodec.tuple(
            PacketCodecs.VAR_LONG,
            AddRoomMusicPayload::roomId,
            PacketCodecs.NBT_COMPOUND,
            AddRoomMusicPayload::musicInfo,
            AddRoomMusicPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ModC2SPacketsId.ADD_ROOM_MUSIC;
    }
}
