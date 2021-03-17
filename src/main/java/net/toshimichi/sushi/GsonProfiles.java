package net.toshimichi.sushi;

import com.google.gson.Gson;
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
        GsonCategories categories = new GsonCategories(new File(dir, "categories.json"), gson);
        GsonModules modules = new GsonModules(new File(dir, "modules.json"), categories, gson);
        return new GsonProfile(modules, categories);
    }

    private static class GsonProfile implements Profile {

        private final GsonModules modules;
        private final GsonCategories categories;

        public GsonProfile(GsonModules modules, GsonCategories categories) {
            this.modules = modules;
            this.categories = categories;
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
            categories.load();
            modules.load();
        }

        @Override
        public void save() {
            categories.save();
            modules.save();
        }
    }
}
