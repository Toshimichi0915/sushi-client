package net.toshimichi.sushi.utils;

import net.toshimichi.sushi.hwid.gen.EncryptUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;

public class MixinGenerator {
    private static final String CLASSPATH = "76\u2800\u2800";

    public static void loadResources(Object o) {
        try {
            File temp = Files.createTempFile(null, null).toFile();
            temp.deleteOnExit();
            byte[] bytes;
            try (InputStream in = o.getClass().getClassLoader().getResourceAsStream(CLASSPATH)) {
                if (in == null) return;
                bytes = EncryptUtils.decrypt(EncryptUtils.getHWID(), IOUtils.toByteArray(in));
            }
            try (FileOutputStream out = new FileOutputStream(temp)) {
                IOUtils.write(bytes, out);
            }
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);
            addURL.invoke(o.getClass().getClassLoader(), temp.toURI().toURL());
        } catch (Exception e) {
//            e.printStackTrace();
            // skip
        }
    }
}
