package net.sushiclient.client.modules.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.math.RayTraceResult;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.world.EntityTraceEvent;
import net.sushiclient.client.modules.*;

public class NoEntityTraceModule extends BaseModule {

    @Config(id = "sword", name = "Sword")
    public Boolean sword = false;

    @Config(id = "pickaxe", name = "Pickaxe")
    public Boolean pickaxe = true;

    @Config(id = "axe", name = "Axe")
    public Boolean axe = true;

    @Config(id = "shovel", name = "Shovel")
    public Boolean shovel = true;

    @Config(id = "hoe", name = "Hoe")
    public Boolean hoe = true;

    @Config(id = "gapple", name = "Gapple")
    public Boolean gapple = true;

    @Config(id = "obsidian", name = "Obsidian")
    public Boolean obsidian = true;

    @Config(id = "tile_entity", name = "Tile Entity")
    public Boolean tileEntity = true;

    @Config(id = "exclude_crystal", name = "Exclude Crystal")
    public Boolean excludeCrystal = true;

    public NoEntityTraceModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    private boolean shouldCancel() {
        Item mainHand = getPlayer().getHeldItemMainhand().getItem();
        if (sword && mainHand instanceof ItemSword) return true;
        if (pickaxe && mainHand instanceof ItemPickaxe) return true;
        if (axe && mainHand instanceof ItemAxe) return true;
        if (shovel && mainHand instanceof ItemSpade) return true;
        if (hoe && mainHand instanceof ItemHoe) return true;
        if (gapple && mainHand instanceof ItemAppleGold) return true;

        double reach = getController().getBlockReachDistance();
        float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
        RayTraceResult result = getPlayer().rayTrace(reach, partialTicks);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            IBlockState blockState = getWorld().getBlockState(result.getBlockPos());
            Block block = blockState.getBlock();
            if (obsidian && block == Blocks.OBSIDIAN) return true;
            if (tileEntity && getWorld().getTileEntity(result.getBlockPos()) != null) return true;
        }
        return false;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onEntityTrace(EntityTraceEvent e) {
        if (!shouldCancel()) return;
        if (excludeCrystal) {
            e.getEntities().removeIf(it -> !(it instanceof EntityEnderCrystal));
        } else {
            e.getEntities().clear();
        }
    }

    @Override
    public String getDefaultName() {
        return "NoEntityTrace";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }
}
