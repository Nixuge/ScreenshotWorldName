package me.nixuge.screenshotworldname.mixins.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.ScreenShotHelper;
import net.minecraftforge.common.DimensionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Mixin(ScreenShotHelper.class)
public class ScreenShotHelperMixin {
    @Unique
    private final static Character[] VALID_CHARS_ARR = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9'};
    @Unique
    private final static List<Character> VALID_CHARS = Arrays.asList(VALID_CHARS_ARR);

    @Unique
    private static String screenshotWorldName$sanitizeText(String text) {
        StringBuilder sb = new StringBuilder();
        for (char currentChar : text.toLowerCase().toCharArray()) {
            if (VALID_CHARS.contains(currentChar)) {
                sb.append(currentChar);
            }
        }
        return sb.toString();
    }
    @Inject(method = "getTimestampedPNGFileForDirectory", at = @At("HEAD"))
    private static void emptyFunctionWhoseOnlyPurposeIsToGetTheRemapWorkingBecauseTheRemapFileIsJustEmptyIfIDontHaveAnIncludeSomewhereForSomeReason(File gameDirectory, CallbackInfoReturnable<File> cir) {}

    @ModifyVariable(method = "saveScreenshot(Ljava/io/File;Ljava/lang/String;IILnet/minecraft/client/shader/Framebuffer;)Lnet/minecraft/util/IChatComponent;", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static String modifyScreenshotName(String x) {
        String name;
        ServerData data = Minecraft.getMinecraft().getCurrentServerData();

        if (data != null) {
            name = Minecraft.getMinecraft().getCurrentServerData().serverName;

            if (name == null || name.equals(""))
                name = Minecraft.getMinecraft().getCurrentServerData().serverIP.replace(":25565", "");
        } else {
            //// Actual world name
            // name = MinecraftServer.getServer().getWorldName();

            //// Name from the saves/ folder
            name = DimensionManager.getCurrentSaveRootDirectory().toPath().getFileName().toString();
        }

        name = screenshotWorldName$sanitizeText(name);
        name = name + ".png";

        return name;
    }
}
