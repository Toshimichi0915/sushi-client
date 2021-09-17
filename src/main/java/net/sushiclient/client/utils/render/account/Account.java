package net.sushiclient.client.utils.render.account;

public class Account {
    private final String email;
    private final String password;
    private final String cachedUsername;

    public Account(String email, String password, String cachedUsername) {
        this.email = email;
        this.password = password;
        this.cachedUsername = cachedUsername;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getCachedUsername() {
        return cachedUsername;
    }
}
