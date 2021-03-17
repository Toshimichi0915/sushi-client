package net.toshimichi.sushi;

public class Sushi {
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
}
