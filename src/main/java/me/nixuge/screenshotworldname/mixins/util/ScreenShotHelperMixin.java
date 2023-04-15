package me.nixuge.screenshotworldname.mixins.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ScreenShotHelper;
import net.minecraftforge.common.DimensionManager;

@Mixin(ScreenShotHelper.class)
public class ScreenShotHelperMixin {
    private static Character[] VALID_CHARS_ARR = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9'};
    private static List<Character> VALID_CHARS = Arrays.asList(VALID_CHARS_ARR);

    private static String sanitizeText(String text) {
        StringBuilder sb = new StringBuilder();  
        for (char currentChar : text.toLowerCase().toCharArray()) {
            if (VALID_CHARS.contains(currentChar)) {
                sb.append(currentChar);
            }
        }
        return sb.toString();
    }

    @Overwrite
    public static IChatComponent saveScreenshot(File gameDirectory, int width, int height, Framebuffer buffer) {
        String name = "undefined";

        ServerData data = Minecraft.getMinecraft().getCurrentServerData();
        if (data != null) {
            name = Minecraft.getMinecraft().getCurrentServerData().serverName;

            if (name == null || name == "")
                name = Minecraft.getMinecraft().getCurrentServerData().serverIP.replace(":25565", "");
        } else {
            //// Actual world name
            // name = MinecraftServer.getServer().getWorldName();

            //// Name from the saves/ folder
            name = DimensionManager.getCurrentSaveRootDirectory().toPath().getFileName().toString();
        }

        name = sanitizeText(name);
        name = name + ".png";

        return ScreenShotHelper.saveScreenshot(gameDirectory, name, width, height, buffer);
    }
}
