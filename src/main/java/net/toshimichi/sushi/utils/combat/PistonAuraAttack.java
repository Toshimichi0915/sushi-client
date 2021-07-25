package net.toshimichi.sushi.utils.combat;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.toshimichi.sushi.utils.world.BlockPlaceInfo;

import java.util.List;

public class PistonAuraAttack implements Comparable<PistonAuraAttack> {
    private BlockPos crystalPos;
    private BlockPos pistonPos;
    private EnumFacing facing;
    private EntityPlayer player;
    private EntityPlayer target;
    private double damage;
    private EntityEnderCrystal crystal;
    private boolean blocked;
    private boolean crystalPlaced;
    private boolean pistonPlaced;
    private boolean redstonePlaced;
    private boolean pistonActivated;
    private List<BlockPlaceInfo> crystalObsidian;
    private List<BlockPlaceInfo> pistonObsidian;
    private int placeCost;

    public PistonAuraAttack(BlockPos crystalPos, BlockPos pistonPos, EnumFacing facing, EntityPlayer player, EntityPlayer target,
                            double damage, EntityEnderCrystal crystal, boolean blocked, boolean crystalPlaced, boolean pistonPlaced, boolean redstonePlaced, boolean pistonActivated,
                            List<BlockPlaceInfo> crystalObsidian, List<BlockPlaceInfo> pistonObsidian) {
        this.crystalPos = crystalPos;
        this.pistonPos = pistonPos;
        this.facing = facing;
        this.player = player;
        this.target = target;
        this.damage = damage;
        this.crystal = crystal;
        this.blocked = blocked;
        this.crystalPlaced = crystalPlaced;
        this.pistonPlaced = pistonPlaced;
        this.redstonePlaced = redstonePlaced;
        this.pistonActivated = pistonActivated;
        this.crystalObsidian = crystalObsidian;
        this.pistonObsidian = pistonObsidian;
        this.placeCost = (crystalObsidian == null ? 0 : crystalObsidian.size()) + (pistonObsidian == null ? 0 : pistonObsidian.size());
    }

    public BlockPos getCrystalPos() {
        return crystalPos;
    }

    public void setCrystalPos(BlockPos crystalPos) {
        this.crystalPos = crystalPos;
    }

    public BlockPos getPistonPos() {
        return pistonPos;
    }

    public void setPistonPos(BlockPos pistonPos) {
        this.pistonPos = pistonPos;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public void setPlayer(EntityPlayer player) {
        this.player = player;
    }

    public EntityPlayer getTarget() {
        return target;
    }

    public void setTarget(EntityPlayer target) {
        this.target = target;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public EntityEnderCrystal getCrystal() {
        return crystal;
    }

    public void setCrystal(EntityEnderCrystal crystal) {
        this.crystal = crystal;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isCrystalPlaced() {
        return crystalPlaced;
    }

    public void setCrystalPlaced(boolean crystalPlaced) {
        this.crystalPlaced = crystalPlaced;
    }

    public boolean isPistonPlaced() {
        return pistonPlaced;
    }

    public void setPistonPlaced(boolean pistonPlaced) {
        this.pistonPlaced = pistonPlaced;
    }

    public boolean isRedstonePlaced() {
        return redstonePlaced;
    }

    public void setRedstonePlaced(boolean redstonePlaced) {
        this.redstonePlaced = redstonePlaced;
    }

    public boolean isPistonActivated() {
        return pistonActivated;
    }

    public void setPistonActivated(boolean pistonActivated) {
        this.pistonActivated = pistonActivated;
    }

    public List<BlockPlaceInfo> getCrystalObsidian() {
        return crystalObsidian;
    }

    public void setCrystalObsidian(List<BlockPlaceInfo> crystalObsidian) {
        this.crystalObsidian = crystalObsidian;
    }

    public List<BlockPlaceInfo> getPistonObsidian() {
        return pistonObsidian;
    }

    public void setPistonObsidian(List<BlockPlaceInfo> pistonObsidian) {
        this.pistonObsidian = pistonObsidian;
    }

    public int getPlaceCost() {
        return placeCost;
    }

    public void setPlaceCost(int placeCost) {
        this.placeCost = placeCost;
    }

    @Override
    public int compareTo(PistonAuraAttack o) {
        int temp = Boolean.compare(blocked, o.blocked);
        if (temp == 0) temp = Boolean.compare(crystal == null, o.crystal == null);
        if (temp == 0) temp = Boolean.compare(!pistonPlaced, !o.pistonPlaced);
        if (temp == 0) temp = Boolean.compare(!redstonePlaced, !o.redstonePlaced);
        if (temp == 0) temp = Boolean.compare(!pistonActivated, !o.pistonActivated);
        if (temp == 0) temp = Integer.compare(placeCost, o.placeCost);
        if (temp == 0) temp = Double.compare(o.damage, damage);
        return temp;
    }
}
