package net.toshimichi.sushi.utils.combat;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PistonAuraUtils {

    public static BlockState getBlockState(EntityPlayer player, BlockPos pos, Block... blocks) {
        Block bedrock = Block.getBlockById(7);
        Block obsidian = Block.getBlockById(49);
        Block block = player.world.getBlockState(pos).getBlock();
        if (Arrays.asList(blocks).contains(block)) return BlockState.PLACED;
        else if (BlockUtils.isAir(player.world, pos)) return BlockState.AIR;
        else if (block == bedrock || block == obsidian) return BlockState.UNREACHABLE;
        else return BlockState.BLOCKED;
    }

    private static List<PistonAuraAttack> find(EntityPlayer player, EntityPlayer target, BlockPos pos) {
        Block bedrock = Block.getBlockById(7);
        Block obsidian = Block.getBlockById(49);
        Block piston = Block.getBlockById(33);
        Block pistonHead = Block.getBlockById(34);
        Block pistonHead2 = Block.getBlockById(36);
        Block stickyPiston = Block.getBlockById(29);
        Block redstone = Block.getBlockById(152);

        Block block = player.world.getBlockState(pos).getBlock();
        pos = pos.add(0, 1, 0);

        EntityEnderCrystal placed = null;
        for (Entity entity : player.world.loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal)) continue;
            if (entity.getPositionVector().squareDistanceTo(BlockUtils.toVec3d(pos).add(0.5, 0, 0.5)) > 0.3) continue;
            placed = (EntityEnderCrystal) entity;
        }

        AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
        if (placed == null && block != bedrock && block != obsidian) return null;
        if (placed == null && (!player.world.collidesWithAnyBlock(box) || !player.world.checkNoEntityCollision(box))) return null;

        double originalDamage = DamageUtils.getCrystalDamage(target, BlockUtils.toVec3d(pos).add(0.5, 0, 0.5));
        ArrayList<PistonAuraAttack> result = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) continue;
            Vec3d crystalPos = BlockUtils.toVec3d(pos).add(0.5, 0, 0.5).add(new Vec3d(facing.getDirectionVec()).scale(0.5));
            double rawDamage = DamageUtils.getCrystalDamage(target, crystalPos);
            double damage = DamageUtils.applyModifier(target, rawDamage, DamageUtils.EXPLOSION);
            if (rawDamage < 50) continue;
            if (rawDamage - originalDamage < 30) continue;

            EnumFacing opposite = facing.getOpposite();
            BlockPos airPos = pos.offset(opposite);
            BlockPos pistonPos = airPos.offset(opposite);
            BlockState state1 = getBlockState(player, airPos, pistonHead, pistonHead2);
            BlockState state2 = getBlockState(player, pistonPos, piston, stickyPiston);
            BlockState state3 = BlockState.UNREACHABLE;
            for (EnumFacing facing1 : EnumFacing.values()) {
                if (facing1 == facing.getOpposite()) continue;
                BlockPos redstonePos = pistonPos.offset(facing1);
                BlockState blockState = getBlockState(player, redstonePos, redstone);
                if (state3 == BlockState.UNREACHABLE) {
                    if (blockState != BlockState.UNREACHABLE) state3 = blockState;
                } else if (state3 == BlockState.BLOCKED) {
                    if (blockState != BlockState.BLOCKED) state3 = blockState;
                } else if (state3 == BlockState.AIR) {
                    if (blockState != BlockState.AIR) state3 = blockState;
                }
            }

            if (state1 == BlockState.UNREACHABLE || state2 == BlockState.UNREACHABLE || state3 == BlockState.UNREACHABLE) {
                continue;
            }
            if (state2 != BlockState.PLACED && BlockUtils.findBlockPlaceInfo(player.world, pistonPos) == null) continue;
            boolean blocked = state1 == BlockState.BLOCKED || state2 == BlockState.BLOCKED || state3 == BlockState.BLOCKED;

            result.add(new PistonAuraAttack(pos, pistonPos, facing, player, target, damage, placed, blocked, placed != null,
                    state2 == BlockState.PLACED, state3 == BlockState.PLACED, state1 == BlockState.PLACED));
        }
        return result;
    }

    public static List<PistonAuraAttack> find(EntityPlayer player, EntityPlayer target) {
        BlockPos origin = BlockUtils.toBlockPos(target.getPositionVector());
        ArrayList<PistonAuraAttack> list = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -5; y <= 4; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos pos = origin.add(x, y, z);
                    List<PistonAuraAttack> attacks = find(player, target, pos);
                    if (attacks != null) list.addAll(attacks);
                }
            }
        }
        return list;
    }

    public static List<PistonAuraAttack> find(EntityPlayer player) {
        ArrayList<PistonAuraAttack> result = new ArrayList<>();
        for (Entity entity : player.world.loadedEntityList) {
            if (!(entity instanceof EntityPlayer)) continue;
            if (entity.getDistanceSq(player) > 12) continue;
            if (entity.getName().equals(player.getName())) continue;
            result.addAll(find(player, (EntityPlayer) entity));
        }
        return result;
    }

    private enum BlockState {
        PLACED, UNREACHABLE, BLOCKED, AIR
    }
}
