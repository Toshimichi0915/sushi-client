package net.toshimichi.sushi.utils.combat;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.utils.EntityUtils;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.List;

public class CevBreakUtils {

    private static CevBreakAttack find(EntityPlayer player, EntityPlayer target, BlockPos pos, BlockPos breakingBlock) {
        BlockPos obsidianPos = pos.add(0, -1, 0);
        IBlockState floorState = player.world.getBlockState(obsidianPos);
        Block floorBlock = floorState.getBlock();
        if (floorBlock != Blocks.OBSIDIAN && floorBlock != Blocks.AIR) return null;
        boolean obsidianPlaced = floorBlock == Blocks.OBSIDIAN;
        EntityEnderCrystal placed = null;
        Vec3d crystalPos = BlockUtils.toVec3d(pos).add(0.5, 0, 0.5);
        player.world.setBlockState(obsidianPos, Blocks.AIR.getDefaultState());
        double damage = DamageUtils.getCrystalDamage(target, crystalPos);
        double selfDamage = DamageUtils.getCrystalDamage(player, crystalPos);
        player.world.setBlockState(obsidianPos, floorState);
        if (damage < 40) return null;
        if (selfDamage > 20) return null;
        for (Entity crystal : player.world.loadedEntityList) {
            if (!(crystal instanceof EntityEnderCrystal)) continue;
            if (crystal.getPositionVector().squareDistanceTo(crystalPos) > 0.3) continue;
            placed = (EntityEnderCrystal) crystal;
        }
        if (placed == null) {
            AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
            if (BlockUtils.isColliding(player.world, box)) return null;
        }
        return new CevBreakAttack(pos, obsidianPos, player, target, placed, breakingBlock, damage, placed != null, obsidianPlaced);
    }

    public static List<CevBreakAttack> find(EntityPlayer player, EntityPlayer target, BlockPos breakingBlock) {
        BlockPos origin = BlockUtils.toBlockPos(target.getPositionVector());
        ArrayList<CevBreakAttack> result = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = 2; y <= 4; y++) {
                for (int z = -1; z <= 1; z++) {
                    CevBreakAttack attack = find(player, target, new BlockPos(origin.getX() + x, origin.getY() + y, origin.getZ() + z), breakingBlock);
                    if (attack != null) result.add(attack);
                }
            }
        }
        return result;
    }

    public static List<CevBreakAttack> find(EntityPlayer player, BlockPos breakingBlock) {
        ArrayList<CevBreakAttack> result = new ArrayList<>();
        for (EntityPlayer entity : EntityUtils.getNearbyPlayers(4)) {
            result.addAll(find(player, entity, breakingBlock));
        }
        return result;
    }
}
