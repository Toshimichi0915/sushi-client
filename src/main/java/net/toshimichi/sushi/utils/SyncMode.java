package net.toshimichi.sushi.utils;

public enum SyncMode {
    BOTH(true, true),
    POSITION(true, false),
    LOOK(false, true),
    NONE(false, false);

    private final boolean syncPosition;
    private final boolean syncLook;

    SyncMode(boolean syncPosition, boolean syncLook) {
        this.syncPosition = syncPosition;
        this.syncLook = syncLook;
    }

    public boolean isSyncPosition() {
        return syncPosition;
    }

    public boolean isSyncLook() {
        return syncLook;
    }
}
