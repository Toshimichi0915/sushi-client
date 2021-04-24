package net.toshimichi.sushi.events.client;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventTiming;

public class UpdateLightEvent extends CancellableEvent {
    private final EnumSkyBlock enumSkyBlock;
    private final BlockPos pos;

    public UpdateLightEvent(EventTiming timing, EnumSkyBlock enumSkyBlock, BlockPos pos) {
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
