package net.toshimichi.sushi.utils.combat;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.utils.EntityUtils;
import net.toshimichi.sushi.utils.world.BlockPlaceInfo;
import net.toshimichi.sushi.utils.world.BlockPlaceUtils;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class PistonAuraUtils {

    public static BlockState getBlockState(EntityPlayer player, BlockPos pos, Block... blocks) {
        Block block = player.world.getBlockState(pos).getBlock();
        if (Arrays.asList(blocks).contains(block)) return BlockState.PLACED;
        else if (BlockUtils.canPlace(player.world, new BlockPlaceInfo(pos, null))) return BlockState.AIR;
        else if (block == Blocks.BEDROCK || block == Blocks.OBSIDIAN) return BlockState.UNREACHABLE;
        else return BlockState.BLOCKED;
    }

    private static List<PistonAuraAttack> find(EntityPlayer player, EntityPlayer target, BlockPos obsidianPos, int distance) {

        Block block = player.world.getBlockState(obsidianPos).getBlock();
        BlockPos pos = obsidianPos.add(0, 1, 0);

        EntityEnderCrystal placed = null;
        for (Entity entity : player.world.loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal)) continue;
            if (entity.getPositionVector().squareDistanceTo(BlockUtils.toVec3d(pos).add(0.5, 0, 0.5)) > 0.3) continue;
            placed = (EntityEnderCrystal) entity;
        }

        AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
        List<BlockPlaceInfo> obsidianPlace = null;
        if (placed == null && block != Blocks.BEDROCK && block != Blocks.OBSIDIAN) {
            if (block != Blocks.AIR) return null;
            BlockPos targetPos = BlockUtils.toBlockPos(target.getPositionVector());
            obsidianPlace = BlockPlaceUtils.search(player.world, obsidianPos, distance, new HashSet<>(),
                    p -> p.getY() <= obsidianPos.getY() && (p.getX() != targetPos.getX() || p.getZ() != targetPos.getZ()));
            if (obsidianPlace == null) return null;
        }
        if (placed == null && BlockUtils.checkCollision(player.world, box))
            return null;

        double originalDamage = DamageUtils.getCrystalDamage(target, BlockUtils.toVec3d(pos).add(0.5, 0, 0.5));
        ArrayList<PistonAuraAttack> result = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) continue;
            Vec3d crystalPos = BlockUtils.toVec3d(pos).add(0.5, 0, 0.5).add(new Vec3d(facing.getDirectionVec()).scale(0.5));
            double rawDamage = DamageUtils.getCrystalDamage(target, crystalPos);
            double damage = DamageUtils.applyModifier(target, rawDamage, DamageUtils.EXPLOSION);
            if (rawDamage < 50) continue;
            if (obsidianPlace == null && rawDamage - originalDamage < 30) continue;

            EnumFacing opposite = facing.getOpposite();
            BlockPos airPos = pos.offset(opposite);
            BlockPos pistonPos = airPos.offset(opposite);
            double sin = (pistonPos.getY() - player.posY) /
                    player.getPositionVector().distanceTo(BlockUtils.toVec3d(pistonPos).add(0.5, 0, 0.5));
            if (Math.abs(sin) > 0.5D) return null;
            BlockState state1 = getBlockState(player, airPos, Blocks.PISTON_HEAD, Blocks.PISTON_EXTENSION);
            BlockState state2 = getBlockState(player, pistonPos, Blocks.PISTON, Blocks.STICKY_PISTON);
            BlockState state3 = BlockState.UNREACHABLE;
            for (EnumFacing facing1 : EnumFacing.values()) {
                if (facing1 == facing.getOpposite()) continue;
                BlockPos redstonePos = pistonPos.offset(facing1);
                BlockState blockState = getBlockState(player, redstonePos, Blocks.REDSTONE_BLOCK);
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
            boolean blocked = state1 == BlockState.BLOCKED || state2 == BlockState.BLOCKED || state3 == BlockState.BLOCKED;

            List<BlockPlaceInfo> pistonPlace = null;
            BlockPlaceInfo info = BlockUtils.findBlockPlaceInfo(player.world, pistonPos);
            if (state2 != BlockState.PLACED && info == null) {
                for (EnumFacing value : EnumFacing.values()) {
                    HashSet<BlockPos> closed = new HashSet<>();
                    closed.add(pistonPos);
                    closed.add(airPos);
                    closed.add(obsidianPos);
                    closed.add(pos);
                    closed.add(pos.add(0, 1, 0));
                    List<BlockPlaceInfo> candidate = BlockPlaceUtils.search(player.world, pistonPos.offset(value), distance, closed, p -> true);
                    if (candidate == null) continue;
                    if (pistonPlace == null || candidate.size() < pistonPlace.size()) pistonPlace = candidate;
                }
                if (pistonPlace == null) return null;
            }

            result.add(new PistonAuraAttack(pos, pistonPos, facing, player, target, damage, placed, blocked, placed != null,
                    state2 == BlockState.PLACED, state3 == BlockState.PLACED, state1 == BlockState.PLACED,
                    obsidianPlace, pistonPlace));
        }
        return result;
    }

    public static List<PistonAuraAttack> find(EntityPlayer player, EntityPlayer target, int distance) {
        BlockPos origin = BlockUtils.toBlockPos(target.getPositionVector());
        ArrayList<PistonAuraAttack> list = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos pos = origin.add(x, y, z);
                    List<PistonAuraAttack> attacks = find(player, target, pos, distance);
                    if (attacks != null) list.addAll(attacks);
                }
            }
        }
        return list;
    }

    public static List<PistonAuraAttack> find(EntityPlayer player, int distance) {
        ArrayList<PistonAuraAttack> result = new ArrayList<>();
        for (EntityPlayer entity : EntityUtils.getNearbyPlayers(3.5)) {
            result.addAll(find(player, entity, distance));
        }
        return result;
    }

    private enum BlockState {
        PLACED, UNREACHABLE, BLOCKED, AIR
    }
}
