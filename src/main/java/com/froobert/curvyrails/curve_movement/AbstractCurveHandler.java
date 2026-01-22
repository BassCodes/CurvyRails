package com.froobert.curvyrails.curve_movement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * @see CurvyMinecartMovementHandler
 */
public interface AbstractCurveHandler {
    /**
     * @return Cart position snapped on x,z axes to rail
     */
    Vec3 snapCartPositionAlongTrack(AbstractMinecart cart, BlockState state, double x, double y, double z);

    /**
     * @return Angle vector representing tangent line on rail at cart
     */
    Vec3 getTrackTangentAtPos(AbstractMinecart cart, BlockPos trackPos, BlockState state, double x, double y, double z);

}
