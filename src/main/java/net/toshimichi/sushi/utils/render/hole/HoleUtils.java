package net.toshimichi.sushi.utils.render.hole;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.util.function.Consumer;

public class HoleUtils {

    private static HoleInfo getSingleHole(World world, BlockPos origin) {
        if (!BlockUtils.isAir(world, origin) || !BlockUtils.isAir(world, origin.add(0, 1, 0))) return null;
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
        root:
        for (EnumFacing facing : new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.EAST}) {
            boolean isObsidian = false;
            Block b = world.getBlockState(origin.offset(facing)).getBlock();
            if (b != Blocks.AIR) continue;
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
}
