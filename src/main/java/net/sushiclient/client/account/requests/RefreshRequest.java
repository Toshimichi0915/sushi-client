package net.sushiclient.client.account.requests;

import net.sushiclient.client.account.responses.MinecraftProfile;

public class RefreshRequest {
    private final String accessToken;
    private final String clientToken;
    private final MinecraftProfile selectedProfile;
    private final boolean requestUser;

    public RefreshRequest(String accessToken, String clientToken, MinecraftProfile selectedProfile, boolean requestUser) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
        this.selectedProfile = selectedProfile;
        this.requestUser = requestUser;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getClientToken() {
        return clientToken;
    }

    public MinecraftProfile getSelectedProfile() {
        return selectedProfile;
    }

    public boolean isRequestUser() {
        return requestUser;
    }
}
