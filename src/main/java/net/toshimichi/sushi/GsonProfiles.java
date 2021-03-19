package net.toshimichi.sushi;

import com.google.gson.Gson;
import net.toshimichi.sushi.gui.theme.Theme;
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
        GsonCategories categories = new GsonCategories(new File(dir, "categories.json"), gson);
        GsonModules modules = new GsonModules(new File(dir, "modules.json"), categories, gson);
        return new GsonProfile(new File(dir, "profile.json"), new ProfileConfig(), modules, categories);
    }

    private class ProfileConfig {
        String theme;

        void load(File file) {
            try {
                if (!file.exists()) return;
                String contents = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                ProfileConfig config = gson.fromJson(contents, ProfileConfig.class);
                this.theme = config.theme;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void save(File file) {
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
        private final GsonModules modules;
        private final GsonCategories categories;

        public GsonProfile(File configFile, ProfileConfig config, GsonModules modules, GsonCategories categories) {
            this.configFile = configFile;
            this.config = config;
            this.modules = modules;
            this.categories = categories;
        }

        @Override
        public Theme getTheme() {
            for (Theme t : Sushi.getThemes()) {
                if (t.getId().equals(config.theme)) return t;
            }
            return Sushi.getDefaultTheme();
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
        public void load() {
            config.load(configFile);
            categories.load();
            modules.load();
        }

        @Override
        public void save() {
            config.save(configFile);
            categories.save();
            modules.save();
        }
    }
}
