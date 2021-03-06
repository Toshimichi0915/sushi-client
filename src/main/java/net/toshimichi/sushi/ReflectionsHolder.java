package net.toshimichi.sushi;

import org.reflections.Reflections;

public class ReflectionsHolder {
    private static final Reflections def = new Reflections();
    public static Reflections getDefault() {
        return def;
    }
}
