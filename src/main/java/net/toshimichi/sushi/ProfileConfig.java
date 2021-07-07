package net.toshimichi.sushi;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

class ProfileConfig {

    @SerializedName("prefix")
    private char prefix = '.';
    @SerializedName("version")
    private int version = Sushi.getVersion();

    public char getPrefix() {
        return prefix;
    }

    public void setPrefix(char prefix) {
        this.prefix = prefix;
    }

    public int getVersion() {
        return version;
    }

    public void load(Gson gson, File file) {
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

    public void save(Gson gson, File file) {
        try {
            FileUtils.writeStringToFile(file, gson.toJson(this), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
