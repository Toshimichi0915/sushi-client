package net.sushiclient.client.utils.player;

import java.io.Closeable;

public class DesyncCloseable implements Closeable {
    @Override
    public void close() {
        PositionUtils.pop(this);
    }
}
