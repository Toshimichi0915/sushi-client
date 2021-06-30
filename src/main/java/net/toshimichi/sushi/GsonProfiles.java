package net.toshimichi.sushi;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.toshimichi.sushi.command.ChatMessageHandler;
import net.toshimichi.sushi.command.MessageHandler;
import net.toshimichi.sushi.hwid.annotations.AsyncAuthentication;
import net.toshimichi.sushi.modules.Categories;
import net.toshimichi.sushi.modules.GsonCategories;
import net.toshimichi.sushi.modules.GsonModules;
import net.toshimichi.sushi.modules.Modules;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
        profileConfig.load(configFile);
        GsonCategories categories = new GsonCategories(new File(dir, "categories.json"), gson);
        GsonModules modules = new GsonModules(profileConfig.version, new File(dir, "modules.json"), categories, gson);
        return new GsonProfile(configFile, profileConfig, modules, categories);
    }

    private class ProfileConfig {

        @SerializedName("prefix")
        char prefix = '.';
        @SerializedName("version")
        int version = Sushi.getVersion();

        void load(File file) {
            try {
                if (!file.exists()) return;
                String contents = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                ProfileConfig config = gson.fromJson(contents, ProfileConfig.class);
                this.prefix = config.prefix;
                this.version = config.version;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void save(File file) {
            try {
                FileUtils.writeStringToFile(file, gson.toJson(this), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class GsonProfile implements Profile {

        private final File configFile;
        private final ProfileConfig config;
        private final Modules modules;
        private final GsonCategories categories;

        GsonProfile(File configFile, ProfileConfig config, GsonModules modules, GsonCategories categories) {
            this.configFile = configFile;
            this.config = config;
            this.modules = modules;
            this.categories = categories;
        }

        @Override
        public int getVersion() {
            return config.version;
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
            return config.prefix;
        }

        @Override
        public void setPrefix(char prefix) {
            config.prefix = prefix;
        }

        @Override
        public MessageHandler getMessageHandler() {
            return new ChatMessageHandler();
        }

        @AsyncAuthentication
        @Override
        public void load() {
            config.load(configFile);
            categories.load();
            modules.load();
        }

        @AsyncAuthentication
        @Override
        public void save() {
            config.save(configFile);
            categories.save();
            modules.save();
        }
    }
}
