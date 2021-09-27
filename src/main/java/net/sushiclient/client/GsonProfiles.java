package net.sushiclient.client;

import com.google.gson.Gson;
import net.sushiclient.client.command.ChatLogger;
import net.sushiclient.client.command.Logger;
import net.sushiclient.client.modules.Categories;
import net.sushiclient.client.modules.GsonCategories;
import net.sushiclient.client.modules.GsonModules;
import net.sushiclient.client.modules.Modules;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GsonProfiles implements Profiles {

    private final File baseDir;
    private final Gson gson;
    private final ArrayList<String> keys = new ArrayList<>();
    private final HashMap<String, GsonProfile> loaded = new HashMap<>();

    public GsonProfiles(File baseDir, Gson gson) {
        this.baseDir = baseDir;
        this.gson = gson;
        baseDir.mkdirs();
        File[] list = baseDir.listFiles();
        if (list != null) Arrays.stream(list).map(File::getName).forEach(keys::add);
    }

    private File getFile(String name) {
        return new File(baseDir, name);
    }

    @Override
    public List<String> getAll() {
        return new ArrayList<>(keys);
    }

    @Override
    public Profile load(String name) {
        if (!keys.contains(name)) keys.add(name);
        if (loaded.containsKey(name)) {
            return loaded.get(name);
        }
        File dir = getFile(name);
        File configFile = new File(getFile(name), "profile.json");
        ProfileConfig profileConfig = new ProfileConfig();
        profileConfig.load(gson, configFile);
        GsonCategories categories = new GsonCategories(new File(dir, "categories.json"), gson);
        GsonModules modules = new GsonModules(profileConfig.getVersion(), new File(dir, "modules.json"), categories, gson);
        GsonProfile result = new GsonProfile(gson, name, configFile, profileConfig, modules, categories);
        loaded.put(name, result);
        return result;
    }

    @Override
    public Profile clone(String old, String name) {
        if (!getFile(old).exists()) return null;
        if (keys.contains(name)) return null;
        try {
            FileUtils.copyDirectory(getFile(old), getFile(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return load(name);
    }

    @Override
    public boolean remove(String name) {
        String main = getName(Sushi.getProfile());
        if (main.equals(name)) return false;
        keys.remove(name);
        loaded.remove(name);
        File f = getFile(name);
        try {
            if (f.exists()) {
                FileUtils.deleteDirectory(getFile(name));
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public String getName(Profile profile) {
        if (!(profile instanceof GsonProfile)) return null;
        GsonProfile prof = (GsonProfile) profile;
        return prof.name;
    }

    private static class GsonProfile implements Profile {

        private final Gson gson;
        private final String name;
        private final File configFile;
        private final ProfileConfig config;
        private final Modules modules;
        private final GsonCategories categories;

        GsonProfile(Gson gson, String name, File configFile, ProfileConfig config, GsonModules modules, GsonCategories categories) {
            this.name = name;
            this.gson = gson;
            this.configFile = configFile;
            this.config = config;
            this.modules = modules;
            this.categories = categories;
        }

        @Override
        public int getVersion() {
            return config.getVersion();
        }

        @Override
        public Modules getModules() {
            return modules;
        }

        @Override
        public Categories getCategories() {
            return categories;
        }

        @Override
        public char getPrefix() {
            return config.getPrefix();
        }

        @Override
        public void setPrefix(char prefix) {
            config.setPrefix(prefix);
        }

        @Override
        public Logger getLogger() {
            return new ChatLogger();
        }

        @Override
        public void load() {
            config.load(gson, configFile);
            categories.load();
            modules.load();
        }

        @Override
        public void save() {
            config.save(gson, configFile);
            categories.save();
            modules.save();
        }
    }
}
