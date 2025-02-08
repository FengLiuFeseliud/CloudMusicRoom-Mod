package fengliu.cloudmusicroom.client.networking;

import fengliu.cloudmusic.command.MusicCommand;
import fengliu.cloudmusic.music163.IMusic;
import fengliu.cloudmusic.util.TextClickItem;
import fengliu.cloudmusic.util.page.Page;
import fengliu.cloudmusicroom.client.config.Configs;
import fengliu.cloudmusicroom.client.mixin.MusicCommandMixin;
import fengliu.cloudmusicroom.networking.packets.payload.JoinRoomPayload;
import fengliu.cloudmusicroom.networking.packets.payload.RoomListPayload;
import fengliu.cloudmusicroom.networking.packets.payload.RoomPlayMusicPayload;
import fengliu.cloudmusicroom.networking.packets.payload.RoomPlayingListPayload;
import fengliu.cloudmusicroom.networking.packets.payload.client.RoomExitPayload;
import fengliu.cloudmusicroom.room.MusicInfo;
import fengliu.cloudmusicroom.room.MusicRoom;
import fengliu.cloudmusicroom.utils.IdUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * S2C 客户端处理
 */
public class MusicRoomClient {
    private static boolean inMusicRoom = false;
    private static NbtCompound roomInfo = null;

    /**
     * 防止播放时加入房间时发送更新包
     */
    public static boolean inJoinRoomOldPlayer = false;
    public static boolean inUpdateMusic = false;

    public static boolean isInMusicRoom(){
        return MusicRoomClient.inMusicRoom;
    }

    public static NbtCompound getRoomInfo(){
        return MusicRoomClient.roomInfo;
    }

    public static void exitRoom(){
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendMessage(Text.translatable(IdUtil.info("exit.room"),
                    MusicRoomClient.roomInfo.getString(MusicRoom.ROOM_NAME_KEY)), false);
            ClientPlayNetworking.send(new RoomExitPayload(MusicRoomClient.getRoomInfo().getLong(MusicRoom.ROOM_ID_KEY)));
        }
        MusicRoomClient.inMusicRoom = false;
        MusicRoomClient.roomInfo = null;
        MusicRoomClient.inJoinRoomOldPlayer = false;
        MusicRoomClient.inUpdateMusic = false;
    }

    /**
     * 成功加入房间
     */
    public static void joinRoom(JoinRoomPayload payload, ClientPlayNetworking.Context context){
        MusicRoomClient.inMusicRoom = true;
        MusicRoomClient.inUpdateMusic = true;
        MusicRoomClient.roomInfo = payload.roomInfoNbt();
        context.player().sendMessage(Text.translatable(IdUtil.info("join.room"), payload.getRoomName()), false);

        if (MusicCommand.getPlayer().isPlaying()){
            MusicRoomClient.inJoinRoomOldPlayer = true;
        }

        MusicInfo musicInfo = payload.getPlayingMusicInfo();
        if (musicInfo == null){
            context.player().sendMessage(Text.translatable(IdUtil.info("join.room.not.playing"), payload.getRoomName()), false);
            if (!Configs.PLAY.JOIN_ROOM_EXIT_PLAYER.getBooleanValue()){
                return;
            }
            MusicCommandMixin.resetPlayer(new ArrayList<>());
            return;
        }

        IMusic playingMusic = MusicCommandMixin.getMusic163().music(musicInfo.musicId());
        context.player().sendMessage(Text.translatable(IdUtil.info("join.room.playing"), payload.getRoomName(), playingMusic.getName()), false);
        MusicCommandMixin.resetPlayer(playingMusic);
        MusicCommand.getPlayer().start();
    }

    /**
     * 房间开始播放
     */
    public static void RoomPlayMusic(RoomPlayMusicPayload payload, ClientPlayNetworking.Context context) {
        inUpdateMusic = true;
        if (payload.unoccupied()){
            context.player().sendMessage(Text.translatable(IdUtil.info("room.play.unoccupied")), false);
            MusicCommandMixin.resetPlayer(new ArrayList<>());
            return;
        }

        IMusic playingMusic = MusicCommandMixin.getMusic163().music(payload.getPlayingMusicInfo().musicId());
        MusicCommandMixin.resetPlayer(playingMusic);
        MusicCommand.getPlayer().start();
    }

    /**
     * 显示房间列表
     */
    public static void RoomList(RoomListPayload payload, ClientPlayNetworking.Context context) {
        NbtList nbtList = (NbtList) payload.musicRoomList();
        if (nbtList.isEmpty()){
            context.player().sendMessage(Text.translatable(IdUtil.info("not.music.room")), false);
            return;
        }

        Page page = new Page(nbtList) {
            @Override
            protected TextClickItem putPageItem(Object item) {
                NbtCompound musicRoomInfoNbt = (NbtCompound) item;
                String roomName = musicRoomInfoNbt.getString(MusicRoom.ROOM_NAME_KEY);
                return new TextClickItem(Text.literal("§b%s - %s§r§7%s - id：%s".formatted(
                        roomName,
                        musicRoomInfoNbt.getString(MusicRoom.ROOM_OWNER_NAME_KEY),
                        MusicInfo.fromNbtCompound(musicRoomInfoNbt.getCompound(MusicRoom.ROOM_PLAYING_MUSIC_KEY)) == null ? Text.translatable(IdUtil.info("room.unoccupied")).getString(): "",
                        musicRoomInfoNbt.getLong(MusicRoom.ROOM_ID_KEY))),
                        "/cloudmusic-room join \"%s\"".formatted(roomName));
            }
        };

        page.setInfoText(Text.translatable(IdUtil.info("show.room.list")));
        MusicCommand.setPage(page);
        page.look();
    }

    /**
     * 显示房间点歌列表
     */
    public static void roomPlayingList(RoomPlayingListPayload payload, ClientPlayNetworking.Context context) {
        NbtList nbtList = (NbtList) payload.roomPlayingList();
        if (nbtList.isEmpty()){
            context.player().sendMessage(Text.translatable(IdUtil.info("room.play.unoccupied")), false);
            return;
        }

        Page page = new Page(nbtList) {
            @Override
            protected TextClickItem putPageItem(Object item) {
                MusicInfo musicInfo = MusicInfo.fromNbtCompound((NbtCompound) item);
                return new TextClickItem(Text.literal("§b%s§r§7- %s - %s".formatted(
                        musicInfo.musicName(),
                        Text.translatable(IdUtil.info("add.music.player.name"), musicInfo.addMusicPlayerName()).getString(),
                        Text.translatable(IdUtil.info("add.music.time"), new SimpleDateFormat("HH:mm").format(new Date(musicInfo.addMusicTime()))).getString())),
                        "/cloudmusic music %s".formatted(musicInfo.musicId()));
            }
        };

        page.setInfoText(Text.translatable(IdUtil.info("show.room.playing.list"), MusicRoomClient.roomInfo.getString(MusicRoom.ROOM_NAME_KEY)));
        MusicCommand.setPage(page);
        page.look();
    }
}
