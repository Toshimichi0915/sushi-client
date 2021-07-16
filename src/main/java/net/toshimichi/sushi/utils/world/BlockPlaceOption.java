package net.toshimichi.sushi.utils.world;

public class BlockPlaceOption {
    private final boolean entityCollisionIgnored;
    private final boolean airPlaceIgnored;

    public BlockPlaceOption() {
        this(false, false);
    }

    public BlockPlaceOption(boolean entityCollisionIgnored, boolean airPlaceIgnored) {
        this.entityCollisionIgnored = entityCollisionIgnored;
        this.airPlaceIgnored = airPlaceIgnored;
    }

    public boolean isEntityCollisionIgnored() {
        return entityCollisionIgnored;
    }

    public boolean isAirPlaceIgnored() {
        return airPlaceIgnored;
    }
}
