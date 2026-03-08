package com.helixcraft.kdtracker.mixin;

import com.helixcraft.kdtracker.KDTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    
    @Inject(method = "die", at = @At("HEAD"))
    private void onPlayerDie(DamageSource damageSource, CallbackInfo ci) {
        Player dyingPlayer = (Player) (Object) this;
        Minecraft mc = Minecraft.getInstance();
        
        // Check if this is a multiplayer environment and not the local player dying
        if (mc.player != null && dyingPlayer != mc.player && 
            KDTracker.getEventHandler() != null && 
            KDTracker.getEventHandler().isOnMultiplayerServer()) {
            
            // Check if the local player killed this player
            Entity attacker = damageSource.getEntity();
            if (attacker != null && attacker == mc.player) {
                KDTracker.getEventHandler().onPlayerKill();
            }
        }
    }
}
