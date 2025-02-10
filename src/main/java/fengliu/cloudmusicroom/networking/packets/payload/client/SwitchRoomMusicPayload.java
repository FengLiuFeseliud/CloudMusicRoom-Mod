package fengliu.cloudmusicroom.networking.packets.payload.client;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SwitchRoomMusicPayload(long roomId) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, SwitchRoomMusicPayload> CODE = PacketCodec.tuple(
            PacketCodecs.VAR_LONG,
            SwitchRoomMusicPayload::roomId,
            SwitchRoomMusicPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ModC2SPacketsId.SWITCH_ROOM_MUSIC;
    }
}
