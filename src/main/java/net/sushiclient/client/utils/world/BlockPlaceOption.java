package net.sushiclient.client.utils.world;

import java.util.Arrays;

public class BlockPlaceOption {
    private final boolean entityCollisionIgnored;
    private final boolean blockCollisionIgnored;
    private final boolean airPlaceIgnored;

    private static boolean contains(PlaceOptions[] options, PlaceOptions any) {
        return Arrays.asList(options).contains(any);
    }

    public BlockPlaceOption(PlaceOptions... options) {
        this(contains(options, PlaceOptions.IGNORE_ENTITY),
                contains(options, PlaceOptions.IGNORE_BLOCK),
                contains(options, PlaceOptions.IGNORE_AIR));
    }

    public BlockPlaceOption(boolean entityCollisionIgnored, boolean blockCollisionIgnored, boolean airPlaceIgnored) {
        this.entityCollisionIgnored = entityCollisionIgnored;
        this.blockCollisionIgnored = blockCollisionIgnored;
        this.airPlaceIgnored = airPlaceIgnored;
    }

    public boolean isEntityCollisionIgnored() {
        return entityCollisionIgnored;
    }

    public boolean isBlockCollisionIgnored() {
        return blockCollisionIgnored;
    }

    public boolean isAirPlaceIgnored() {
        return airPlaceIgnored;
    }
}
