package net.sushiclient.client.account.responses;

public class MinecraftProfile {
    private String id;
    private String name;
    private String userId;
    private long createdAt;
    private boolean legacyProfile;
    private boolean suspended;
    private boolean paid;
    private boolean migrated;
    private boolean legacy;

    public MinecraftProfile() {
    }

    public MinecraftProfile(String id, String name, String userId, long createdAt, boolean legacyProfile, boolean suspended, boolean paid, boolean migrated, boolean legacy) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.createdAt = createdAt;
        this.legacyProfile = legacyProfile;
        this.suspended = suspended;
        this.paid = paid;
        this.migrated = migrated;
        this.legacy = legacy;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isLegacyProfile() {
        return legacyProfile;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public boolean isPaid() {
        return paid;
    }

    public boolean isMigrated() {
        return migrated;
    }

    public boolean isLegacy() {
        return legacy;
    }
}
