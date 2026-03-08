package com.helixcraft.kdtracker.mixin;

import com.helixcraft.kdtracker.KDTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    
    @Inject(method = "addEntity", at = @At("RETURN"))
    private void onEntityAdded(Entity entity, CallbackInfo ci) {
        // This can be used for additional tracking if needed
    }
}
