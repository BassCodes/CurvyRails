package com.froobert.curvyrails.curve_movement;

import com.froobert.curvyrails.Util;
import com.froobert.curvyrails.blocks.AbstractCurveRail;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * Contains methods which replace like-named methods in AbstractMinecart when cart
 * is on a curvyrail track.
 *
 * @see com.froobert.curvyrails.mixin.AbstractMinecartMixin
 * @see AbstractMinecart
 * @see AbstractCurveHandler
 *
 */
public class CurvyMinecartMovementHandler {

    private static final double CART_ALTITUDE = 0.0625;

    public static void moveAlongTrack(AbstractMinecart cart, BlockPos trackPos, BlockState state, AbstractCurveRail<?> curveBlock, ApplyNaturalSlowdown applyNaturalSlowdown) {
        final Vec3 cartStartPos = cart.position();
        final Vec3 adjustedPos = cart.getPos(cartStartPos.x, cartStartPos.y, cartStartPos.z);

        // snap cart vertically to track.
        double y1 = trackPos.getY();

        AbstractCurveHandler curveHandler = curveBlock.getCurveHandler();

        if (adjustedPos != null) {
            var snappedVelocity = snapCartVelocityAlongTrack(cart, trackPos, state, curveHandler, adjustedPos.x, adjustedPos.y, adjustedPos.z);
            cart.setDeltaMovement(snappedVelocity);
        }

        {
            // if passenger is moving and cart is not
            // then give cart some of that speed
            // (Why is this a feature?)
            Entity passenger = cart.getFirstPassenger();
            if (passenger instanceof Player) {
                Vec3 p_delta = passenger.getDeltaMovement();
                double p_delta_horoz = p_delta.horizontalDistanceSqr();
                double c_delta = cart.getDeltaMovement().horizontalDistanceSqr();
                if (p_delta_horoz > 1.0E-4 && c_delta < 0.01) {
                    cart.setDeltaMovement(cart.getDeltaMovement().add(p_delta.x * 0.1, 0.0, p_delta.z * 0.1));
                }
            }
        }


        final Vec3 snapped = curveHandler.snapCartPositionAlongTrack(cart, state, cart.getX(), cart.getY(), cart.getZ());
        cart.setPos(snapped.x, y1, snapped.z);

        // Changes pos
        cart.moveMinecartOnRail(trackPos);
        applyNaturalSlowdown.applyNaturalSlowdown();

        {
            final Vec3 new_pos = cart.getPos(cart.getX(), cart.getY(), cart.getZ());
            // (they are null if position is no longer on a rail block (or was never on a rail block))
            if (new_pos != null && adjustedPos != null) {
                cart.setPos(cart.getX(), new_pos.y, cart.getZ());
            }
        }

        if (cart.shouldDoRailFunctions()) {
            // activator rail, etc
            curveBlock.onMinecartPass(state, cart.level(), trackPos, cart);
        }


    }

    @Nullable
    public static Vec3 getPos(AbstractMinecart cart, AbstractCurveHandler curveHandler, double x, double y, double z) {
        var cartPos = new Vec3(x, y, z);
        var trackPos = Util.Math.blockPosFromVec(cartPos);

        // vanilla code assumes
        Level level = cart.level();

        int x_f = Mth.floor(cartPos.x);
        int y_f = Mth.floor(cartPos.y);
        int z_f = Mth.floor(cartPos.z);

        if (level.getBlockState(new BlockPos(x_f, y_f - 1, z_f)).is(BlockTags.RAILS)) {
            --y_f;
        }
        BlockState state = level.getBlockState(trackPos);
        if (BaseRailBlock.isRail(state)) {
            return curveHandler.snapCartPositionAlongTrack(cart, state, x, y_f + CART_ALTITUDE, z);
        } else {
            return null;
        }
    }

    private static Vec3 snapCartVelocityAlongTrack(AbstractMinecart cart, BlockPos trackPos, BlockState state, AbstractCurveHandler curveHandler, double x, double y, double z) {
        // Redirect velocity in track direction.
        final Vec3 m_delta = cart.getDeltaMovement();

        // Clamp delta-position to 2.0 max
        double limited_magnitude = Math.min(2.0, m_delta.horizontalDistance());

        final Vec3 tangent = curveHandler.getTrackTangentAtPos(cart, trackPos, state, x, y, z);

        final double dot = tangent.x * m_delta.x + tangent.z * m_delta.z;
        if (dot < 0.0) {
            limited_magnitude *= -1.0;
        }


        final double vx = limited_magnitude * tangent.x;
        final double vz = limited_magnitude * tangent.z;

        return new Vec3(vx, 0.0, vz);
    }


    // Hack to access private method of AbstractMinecart
    @FunctionalInterface
    public interface ApplyNaturalSlowdown {
        void applyNaturalSlowdown();
    }


}
