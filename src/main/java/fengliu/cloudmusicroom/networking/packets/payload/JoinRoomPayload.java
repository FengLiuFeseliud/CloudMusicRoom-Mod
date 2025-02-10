package fengliu.cloudmusicroom.networking.packets.payload;

import fengliu.cloudmusicroom.room.MusicInfo;
import fengliu.cloudmusicroom.room.MusicRoom;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record JoinRoomPayload(NbtCompound roomInfoNbt) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, JoinRoomPayload> CODE = PacketCodec.tuple(
            PacketCodecs.NBT_COMPOUND,
            JoinRoomPayload::roomInfoNbt,
            JoinRoomPayload::new
    );

    public MusicInfo getPlayingMusicInfo(){
        return MusicInfo.fromNbtCompound(this.roomInfoNbt().getCompound(MusicRoom.ROOM_PLAYING_MUSIC_KEY));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ModS2CPacketsId.JOIN_ROOM;
    }
}
