package net.sushiclient.client.account.requests;

public class NameRequest {
    private final String name;
    private final String password;

    public NameRequest(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
