package net.sushiclient.client.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PhaseWalkPlayer extends EntityOtherPlayerMP {

    private static final int KEEP_Y = 7;
    private final EntityPlayerSP original;

    public PhaseWalkPlayer(World worldIn) {
        super(worldIn, Minecraft.getMinecraft().getSession().getProfile());
        original = Minecraft.getMinecraft().player;
        if (original == null) throw new IllegalStateException();
        capabilities.allowFlying = true;
        capabilities.isFlying = true;
    }

    @Override
    public void onLivingUpdate() {
        setHealth(original.getHealth());
        setAbsorptionAmount(original.getAbsorptionAmount());
        inventory.copyInventory(original.inventory);
        updateEntityActionState();
        posX = original.posX;
        if (original.posY < KEEP_Y - 2) posY = KEEP_Y;
        else posY = original.posY + 2;
        posZ = original.posZ;
        rotationYaw = original.rotationYaw;
        rotationPitch = original.rotationPitch;
    }

    @Override
    public boolean isSpectator() {
        return true;
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public boolean isInvisibleToPlayer(EntityPlayer player) {
        return true;
    }
}
