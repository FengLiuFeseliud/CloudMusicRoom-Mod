package fengliu.cloudmusicroom.networking.packets.payload.client;

import com.mojang.authlib.GameProfile;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record RoomMusicPlayEndPayload(long roomId) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, RoomMusicPlayEndPayload> CODE = PacketCodec.tuple(
            PacketCodecs.VAR_LONG,
            RoomMusicPlayEndPayload::roomId,
            RoomMusicPlayEndPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ModC2SPacketsId.ROOM_MUSIC_PLAY_END;
    }
}
