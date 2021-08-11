package net.toshimichi.sushi.utils.player;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
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

    public static float getDestroySpeed(IBlockState blockState, ItemStack itemStack) {
        float destroySpeed = itemStack.getDestroySpeed(blockState);
        if (destroySpeed > 1) {
            int level = ItemUtils.getEnchantmentLevel(itemStack, Enchantments.EFFICIENCY);
            destroySpeed += level * level + 1;
        }
        return destroySpeed;
    }

    public static boolean canToolHarvestBlock(IBlockAccess world, BlockPos pos, ItemStack stack) {
        IBlockState state = world.getBlockState(pos);
        state = state.getBlock().getActualState(state, world, pos);
        String tool = state.getBlock().getHarvestTool(state);
        if (tool == null) return true;
        if (stack.isEmpty()) return false;
        return stack.getItem().getHarvestLevel(stack, tool, null, null) >= state.getBlock().getHarvestLevel(state);
    }

    public static int getDestroyTime(BlockPos blockPos, ItemStack itemStack) {
        WorldClient world = Minecraft.getMinecraft().world;
        IBlockState blockState = world.getBlockState(blockPos);
        float hardness = blockState.getBlockHardness(world, blockPos);
        float speed = getDestroySpeed(blockState, itemStack);
        if (canToolHarvestBlock(world, blockPos, itemStack)) {
            return (int) Math.ceil(hardness * 30 / speed);
        } else {
            return (int) Math.ceil(hardness * 100);
        }
    }
}
