package net.toshimichi.sushi.utils;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class PistonAuraAttack implements Comparable<PistonAuraAttack> {
    private final BlockPos crystalPos;
    private final BlockPos pistonPos;
    private final EnumFacing facing;
    private final EntityPlayer player;
    private final EntityPlayer target;
    private final double damage;
    private final EntityEnderCrystal placed;
    private final boolean blocked;
    private final boolean pistonPlaced;
    private final boolean redstonePlaced;
    private final boolean pistonActivated;

    public PistonAuraAttack(BlockPos crystalPos, BlockPos pistonPos, EnumFacing facing, EntityPlayer player, EntityPlayer target,
                            double damage, EntityEnderCrystal placed, boolean blocked, boolean pistonPlaced, boolean redstonePlaced, boolean pistonActivated) {
        this.crystalPos = crystalPos;
        this.pistonPos = pistonPos;
        this.facing = facing;
        this.player = player;
        this.target = target;
        this.damage = damage;
        this.placed = placed;
        this.blocked = blocked;
        this.pistonPlaced = pistonPlaced;
        this.redstonePlaced = redstonePlaced;
        this.pistonActivated = pistonActivated;
    }

    public BlockPos getCrystalPos() {
        return crystalPos;
    }

    public BlockPos getPistonPos() {
        return pistonPos;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public EntityPlayer getTarget() {
        return target;
    }

    public double getDamage() {
        return damage;
    }

    public EntityEnderCrystal getPlaced() {
        return placed;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public boolean isPistonPlaced() {
        return pistonPlaced;
    }

    public boolean isRedstonePlaced() {
        return redstonePlaced;
    }

    public boolean isPistonActivated() {
        return pistonActivated;
    }

    @Override
    public int compareTo(PistonAuraAttack o) {
        int temp = Boolean.compare(blocked, o.blocked);
        if (temp == 0) temp = Boolean.compare(placed == null, o.placed == null);
        if (temp == 0) temp = Boolean.compare(!pistonPlaced, !o.pistonPlaced);
        if (temp == 0) temp = Boolean.compare(!redstonePlaced, !o.redstonePlaced);
        if (temp == 0) temp = Boolean.compare(!pistonActivated, !o.pistonActivated);
        if (temp == 0) temp = Double.compare(o.damage, damage);
        return temp;
    }

    @Override
    public String toString() {
        return "PistonAuraAttack{" +
                "crystalPos=" + crystalPos +
                ", pistonPos=" + pistonPos +
                ", facing=" + facing +
                ", player=" + player +
                ", target=" + target +
                ", damage=" + damage +
                ", placed=" + placed +
                ", blocked=" + blocked +
                ", pistonPlaced=" + pistonPlaced +
                ", redstonePlaced=" + redstonePlaced +
                ", pistonActivated=" + pistonActivated +
                '}';
    }
}
