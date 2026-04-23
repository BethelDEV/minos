package funs.gamez.model;

import java.util.Arrays;

public enum Direction {

    NORTH,
    EAST,
    SOUTH,
    WEST;

    public static final int ALL = 15;

    private int bit;

    public static int toBits(Direction... directions) {
        int directionBits = 0;
        for (Direction direction : directions) {
            directionBits |= direction.bit();
        }
        return directionBits;
    }

    private Direction() {
        this.bit = 1 << ordinal();
    }

    public int bit() {
        return bit;
    }

    public Direction getOpposite() {
        return Direction.values()[(Arrays.asList(Direction.values()).indexOf(this) + Direction.values().length / 2) % Direction.values().length];
    }

}
