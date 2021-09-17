package net.sushiclient.client.modules.combat;

import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.modules.*;

public class SelfAnvilModule extends BaseModule {

    public SelfAnvilModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
    }

    @Override
    public String getDefaultName() {
        return "SelfAnvil";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }
}
