package com.qouteall.immersive_portals.mixin.collision;

import com.qouteall.immersive_portals.ducks.IEEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartEntity.class)
public class MixinAbstractMinecartEntity {
    @Inject(
        method = "Lnet/minecraft/entity/item/minecart/AbstractMinecartEntity;tick()V",
        at = @At("HEAD")
    )
    private void onTick(CallbackInfo ci) {
        ((IEEntity) this).tickCollidingPortal(1);
    }
}
