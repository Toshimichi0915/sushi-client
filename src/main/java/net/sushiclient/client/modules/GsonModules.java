package net.sushiclient.client.modules;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.sushiclient.client.config.GsonConfigurations;
import net.sushiclient.client.config.GsonRootConfigurations;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.modules.client.AntiErrorKickModule;
import net.sushiclient.client.modules.client.ClickGuiModule;
import net.sushiclient.client.modules.client.HudModule;
import net.sushiclient.client.modules.client.RotationViewModule;
import net.sushiclient.client.modules.combat.*;
import net.sushiclient.client.modules.movement.*;
import net.sushiclient.client.modules.player.*;
import net.sushiclient.client.modules.render.*;
import net.sushiclient.client.modules.world.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class GsonModules implements Modules {

    private static final String ENABLED_TAG = "enabled";
    private static final String FACTORY_TAG = "base";

    private final Gson gson;
    private final int version;
    private final File conf;
    private final Categories categories;
    private final HashSet<GsonModuleFactory> factories = new HashSet<>();
    private final ArrayList<Module> modules = new ArrayList<>();
    private final ArrayList<DefaultModule> defaults = new ArrayList<>();
    private final ArrayList<ModulesHandler> handlers = new ArrayList<>();
    private JsonObject root = new JsonObject();
    private boolean enabled;

    public GsonModules(int version, File conf, Categories categories, Gson gson) {
        this.version = version;
        this.conf = conf;
        this.categories = categories;
        this.gson = gson;
        addModuleFactory("click_gui", ClickGuiModule::new, true);
        addModuleFactory("no_render", NoRenderModule::new, true);
        addModuleFactory("velocity", VelocityModule::new, true);
        addModuleFactory("hud", HudModule::new, true);
        addModuleFactory("full_bright", FullBrightModule::new, true);
        addModuleFactory("sprint", SprintModule::new, true);
        addModuleFactory("packet_canceller", PacketCancellerModule::new, true);
        addModuleFactory("phase_fly", PhaseFlyModule::new, true);
        addModuleFactory("phase_walk", PhaseWalkModule::new, false);
        addModuleFactory("anti_stuck", AntiStuckModule::new, true);
        addModuleFactory("timer", TimerModule::new, true);
        addModuleFactory("anti_civ_break", AntiCivBreakModule::new, true);
        addModuleFactory("no_swing", NoSwingModule::new, true);
        addModuleFactory("lock_yaw", LockYawModule::new, true);
        addModuleFactory("no_fall", NoFallModule::new, true);
        addModuleFactory("surround", SurroundModule::new, true);
        addModuleFactory("auto_totem", AutoTotemModule::new, true);
        addModuleFactory("anti_hunger", AntiHungerModule::new, true);
        addModuleFactory("auto_armor", AutoArmorModule::new, true);
        addModuleFactory("scaffold", ScaffoldModule::new, true);
        addModuleFactory("auto_walk", AutoWalkModule::new, true);
        addModuleFactory("crystal_aura", CrystalAuraModule::new, true);
        addModuleFactory("piston_aura", PistonAuraModule::new, true);
        addModuleFactory("anti_error_kick", AntiErrorKickModule::new, true);
        addModuleFactory("block_highlight", BlockHighlightModule::new, true);
        addModuleFactory("storage_esp", StorageEspModule::new, true);
        addModuleFactory("no_entity_trace", NoEntityTraceModule::new, true);
        addModuleFactory("xray", XrayModule::new, true);
        addModuleFactory("search", SearchModule::new, true);
        addModuleFactory("anti_piston_aura", AntiPistonAuraModule::new, true);
        addModuleFactory("civ_break", CivBreakModule::new, true);
        addModuleFactory("civ_break_helper", CivBreakHelperModule::new, true);
        addModuleFactory("name_tags", NameTagsModule::new, true);
        addModuleFactory("tracers", TracersModule::new, true);
        addModuleFactory("auto_tool", AutoToolModule::new, true);
        addModuleFactory("hole_esp", HoleEspModule::new, true);
        addModuleFactory("anchor", AnchorModule::new, true);
        addModuleFactory("weather", WeatherModule::new, true);
        addModuleFactory("no_slow", NoSlowModule::new, true);
        addModuleFactory("auto_pull", AutoPullModule::new, true);
        addModuleFactory("hole_miner", HoleMinerModule::new, true);
        addModuleFactory("fake_player", FakePlayerModule::new, true);
        addModuleFactory("hole_miner_helper", HoleMinerHelperModule::new, true);
        addModuleFactory("step", StepModule::new, true);
        addModuleFactory("freecam", FreecamModule::new, true);
        addModuleFactory("kill_aura", KillAuraModule::new, true);
        addModuleFactory("speed", SpeedModule::new, true);
        addModuleFactory("world_time", WorldTimeModule::new, true);
        addModuleFactory("criticals", CriticalsModule::new, true);
        addModuleFactory("packet_fly", PacketFlyModule::new, true);
        addModuleFactory("safe_walk", SafeWalkModule::new, true);
        addModuleFactory("player_esp", PlayerEspModule::new, true);
        addModuleFactory("speed_mine", SpeedMineModule::new, true);
        addModuleFactory("anti_ghost_block", AntiGhostBlockModule::new, true);
        addModuleFactory("gui_move", GuiMoveModule::new, true);
        addModuleFactory("hole_break_esp", HoleBreakEsp::new, true);
        addModuleFactory("offhand", OffhandModule::new, true);
        addModuleFactory("refill", RefillModule::new, true);
        addModuleFactory("fast_use", FastUseModule::new, true);
        addModuleFactory("auto_trap", AutoTrapModule::new, true);
        addModuleFactory("auto_mend", AutoMendModule::new, true);
        addModuleFactory("inventory_manager", InventoryManagerModule::new, true);
        addModuleFactory("hole_fill", HoleFillModule::new, true);
        addModuleFactory("blink", BlinkModule::new, true);
        addModuleFactory("anvil_miner", AnvilMinerModule::new, true);
        addModuleFactory("virtual_tp", VirtualTpModule::new, false);
        addModuleFactory("rotation_view", RotationViewModule::new, true);
    }

    private void addModuleFactory(String id, ModuleConstructor constructor, boolean isDefault) {
        factories.add(new GsonModuleFactory(id, constructor));
        if (isDefault) defaults.add(new DefaultModule(id, id));
    }

    @Override
    public Module getModule(String id) {
        for (Module module : modules) {
            if (module.getId().equals(id))
                return module;
        }
        return null;
    }

    @Override
    public ModuleFactory getModuleFactory(String id) {
        for (ModuleFactory factory : factories) {
            if (factory.getId().equals(id))
                return factory;
        }
        return null;
    }

    @Override
    public List<Module> getAll() {
        return new ArrayList<>(modules);
    }

    @Override
    public Module addModule(String id, ModuleFactory factory) {
        GsonRootConfigurations provider = new GsonRootConfigurations(gson);
        JsonObject object = loadJson(id);
        object.add(FACTORY_TAG, new JsonPrimitive(factory.getId()));
        if (!object.has(ENABLED_TAG)) object.add(ENABLED_TAG, new JsonPrimitive(false));
        provider.load(object);
        Module module = factory.getConstructor().newModule(id, this, categories, provider, factory);
        modules.add(module);
        handlers.forEach(it -> it.addModule(module));
        return module;
    }

    @Override
    public Module cloneModule(String id, String newId) {
        Module original = getModule(id);
        if (original == null) return null;

        // deep copy JsonElement
        root.add(newId, gson.fromJson(gson.toJson(root.get(id)), JsonElement.class));
        return addModule(newId, original.getModuleFactory());
    }

    @Override
    public void removeModule(String id) {
        Module module = getModule(id);
        if (module == null) return;
        modules.remove(module);
        root.remove(module.getId());
        handlers.forEach(it -> it.removeModule(module));
    }

    @Override
    public void save() {
        handlers.forEach(ModulesHandler::save);
        try {
            JsonObject savedRoot = new JsonObject();
            for (Module module : modules) {
                RootConfigurations conf = module.getConfigurations();
                if (!(module.getConfigurations() instanceof GsonConfigurations)) continue;
                JsonObject obj = ((GsonRootConfigurations) conf).save();
                obj.add(FACTORY_TAG, new JsonPrimitive(module.getModuleFactory().getId()));
                savedRoot.add(module.getId(), ((GsonRootConfigurations) conf).save());
            }
            FileUtils.writeStringToFile(conf, gson.toJson(savedRoot), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateConfig(JsonObject root) {
        for (DefaultModule module : defaults) {
            if (root.has(module.id)) continue;
            root.add(module.id, new JsonObject());
            addModule(module.id, getModuleFactory(module.factory));
        }
    }

    @Override
    public void load() {
        handlers.forEach(ModulesHandler::load);
        try {
            modules.clear();
            if (!conf.exists()) {
                updateConfig(new JsonObject());
            } else {
                String contents = FileUtils.readFileToString(conf, StandardCharsets.UTF_8);
                root = gson.fromJson(contents, JsonObject.class);
                updateConfig(root);
            }
            for (Map.Entry<String, JsonElement> entry : new HashSet<>(root.getAsJsonObject().entrySet())) {
                JsonObject moduleJson = entry.getValue().getAsJsonObject();
                String factoryId = moduleJson.getAsJsonPrimitive(FACTORY_TAG).getAsString();
                ModuleFactory factory = getModuleFactory(factoryId);
                if (factory == null) {
                    root.remove(entry.getKey());
                } else if (getModule(entry.getKey()) == null) {
                    addModule(entry.getKey(), getModuleFactory(factoryId));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void enable() {
        if (enabled) return;
        handlers.forEach(ModulesHandler::enable);
        enabled = true;
        for (Module module : modules) {
            if (!(module.getConfigurations() instanceof GsonRootConfigurations)) continue;
            JsonObject object = ((GsonRootConfigurations) module.getConfigurations()).save();
            JsonPrimitive enabledTag = object.getAsJsonPrimitive(ENABLED_TAG);
            if (enabledTag != null && enabledTag.getAsBoolean())
                module.setEnabled(true);
        }
    }

    @Override
    public void disable() {
        if (!enabled) return;
        handlers.forEach(ModulesHandler::disable);
        enabled = false;
        for (Module module : modules) {
            if (!(module.getConfigurations() instanceof GsonRootConfigurations)) continue;
            JsonObject object = ((GsonRootConfigurations) module.getConfigurations()).save();
            if (!module.isTemporary()) object.add(ENABLED_TAG, new JsonPrimitive(module.isEnabled()));
            else object.remove(ENABLED_TAG);
            module.setEnabled(false);
        }
    }

    @Override
    public void addHandler(ModulesHandler handler) {
        handlers.add(handler);
    }

    @Override
    public boolean removeHandler(ModulesHandler handler) {
        return handlers.remove(handler);
    }

    @Override
    public ArrayList<ModulesHandler> getHandlers() {
        return new ArrayList<>(handlers);
    }

    private JsonObject loadJson(String id) {
        JsonObject object = root.getAsJsonObject(id);
        if (object == null) {
            object = new JsonObject();
            root.add(id, object);
        }
        return object;
    }

    private static class DefaultModule {
        String id;
        String factory;

        DefaultModule(String id, String factory) {
            this.id = id;
            this.factory = factory;
        }
    }
}
