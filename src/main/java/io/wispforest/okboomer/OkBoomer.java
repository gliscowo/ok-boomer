package io.wispforest.okboomer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class OkBoomer implements ClientModInitializer {

    public static final io.wispforest.okboomer.OkConfig CONFIG = io.wispforest.okboomer.OkConfig.createAndLoad();

    public static double boomDivisor = 1;
    public static boolean booming = false;

    public static double screenBoom = 1;
    public static boolean currentlyScreenBooming = false;

    private static boolean smoothCameraRestoreValue = false;

    public static final KeyBinding BOOM_BINDING = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.ok-boomer.boom", GLFW.GLFW_KEY_C, KeyBinding.MISC_CATEGORY)
    );

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            currentlyScreenBooming = OkBoomer.CONFIG.enableScreenBooming()
                    && InputUtil.isKeyPressed(client.getWindow().getHandle(), KeyBindingHelper.getBoundKeyOf(BOOM_BINDING).getCode())
                    && (Screen.hasControlDown() || currentlyScreenBooming);

            if (booming != BOOM_BINDING.isPressed()) {
                boolean nowBooming = false;
                while (BOOM_BINDING.wasPressed()) {
                    nowBooming = true;
                }

                if (booming) {
                    boomDivisor = 1;
                    client.options.smoothCameraEnabled = smoothCameraRestoreValue;
                } else {
                    boomDivisor = 7.5;
                    smoothCameraRestoreValue = client.options.smoothCameraEnabled;

                    if (CONFIG.useCinematicCamera()) {
                        client.options.smoothCameraEnabled = true;
                    }
                }

                booming = nowBooming;
            }
        });
    }

    public static int minBoom() {
        return CONFIG.boomLimits.allowBoomingOut() ? 0 : 1;
    }

    public static int maxBoom() {
        return CONFIG.boomLimits.enableLimits() ? CONFIG.boomLimits.maxBoom() : Integer.MAX_VALUE;
    }

    public static int maxScreenBoom() {
        return CONFIG.boomLimits.enableLimits() ? CONFIG.boomLimits.maxScreenBoom() : Integer.MAX_VALUE;
    }
}
