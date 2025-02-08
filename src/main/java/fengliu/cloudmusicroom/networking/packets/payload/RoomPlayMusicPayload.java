package fengliu.cloudmusicroom.networking.packets.payload;

import com.mojang.authlib.GameProfile;
import fengliu.cloudmusicroom.room.MusicInfo;
import fengliu.cloudmusicroom.room.MusicRoom;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record RoomPlayMusicPayload(NbtCompound musicInfo, boolean unoccupied) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, RoomPlayMusicPayload> CODE = PacketCodec.tuple(
            PacketCodecs.NBT_COMPOUND,
            RoomPlayMusicPayload::musicInfo,
            PacketCodecs.BOOLEAN,
            RoomPlayMusicPayload::unoccupied,
            RoomPlayMusicPayload::new
    );

    public MusicInfo getPlayingMusicInfo(){
        return MusicInfo.fromNbtCompound(this.musicInfo());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ModS2CPacketsId.ROOM_PLAY_MUSIC;
    }
}
