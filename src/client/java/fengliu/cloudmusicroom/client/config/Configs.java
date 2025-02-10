package fengliu.cloudmusicroom.client.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fengliu.cloudmusic.util.ConfigUtil;
import fengliu.cloudmusicroom.CloudMusicRoom;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;

import java.io.File;
import java.util.List;

public class Configs implements IConfigHandler {
    public static Configs INSTANCE = new Configs();
    private static final String CONFIG_FILE_NAME = "%s.json".formatted(CloudMusicRoom.MOD_ID);

    public static class ALL {
        public static final ConfigBoolean JOIN_ROOM_EXIT_PLAYER = ConfigUtil.addConfigBoolean("join.room.exit.player");
        public static final ConfigHotkey OPEN_CONFIG_GUI = ConfigUtil.addConfigHotkey("open.cloudmusicroom.config.gui", "LEFT_CONTROL,C,N");
        public static final ConfigHotkey SWITCH_MUSIC = ConfigUtil.addConfigHotkey("switch.music");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                JOIN_ROOM_EXIT_PLAYER,
                OPEN_CONFIG_GUI,
                SWITCH_MUSIC
        );
    }

    public static class PLAY {
        public static final ConfigBoolean JOIN_ROOM_EXIT_PLAYER = ALL.JOIN_ROOM_EXIT_PLAYER;

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                JOIN_ROOM_EXIT_PLAYER
        );
    }

    public static class HOTKEY {
        public static final ConfigHotkey OPEN_CONFIG_GUI = ALL.OPEN_CONFIG_GUI;
        public static final ConfigHotkey SWITCH_MUSIC = ALL.SWITCH_MUSIC;

        public static final List<ConfigHotkey> HOTKEY_LIST = ImmutableList.of(
                OPEN_CONFIG_GUI,
                SWITCH_MUSIC
        );
    }

    @Override
    public void load() {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);
        if (configFile.isFile() && configFile.exists()) {
            JsonElement element = JsonUtils.parseJsonFile(configFile);
            if (element == null || !element.isJsonObject()) {
                return;
            }

            JsonObject root = element.getAsJsonObject();
            ConfigUtils.readConfigBase(root, "ALLConfigs", Configs.ALL.OPTIONS);
        }
    }

    @Override
    public void save() {
        File dir = FileUtils.getConfigDirectory();
        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            JsonObject root = new JsonObject();
            ConfigUtils.writeConfigBase(root, "ALLConfigs", Configs.ALL.OPTIONS);
            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }
}
