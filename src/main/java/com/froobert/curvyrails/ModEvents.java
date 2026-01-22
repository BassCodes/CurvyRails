package com.froobert.curvyrails;

import com.froobert.curvyrails.blocks.AbstractCurveRail;
import com.froobert.curvyrails.blocks.CurveRailProperties;
import com.froobert.curvyrails.blocks.SmallCurveRail;
import com.froobert.curvyrails.sound.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import static com.froobert.curvyrails.blocks.ModBlocks.MEDIUM_CURVE_RAIL;
import static com.froobert.curvyrails.blocks.ModBlocks.SMALL_CURVE_RAIL;

@EventBusSubscriber(modid = CurvyRails.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (state.is(Blocks.RAIL)) {
            Player player = event.getEntity();
            if (!player.isShiftKeyDown()) return;

            if (!event.getItemStack().isEmpty()) return;

            final RailShape shape = state.getValue(RailBlock.SHAPE);

            if (!Util.Rail.isCorner(shape)) {
                return;
            }

            final Direction facing = switch (shape) {
                case SOUTH_EAST -> Direction.NORTH;
                case SOUTH_WEST -> Direction.EAST;
                case NORTH_WEST -> Direction.SOUTH;
                case NORTH_EAST -> Direction.WEST;
                default -> throw new IllegalStateException("Unexpected value: " + shape);
            };

            var dir1 = facing.getClockWise();
            var dir2 = dir1.getClockWise();

            var adjacent1 = pos.relative(dir1);
            var adjacent2 = pos.relative(dir2);

            for (BlockPos adj : new BlockPos[]{adjacent1, adjacent2}) {
                var adjBlock = level.getBlockState(adj);
                if (!adjBlock.is(Blocks.RAIL)) {
                    return;
                }
                var adjShape = adjBlock.getValue(RailBlock.SHAPE);
                if (!Util.Rail.isStraight(adjShape)) {
                    return;
                }
            }

            if (!level.getBlockState(pos.relative(facing.getClockWise()).relative(facing.getOpposite())).is(BlockTags.REPLACEABLE)) {
                return;
            }

            if (event.getLevel().isClientSide()) {
                final var rand = level.getRandom();
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), ModSoundEvents.CURVE_FORM_TINK.get(),
                        SoundSource.BLOCKS,
                        0.3F + rand.nextFloat() * 0.1F, 0.9F + rand.nextFloat() * 0.15F, false);
            } else {
                SMALL_CURVE_RAIL.get().placeMultiblock(facing, level, pos.offset(dir2.getNormal()).offset(dir1.getNormal()));
            }

        } else if (state.is(SMALL_CURVE_RAIL.get())) {
            var facing = state.getValue(CurveRailProperties.FACING);
            var section = state.getValue(SmallCurveRail.SECTION);

            if (section != SmallCurveRail.SmallCurveSection.Middle) {
                return;
            }

            // check to see if blocks are correct rail type
            for (var f : new Direction[]{facing.getClockWise(), facing.getOpposite()}) {
                var nb = level.getBlockState(pos.relative(f, 2));
                if (!nb.is(Blocks.RAIL)) {
                    return;
                }
                if (nb.getValue(RailBlock.SHAPE) != Util.Rail.shapeFromDirection(f)) {
                    return;
                }
            }

            if (event.getLevel().isClientSide()) {
                final var rand = level.getRandom();
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), ModSoundEvents.CURVE_FORM_TINK.get(),
                        SoundSource.BLOCKS,
                        0.3F + rand.nextFloat() * 0.1F, 0.9F + rand.nextFloat() * 0.15F, false);
            } else {
                SMALL_CURVE_RAIL.get().destroyMultiblock(level, state, pos, false);
                MEDIUM_CURVE_RAIL.get().placeMultiblock(facing, level, pos.relative(facing, -2).relative(facing.getClockWise(), 2));
            }
        }
    }

    @SubscribeEvent
    private static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return;

        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        Block block = state.getBlock();
        LevelAccessor level = event.getLevel();

        if (block instanceof AbstractCurveRail curveBlock) {
            boolean dropItems = !event.getPlayer().isCreative();
            curveBlock.destroyMultiblock(level, state, pos, dropItems);
        }
    }


}
