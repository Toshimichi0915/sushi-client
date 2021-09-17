package net.sushiclient.client.account;

import java.util.Objects;

final public class MojangAccount {

    private String email;
    private String uuid;
    private String name;
    private String password;
    private String accessToken;
    private String clientToken;

    public MojangAccount() {
    }

    public MojangAccount(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public MojangAccount(MojangAccount acc) {
        this.email = acc.getEmail();
        this.uuid = acc.getId();
        this.name = acc.getName();
        this.password = acc.getPassword();
        this.accessToken = acc.getAccessToken();
        this.clientToken = acc.getClientToken();
    }

    public MojangAccount(String email, String uuid, String name, String password, String accessToken, String clientToken) {
        this.email = email;
        this.uuid = uuid;
        this.name = name;
        this.password = password;
        this.accessToken = accessToken;
        this.clientToken = clientToken;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getClientToken() {
        return clientToken;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MojangAccount that = (MojangAccount) o;
        return Objects.equals(email, that.email) && Objects.equals(uuid, that.uuid) && Objects.equals(name, that.name) && Objects.equals(password, that.password) && Objects.equals(accessToken, that.accessToken) && Objects.equals(clientToken, that.clientToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, uuid, name, password, accessToken, clientToken);
    }
}
