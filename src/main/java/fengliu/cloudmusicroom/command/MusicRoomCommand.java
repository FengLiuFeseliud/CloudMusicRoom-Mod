package fengliu.cloudmusicroom.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fengliu.cloudmusicroom.networking.packets.payload.RoomListPayload;
import fengliu.cloudmusicroom.networking.packets.payload.RoomPlayingListPayload;
import fengliu.cloudmusicroom.room.ServerMusicRoom;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MusicRoomCommand {
    public static final List<ServerMusicRoom> musicRoomList = new ArrayList<>();

    private static void runRoom(long roomId, PlayerEntity player, Consumer<ServerMusicRoom> run){
        musicRoomList.forEach(musicRoom -> {
            if (musicRoom.getId() != roomId){
                return;
            }

            run.accept(musicRoom);
        });
    }

    public static void registerAll() {
        LiteralArgumentBuilder<ServerCommandSource> CloudMusicRoom = literal("cloudmusic-room");
        LiteralArgumentBuilder<ServerCommandSource> Create = literal("create");
        LiteralArgumentBuilder<ServerCommandSource> List = literal("list");
        LiteralArgumentBuilder<ServerCommandSource> Join = literal("join");
        LiteralArgumentBuilder<ServerCommandSource> Queue = literal("queue");
        LiteralArgumentBuilder<ServerCommandSource> Switch = literal("switch");
        LiteralArgumentBuilder<ServerCommandSource> Delete = literal("delete");

        CloudMusicRoom.then(Create.then(argument("name",
                StringArgumentType.string()).executes(context -> {
                    musicRoomList.add(new ServerMusicRoom(context.getSource().getServer(), StringArgumentType.getString(context, "name"), context.getSource().getPlayer()));
                    return Command.SINGLE_SUCCESS;
        })));

        CloudMusicRoom.then(List.executes(context -> {
            NbtList musicRoomListNbt = new NbtList();
            musicRoomList.forEach(musicRoom -> musicRoomListNbt.add(musicRoom.toNbtCompound()));
            ServerPlayNetworking.send(context.getSource().getPlayer(), new RoomListPayload(musicRoomListNbt));
            return Command.SINGLE_SUCCESS;
        }));

        CloudMusicRoom.then(Join.then(argument("roomId", LongArgumentType.longArg()).executes(context -> {
            if(context.getSource().getWorld().isClient()){
                return Command.SINGLE_SUCCESS;
            }

            runRoom(LongArgumentType.getLong(context, "roomId"), context.getSource().getPlayer(),
                    musicRoom -> musicRoom.join(context.getSource().getPlayer()));
            return Command.SINGLE_SUCCESS;
        })));

        CloudMusicRoom.then(Queue.executes(commandContext -> {
            musicRoomList.forEach(musicRoom -> {
                if (!musicRoom.inJoinRoom(commandContext.getSource().getPlayer())){
                    return;
                }
                ServerPlayNetworking.send(commandContext.getSource().getPlayer(), new RoomPlayingListPayload(musicRoom.getQueue().toNbtList()));
            });
            return Command.SINGLE_SUCCESS;
        }));

        CloudMusicRoom.then(Switch.executes(commandContext -> {
               musicRoomList.forEach(musicRoom -> {
                   if (!musicRoom.inJoinRoom(commandContext.getSource().getPlayer())) {
                       return;
                   }
                   musicRoom.switchMusic(commandContext.getSource().getPlayer());
               });
               return Command.SINGLE_SUCCESS;
        }));

        CloudMusicRoom.then(Delete.then(
                argument("roomId", LongArgumentType.longArg()).executes(commandContext -> {
                    runRoom(LongArgumentType.getLong(commandContext, "roomId"), commandContext.getSource().getPlayer(), musicRoom -> musicRoom.delete(commandContext.getSource().getPlayer()));
                    return Command.SINGLE_SUCCESS;
        })));

        CommandRegistrationCallback.EVENT.register(((dispatcher, access, environment) -> dispatcher.register(CloudMusicRoom)));
    }
}
