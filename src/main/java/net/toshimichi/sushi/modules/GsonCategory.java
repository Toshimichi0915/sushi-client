package net.toshimichi.sushi.modules;

import com.google.gson.annotations.SerializedName;

import java.awt.Image;

public class GsonCategory implements Category {

    @SerializedName("name")
    private String name;

    public GsonCategory() {
    }

    public GsonCategory(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Image getIcon() {
        return null;
    }
}
