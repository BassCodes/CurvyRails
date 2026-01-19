package com.froobert.curvyrails;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

public final class Util {

    public static class Rail {
        public static boolean isCorner(RailShape shape) {
            return switch (shape) {
                case SOUTH_EAST, SOUTH_WEST, NORTH_EAST, NORTH_WEST -> true;
                default -> false;
            };
        }
        public static boolean isStraight(RailShape shape) {
            return switch (shape) {
                case NORTH_SOUTH, EAST_WEST -> true;
                default -> false;
            };
        }

        public static RailShape shapeFromDirection(Direction dir) {
            return switch (dir) {
                case NORTH,SOUTH -> RailShape.NORTH_SOUTH;
                case WEST,EAST -> RailShape.EAST_WEST;
                default -> throw new IllegalStateException("Unexpected value: " + dir);
            };
        }

        public static RailShape cornerFromDirectionClockwise(Direction dir) {
            return switch (dir) {
                case NORTH -> RailShape.NORTH_EAST;
                case SOUTH -> RailShape.SOUTH_WEST;
                case WEST -> RailShape.NORTH_WEST;
                case EAST -> RailShape.SOUTH_EAST;
                default -> throw new IllegalStateException("Unexpected value: " + dir);
            };
        }

        public static RailShape rotateShapeClockwise(RailShape shape) {
            return switch (shape) {
                case NORTH_SOUTH -> RailShape.EAST_WEST;
                case EAST_WEST -> RailShape.NORTH_SOUTH;
                case ASCENDING_EAST -> RailShape.ASCENDING_SOUTH;
                case ASCENDING_WEST -> RailShape.ASCENDING_NORTH;
                case ASCENDING_NORTH -> RailShape.ASCENDING_EAST;
                case ASCENDING_SOUTH -> RailShape.ASCENDING_WEST;
                case SOUTH_EAST -> RailShape.SOUTH_WEST;
                case SOUTH_WEST -> RailShape.NORTH_WEST;
                case NORTH_WEST -> RailShape.NORTH_EAST;
                case NORTH_EAST -> RailShape.SOUTH_EAST;
            };
        }
    }


    public static final class Math {
        public static Vec3 vec3FromI(Vec3i v) {
            return new Vec3(v.getX(),v.getY(),v.getZ());
        }
        public static Vec3 vec3FromICentered(Vec3i v) {
            return new Vec3(v.getX()+0.5,v.getY()+0.5,v.getZ()+0.5);
        }
        public static Vec3i vec3iFromF(Vec3 v) {
            return new Vec3i(Mth.floor(v.x()), Mth.floor(v.y()), Mth.floor(v.z()));
        }
        public static BlockPos blockPosFromVec(Vec3 v) { return new BlockPos(Mth.floor(v.x()), Mth.floor(v.y()), Mth.floor(v.z())); }
    }

}
