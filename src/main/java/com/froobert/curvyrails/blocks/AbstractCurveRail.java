package com.froobert.curvyrails.blocks;

import com.froobert.curvyrails.Util;
import com.froobert.curvyrails.curve_movement.AbstractCurveHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

import static com.froobert.curvyrails.blocks.CurveRailProperties.FACING;

public abstract class AbstractCurveRail<S extends Enum<S> & ICurveSection> extends RailBlock {
    public AbstractCurveRail(Properties properties) {
        super(properties);
    }

    public abstract AbstractCurveHandler getCurveHandler();

    public abstract BlockPos getMultiblockOrigin(BlockState state, BlockPos pos);

    public abstract S[] getSections();

    public abstract EnumProperty<S> getMultiblockSectionProperty();

    public void placeMultiblock(Direction facing, Level level, BlockPos pos) {
        final var baseRail = this.defaultBlockState().setValue(FACING, facing);
        for (var section : this.getSections()) {
            var offset = pos.offset(Util.Math.vec3iOrientDirection(section.getOffsetFromOrigin(), facing));
            var state = baseRail.setValue(this.getMultiblockSectionProperty(), section);
            level.setBlock(offset, state, Block.UPDATE_CLIENTS);
        }
    }

    public void destroyMultiblock(LevelAccessor level, BlockState state, BlockPos pos, boolean dropItems) {
        var facing = state.getValue(FACING);
        for (var section : this.getSections()) {
            var origin = this.getMultiblockOrigin(state, pos);
            var sectionPos = origin.offset(Util.Math.vec3iOrientDirection(section.getOffsetFromOrigin(), facing));
            var existingBlock = level.getBlockState(sectionPos);
            if (existingBlock.is(state.getBlock())) {
                level.destroyBlock(sectionPos, dropItems);
            }
        }
    }

    public boolean isMultiblockValid(LevelReader level, BlockState state, BlockPos pos) {
        for (var s : this.getSections()) {
            var sectionState = level.getBlockState(this.getPosOfMultiblockSection(s, state, pos));
            if (sectionState.getBlock() instanceof SmallCurveRail curveBlock) {
                var section = sectionState.getValue(this.getMultiblockSectionProperty());
                if (section != s) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    public BlockPos getPosOfMultiblockSection(S section, BlockState state, BlockPos pos) {
        var existingSection = state.getValue(this.getMultiblockSectionProperty());
        var facing = state.getValue(FACING);
        var origin = pos.subtract(Util.Math.vec3iOrientDirection(existingSection.getOffsetFromOrigin(), facing));
        return origin.offset(Util.Math.vec3iOrientDirection(section.getOffsetFromOrigin(), facing));
    }

    /* todo: this really shouldn't be part of this class; it only applies to circle curves */
    public Vec3 getCurveRotationOrigin(BlockState state, BlockPos pos) {
        final var facing = state.getValue(FACING);
        final Vec3i originBlock = this.getMultiblockOrigin(state, pos);
        // Block coordinates are in an arbitrary corner of the block.
        // This offsets the block coordinate to be in the corner of the block
        // opposing the middle of the rail curve.
        final Vec3i offset = switch (facing) {
            case NORTH -> new Vec3i(1, 0, 1);
            case SOUTH -> Vec3i.ZERO;
            case EAST -> new Vec3i(0, 0, 1);
            case WEST -> new Vec3i(1, 0, 0);
            default -> throw new IllegalStateException("Unexpected value: " + facing);
        };
        return Util.Math.vec3FromI(originBlock.offset(offset));
    }


    //
    // RailBlock overrides
    //

    @Override
    public boolean canMakeSlopes(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return super.canSurvive(state, level, pos) && this.isMultiblockValid(level, state, pos);
    }


    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

//    @Override
//    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {}
//
//    @Override
//    protected BlockState updateState(BlockState state, Level level, BlockPos pos, boolean movedByPiston) { return state; }
//
//    @Override
//    protected void updateState(BlockState state, Level level, BlockPos pos, Block neighborBlock) {}
//
//    @Override
//    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {}
//
//    @Override
//    protected BlockState updateDir(Level level, BlockPos pos, BlockState state, boolean alwaysPlace) { return state; }
//
//    @Override
//    protected BlockState rotate(BlockState state, Rotation rot) { return state; }
//
//    @Override
//    protected BlockState mirror(BlockState state, Mirror mirror) { return state; }

}
