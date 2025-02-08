package fengliu.cloudmusicroom.client.mixin;

import fengliu.cloudmusic.command.MusicCommand;
import fengliu.cloudmusic.music163.IMusic;
import fengliu.cloudmusic.music163.Music163;
import fengliu.cloudmusic.util.MusicPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = MusicCommand.class, remap = false)
public interface MusicCommandMixin {
    @Accessor(value = "music163")
    static Music163 getMusic163(){
        throw new AssertionError();
    }

    @Invoker(value = "resetPlayer")
    static void resetPlayer(IMusic music){};

    @Invoker("resetPlayer")
    static void resetPlayer(List<IMusic> musics){};

}
