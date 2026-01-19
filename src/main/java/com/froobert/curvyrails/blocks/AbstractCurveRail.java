package com.froobert.curvyrails.blocks;

import com.froobert.curvyrails.curve_movement.AbstractCurveHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public abstract class AbstractCurveRail extends RailBlock {
    public AbstractCurveRail(Properties properties) {
        super(properties);
    }

    public abstract AbstractCurveHandler getCurveHandler();

    @Override
    public boolean canMakeSlopes(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {}

    @Override
    protected BlockState updateState(BlockState state, Level level, BlockPos pos, boolean movedByPiston) { return state; }

    @Override
    protected void updateState(BlockState state, Level level, BlockPos pos, Block neighborBlock) {}

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {}

    @Override
    protected BlockState updateDir(Level level, BlockPos pos, BlockState state, boolean alwaysPlace) { return state; }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) { return state; }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) { return state; }



}
