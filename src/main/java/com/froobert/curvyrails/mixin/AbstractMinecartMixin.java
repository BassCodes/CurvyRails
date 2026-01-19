package com.froobert.curvyrails.mixin;

import com.froobert.curvyrails.blocks.AbstractCurveRail;
import com.froobert.curvyrails.curve_movement.CurvyMinecartMovementHandler;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Inject into AbstractMinecart and hijack relevant movement functions if
 * cart is interacting with curvyrails tracks.
 */
@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin {

    @Shadow
    protected abstract void applyNaturalSlowdown();

    @Inject(at=@At("HEAD"), method="moveAlongTrack", cancellable = true)
    private void curvyrails$moveAlongTrack(BlockPos trackPos, BlockState state, CallbackInfo ci) {
        var block = state.getBlock();
        if (block instanceof AbstractCurveRail curveBlock) {
            AbstractMinecart cart = (AbstractMinecart) (Object) this;
            CurvyMinecartMovementHandler.ApplyNaturalSlowdown ans = this::applyNaturalSlowdown;
            CurvyMinecartMovementHandler.moveAlongTrack(cart, trackPos, state, curveBlock, ans);
            ci.cancel();
        }
    }

    @Inject(at=@At("HEAD"), method="getPos", cancellable = true)
    private void curvyrails$getPos(double x, double y, double z, CallbackInfoReturnable<Vec3> cir) {
        AbstractMinecart cart = (AbstractMinecart) (Object) this;
        int x_f = Mth.floor(x);
        int y_f = Mth.floor(y);
        int z_f = Mth.floor(z);


        BlockState state = cart.level().getBlockState(new BlockPos(x_f, y_f, z_f));

        var block = state.getBlock();
        if (block instanceof AbstractCurveRail curveBlock) {
            Vec3 ret = CurvyMinecartMovementHandler.getPos(cart, curveBlock.getCurveHandler(), x, y_f, z);
            cir.setReturnValue(ret);
        }
    }

}