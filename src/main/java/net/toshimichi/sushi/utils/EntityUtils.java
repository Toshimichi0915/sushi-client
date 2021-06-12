package net.toshimichi.sushi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntityUtils {

    @SuppressWarnings("unchecked")
    public static <T extends Entity> List<EntityInfo<T>> getNearbyEntities(Vec3d origin, Class<T> entityClass) {
        ArrayList<EntityInfo<T>> result = new ArrayList<>();
        for (Entity entity : Minecraft.getMinecraft().world.loadedEntityList) {
            if (!entity.getClass().equals(entityClass)) continue;
            result.add(new EntityInfo<>((T) entity, origin.squareDistanceTo(entity.getPositionVector())));
        }
        result.sort(null);
        return result;
    }

    public static List<EntityPlayer> getNearbyPlayers(double distance) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        World world = Minecraft.getMinecraft().world;
        if (world == null) return new ArrayList<>();
        ArrayList<EntityPlayer> result = new ArrayList<>();
        for (Entity entity : world.loadedEntityList) {
            if (!(entity instanceof EntityPlayer)) continue;
            if (entity.getDistanceSq(player) > distance * distance) continue;
            if (entity.getName().equals(player.getName())) continue;
            result.add((EntityPlayer) entity);
        }
        return result;
    }

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