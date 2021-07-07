package net.toshimichi.sushi;

import com.google.gson.Gson;
import net.toshimichi.sushi.command.ChatMessageHandler;
import net.toshimichi.sushi.command.MessageHandler;
import net.toshimichi.sushi.hwid.annotations.AsyncAuthentication;
import net.toshimichi.sushi.modules.Categories;
import net.toshimichi.sushi.modules.GsonCategories;
import net.toshimichi.sushi.modules.GsonModules;
import net.toshimichi.sushi.modules.Modules;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GsonProfiles implements Profiles {

    private final File baseDir;
    private final Gson gson;

    public GsonProfiles(File baseDir, Gson gson) {
        this.baseDir = baseDir;
        this.gson = gson;
    }

    private File getFile(String name) {
        return new File(baseDir, name);
    }

    @Override
    public List<String> getAll() {
        baseDir.mkdirs();
        File[] list = baseDir.listFiles();
        if (list == null) return Collections.emptyList();
        else return Arrays.stream(list).map(File::getName).collect(Collectors.toList());
    }

    @Override
    public Profile load(String name) {
        File dir = getFile(name);
        File configFile = new File(getFile(name), "profile.json");
        ProfileConfig profileConfig = new ProfileConfig();
        profileConfig.load(gson, configFile);
        GsonCategories categories = new GsonCategories(new File(dir, "categories.json"), gson);
        GsonModules modules = new GsonModules(profileConfig.getVersion(), new File(dir, "modules.json"), categories, gson);
        return new GsonProfile(gson, configFile, profileConfig, modules, categories);
    }

    private static class GsonProfile implements Profile {

        private final Gson gson;
        private final File configFile;
        private final ProfileConfig config;
        private final Modules modules;
        private final GsonCategories categories;

        GsonProfile(Gson gson, File configFile, ProfileConfig config, GsonModules modules, GsonCategories categories) {
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
        public MessageHandler getMessageHandler() {
            return new ChatMessageHandler();
        }

        @AsyncAuthentication
        @Override
        public void load() {
            config.load(gson, configFile);
            categories.load();
            modules.load();
        }

        @AsyncAuthentication
        @Override
        public void save() {
            config.save(gson, configFile);
            categories.save();
            modules.save();
        }
    }
}
