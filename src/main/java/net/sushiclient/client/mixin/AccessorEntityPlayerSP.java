package net.sushiclient.client.mixin;

import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityPlayerSP.class)
public interface AccessorEntityPlayerSP {

    @Invoker("updateAutoJump")
    void invokeUpdateAutoJump(float a, float b);

    @Accessor("positionUpdateTicks")
    int getPositionUpdateTicks();

    @Accessor("positionUpdateTicks")
    void setPositionUpdateTicks(int positionUpdateTicks);
}
