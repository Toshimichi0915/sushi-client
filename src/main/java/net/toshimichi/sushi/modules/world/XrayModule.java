package net.toshimichi.sushi.modules.world;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.BlockVisibility;
import net.toshimichi.sushi.utils.XrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XrayModule extends BaseModule {

    @Config(id = "blocks", name = "Blocks")
    private List<Block> blocks = new ArrayList<>(Arrays.asList(Blocks.DIAMOND_BLOCK, Blocks.GOLD_ORE, Blocks.REDSTONE_ORE, Blocks.IRON_ORE, Blocks.COAL_ORE));

    public XrayModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
    }

    @Override
    public void onEnable() {
        for (Block block : Block.REGISTRY) {
            if (blocks.contains(block)) {
                XrayUtils.setBlockVisibility(block, BlockVisibility.INVISIBLE);
            }
        }
        XrayUtils.apply();
    }

    @Override
    public void onDisable() {
        XrayUtils.reset();
    }

    @Override
    public String getDefaultName() {
        return "Xray";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }
}
