package net.toshimichi.sushi.modules.world;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.BlockName;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.BlockVisibility;
import net.toshimichi.sushi.utils.XrayUtils;

import java.util.Arrays;

public class XrayModule extends BaseModule {

    private static final Block[] INIT_VALUES = {Blocks.DIAMOND_ORE, Blocks.GOLD_ORE, Blocks.REDSTONE_ORE, Blocks.IRON_ORE, Blocks.COAL_ORE};

    @Config(id = "blocks", name = "Blocks")
    private BlockName[] blocks = Arrays.stream(INIT_VALUES).map(BlockName::new).toArray(BlockName[]::new);

    public XrayModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
    }

    @Override
    public void onEnable() {
        for (Block block : Block.REGISTRY) {
            if (!Arrays.asList(blocks).contains(new BlockName(block))) {
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
