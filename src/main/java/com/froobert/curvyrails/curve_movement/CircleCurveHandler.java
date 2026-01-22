package com.froobert.curvyrails.curve_movement;

import com.froobert.curvyrails.blocks.AbstractCurveRail;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CircleCurveHandler implements AbstractCurveHandler {


    final double radius;
    final double radiusSquared;

    public CircleCurveHandler(double radius) {
        this.radius = radius;
        this.radiusSquared = this.radius * this.radius;
    }


    @Override
    public Vec3 snapCartPositionAlongTrack(AbstractMinecart cart, BlockState state, double x, double y, double z) {
        AbstractCurveRail block = (AbstractCurveRail) state.getBlock();
        // Get center of rotation block
        BlockPos trackPos = new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
        final Vec3 center = block.getCurveRotationOrigin(state, trackPos);

        // Get cart position relative to center of rotation
        final double cxe = x - center.x();
        final double cze = z - center.z();

        double distance = Math.sqrt(cxe * cxe + cze * cze);
        // Prevent divide by zero
        if (distance == 0.0) {
            distance = 1.0;
        }

        final double scaled_x = cxe / distance * this.radius;
        final double scaled_z = cze / distance * this.radius;

        // Get world pos
        final double wx = scaled_x + center.x();
        final double wz = scaled_z + center.z();

        return new Vec3(wx, y, wz);
    }

    @Override
    public Vec3 getTrackTangentAtPos(AbstractMinecart cart, BlockPos trackPos, BlockState state, double x, double y, double z) {
        AbstractCurveRail block = (AbstractCurveRail) state.getBlock();
        final Vec3 center = block.getCurveRotationOrigin(state, trackPos);

        // Get cart position relative to center of rotation
        final double cxe = x - center.x();
        final double cze = z - center.z();

        final double t = Math.sqrt((radiusSquared) / (cxe * cxe) - 1);

        double slope = cxe / t;

        if (cze > 0.0) {
            slope *= -1.0;
        }

        return new Vec3(1.0, 0.0, slope).normalize();
    }
}
