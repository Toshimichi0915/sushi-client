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
    private BlockPos breakingBlock;
    private double damage;
    private boolean crystalPlaced;
    private boolean obsidianPlaced;

    public CevBreakAttack(BlockPos crystalPos, BlockPos obsidianPos, EntityPlayer player, EntityPlayer target, EntityEnderCrystal crystal, BlockPos breakingBlock, double damage, boolean crystalPlaced, boolean obsidianPlaced) {
        this.crystalPos = crystalPos;
        this.obsidianPos = obsidianPos;
        this.player = player;
        this.target = target;
        this.crystal = crystal;
        this.breakingBlock = breakingBlock;
        this.damage = damage;
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
        int result = Boolean.compare(o.obsidianPos.equals(o.breakingBlock), obsidianPos.equals(breakingBlock));
        if (result == 0) result = Double.compare(player.getDistanceSq(target), player.getDistanceSq(o.target));
        return result;
    }

    @Override
    public String toString() {
        return "CevBreakAttack{" +
                "crystalPos=" + crystalPos +
                ", obsidianPos=" + obsidianPos +
                ", player=" + player +
                ", target=" + target +
                ", crystal=" + crystal +
                ", breakingBlock=" + breakingBlock +
                ", damage=" + damage +
                ", crystalPlaced=" + crystalPlaced +
                ", obsidianPlaced=" + obsidianPlaced +
                '}';
    }
}
