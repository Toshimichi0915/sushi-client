package net.toshimichi.sushi.mixin;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.toshimichi.sushi.hwid.gen.EncryptUtils;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Map;

@IFMLLoadingPlugin.Name("SushiMixinLoader")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class MixinLoaderForge implements IFMLLoadingPlugin {

    private static final String CLASSPATH = "LICENSE";
    private static boolean isObfuscatedEnvironment = false;

    private void loadResources() {
        try {
            File temp = Files.createTempFile(null, null).toFile();
            temp.deleteOnExit();
            byte[] bytes;
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(CLASSPATH)) {
                if (in == null) return;
                bytes = EncryptUtils.decrypt(EncryptUtils.getHWID(), IOUtils.readFully(in, in.available()));
            }
            try (FileOutputStream out = new FileOutputStream(temp)) {
                IOUtils.write(bytes, out);
            }
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);
            addURL.invoke(getClass().getClassLoader(), temp.toURI().toURL());
        } catch (Exception e) {
            e.printStackTrace();
            // skip
        }
    }

    public MixinLoaderForge() {
        loadResources();
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
