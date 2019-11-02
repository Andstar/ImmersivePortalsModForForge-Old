package com.qouteall.immersive_portals.mixin_client;

import io.netty.channel.Channel;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NetworkManager.class, remap = false)
public class MixinNetworkManager {
    @Shadow
    private Channel channel;
    
    //avoid crashing by npe
    @Inject(method = "closeChannel", at = @At("HEAD"), cancellable = true)
    private void onBeforeDisconnect(ITextComponent text_1, CallbackInfo ci) {
        if (channel == null) {
            ci.cancel();
        }
    }
}
