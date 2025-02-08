package fengliu.cloudmusicroom.networking.packets.payload;

import fengliu.cloudmusicroom.room.IMusicRoom;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;

public record RoomListPayload(NbtElement musicRoomList) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, RoomListPayload> CODE = PacketCodec.tuple(
            PacketCodecs.NBT_ELEMENT,
            RoomListPayload::musicRoomList,
            RoomListPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ModS2CPacketsId.ROOM_LIST;
    }
}
