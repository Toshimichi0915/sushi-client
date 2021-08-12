package net.sushiclient.client;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.sushiclient.client.utils.MixinGenerator;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

@IFMLLoadingPlugin.Name("SushiMixinLoader")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class MixinLoaderForge implements IFMLLoadingPlugin {

    private static boolean isObfuscatedEnvironment = false;

    public MixinLoaderForge() {
        MixinGenerator.loadResources(this);
        MixinBootstrap.init();
        Mixins.addConfigurations("mixins.sushi.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        isObfuscatedEnvironment = (boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
