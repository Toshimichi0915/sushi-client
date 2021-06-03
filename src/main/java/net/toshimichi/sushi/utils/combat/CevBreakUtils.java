package net.toshimichi.sushi.utils.combat;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.List;

public class CevBreakUtils {

    private static CevBreakAttack find(EntityPlayer player, EntityPlayer target, BlockPos pos) {
        BlockPos obsidianPos = pos.add(0, -1, 0);
        Block floorBlock = player.world.getBlockState(obsidianPos).getBlock();
        if (floorBlock != Blocks.OBSIDIAN && floorBlock != Blocks.AIR) return null;
        boolean obsidianPlaced = floorBlock == Blocks.OBSIDIAN;
        EntityEnderCrystal placed = null;
        Vec3d crystalPos = BlockUtils.toVec3d(pos).add(0.5, 0, 0.5);
        for (Entity crystal : player.world.loadedEntityList) {
            if (!(crystal instanceof EntityEnderCrystal)) continue;
            if (crystal.getPositionVector().squareDistanceTo(crystalPos) > 0.3) continue;
            placed = (EntityEnderCrystal) crystal;
        }
        if (placed == null) {
            AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
            if (player.world.collidesWithAnyBlock(box) || !player.world.checkNoEntityCollision(box)) return null;
        }
        return new CevBreakAttack(pos, obsidianPos, player, target, placed, placed != null, obsidianPlaced);
    }

    public static CevBreakAttack find(EntityPlayer player, EntityPlayer target) {
        BlockPos origin = BlockUtils.toBlockPos(target.getPositionVector());
        return find(player, target, new BlockPos(origin.getX(), origin.getY() + 3, origin.getZ()));
    }

    public static List<CevBreakAttack> find(EntityPlayer player) {
        ArrayList<CevBreakAttack> result = new ArrayList<>();
        for (Entity entity : player.world.loadedEntityList) {
            if (!(entity instanceof EntityPlayer)) continue;
            if (entity.getDistanceSq(player) > 12) continue;
            if (entity.getName().equals(player.getName())) continue;
            result.add(find(player, (EntityPlayer) entity));
        }
        return result;
    }
}
