package net.sushiclient.client.account.requests;

public class InvalidateRequest {
    private final String accessToken;
    private final String clientToken;

    public InvalidateRequest(String accessToken, String clientToken) {
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
