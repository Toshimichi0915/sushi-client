package net.sushiclient.client.account.requests;

public class SignoutRequest {
    private final String username;
    private final String password;

    public SignoutRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
