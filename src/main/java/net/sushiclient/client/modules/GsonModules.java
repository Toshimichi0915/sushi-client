package net.sushiclient.client.modules;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.sushiclient.client.config.GsonConfigurations;
import net.sushiclient.client.config.GsonRootConfigurations;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.modules.client.AntiCrasherModule;
import net.sushiclient.client.modules.client.AntiErrorKickModule;
import net.sushiclient.client.modules.client.ChatSuffix;
import net.sushiclient.client.modules.client.ClickGuiModule;
import net.sushiclient.client.modules.client.CommandAbuseModule;
import net.sushiclient.client.modules.client.HudModule;
import net.sushiclient.client.modules.client.RotationViewModule;
import net.sushiclient.client.modules.combat.AntiCivBreakModule;
import net.sushiclient.client.modules.combat.AntiPistonAuraModule;
import net.sushiclient.client.modules.combat.AnvilMinerModule;
import net.sushiclient.client.modules.combat.AutoPullModule;
import net.sushiclient.client.modules.combat.AutoTotemModule;
import net.sushiclient.client.modules.combat.AutoTrapModule;
import net.sushiclient.client.modules.combat.CivBreakModule;
import net.sushiclient.client.modules.combat.CriticalsModule;
import net.sushiclient.client.modules.combat.CrystalAuraModule;
import net.sushiclient.client.modules.combat.DeathBowModule;
import net.sushiclient.client.modules.combat.HoleFillModule;
import net.sushiclient.client.modules.combat.HoleMinerModule;
import net.sushiclient.client.modules.combat.KillAuraModule;
import net.sushiclient.client.modules.combat.OffhandModule;
import net.sushiclient.client.modules.combat.PistonAuraModule;
import net.sushiclient.client.modules.combat.SurroundModule;
import net.sushiclient.client.modules.combat.VelocityModule;
import net.sushiclient.client.modules.movement.AnchorModule;
import net.sushiclient.client.modules.movement.AutoWalkModule;
import net.sushiclient.client.modules.movement.GuiMoveModule;
import net.sushiclient.client.modules.movement.NoSlowModule;
import net.sushiclient.client.modules.movement.PacketFlyModule;
import net.sushiclient.client.modules.movement.PhaseFlyModule;
import net.sushiclient.client.modules.movement.PhaseWalkModule;
import net.sushiclient.client.modules.movement.SafeWalkModule;
import net.sushiclient.client.modules.movement.SpeedModule;
import net.sushiclient.client.modules.movement.SprintModule;
import net.sushiclient.client.modules.movement.StepModule;
import net.sushiclient.client.modules.player.AntiHungerModule;
import net.sushiclient.client.modules.player.AutoArmorModule;
import net.sushiclient.client.modules.player.AutoMendModule;
import net.sushiclient.client.modules.player.AutoToolModule;
import net.sushiclient.client.modules.player.BlinkModule;
import net.sushiclient.client.modules.player.FastUseModule;
import net.sushiclient.client.modules.player.FreecamModule;
import net.sushiclient.client.modules.player.InventoryManagerModule;
import net.sushiclient.client.modules.player.LockYawModule;
import net.sushiclient.client.modules.player.NoFallModule;
import net.sushiclient.client.modules.player.NoSwingModule;
import net.sushiclient.client.modules.player.PacketCancellerModule;
import net.sushiclient.client.modules.player.RefillModule;
import net.sushiclient.client.modules.player.SilentCloseModule;
import net.sushiclient.client.modules.player.TimerModule;
import net.sushiclient.client.modules.render.BlockHighlightModule;
import net.sushiclient.client.modules.render.CivBreakHelperModule;
import net.sushiclient.client.modules.render.FullBrightModule;
import net.sushiclient.client.modules.render.HoleBreakEsp;
import net.sushiclient.client.modules.render.HoleEspModule;
import net.sushiclient.client.modules.render.HoleMinerHelperModule;
import net.sushiclient.client.modules.render.NameTagsModule;
import net.sushiclient.client.modules.render.NoRenderModule;
import net.sushiclient.client.modules.render.PlayerEspModule;
import net.sushiclient.client.modules.render.SearchModule;
import net.sushiclient.client.modules.render.StorageEspModule;
import net.sushiclient.client.modules.render.TracersModule;
import net.sushiclient.client.modules.world.AntiGhostBlockModule;
import net.sushiclient.client.modules.world.FakePlayerModule;
import net.sushiclient.client.modules.world.NoEntityTraceModule;
import net.sushiclient.client.modules.world.PacketRecordModule;
import net.sushiclient.client.modules.world.ScaffoldModule;
import net.sushiclient.client.modules.world.SpeedMineModule;
import net.sushiclient.client.modules.world.VirtualTpModule;
import net.sushiclient.client.modules.world.WeatherModule;
import net.sushiclient.client.modules.world.WorldTimeModule;
import net.sushiclient.client.modules.world.XrayModule;
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
        // Combat
        addModuleFactory("anti_civ_break", AntiCivBreakModule::new, true);
        addModuleFactory("anti_piston_aura", AntiPistonAuraModule::new, true);
        addModuleFactory("anvil_miner", AnvilMinerModule::new, true);
        addModuleFactory("auto_pull", AutoPullModule::new, true);
        addModuleFactory("auto_totem", AutoTotemModule::new, true);
        addModuleFactory("auto_trap", AutoTrapModule::new, true);
        addModuleFactory("civ_break", CivBreakModule::new, true);
        addModuleFactory("criticals", CriticalsModule::new, true);
        addModuleFactory("crystal_aura", CrystalAuraModule::new, true);
        addModuleFactory("death_bow", DeathBowModule::new, true);
        addModuleFactory("hole_fill", HoleFillModule::new, true);
        addModuleFactory("hole_miner", HoleMinerModule::new, true);
        addModuleFactory("kill_aura", KillAuraModule::new, true);
        addModuleFactory("offhand", OffhandModule::new, true);
        addModuleFactory("piston_aura", PistonAuraModule::new, true);
        addModuleFactory("surround", SurroundModule::new, true);
        addModuleFactory("velocity", VelocityModule::new, true);

        // Movement
        addModuleFactory("anchor", AnchorModule::new, true);
        addModuleFactory("auto_walk", AutoWalkModule::new, true);
        addModuleFactory("gui_move", GuiMoveModule::new, true);
        addModuleFactory("no_slow", NoSlowModule::new, true);
        addModuleFactory("packet_fly", PacketFlyModule::new, true);
        addModuleFactory("phase_fly", PhaseFlyModule::new, true);
        addModuleFactory("phase_walk", PhaseWalkModule::new, false);
        addModuleFactory("safe_walk", SafeWalkModule::new, true);
        addModuleFactory("speed", SpeedModule::new, true);
        addModuleFactory("sprint", SprintModule::new, true);
        addModuleFactory("step", StepModule::new, true);

        // Render
        addModuleFactory("block_highlight", BlockHighlightModule::new, true);
        addModuleFactory("civ_break_helper", CivBreakHelperModule::new, true);
        addModuleFactory("full_bright", FullBrightModule::new, true);
        addModuleFactory("hole_break_esp", HoleBreakEsp::new, true);
        addModuleFactory("hole_esp", HoleEspModule::new, true);
        addModuleFactory("hole_miner_helper", HoleMinerHelperModule::new, true);
        addModuleFactory("name_tags", NameTagsModule::new, true);
        addModuleFactory("no_render", NoRenderModule::new, true);
        addModuleFactory("player_esp", PlayerEspModule::new, true);
        addModuleFactory("search", SearchModule::new, true);
        addModuleFactory("storage_esp", StorageEspModule::new, true);
        addModuleFactory("tracers", TracersModule::new, true);

        // Player
        addModuleFactory("anti_hunger", AntiHungerModule::new, true);
        addModuleFactory("auto_armor", AutoArmorModule::new, true);
        addModuleFactory("auto_mend", AutoMendModule::new, true);
        addModuleFactory("auto_tool", AutoToolModule::new, true);
        addModuleFactory("blink", BlinkModule::new, true);
        addModuleFactory("fast_use", FastUseModule::new, true);
        addModuleFactory("freecam", FreecamModule::new, true);
        addModuleFactory("inventory_manager", InventoryManagerModule::new, true);
        addModuleFactory("lock_yaw", LockYawModule::new, true);
        addModuleFactory("no_fall", NoFallModule::new, true);
        addModuleFactory("no_swing", NoSwingModule::new, true);
        addModuleFactory("packet_canceller", PacketCancellerModule::new, true);
        addModuleFactory("refill", RefillModule::new, true);
        addModuleFactory("silent_close", SilentCloseModule::new, true);
        addModuleFactory("timer", TimerModule::new, true);

        // World
        addModuleFactory("anti_ghost_block", AntiGhostBlockModule::new, true);
        addModuleFactory("fake_player", FakePlayerModule::new, true);
        addModuleFactory("no_entity_trace", NoEntityTraceModule::new, true);
        addModuleFactory("packet_record", PacketRecordModule::new, true);
        addModuleFactory("scaffold", ScaffoldModule::new, true);
        addModuleFactory("speed_mine", SpeedMineModule::new, true);
        addModuleFactory("virtual_tp", VirtualTpModule::new, true);
        addModuleFactory("weather", WeatherModule::new, true);
        addModuleFactory("world_time", WorldTimeModule::new, true);
        addModuleFactory("xray", XrayModule::new, true);

        // Client
        addModuleFactory("anti_crasher", AntiCrasherModule::new, true);
        addModuleFactory("anti_error_kick", AntiErrorKickModule::new, true);
        addModuleFactory("chat_suffix", ChatSuffix::new, true);
        addModuleFactory("click_gui", ClickGuiModule::new, true);
        addModuleFactory("hud", HudModule::new, true);
        addModuleFactory("command_abuse", CommandAbuseModule::new, true);
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
    public List<Module> restoreAll() {
        ArrayList<Module> result = new ArrayList<>();
        for (DefaultModule module : defaults) {
            Module newModule = restore(module.id);
            if (newModule != null) {
                result.add(newModule);
            }
        }
        return result;
    }

    @Override
    public Module restore(String id) {
        return defaults.stream()
                .filter(it -> it.id.equals(id))
                .filter(it -> getModule(id) == null)
                .map(it -> {
                    root.add(id, new JsonObject());
                    return addModule(id, getModuleFactory(it.factory));
                }).findAny().orElse(null);
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
        saveEnableTag();
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

    @Override
    public void load() {
        handlers.forEach(ModulesHandler::load);
        try {
            modules.clear();
            if (!conf.exists()) {
                root = new JsonObject();
                restoreAll();
            } else {
                String contents = FileUtils.readFileToString(conf, StandardCharsets.UTF_8);
                root = gson.fromJson(contents, JsonObject.class);
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

    private void saveEnableTag() {
        if (!enabled) return;
        for (Module module : modules) {
            if (!(module.getConfigurations() instanceof GsonRootConfigurations)) continue;
            JsonObject object = ((GsonRootConfigurations) module.getConfigurations()).save();
            if (!module.isTemporary()) object.add(ENABLED_TAG, new JsonPrimitive(module.isEnabled()));
            else object.remove(ENABLED_TAG);
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
        saveEnableTag();
        enabled = false;
        for (Module module : modules) {
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
