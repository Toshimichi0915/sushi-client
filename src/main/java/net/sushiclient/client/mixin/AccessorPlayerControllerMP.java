package net.sushiclient.client.mixin;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerControllerMP.class)
public interface AccessorPlayerControllerMP {

    @Accessor("blockHitDelay")
    int getBlockHitDelay();

    @Accessor("blockHitDelay")
    void setBlockHitDelay(int blockHitDelay);

    @Accessor("curBlockDamageMP")
    void setCurBlockDamageMP(float curBlockDamageMP);

    @Accessor("curBlockDamageMP")
    float getCurBlockDamageMP();
}
