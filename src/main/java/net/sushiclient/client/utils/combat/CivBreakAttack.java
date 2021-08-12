package net.sushiclient.client.utils.combat;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.sushiclient.client.utils.world.BlockUtils;

public class CivBreakAttack implements Comparable<CivBreakAttack> {
    private BlockPos crystalPos;
    private BlockPos obsidianPos;
    private EntityPlayer player;
    private EntityPlayer target;
    private EntityEnderCrystal crystal;
    private double damage;
    private boolean crystalPlaced;
    private boolean obsidianPlaced;

    public CivBreakAttack(BlockPos crystalPos, BlockPos obsidianPos, EntityPlayer player, EntityPlayer target, EntityEnderCrystal crystal, double damage, boolean crystalPlaced, boolean obsidianPlaced) {
        this.crystalPos = crystalPos;
        this.obsidianPos = obsidianPos;
        this.player = player;
        this.target = target;
        this.crystal = crystal;
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
    public int compareTo(CivBreakAttack o) {
        int result = Boolean.compare(o.obsidianPos.equals(BlockUtils.getBreakingBlockPos()), obsidianPos.equals(BlockUtils.getBreakingBlockPos()));
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
                ", damage=" + damage +
                ", crystalPlaced=" + crystalPlaced +
                ", obsidianPlaced=" + obsidianPlaced +
                '}';
    }
}
