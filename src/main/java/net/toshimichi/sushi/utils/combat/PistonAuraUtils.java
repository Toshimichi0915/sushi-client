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
import net.minecraft.util.math.Vec3i;
import net.toshimichi.sushi.utils.EntityUtils;
import net.toshimichi.sushi.utils.world.BlockPlaceInfo;
import net.toshimichi.sushi.utils.world.BlockPlaceUtils;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class PistonAuraUtils {

    public static PlaceState getPlaceState(EntityPlayer player, BlockPos pos, Block... blocks) {
        Block block = player.world.getBlockState(pos).getBlock();
        if (Arrays.asList(blocks).contains(block)) return PlaceState.PLACED;
        else if (BlockUtils.canPlace(player.world, new BlockPlaceInfo(pos, null))) return PlaceState.AIR;
        else if (block == Blocks.BEDROCK || block == Blocks.OBSIDIAN) return PlaceState.UNREACHABLE;
        else return PlaceState.BLOCKED;
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
        if (placed == null && BlockUtils.isColliding(player.world, box))
            return null;

        double originalDamage = DamageUtils.getCrystalDamage(target, BlockUtils.toVec3d(pos).add(0.5, 0, 0.5));
        ArrayList<PistonAuraAttack> result = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) continue;
            Vec3d crystalPos = BlockUtils.toVec3d(pos).add(0.5, 0, 0.5).add(new Vec3d(facing.getDirectionVec()).scale(0.5));
            if (!EntityUtils.canInteract(crystalPos.add(0, 1.7, 0), 6, 3)) continue;
            double rawDamage = DamageUtils.getCrystalDamage(target, crystalPos);
            double damage = DamageUtils.applyModifier(target, rawDamage, DamageUtils.EXPLOSION);
            if (rawDamage < 50) continue;
            if (obsidianPlace == null && rawDamage - originalDamage < 30) continue;

            EnumFacing opposite = facing.getOpposite();
            for (int i = 0; i < 6; i++) {
                EnumFacing horizontal = EnumFacing.byHorizontalIndex(opposite.getHorizontalIndex() + 1);
                Vec3i hVec = horizontal.getDirectionVec();
                BlockPos pos2 = pos.add(hVec.getX() * (i % 3 - 1), hVec.getY() * (i % 3 - 1) + i % 2, hVec.getZ() * (i % 3 - 1));

                BlockPos airPos = pos2.offset(opposite);
                BlockPos pistonPos = airPos.offset(opposite);
                double sin = (pistonPos.getY() - player.posY) /
                        player.getPositionVector().distanceTo(BlockUtils.toVec3d(pistonPos).add(0.5, 0.5, 0.5));
                if (Math.abs(sin) > 0.5D) continue;
                PlaceState state1 = getPlaceState(player, airPos, Blocks.PISTON_HEAD, Blocks.PISTON_EXTENSION);
                PlaceState state2 = getPlaceState(player, pistonPos, Blocks.PISTON, Blocks.STICKY_PISTON);
                PlaceState state3 = PlaceState.UNREACHABLE;
                for (EnumFacing facing1 : EnumFacing.values()) {
                    if (facing1 == facing.getOpposite()) continue;
                    BlockPos redstonePos = pistonPos.offset(facing1);
                    PlaceState placeState = getPlaceState(player, redstonePos, Blocks.REDSTONE_BLOCK);
                    if (state3 == PlaceState.UNREACHABLE) {
                        if (placeState != PlaceState.UNREACHABLE) state3 = placeState;
                    } else if (state3 == PlaceState.BLOCKED) {
                        if (placeState != PlaceState.BLOCKED) state3 = placeState;
                    } else if (state3 == PlaceState.AIR) {
                        if (placeState != PlaceState.AIR) state3 = placeState;
                    }
                }

                if (state1 == PlaceState.UNREACHABLE || state2 == PlaceState.UNREACHABLE || state3 == PlaceState.UNREACHABLE) {
                    continue;
                }
                boolean blocked = state1 == PlaceState.BLOCKED || state2 == PlaceState.BLOCKED || state3 == PlaceState.BLOCKED;

                List<BlockPlaceInfo> pistonPlace = null;
                BlockPlaceInfo info = BlockUtils.findBlockPlaceInfo(player.world, pistonPos);
                if (state2 != PlaceState.PLACED && info == null) {
                    for (EnumFacing value : EnumFacing.values()) {
                        HashSet<BlockPos> closed = new HashSet<>();
                        closed.add(pistonPos);
                        closed.add(airPos);
                        closed.add(obsidianPos);
                        closed.add(pos2);
                        closed.add(pos2.add(0, 1, 0));
                        List<BlockPlaceInfo> candidate = BlockPlaceUtils.search(player.world, pistonPos.offset(value), distance, closed, p -> true);
                        if (candidate == null) continue;
                        if (pistonPlace == null || candidate.size() < pistonPlace.size()) pistonPlace = candidate;
                    }
                    if (pistonPlace == null) continue;
                }

                result.add(new PistonAuraAttack(pos, pistonPos, facing, player, target, damage, placed, blocked, placed != null,
                        state2 == PlaceState.PLACED, state3 == PlaceState.PLACED, state1 == PlaceState.PLACED,
                        obsidianPlace, pistonPlace));
            }

        }
        return result;
    }

    public static List<PistonAuraAttack> find(EntityPlayer player, EntityPlayer target, int distance) {
        BlockPos origin = BlockUtils.toBlockPos(target.getPositionVector());
        ArrayList<PistonAuraAttack> list = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 3; y++) {
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
        for (EntityPlayer entity : EntityUtils.getNearbyPlayers(6)) {
            result.addAll(find(player, entity, distance));
        }
        return result;
    }

    private enum PlaceState {
        PLACED, UNREACHABLE, BLOCKED, AIR
    }
}
