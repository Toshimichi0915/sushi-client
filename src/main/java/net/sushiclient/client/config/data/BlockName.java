package net.sushiclient.client.config.data;

import com.google.gson.annotations.SerializedName;
import net.minecraft.block.Block;

import java.util.ArrayList;

public class BlockName {
    private static final BlockName[] VALUES;
    @SerializedName("name")
    private final String name;

    static {
        ArrayList<BlockName> blocks = new ArrayList<>();
        for (Block block : Block.REGISTRY) {
            blocks.add(new BlockName(block));
        }
        VALUES = blocks.toArray(new BlockName[0]);
    }

    public BlockName(Block block) {
        this.name = block.getRegistryName().toString();
    }

    public Block toBlock() {
        return Block.getBlockFromName(name);
    }

    public BlockName[] values() {
        BlockName[] copy = new BlockName[VALUES.length];
        System.arraycopy(VALUES, 0, copy, 0, VALUES.length);
        return copy;
    }

    public static BlockName fromName(String name) {
        Block block = Block.getBlockFromName(name);
        if (block == null) return null;
        else return new BlockName(block);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockName blockName = (BlockName) o;

        return name != null ? name.equals(blockName.name) : blockName.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "BlockName{" +
                "name='" + name + '\'' +
                '}';
    }
}
