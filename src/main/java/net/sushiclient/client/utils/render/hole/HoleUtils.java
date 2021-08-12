package net.sushiclient.client.utils.render.hole;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.sushiclient.client.modules.combat.HoleMineInfo;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.world.BlockUtils;

import java.util.function.Consumer;

public class HoleUtils {

    private static HoleInfo getSingleHole(World world, BlockPos origin) {
        if (!BlockUtils.isAir(world, origin) ||
                !BlockUtils.isAir(world, origin.add(0, 1, 0)) ||
                !BlockUtils.isAir(world, origin.add(0, 2, 0))) return null;
        boolean isObsidian = false;
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == EnumFacing.UP) continue;
            Block block = world.getBlockState(origin.offset(facing)).getBlock();
            if (block == Blocks.OBSIDIAN) isObsidian = true;
            else if (block != Blocks.BEDROCK) return null;
        }
        AxisAlignedBB box = world.getBlockState(origin).getBoundingBox(world, origin).offset(origin);
        return new HoleInfo(new BlockPos[]{origin}, box, isObsidian ? HoleType.UNSAFE : HoleType.SAFE);
    }

    private static HoleInfo getDoubleHole(World world, BlockPos origin) {
        if (!BlockUtils.isAir(world, origin) || !BlockUtils.isAir(world, origin.add(0, 1, 0))) return null;
        boolean canAccess = BlockUtils.isAir(world, origin.add(0, 2, 0));
        root:
        for (EnumFacing facing : new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.EAST}) {
            if (!BlockUtils.isAir(world, origin.offset(facing)) ||
                    !BlockUtils.isAir(world, origin.offset(facing).add(0, 1, 0))) continue;
            if (!canAccess && !BlockUtils.isAir(world, origin.offset(facing).add(0, 2, 0))) continue;
            boolean isObsidian = false;
            for (EnumFacing i : EnumFacing.values()) {
                if (i == facing || i == EnumFacing.UP) continue;
                Block b1 = world.getBlockState(origin.offset(i)).getBlock();
                Block b2;
                if (i == EnumFacing.DOWN) b2 = world.getBlockState(origin.offset(facing).offset(i)).getBlock();
                else b2 = world.getBlockState(origin.offset(facing).offset(i.getOpposite())).getBlock();
                for (Block block : new Block[]{b1, b2}) {
                    if (block == Blocks.OBSIDIAN) isObsidian = true;
                    else if (block != Blocks.BEDROCK) continue root;
                }
            }
            AxisAlignedBB box = world.getBlockState(origin).getBoundingBox(world, origin).offset(origin);
            Vec3i offset = facing.getDirectionVec();
            box = box.expand(offset.getX(), offset.getY(), offset.getZ());
            return new HoleInfo(new BlockPos[]{origin, origin.offset(facing)}, box, isObsidian ? HoleType.UNSAFE_DOUBLE : HoleType.SAFE_DOUBLE);
        }
        return null;
    }

    public static HoleInfo getHoleInfo(World world, BlockPos origin, boolean doubleHole) {
        HoleInfo info = getSingleHole(world, origin);
        if (info == null && doubleHole) info = getDoubleHole(world, origin);
        return info;
    }

    public static void findHoles(World world, BlockPos from, BlockPos to, boolean doubleHole, Consumer<HoleInfo> onFound) {
        for (int x = from.getX(); x <= to.getX(); x++) {
            for (int y = from.getY(); y <= to.getY(); y++) {
                for (int z = from.getZ(); z <= to.getZ(); z++) {
                    BlockPos origin = new BlockPos(x, y, z);
                    HoleInfo info = getHoleInfo(world, origin, doubleHole);
                    if (info != null) onFound.accept(info);
                }
            }
        }
    }

    public static HoleMineInfo findNormal(EntityPlayer target, EnumFacing facing) {
        WorldClient world = Minecraft.getMinecraft().world;
        if (world == null) return null;
        BlockPos playerPos = BlockUtils.toBlockPos(target.getPositionVector());
        BlockPos surroundPos = playerPos.offset(facing);
        BlockPos aboveSurroundPos = surroundPos.offset(EnumFacing.UP);
        BlockPos crystalPos = surroundPos.offset(facing);
        if (world.getBlockState(surroundPos).getBlock() != Blocks.OBSIDIAN) return null;
        if (!BlockUtils.isAir(world, aboveSurroundPos) && !BlockUtils.isAir(world, crystalPos)) return null;

        return new HoleMineInfo(surroundPos, crystalPos, false);
    }

    public static HoleMineInfo findAntiSurround(EntityPlayer target, EnumFacing facing) {
        WorldClient world = Minecraft.getMinecraft().world;
        if (world == null) return null;
        BlockPos playerPos = BlockUtils.toBlockPos(target.getPositionVector());
        BlockPos surroundPos = playerPos.offset(facing);
        BlockPos crystalPos = surroundPos.offset(facing);
        BlockPos crystalFloor = crystalPos.offset(EnumFacing.DOWN);
        if (world.getBlockState(surroundPos).getBlock() != Blocks.OBSIDIAN) return null;
        if (world.getBlockState(crystalFloor).getBlock() != Blocks.OBSIDIAN) return null;
        AxisAlignedBB crystalBox = new AxisAlignedBB(crystalPos.getX(), crystalPos.getY(), crystalPos.getZ(),
                crystalPos.getX() + 1, crystalPos.getY() + 2, crystalPos.getZ() + 1);
        if (BlockUtils.isColliding(world, crystalBox)) return null;
        if (!EntityUtils.canInteract(BlockUtils.toVec3d(crystalPos).add(0.5, 1.7, 0.5), 6, 3)) return null;

        return new HoleMineInfo(surroundPos, crystalFloor, true);
    }
}
