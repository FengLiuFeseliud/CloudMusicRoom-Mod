package fengliu.cloudmusicroom.client.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fengliu.cloudmusic.command.MusicCommand;
import fengliu.cloudmusic.music163.IMusic;
import fengliu.cloudmusic.music163.data.Music;
import fengliu.cloudmusicroom.CloudMusicRoom;
import fengliu.cloudmusicroom.client.mixin.MusicCommandMixin;
import fengliu.cloudmusicroom.networking.packets.payload.client.AddRoomMusicPayload;
import fengliu.cloudmusicroom.room.MusicInfo;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.function.Consumer;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;


public class MusicRoomClientCommand {

    private static void runCommand(CommandContext<FabricClientCommandSource> context, Consumer<CommandContext<FabricClientCommandSource>> run) {
        Thread commandThread = new Thread(() -> {
            try {
                run.accept(context);
            } catch (Exception err) {
                context.getSource().sendFeedback(Text.literal(err.getMessage()));
            }

        });
        commandThread.setDaemon(true);
        commandThread.setName("%s Thread".formatted(CloudMusicRoom.MOD_ID));
        commandThread.start();
    }

    public static void registerAll() {
        LiteralArgumentBuilder<FabricClientCommandSource> CloudMusicRoomClient = literal("cloudmusic-room-client");
        LiteralArgumentBuilder<FabricClientCommandSource> Add = literal("add");

        CloudMusicRoomClient.then(Add.then(argument("roomId", LongArgumentType.longArg()).then(
                argument("musicId", LongArgumentType.longArg()).executes(commandContext -> {
                    runCommand(commandContext, context -> {
                        Music music = MusicCommandMixin.getMusic163().music(LongArgumentType.getLong(context, "musicId"));

                        ClientPlayNetworking.send(new AddRoomMusicPayload(
                                LongArgumentType.getLong(context, "roomId"),
                                (new MusicInfo(
                                        music.getId(),
                                        "%s - %s".formatted(music.getName(), Music.getArtistsName(music.artists)),
                                        MinecraftClient.getInstance().player.getUuid(),
                                        System.currentTimeMillis()
                                )).toNbtCompound()));
                    });
                    return Command.SINGLE_SUCCESS;
                }))));

        ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess) -> {
           commandDispatcher.register(CloudMusicRoomClient);
        });
    }
}
