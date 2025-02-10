package fengliu.cloudmusicroom.client.networking;

import fengliu.cloudmusic.command.MusicCommand;
import fengliu.cloudmusic.music163.IMusic;
import fengliu.cloudmusic.util.TextClickItem;
import fengliu.cloudmusic.util.page.Page;
import fengliu.cloudmusicroom.client.config.Configs;
import fengliu.cloudmusicroom.client.mixin.MusicCommandMixin;
import fengliu.cloudmusicroom.client.room.ClientMusicRoom;
import fengliu.cloudmusicroom.networking.packets.payload.*;
import fengliu.cloudmusicroom.room.MusicInfo;
import fengliu.cloudmusicroom.room.MusicQueue;
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
    public static ClientMusicRoom musicRoom = null;

    /**
     * 防止播放时加入房间时发送更新包
     */
    public static boolean inJoinRoomOldPlayer = false;
    public static boolean inUpdateMusic = false;
    public static boolean inNotNext = false;

    public static boolean inMusicRoom(){
        return MusicRoomClient.musicRoom != null;
    }

    /**
     * 成功加入房间
     */
    public static void joinRoom(JoinRoomPayload payload, ClientPlayNetworking.Context context){
        MusicRoomClient.inUpdateMusic = true;
        MusicRoomClient.musicRoom = new ClientMusicRoom(payload.roomInfoNbt());
        context.player().sendMessage(Text.translatable(IdUtil.info("join.room"), musicRoom.getName()), false);

        if (MusicCommand.getPlayer().isPlaying()){
            MusicRoomClient.inJoinRoomOldPlayer = true;
        }

        if (musicRoom.getQueue().isUnoccupied()){
            context.player().sendMessage(Text.translatable(IdUtil.info("join.room.not.playing"), musicRoom.getName()), false);
            if (!Configs.PLAY.JOIN_ROOM_EXIT_PLAYER.getBooleanValue()){
                return;
            }
            MusicCommandMixin.resetPlayer(new ArrayList<>());
            return;
        }

        IMusic playingMusic = MusicCommandMixin.getMusic163().music(musicRoom.getQueue().getPlayingMusicInfo().musicId());
        context.player().sendMessage(Text.translatable(IdUtil.info("join.room.playing"), musicRoom.getName(), playingMusic.getName()), false);
        MusicCommandMixin.resetPlayer(playingMusic);
        MusicCommand.getPlayer().start();
        MusicRoomClient.inNotNext = true;
    }

    /**
     * 房间开始播放
     */
    public static void roomPlayMusic(RoomPlayMusicPayload payload, ClientPlayNetworking.Context context) {
        inUpdateMusic = true;
        MusicQueue queue = musicRoom.getQueue();

        if (payload.unoccupied()){
            queue.resetPlaying(null);
            context.player().sendMessage(Text.translatable(IdUtil.info("room.play.unoccupied")), false);
            MusicCommandMixin.resetPlayer(new ArrayList<>());
            return;
        }

        queue.fromNbt(payload.musicInfo());
        MusicInfo musicInfo = queue.getPlayingMusicInfo();

        context.player().sendMessage(Text.translatable(IdUtil.info("room.playing"), musicRoom.getName(), musicInfo.musicName(), musicInfo.addMusicPlayerName()), false);
        IMusic playingMusic = MusicCommandMixin.getMusic163().music(musicInfo.musicId());
        MusicCommandMixin.resetPlayer(playingMusic);
        MusicCommand.getPlayer().start();
        MusicRoomClient.inNotNext = true;
    }

    /**
     * 显示房间列表
     */
    public static void roomList(RoomListPayload payload, ClientPlayNetworking.Context context) {
        NbtList nbtList = (NbtList) payload.musicRoomList();
        if (nbtList.isEmpty()){
            context.player().sendMessage(Text.translatable(IdUtil.info("not.music.room")), false);
            return;
        }

        Page page = new Page(nbtList) {
            @Override
            protected TextClickItem putPageItem(Object item) {
                ClientMusicRoom clientMusicRoom = new ClientMusicRoom((NbtCompound) item);
                return new TextClickItem(Text.literal("§b%s - %s§r§7%s - id：%s".formatted(
                        clientMusicRoom.getName(),
                        clientMusicRoom.getOwnerName(),
                        clientMusicRoom.getQueue().isUnoccupied() ? Text.translatable(IdUtil.info("room.unoccupied")).getString(): "",
                        clientMusicRoom.getId())),
                        "/cloudmusic-room join %s".formatted(clientMusicRoom.getId()));
            }
        };

        page.setInfoText(Text.translatable(IdUtil.info("page.show.room.list")));
        MusicCommand.setPage(page);
        page.look();
    }

    /**
     * 显示房间点歌列表
     */
    public static void roomPlayingList(RoomPlayingListPayload payload, ClientPlayNetworking.Context context) {
        MusicQueue queue = musicRoom.getQueue();
        queue.fromNbt((NbtList) payload.roomPlayingList());

        if (queue.isUnoccupied()){
            context.player().sendMessage(Text.translatable(IdUtil.info("room.play.unoccupied")), false);
            return;
        }

        Page page = new Page(queue.getMusicList()) {
            @Override
            protected TextClickItem putPageItem(Object item) {
                MusicInfo musicInfo = (MusicInfo) item;
                return new TextClickItem(Text.literal("§b%s§r§7- %s - %s".formatted(
                        musicInfo.musicName(),
                        Text.translatable(IdUtil.info("add.music.player.name"), musicInfo.addMusicPlayerName()).getString(),
                        Text.translatable(IdUtil.info("add.music.time"), new SimpleDateFormat("HH:mm").format(new Date(musicInfo.addMusicTime()))).getString())),
                        "/cloudmusic music %s".formatted(musicInfo.musicId()));
            }
        };

        page.setInfoText(Text.translatable(IdUtil.info("page.show.room.playing.list"), musicRoom.getName()));
        MusicCommand.setPage(page);
        page.look();
    }

    /**
     * 所在房间被删除
     */
    public static void roomDelete(RoomDeletePayload payload, ClientPlayNetworking.Context context) {
        musicRoom.delete(MinecraftClient.getInstance().getServer().getPlayerManager().getPlayer(payload.deletePlayerName()));
        MusicRoomClient.musicRoom = null;
        MusicRoomClient.inJoinRoomOldPlayer = false;
        MusicRoomClient.inUpdateMusic = false;
        MusicCommandMixin.resetPlayer(new ArrayList<>());
    }

    /**
     * 所在房间被更新
     */
    public static void roomUpdate(RoomUpdatePayload payload, ClientPlayNetworking.Context context) {
        MusicRoomClient.musicRoom = new ClientMusicRoom(payload.roomInfoNbt());
    }
}
