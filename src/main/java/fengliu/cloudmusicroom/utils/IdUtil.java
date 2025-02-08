package fengliu.cloudmusicroom.utils;

import fengliu.cloudmusicroom.CloudMusicRoom;

public class IdUtil{

    public static String info(String name){
        return "%s.info.%s".formatted(CloudMusicRoom.MOD_ID, name);
    }

    public static String option(String name){
        return "%s.option.%s".formatted(CloudMusicRoom.MOD_ID, name);
    }

    public static String optionShow(String name){
        return "%s.option.%s.show".formatted(CloudMusicRoom.MOD_ID, name);
    }

    public static String error(String name){
        return "%s.error.%s".formatted(CloudMusicRoom.MOD_ID, name);
    }
}
