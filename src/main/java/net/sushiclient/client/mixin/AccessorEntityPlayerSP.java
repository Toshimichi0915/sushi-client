package net.sushiclient.client.mixin;

import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPlayerSP.class)
public interface AccessorEntityPlayerSP {
    @Accessor("positionUpdateTicks")
    int getPositionUpdateTicks();

    @Accessor("positionUpdateTicks")
    void setPositionUpdateTicks(int positionUpdateTicks);
}
