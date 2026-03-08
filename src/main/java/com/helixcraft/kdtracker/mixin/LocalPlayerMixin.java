package com.helixcraft.kdtracker.mixin;

import com.helixcraft.kdtracker.KDTracker;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    
    @Inject(method = "respawn", at = @At("HEAD"))
    private void onPlayerRespawn(CallbackInfo ci) {
        if (KDTracker.getEventHandler() != null && KDTracker.getEventHandler().isOnMultiplayerServer()) {
            KDTracker.getEventHandler().onPlayerDeath();
        }
    }
}
