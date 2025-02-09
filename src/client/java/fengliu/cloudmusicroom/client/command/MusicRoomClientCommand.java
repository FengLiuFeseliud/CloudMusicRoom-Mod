package fengliu.cloudmusicroom.client.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fengliu.cloudmusic.command.MusicCommand;
import fengliu.cloudmusic.music163.data.Music;
import fengliu.cloudmusic.util.TextClickItem;
import fengliu.cloudmusic.util.page.Page;
import fengliu.cloudmusicroom.CloudMusicRoom;
import fengliu.cloudmusicroom.client.mixin.MusicCommandMixin;
import fengliu.cloudmusicroom.networking.packets.payload.client.AddRoomMusicPayload;
import fengliu.cloudmusicroom.room.MusicInfo;
import fengliu.cloudmusicroom.utils.IdUtil;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;


public class MusicRoomClientCommand {

    private static final Text[] helps = {
            Text.translatable("cloudmusicroom.help.create"),
            Text.translatable("cloudmusicroom.help.list"),
            Text.translatable("cloudmusicroom.help.join"),
            Text.translatable("cloudmusicroom.help.queue"),
            Text.translatable("cloudmusicroom.help.switch"),
            Text.translatable("cloudmusicroom.help.delete"),
            Text.translatable("cloudmusicroom.help.exit"),
            Text.translatable("cloudmusicroom.help.add"),
            Text.translatable("cloudmusicroom.help.cloudmusicroom")
    };
    private static final List<Text> helpsList = new ArrayList<>();

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

        Collections.addAll(helpsList, helps);
        CloudMusicRoomClient.executes(commandContext -> {
            Page page = new Page(helpsList) {
                @Override
                protected TextClickItem putPageItem(Object data) {
                    return new TextClickItem((MutableText) data, "");
                }
            };
            page.setInfoText(Text.translatable(IdUtil.info("page.help")));
            MusicCommand.setPage(page);
            page.look(commandContext.getSource());
            return Command.SINGLE_SUCCESS;
        });

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
                                        "",
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
