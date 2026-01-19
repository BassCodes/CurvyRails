package com.froobert.curvyrails;

import com.froobert.curvyrails.blocks.SmallCurveRail;
import com.froobert.curvyrails.sound.ModSoundEvents;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
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
import org.joml.Vector3f;

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

            final Pair<Direction, Direction> cornerAdjacent = switch (shape) {
                case SOUTH_EAST -> new Pair<>(Direction.SOUTH, Direction.EAST);
                case SOUTH_WEST -> new Pair<>(Direction.SOUTH, Direction.WEST);
                case NORTH_WEST -> new Pair<>(Direction.NORTH, Direction.WEST);
                case NORTH_EAST -> new Pair<>(Direction.NORTH, Direction.EAST);
                default -> null;
            };

            if (cornerAdjacent == null) {
                return;
            }

            final Direction facing = switch (shape) {
                case SOUTH_EAST -> Direction.NORTH;
                case SOUTH_WEST -> Direction.EAST;
                case NORTH_WEST -> Direction.SOUTH;
                case NORTH_EAST -> Direction.WEST;
                default -> throw new IllegalStateException("Unexpected value: " + shape);
            };

            var adjacent1 = pos.relative(cornerAdjacent.getFirst());
            var adjacent2 = pos.relative(cornerAdjacent.getSecond());

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

            if (event.getLevel().isClientSide()) {
                final var rand = level.getRandom();
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), ModSoundEvents.CURVE_FORM_TINK.get(),
                        SoundSource.BLOCKS,
                        0.3F + rand.nextFloat() * 0.1F, 0.9F + rand.nextFloat() * 0.15F, false);
            } else {
                SMALL_CURVE_RAIL.get().placeCurve(facing, level, pos);
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

        var railBlockType = SMALL_CURVE_RAIL.get();
        if (state.is(railBlockType)) {
            var curveBlock = (SmallCurveRail) block;
            var entry = curveBlock.getEntryPos(state, pos);
            if (level.getBlockState(entry).is(railBlockType)) {
                level.destroyBlock(entry, false);
            }
            var middle = curveBlock.getMiddlePos(state, pos);
            if (level.getBlockState(middle).is(railBlockType)) {
                level.destroyBlock(middle, false);
            }
            var exit = curveBlock.getExitPos(state, pos);
            if (level.getBlockState(exit).is(railBlockType)) {
                level.destroyBlock(exit, false);
            }
            var middleLow = curveBlock.getMiddleLowPos(state, pos);
            if (level.getBlockState(middleLow).is(railBlockType)) {
                level.destroyBlock(middleLow, false);
            }

        }
    }
}
