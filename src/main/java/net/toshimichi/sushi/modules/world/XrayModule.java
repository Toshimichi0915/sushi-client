package net.toshimichi.sushi.modules.world;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.BlockVisibility;
import net.toshimichi.sushi.utils.XrayUtils;

import java.util.Arrays;

public class XrayModule extends BaseModule {

    private Block[] init = {Blocks.DIAMOND_ORE, Blocks.GOLD_ORE, Blocks.REDSTONE_ORE, Blocks.IRON_ORE, Blocks.COAL_ORE};

    @Config(id = "blocks", name = "Blocks")
    private String[] blocks = Arrays.stream(init).map(it -> it.getRegistryName().getPath()).toArray(String[]::new);

    public XrayModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
    }

    @Override
    public void onEnable() {
        for (Block block : Block.REGISTRY) {
            if (!Arrays.asList(blocks).contains(block.getRegistryName().getPath())) {
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
