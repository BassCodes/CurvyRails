package com.froobert.curvyrails.blocks;

import com.froobert.curvyrails.Util;
import com.froobert.curvyrails.curve_movement.AbstractCurveHandler;
import com.froobert.curvyrails.curve_movement.CircleCurveHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.froobert.curvyrails.blocks.CurveRailProperties.FACING;


public class MediumCurveRail extends AbstractCurveRail<MediumCurveRail.MediumCurveSection> {

    public static final VoxelShape BOUNDS_NORTH = Shapes.or(
            Block.box(
                    -24, 0, -8,
                    -8, 2, 24
            ),
            Block.box(
                    -8, 0, -24,
                    24, 2, -8
            ),
            Block.box(
                    -8, 0, -8,
                    8, 2, 8
            ));
    public static final VoxelShape BOUNDS_EAST = Util.Math.rotateVoxelShapeClockwise(BOUNDS_NORTH);
    public static final VoxelShape BOUNDS_SOUTH = Util.Math.rotateVoxelShapeClockwise(BOUNDS_EAST);
    public static final VoxelShape BOUNDS_WEST = Util.Math.rotateVoxelShapeClockwise(BOUNDS_SOUTH);
    public static final EnumProperty<MediumCurveSection> SECTION = EnumProperty.create("curve_section", MediumCurveSection.class, MediumCurveSection.Entry, MediumCurveSection.Exit, MediumCurveSection.Middle, MediumCurveSection.LeftMiddle, MediumCurveSection.TopMiddle);
    public static final CircleCurveHandler CURVE_HANDLER = new CircleCurveHandler(2.5);

    public MediumCurveRail(Properties p) {
        super(p);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(BlockStateProperties.WATERLOGGED, false)
                .setValue(FACING, Direction.NORTH)
                .setValue(SECTION, MediumCurveSection.Entry)
                .setValue(SHAPE, RailShape.EAST_WEST));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, BlockStateProperties.WATERLOGGED, FACING, SECTION);
    }

    @Override
    public AbstractCurveHandler getCurveHandler() {
        return CURVE_HANDLER;
    }

    @Override
    public MediumCurveSection[] getSections() {
        return MediumCurveSection.values();
    }

    @Override
    public EnumProperty<MediumCurveSection> getMultiblockSectionProperty() {
        return SECTION;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {

        var entry = this.getPosOfMultiblockSection(MediumCurveSection.Entry, state, pos);
        var exit = this.getPosOfMultiblockSection(MediumCurveSection.Exit, state, pos);

        var center = Util.Math.vec3FromICentered(entry).add(Util.Math.vec3FromICentered(exit)).multiply(0.5, 0.5, 0.5);
        var posF = Util.Math.vec3FromI(pos);
        var offset = posF.subtract(center);
        var facing = state.getValue(FACING);
        var bounds = switch (facing) {
            case NORTH -> BOUNDS_NORTH;
            case SOUTH -> BOUNDS_SOUTH;
            case WEST -> BOUNDS_WEST;
            case EAST -> BOUNDS_EAST;
            default -> throw new IllegalStateException("Unexpected value: " + facing);
        };

        return bounds.move(-offset.x, 0.0, -offset.z);
    }

    public BlockPos getMultiblockOrigin(BlockState state, BlockPos pos) {
        final var facing = state.getValue(FACING);
        final var section = state.getValue(SECTION);

        final var offset = section.getOffsetFromOrigin();
        final var rotated = Util.Math.vec3iOrientDirection(offset, facing);
        return pos.subtract(rotated);
    }

    public enum MediumCurveSection implements ICurveSection {
        Entry("entry"),
        Exit("exit"),
        LeftMiddle("left_middle"),
        Middle("middle"),
        TopMiddle("top_middle");

        final String name;

        MediumCurveSection(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        @Override
        public Vec3i getOffsetFromOrigin() {
            return switch (this) {
                case Entry -> new Vec3i(-2, 0, 0);
                case Exit -> new Vec3i(0, 0, -2);
                case Middle -> new Vec3i(-1, 0, -1);
                case TopMiddle -> new Vec3i(-1, 0, -2);
                case LeftMiddle -> new Vec3i(-2, 0, -1);
            };
        }
    }

}


