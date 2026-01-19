package com.froobert.curvyrails.curve_movement;

import com.froobert.curvyrails.blocks.SmallCurveRail;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SmallCurveHandler implements AbstractCurveHandler {

    @Override
    public Vec3 snapCartPositionAlongTrack(AbstractMinecart cart, BlockState state, double x, double y, double z) {
        SmallCurveRail block = (SmallCurveRail) state.getBlock();
        // Get center of rotation block
        BlockPos trackPos = new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
        final Vec3i center = block.getRotationOrigin(state, trackPos);

        // Get cart position relative to center of rotation
        final double cxe = x - center.getX();
        final double cze = z - center.getZ();

        double distance = Math.sqrt(cxe*cxe + cze*cze);
        // Prevent divide by zero
        if (distance == 0.0 ) {
            distance = 1.0;
        }
        final double scaled_x = cxe / distance * SmallCurveRail.RADIUS;
        final double scaled_z = cze / distance * SmallCurveRail.RADIUS;

        // Get world pos
        final double wx = scaled_x + center.getX();
        final double wz = scaled_z + center.getZ();


        return new Vec3(wx, y, wz);
    }

    @Override
    public Vec3 getTrackTangentAtPos(AbstractMinecart cart, BlockPos trackPos, BlockState state, double x, double y, double z) {
        final SmallCurveRail block = (SmallCurveRail) state.getBlock();
        final Vec3i center = block.getRotationOrigin(state,trackPos);

        // Get cart position relative to center of rotation
        final double cxe = x - center.getX();
        final double cze = z - center.getZ();

        final double t = Math.sqrt((SmallCurveRail.RADIUS*SmallCurveRail.RADIUS)/(cxe*cxe) - 1);

        double slope = cxe / t;

        if (cze > 0.0) {
            slope *= -1.0;
        }

        return new Vec3(1.0, 0.0, slope).normalize();
    }
}
