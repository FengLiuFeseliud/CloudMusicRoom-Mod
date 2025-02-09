package fengliu.cloudmusicroom.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fengliu.cloudmusicroom.networking.packets.payload.RoomListPayload;
import fengliu.cloudmusicroom.networking.packets.payload.RoomPlayingListPayload;
import fengliu.cloudmusicroom.room.IMusicRoom;
import fengliu.cloudmusicroom.room.MusicRoom;
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
    public static final List<IMusicRoom> musicRoomList = new ArrayList<>();

    private static void runRoom(String roomName, PlayerEntity player, Consumer<IMusicRoom> run){
        musicRoomList.forEach(iMusicRoom -> {
            if (!iMusicRoom.getName().equals(roomName)){
                return;
            }

            run.accept(iMusicRoom);
        });
    }

    public static void registerAll() {
        LiteralArgumentBuilder<ServerCommandSource> CloudMusicRoom = literal("cloudmusic-room");
        LiteralArgumentBuilder<ServerCommandSource> Create = literal("create");
        LiteralArgumentBuilder<ServerCommandSource> List = literal("list");
        LiteralArgumentBuilder<ServerCommandSource> Join = literal("join");
        LiteralArgumentBuilder<ServerCommandSource> Queue = literal("queue");
        LiteralArgumentBuilder<ServerCommandSource> Switch = literal("switch");

        CloudMusicRoom.then(Create.then(argument("name",
                StringArgumentType.string()).executes(context -> {
                    musicRoomList.add(new MusicRoom(context.getSource().getServer(), StringArgumentType.getString(context, "name"), context.getSource().getPlayer()));
                    return Command.SINGLE_SUCCESS;
        })));

        CloudMusicRoom.then(List.executes(context -> {
            NbtList musicRoomListNbt = new NbtList();
            musicRoomList.forEach(musicRoom -> musicRoomListNbt.add(((MusicRoom) musicRoom).toNbtCompound()));
            ServerPlayNetworking.send(context.getSource().getPlayer(), new RoomListPayload(musicRoomListNbt));
            return Command.SINGLE_SUCCESS;
        }));

        CloudMusicRoom.then(Join.then(argument("name", StringArgumentType.string()).executes(context -> {
            if(context.getSource().getWorld().isClient()){
                return Command.SINGLE_SUCCESS;
            }

            runRoom(StringArgumentType.getString(context, "name"), context.getSource().getPlayer(),
                    iMusicRoom -> iMusicRoom.join(context.getSource().getPlayer()));
            return Command.SINGLE_SUCCESS;
        })));

        CloudMusicRoom.then(Queue.executes(commandContext -> {
            musicRoomList.forEach(iMusicRoom -> {
                if (!iMusicRoom.inJoinRoom(commandContext.getSource().getPlayer())){
                    return;
                }
                ServerPlayNetworking.send(commandContext.getSource().getPlayer(), new RoomPlayingListPayload(iMusicRoom.getQueue().toNbtList()));
            });
            return Command.SINGLE_SUCCESS;
        }));

        CommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            commandDispatcher.register(CloudMusicRoom.then(Switch.executes(commandContext -> {
                musicRoomList.forEach(iMusicRoom -> {
                    if (!iMusicRoom.inJoinRoom(commandContext.getSource().getPlayer())){
                        return;
                    }
                    iMusicRoom.switchMusic(commandContext.getSource().getPlayer());
                });
                return Command.SINGLE_SUCCESS;
            })));
        }));
    }
}
