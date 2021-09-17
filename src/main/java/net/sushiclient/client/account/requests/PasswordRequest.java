package net.sushiclient.client.account.requests;

public class PasswordRequest {
    private final String password;
    private final String oldPassword;

    public PasswordRequest(String password, String oldPassword) {
        this.password = password;
        this.oldPassword = oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public String getOldPassword() {
        return oldPassword;
    }
}
