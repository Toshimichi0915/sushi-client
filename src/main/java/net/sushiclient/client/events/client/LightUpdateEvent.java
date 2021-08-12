package net.sushiclient.client.events.client;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.sushiclient.client.events.CancellableEvent;
import net.sushiclient.client.events.EventTiming;

public class LightUpdateEvent extends CancellableEvent {
    private final EnumSkyBlock enumSkyBlock;
    private final BlockPos pos;

    public LightUpdateEvent(EventTiming timing, EnumSkyBlock enumSkyBlock, BlockPos pos) {
        super(timing);
        this.enumSkyBlock = enumSkyBlock;
        this.pos = pos;
    }

    public EnumSkyBlock getEnumSkyBlock() {
        return enumSkyBlock;
    }

    public BlockPos getPos() {
        return pos;
    }
}
