package com.froobert.curvyrails.blocks;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class CurveRailProperties {
    public static final EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);
}
