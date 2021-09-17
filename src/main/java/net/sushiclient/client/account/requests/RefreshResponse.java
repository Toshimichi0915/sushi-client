package net.sushiclient.client.account.requests;

import net.sushiclient.client.account.MojangAccount;
import net.sushiclient.client.account.responses.AuthResponse;
import net.sushiclient.client.account.responses.MinecraftProfile;
import net.sushiclient.client.account.responses.MojangUser;

public class RefreshResponse {
    private final String accessToken;
    private final String clientToken;
    private final MinecraftProfile selectedProfile;
    private final MojangUser user;

    public RefreshResponse(String accessToken, String clientToken, MinecraftProfile selectedProfile, MojangUser user) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
        this.selectedProfile = selectedProfile;
        this.user = user;
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

    public MojangUser getUser() {
        return user;
    }

    public void update(MojangAccount acc) {
        AuthResponse.update(acc, user, selectedProfile, accessToken, clientToken);
    }
}
