package net.toshimichi.sushi.utils.combat;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class CevBreakAttack implements Comparable<CevBreakAttack> {
    private BlockPos crystalPos;
    private BlockPos obsidianPos;
    private EntityPlayer player;
    private EntityPlayer target;
    private EntityEnderCrystal crystal;
    private boolean crystalPlaced;
    private boolean obsidianPlaced;

    public CevBreakAttack(BlockPos crystalPos, BlockPos obsidianPos, EntityPlayer player, EntityPlayer target, EntityEnderCrystal crystal, boolean crystalPlaced, boolean obsidianPlaced) {
        this.crystalPos = crystalPos;
        this.obsidianPos = obsidianPos;
        this.player = player;
        this.target = target;
        this.crystal = crystal;
        this.crystalPlaced = crystalPlaced;
        this.obsidianPlaced = obsidianPlaced;
    }

    public BlockPos getCrystalPos() {
        return crystalPos;
    }

    public BlockPos getObsidianPos() {
        return obsidianPos;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public EntityPlayer getTarget() {
        return target;
    }

    public EntityEnderCrystal getCrystal() {
        return crystal;
    }

    public boolean isCrystalPlaced() {
        return crystalPlaced;
    }

    public boolean isObsidianPlaced() {
        return obsidianPlaced;
    }

    @Override
    public int compareTo(CevBreakAttack o) {
        return Double.compare(player.getDistanceSq(target), player.getDistanceSq(o.target));
    }

    @Override
    public String toString() {
        return "CevBreakAttack{" +
                "crystalPos=" + crystalPos +
                ", obsidianPos=" + obsidianPos +
                ", player=" + player +
                ", target=" + target +
                ", crystal=" + crystal +
                ", crystalPlaced=" + crystalPlaced +
                ", obsidianPlaced=" + obsidianPlaced +
                '}';
    }
}
