package net.toshimichi.sushi.utils.combat;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DamageUtils {

    public static final DamageSource EXPLOSION = new DamageSource("player.explosion").setDifficultyScaled().setExplosion();

    public static double applyModifier(EntityLivingBase entity, double damage, DamageSource source) {
        if (source.isUnblockable()) return damage;
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative() && !source.canHarmInCreative()) {
            return 0;
        }

        // armor
        // https://minecraft.fandom.com/wiki/Armor#Defense_points
        double defense = entity.getTotalArmorValue();
        double toughness = entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
        damage *= 1 - MathHelper.clamp(defense - 4 * damage / (toughness + 8), defense / 5, 20) / 25;

        // potion
        PotionEffect resistance = entity.getActivePotionEffect(MobEffects.RESISTANCE);
        if (resistance != null) {
            damage *= Math.max(1.0 - (resistance.getAmplifier() + 1) * 0.2, 0);
        }

        // enchants
        int modifier = 0;
        for (ItemStack armor : entity.getArmorInventoryList()) {
            if (armor.isEmpty()) continue;
            for (NBTBase element : armor.getEnchantmentTagList()) {
                NBTTagCompound enchantTag = (NBTTagCompound) element;
                int id = enchantTag.getInteger("id");
                int level = enchantTag.getInteger("lvl");
                Enchantment enchant = Enchantment.getEnchantmentByID(id);
                if (enchant != null) modifier += enchant.calcModifierDamage(level, source);
            }
        }
        damage *= (1 - Math.min(modifier, 20) / 25D);

        // difficulty
        if (source.isDifficultyScaled()) {
            damage *= entity.world.getDifficulty().getId() * 0.5;
        }

        return Math.max(0, damage);
    }

    public static double getExplosionDamage(EntityLivingBase entity, Vec3d offset, Vec3d crystal, double power) {
        Vec3d pos = entity.getPositionVector().add(offset);
        double distance = pos.distanceTo(crystal);
        double radius = 2 * power;
        if (distance > radius) return 0;
        double impact = (1 - (distance / radius)) * entity.world.getBlockDensity(crystal, entity.getEntityBoundingBox().offset(offset));
        return (impact * impact + impact) * 7 * power + 1;
    }

    public static double getExplosionDamage(EntityLivingBase entity, Vec3d crystal, double power) {
        return getExplosionDamage(entity, new Vec3d(0, 0, 0), crystal, power);
    }

    public static double getCrystalDamage(EntityLivingBase entity, Vec3d crystal) {
        return getExplosionDamage(entity, crystal, 6);
    }
}
