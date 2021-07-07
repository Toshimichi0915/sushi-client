package net.toshimichi.sushi.utils.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * obfuscation friendly {@link com.google.gson.internal.bind.TypeAdapters.EnumTypeAdapter}
 */
public class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {
    private final Map<String, T> nameToConstant = new HashMap<>();
    private final Map<T, String> constantToName = new HashMap<>();

    @SuppressWarnings("unchecked")
    public EnumTypeAdapter(Class<T> classOfT) {
        try {
            for (Field field : classOfT.getDeclaredFields()) {
                if (!field.isEnumConstant()) continue;
                field.setAccessible(true);
                T constant = (T) (field.get(null));
                String name = constant.name();
                SerializedName annotation = field.getAnnotation(SerializedName.class);
                if (annotation != null) {
                    name = annotation.value();
                    for (String alternate : annotation.alternate()) {
                        nameToConstant.put(alternate, constant);
                    }
                }
                nameToConstant.put(name, constant);
                constantToName.put(constant, name);
            }
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public T read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return nameToConstant.get(in.nextString());
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        out.value(value == null ? null : constantToName.get(value));
    }
}
