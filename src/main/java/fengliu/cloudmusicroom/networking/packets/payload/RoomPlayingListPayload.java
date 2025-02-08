package fengliu.cloudmusicroom.networking.packets.payload;

import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record RoomPlayingListPayload(NbtElement roomPlayingList) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, RoomPlayingListPayload> CODE = PacketCodec.tuple(
            PacketCodecs.NBT_ELEMENT,
            RoomPlayingListPayload::roomPlayingList,
            RoomPlayingListPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ModS2CPacketsId.ROOM_PLAYING_LIST;
    }
}
