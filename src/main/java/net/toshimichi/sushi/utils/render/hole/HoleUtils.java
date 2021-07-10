package net.toshimichi.sushi.utils.render.hole;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.util.function.Consumer;

public class HoleUtils {

    private static HoleInfo getSingleHole(World world, BlockPos origin) {
        Block originBlock = world.getBlockState(origin).getBlock();
        if (originBlock != Blocks.OBSIDIAN && originBlock != Blocks.BEDROCK) return null;
        origin = origin.add(0, 1, 0);
        if (!BlockUtils.isAir(world, origin) || !BlockUtils.isAir(world, origin.add(0, 1, 0))) return null;
        boolean isObsidian = false;
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) continue;
            Block block = world.getBlockState(origin.offset(facing)).getBlock();
            if (block == Blocks.OBSIDIAN) {
                isObsidian = true;
            } else if (block != Blocks.BEDROCK) {
                for (EnumFacing facing1 : EnumFacing.values()) {
                    if (facing1.getOpposite() == facing) continue;
                    Block block1 = world.getBlockState(origin.offset(facing).offset(facing1)).getBlock();
                    if (block1 != Blocks.BEDROCK) return null;
                }
            }
        }
        return new HoleInfo(new BlockPos[]{origin}, isObsidian ? HoleType.UNSAFE : HoleType.SAFE);
    }

    private static HoleInfo getDoubleHole(World world, BlockPos origin) {
        Block originBlock = world.getBlockState(origin).getBlock();
        if (originBlock != Blocks.OBSIDIAN && originBlock != Blocks.BEDROCK) return null;
        origin = origin.add(0, 1, 0);
        if (!BlockUtils.isAir(world, origin) || !BlockUtils.isAir(world, origin.add(0, 1, 0))) return null;
        root:
        for (EnumFacing facing : new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.EAST}) {
            boolean isObsidian = false;
            if (world.getBlockState(origin.offset(facing)).getBlock() != Blocks.AIR) continue;
            for (EnumFacing i : EnumFacing.values()) {
                if (i == facing || i == EnumFacing.UP) continue;
                Block b = world.getBlockState(origin.offset(i)).getBlock();
                Block b1;
                if (i == EnumFacing.DOWN) b1 = world.getBlockState(origin.offset(facing).offset(i)).getBlock();
                else b1 = world.getBlockState(origin.offset(facing).offset(i.getOpposite())).getBlock();
                for (Block block : new Block[]{b, b1}) {
                    if (block == Blocks.OBSIDIAN) isObsidian = true;
                    else if (block != Blocks.BEDROCK) continue root;
                }
            }
            return new HoleInfo(new BlockPos[]{origin, origin.offset(facing)}, isObsidian ? HoleType.UNSAFE_DOUBLE : HoleType.SAFE_DOUBLE);
        }
        return null;
    }

    private static HoleInfo getHoleInfo(World world, BlockPos origin) {
        HoleInfo info = getSingleHole(world, origin);
        if (info == null) info = getDoubleHole(world, origin);
        return info;
    }

    public static void findHoles(World world, BlockPos from, BlockPos to, Consumer<HoleInfo> onFound) {
        for (int x = from.getX(); x <= to.getX(); x++) {
            for (int y = from.getY(); y <= to.getY(); y++) {
                for (int z = from.getZ(); z <= to.getZ(); z++) {
                    BlockPos origin = new BlockPos(x, y, z);
                    HoleInfo info = getHoleInfo(world, origin);
                    if (info != null) onFound.accept(info);
                }
            }
        }
    }
}
