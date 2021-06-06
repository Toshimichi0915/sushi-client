package net.toshimichi.sushi.utils.player;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.toshimichi.sushi.mixin.AccessorItemTool;

public class ItemUtils {

    public static float getAttackDamage(ItemStack itemStack) {
        Item item = itemStack.getItem();

        float damage = 1;
        if (item instanceof ItemSword) damage = ((ItemSword) item).getAttackDamage() + 4;
        else if (item instanceof ItemTool) damage = ((AccessorItemTool) item).getAttackDamage();

        // enchantment
        int level = getEnchantmentLevel(itemStack, Enchantments.SHARPNESS);
        if (level > 0) damage += level * 0.5F + 0.5F;
        return damage;
    }

    public static int getEnchantmentLevel(ItemStack itemStack, Enchantment enchantment) {
        if (enchantment == null) return 0;
        for (NBTBase enchantmentBase : itemStack.getEnchantmentTagList()) {
            if (!(enchantmentBase instanceof NBTTagCompound)) continue;
            NBTTagCompound compound = (NBTTagCompound) enchantmentBase;
            if (!enchantment.equals(Enchantment.getEnchantmentByID(compound.getInteger("id")))) continue;
            return compound.getInteger("lvl");
        }
        return 0;
    }
}
