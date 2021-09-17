package net.sushiclient.client.account.requests;

public class ValidateRequest {
    private String accessToken;
    private String clientToken;

    public ValidateRequest(String accessToken, String clientToken) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getClientToken() {
        return clientToken;
    }
}
