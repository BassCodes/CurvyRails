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
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.froobert.curvyrails.blocks.CurveRailProperties.FACING;


public class SmallCurveRail extends AbstractCurveRail<SmallCurveRail.SmallCurveSection> {

    public static final EnumProperty<SmallCurveSection> SECTION = EnumProperty.create("curve_section", SmallCurveSection.class, SmallCurveSection.Entry, SmallCurveSection.Exit, SmallCurveSection.Middle, SmallCurveSection.MiddleLow);
    public static final CircleCurveHandler CURVE_HANDLER = new CircleCurveHandler(1.5);

    public SmallCurveRail(Properties p) {
        super(p);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(BlockStateProperties.WATERLOGGED, false)
                .setValue(FACING, Direction.NORTH)
                .setValue(this.getShapeProperty(), RailShape.EAST_WEST)
                .setValue(SECTION, SmallCurveSection.Entry));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(this.getShapeProperty(), BlockStateProperties.WATERLOGGED, FACING, SECTION);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {

        var entry = this.getPosOfMultiblockSection(SmallCurveSection.Entry, state, pos);
        var exit = this.getPosOfMultiblockSection(SmallCurveSection.Exit, state, pos);
        var center = Util.Math.vec3FromICentered(entry).add(Util.Math.vec3FromICentered(exit)).multiply(0.5, 0.5, 0.5);
        var posF = Util.Math.vec3FromI(pos);
        var offset = posF.subtract(center);

        return Block.box(
                -16, 0, -16,
                16, 2, 16
        ).move(-offset.x, 0.0, -offset.z);
    }

    @Override
    public EnumProperty<SmallCurveSection> getMultiblockSectionProperty() {
        return SECTION;
    }

    @Override
    public AbstractCurveHandler getCurveHandler() {
        return CURVE_HANDLER;
    }

    @Override
    public SmallCurveSection[] getSections() {
        return SmallCurveSection.values();
    }

    @Override
    public BlockPos getMultiblockOrigin(BlockState state, BlockPos pos) {
        final var facing = state.getValue(FACING);
        final var section = state.getValue(SECTION);

        final var offset = section.getOffsetFromOrigin();
        final var rotated = Util.Math.vec3iOrientDirection(offset, facing);
        return pos.subtract(rotated);
    }

    public enum SmallCurveSection implements ICurveSection {
        Entry("entry"),
        Middle("middle"),
        Exit("exit"),
        MiddleLow("middle_low");

        final String name;

        SmallCurveSection(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public Vec3i getOffsetFromOrigin() {
            return switch (this) {
                case Entry -> new Vec3i(-1, 0, 0);
                case Middle -> new Vec3i(-1, 0, -1);
                case Exit -> new Vec3i(0, 0, -1);
                case MiddleLow -> Vec3i.ZERO;
            };
        }
    }

}


