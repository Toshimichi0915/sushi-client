package net.sushiclient.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public enum EntityType {
    PASSIVE, NEUTRAL, HOSTILE;

    public static boolean match(Entity entity, boolean player, boolean self, boolean mob, boolean passive, boolean neutral, boolean hostile) {
        if (!(entity instanceof EntityLivingBase)) return false;
        EntityPlayerSP entityPlayer = Minecraft.getMinecraft().player;
        if (entity instanceof EntityPlayer) {
            if (entity.getName().equals(entityPlayer == null ? "" : entityPlayer.getName())) return self;
            else return player;
        }
        if (!mob) return false;
        EntityType state = EntityUtils.getEntityType((EntityLivingBase) entity);
        switch (state) {
            case PASSIVE:
                return passive;
            case NEUTRAL:
                return neutral;
            case HOSTILE:
                return hostile;
            default:
                throw new UnsupportedOperationException();
        }
    }

}
