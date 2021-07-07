package net.toshimichi.sushi.utils.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.awt.Color;
import java.io.IOException;

public class ColorTypeAdapter extends TypeAdapter<Color> {
    @Override
    public void write(JsonWriter out, Color value) throws IOException {
        out.value("#" + Integer.toHexString(value.getRGB()));
    }

    @Override
    public Color read(JsonReader in) throws IOException {
        return new Color((int) Long.parseLong(in.nextString().replaceFirst("#", ""), 16));
    }
}
