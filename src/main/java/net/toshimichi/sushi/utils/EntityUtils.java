package net.toshimichi.sushi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;

public class EntityUtils {

    private static boolean isAggressive(EntityLivingBase entity) {
        if (entity instanceof EntityPigZombie) {
            return ((EntityPigZombie) entity).isArmsRaised() || ((EntityPigZombie) entity).isAngry();
        } else if (entity instanceof EntityWolf) {
            return ((EntityWolf) entity).isAngry() && Minecraft.getMinecraft().player != ((EntityWolf) entity).getOwner();
        } else if (entity instanceof EntityEnderman) {
            return ((EntityEnderman) entity).isScreaming();
        } else if (entity instanceof EntityIronGolem) {
            return entity.getRevengeTarget() != null;
        } else {
            return entity.isCreatureType(EnumCreatureType.MONSTER, false);
        }
    }

    public static EntityState getEntityType(EntityLivingBase entity) {
        if (entity instanceof EntityAgeable || entity instanceof EntityAmbientCreature || entity instanceof EntitySquid) {
            return EntityState.PASSIVE;
        } else if (isAggressive(entity)) {
            return EntityState.HOSTILE;
        } else {
            return EntityState.NEUTRAL;
        }
    }
}