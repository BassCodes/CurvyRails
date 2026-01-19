package com.froobert.curvyrails.blocks;

import com.froobert.curvyrails.Util;
import com.froobert.curvyrails.curve_movement.AbstractCurveHandler;
import com.froobert.curvyrails.curve_movement.SmallCurveHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.froobert.curvyrails.blocks.ModBlocks.SMALL_CURVE_RAIL;

public class SmallCurveRail extends AbstractCurveRail {

    public enum CurveSection implements StringRepresentable {
        Entry("entry"),
        Middle("middle"),
        Exit("exit"),
        MiddleLow("middle_low");

        final String name;
        CurveSection(String name) { this.name = name; }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }



    public static final EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);
    public static final EnumProperty<CurveSection> SECTION = EnumProperty.create("curve_section", CurveSection.class, CurveSection.Entry, CurveSection.Exit, CurveSection.Middle, CurveSection.MiddleLow);

    public SmallCurveRail(Properties p) {
        super(p);
        this.registerDefaultState(this.stateDefinition.any()
                        .setValue(BlockStateProperties.WATERLOGGED, false)
                        .setValue(FACING,Direction.NORTH)
                        .setValue(SECTION, CurveSection.Entry));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, BlockStateProperties.WATERLOGGED, FACING, SECTION);
    }

    public static final SmallCurveHandler CURVE_HANDLER = new SmallCurveHandler();
    public static final double RADIUS = 1.5;


    @Override
    public AbstractCurveHandler getCurveHandler() { return CURVE_HANDLER; }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,  CollisionContext context) {
        var center = this.getCenter(state,pos);
        var posF = Util.Math.vec3FromI(pos);
        var offset = posF.subtract(center);
        final double offset_x = offset.x * 16;
        final double offset_z = offset.z * 16;

        return Block.box(
                -16 - offset_x, 0, -16 - offset_z,
                16 - offset_x, 2, 16 - offset_z
        );
    }

    public void placeCurve(Direction facing, Level level, BlockPos middlePos) {
        final var entryPos = middlePos.relative(facing.getOpposite());
        final var exitPos = middlePos.relative(facing.getClockWise());
        final var middleLowPos = entryPos.relative(facing.getClockWise());
        final var baseRail = SMALL_CURVE_RAIL.get().defaultBlockState().setValue(SmallCurveRail.FACING, facing);

        level.setBlock(middlePos, baseRail.setValue(SmallCurveRail.SECTION, SmallCurveRail.CurveSection.Middle).setValue(SHAPE, Util.Rail.cornerFromDirectionClockwise(facing)), Block.UPDATE_ALL );
        level.setBlock(entryPos, baseRail.setValue(SmallCurveRail.SECTION, SmallCurveRail.CurveSection.Entry).setValue(SHAPE, Util.Rail.shapeFromDirection(facing)), Block.UPDATE_ALL);
        level.setBlock(exitPos, baseRail.setValue(SmallCurveRail.SECTION, SmallCurveRail.CurveSection.Exit).setValue(SHAPE, Util.Rail.shapeFromDirection(facing.getClockWise())), Block.UPDATE_ALL);
        level.setBlock(middleLowPos, baseRail.setValue(SmallCurveRail.SECTION, SmallCurveRail.CurveSection.MiddleLow).setValue(SHAPE, Util.Rail.cornerFromDirectionClockwise(facing.getCounterClockWise())), Block.UPDATE_ALL);
    }

    public Vec3i getRotationOriginBlock(BlockState state,  BlockPos pos) {
        final var facing = state.getValue(SmallCurveRail.FACING);
        final var section = state.getValue(SmallCurveRail.SECTION);

        final Vec3i offset_from_origin = switch (section) {
            case CurveSection.Entry -> facing.getClockWise().getNormal();
            case CurveSection.Exit -> facing.getOpposite().getNormal();
            case CurveSection.Middle -> facing.getOpposite().getNormal().offset(facing.getClockWise().getNormal());
            case CurveSection.MiddleLow -> Vec3i.ZERO;
        };

        return pos.offset(offset_from_origin);
    }

    public Vec3i getRotationOrigin(BlockState state, BlockPos pos) {
        final var facing = state.getValue(SmallCurveRail.FACING);
        final Vec3i originBlock = this.getRotationOriginBlock(state,pos);
        // Block coordinates are in an arbitrary corner of the block.
        // This offsets the block coordinate to be in the corner of the block
        // opposing the middle of the rail curve.
        final Vec3i offset = switch (facing) {
            case NORTH -> new Vec3i(1,0,1);
            case SOUTH -> Vec3i.ZERO;
            case EAST -> new Vec3i(0, 0, 1);
            case WEST -> new Vec3i(1, 0, 0);
            default -> throw new IllegalStateException("Unexpected value: " + facing);
        };

        return originBlock.offset(offset);
    }

    public Vec3 getCenter(BlockState state, BlockPos pos) {

        var entry = this.getEntryPos(state,pos);
        var exit = this.getExitPos(state,pos);

        return Util.Math.vec3FromICentered(entry).add(Util.Math.vec3FromICentered(exit)).multiply(0.5, 0.5, 0.5);
    }

    public BlockPos getMiddlePos(BlockState state, BlockPos pos) {
        final var facing = state.getValue(SmallCurveRail.FACING);
        final Vec3i originBlock = getRotationOriginBlock(state,pos);
        return new BlockPos(originBlock.relative(facing).relative(facing.getCounterClockWise()));
    }

    public BlockPos getExitPos(BlockState state, BlockPos pos) {
        final var facing = state.getValue(SmallCurveRail.FACING);
        final Vec3i originBlock = getRotationOriginBlock(state,pos);
        return new BlockPos(originBlock.relative(facing));
    }

    public BlockPos getEntryPos(BlockState state, BlockPos pos) {
        final var facing = state.getValue(SmallCurveRail.FACING);
        final Vec3i originBlock = getRotationOriginBlock(state,pos);
        return new BlockPos(originBlock.relative(facing.getCounterClockWise()));
    }

    public BlockPos getMiddleLowPos(BlockState state, BlockPos pos) {
        return new BlockPos(getRotationOriginBlock(state,pos));
    }

}


