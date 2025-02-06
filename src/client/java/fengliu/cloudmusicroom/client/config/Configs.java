package fengliu.cloudmusicroom.client.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fengliu.cloudmusicroom.CloudMusicRoom;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;

import java.io.File;

public class Configs implements IConfigHandler {
    public static Configs INSTANCE = new Configs();
    private static final String CONFIG_FILE_NAME = CloudMusicRoom.MOD_ID + ".json";

    public static class ALL {
        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(

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
