package com.helixcraft.kdtracker.mixin;

import com.helixcraft.kdtracker.ui.StatsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NUCLEAR OPTION: Completely disable blur effect for StatsScreen only.
 * 
 * This mixin intercepts the blur shader call in GameRenderer and cancels it
 * when StatsScreen is open. This is necessary because the blur is being triggered
 * somewhere deeper in Minecraft's rendering pipeline, outside of our render() method.
 * 
 * Result:
 * - Game world behind StatsScreen will NOT be blurred
 * - ALL GUI content will be 100% sharp and readable
 * - Dark semi-transparent panel provides visual separation
 * - Only affects StatsScreen - all other screens keep their blur
 */
@Mixin(GameRenderer.class)
public class BlurMixin {
    
    @Inject(
        method = "processBlurEffect",
        at = @At("HEAD"),
        cancellable = true
    )
    private void cancelBlurForStatsScreen(CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof StatsScreen) {
            ci.cancel();
        }
    }
}
