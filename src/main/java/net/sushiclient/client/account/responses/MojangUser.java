package net.sushiclient.client.account.responses;

public class MojangUser {
    private String id;
    private String email;
    private String username;
    private String registerIp;
    private String migratedFrom;
    private long migratedAt;
    private long registeredAt;
    private long passwordChangedAt;
    private long dateOfBirth;
    private boolean suspended;
    private boolean blocked;
    private boolean secured;
    private boolean migrated;
    private boolean emailVerified;
    private boolean legacyUser;
    private boolean verifiedByParent;

    public MojangUser() {
    }

    public MojangUser(String id, String email, String username, String registerIp, String migratedFrom, long migratedAt, long registeredAt, long passwordChangedAt, long dateOfBirth, boolean suspended, boolean blocked, boolean secured, boolean migrated, boolean emailVerified, boolean legacyUser, boolean verifiedByParent) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.registerIp = registerIp;
        this.migratedFrom = migratedFrom;
        this.migratedAt = migratedAt;
        this.registeredAt = registeredAt;
        this.passwordChangedAt = passwordChangedAt;
        this.dateOfBirth = dateOfBirth;
        this.suspended = suspended;
        this.blocked = blocked;
        this.secured = secured;
        this.migrated = migrated;
        this.emailVerified = emailVerified;
        this.legacyUser = legacyUser;
        this.verifiedByParent = verifiedByParent;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public String getMigratedFrom() {
        return migratedFrom;
    }

    public long getMigratedAt() {
        return migratedAt;
    }

    public long getRegisteredAt() {
        return registeredAt;
    }

    public long getPasswordChangedAt() {
        return passwordChangedAt;
    }

    public long getDateOfBirth() {
        return dateOfBirth;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public boolean isSecured() {
        return secured;
    }

    public boolean isMigrated() {
        return migrated;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public boolean isLegacyUser() {
        return legacyUser;
    }

    public boolean isVerifiedByParent() {
        return verifiedByParent;
    }
}
