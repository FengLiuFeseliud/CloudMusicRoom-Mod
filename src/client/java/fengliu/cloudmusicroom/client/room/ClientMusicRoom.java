package fengliu.cloudmusicroom.client.room;

import fengliu.cloudmusicroom.client.networking.MusicRoomClient;
import fengliu.cloudmusicroom.networking.packets.payload.client.RoomExitPayload;
import fengliu.cloudmusicroom.networking.packets.payload.client.SwitchRoomMusicPayload;
import fengliu.cloudmusicroom.room.MusicInfo;
import fengliu.cloudmusicroom.room.MusicQueue;
import fengliu.cloudmusicroom.room.MusicRoom;
import fengliu.cloudmusicroom.utils.IdUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ClientMusicRoom extends MusicRoom {

    public ClientMusicRoom(NbtCompound roomInfo) {
        super(roomInfo);

        if (roomInfo.contains(MusicRoom.ROOM_PLAYING_MUSIC_KEY, NbtElement.COMPOUND_TYPE)){
            this.musicQueue.fromNbt((NbtCompound) roomInfo.get(MusicRoom.ROOM_PLAYING_MUSIC_KEY));
        }
    }

    public String getOwnerName(){
        return this.ownerName;
    }

    public UUID getOwnerUuid(){
        return this.ownerUuid;
    }

    @Override
    public void delete(PlayerEntity player) {
        player.sendMessage(Text.translatable(IdUtil.info("room.delete"), this.getName(), player.getName().getString()), false);
    }

    @Override
    public void join(PlayerEntity player) {

    }

    @Override
    public void exit(@Nullable PlayerEntity player) {
        if (player != null) {
            player.sendMessage(Text.translatable(IdUtil.info("exit.room"), this.getName()), false);
            ClientPlayNetworking.send(new RoomExitPayload(this.getId()));
        }
        MusicRoomClient.musicRoom = null;
        MusicRoomClient.inJoinRoomOldPlayer = false;
        MusicRoomClient.inUpdateMusic = false;
    }

    public MusicQueue getQueue(){
        return this.musicQueue;
    }

    @Override
    public void updateMusic() {

    }

    @Override
    public void updateRoomInfo() {

    }

    @Override
    public void nextMusic() {

    }

    @Override
    public void switchMusic(@Nullable PlayerEntity player) {
        ClientPlayNetworking.send(new SwitchRoomMusicPayload(this.getId()));
    }

    @Override
    public void addMusic(MusicInfo musicInfo, PlayerEntity player) {

    }

    @Override
    public void deleteMusic(long musicId, PlayerEntity player) {

    }
}
