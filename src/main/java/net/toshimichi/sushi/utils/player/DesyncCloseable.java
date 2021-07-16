package net.toshimichi.sushi.utils.player;

import java.io.Closeable;

public class DesyncCloseable implements Closeable {
    @Override
    public void close() {
        PositionUtils.pop(this);
    }
}
