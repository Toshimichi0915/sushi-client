package net.toshimichi.sushi.mixin;

import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Timer.class)
public interface AccessorTimer {
    @Accessor
    float getTickLength();

    @Accessor("tickLength")
    void setTickLength(float length);
}