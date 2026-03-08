package com.helixcraft.kdtracker.mixin;

import com.helixcraft.kdtracker.KDTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    
    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void onServerJoin(ClientboundLoginPacket packet, CallbackInfo ci) {
        ClientPacketListener listener = (ClientPacketListener) (Object) this;
        if (listener.getConnection().getRemoteAddress() != null) {
            String serverAddress = Minecraft.getInstance().getCurrentServer() != null ?
                Minecraft.getInstance().getCurrentServer().ip : "unknown";
            KDTracker.getEventHandler().onServerJoin(serverAddress);
        }
    }
}
