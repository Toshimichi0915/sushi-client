package net.toshimichi.sushi;

import net.toshimichi.sushi.gui.theme.Theme;

import java.util.ArrayList;
import java.util.List;

public class Sushi {
    private static final List<Theme> themes = new ArrayList<>();
    private static Theme defaultTheme;
    private static Profile profile;
    private static Profiles profiles;

    public static Profile getProfile() {
        return profile;
    }

    public static void setProfile(Profile profile) {
        Sushi.profile = profile;
    }

    public static Profiles getProfiles() {
        return profiles;
    }

    public static void setProfiles(Profiles profiles) {
        Sushi.profiles = profiles;
    }

    public static Theme getDefaultTheme() {
        return defaultTheme;
    }

    public static void setDefaultTheme(Theme defaultTheme) {
        Sushi.defaultTheme = defaultTheme;
    }

    public static List<Theme> getThemes() {
        return new ArrayList<>(themes);
    }

    public static void setThemes(List<Theme> themes) {
        Sushi.themes.clear();
        Sushi.themes.addAll(themes);
    }
}
