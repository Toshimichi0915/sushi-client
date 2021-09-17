package net.sushiclient.client.account;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.sushiclient.client.account.exceptions.ForbiddenOperationException;
import net.sushiclient.client.account.exceptions.MojangBlockedException;
import net.sushiclient.client.account.exceptions.StatusCodeException;
import net.sushiclient.client.account.requests.*;
import net.sushiclient.client.account.responses.AuthResponse;
import net.sushiclient.client.account.responses.BriefProfile;
import net.sushiclient.client.account.responses.ErrorResponse;
import net.sushiclient.client.account.responses.NameHistory;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;

public class SecureMojangAPI implements MojangAPI {

    private static final String authURL = "https://authserver.mojang.com/authenticate";
    private static final String validateURL = "https://authserver.mojang.com/validate";
    private static final String refreshURL = "https://authserver.mojang.com/refresh";
    private static final String signoutURL = "https://authserver.mojang.com/signout";
    private static final String invalidateURL = "https://authserver.mojang.com/invalidate";
    private static final String passwordURL = "https://api.mojang.com/users/password";
    private static final Gson gson = new Gson();

    private boolean checkStatus(CloseableHttpResponse res, int code) throws IOException {
        if (res.getStatusLine().getStatusCode() == code) {
            return true;
        } else {
            if (res.getEntity() == null)
                throw new StatusCodeException(res.getStatusLine().toString(), res.getStatusLine().getStatusCode());

            String body = IOUtils.toString(res.getEntity().getContent(), StandardCharsets.UTF_8);
            ErrorResponse error = null;
            try {
                error = gson.fromJson(body, ErrorResponse.class);
            } catch (JsonSyntaxException e) {
                // illegal response
            }
            Header header = res.getFirstHeader("Content-Type");
            if (header != null && header.getValue().contains("text"))
                throw new MojangBlockedException(res.getStatusLine().getStatusCode());
            else if (body.contains("blocked"))
                throw new MojangBlockedException(res.getStatusLine().getStatusCode());
            else if (error != null && error.getError() != null)
                throw new ForbiddenOperationException(error.getErrorMessage());
            else
                throw new StatusCodeException(res.getStatusLine().toString() + " " + body, res.getStatusLine().getStatusCode());
        }
    }

    private RequestBuilder newGsonRequest(String url, String method, Object o, String accessToken) {
        RequestBuilder builder = RequestBuilder.create(method).setUri(url);
        if (accessToken != null)
            builder.addHeader("Authorization", "Bearer " + accessToken);
        return builder.setEntity(new StringEntity(gson.toJson(o), ContentType.APPLICATION_JSON));
    }

    private CloseableHttpResponse execute(RequestBuilder req) throws IOException {
        HttpClientBuilder builder = HttpClientBuilder.create();
        HttpClientContext context = new HttpClientContext();
        return builder.build().execute(req.build(), context);
    }

    @Override
    public AuthResponse auth(MojangAccount acc) throws IOException {
        AuthRequest auth = new AuthRequest(null, null, acc.getEmail(), acc.getPassword(), new Agent("Minecraft", 1), true);

        try (CloseableHttpResponse res = execute(newGsonRequest(authURL, "POST", auth, null))) {
            checkStatus(res, 200);
            return gson.fromJson(IOUtils.toString(res.getEntity().getContent(), StandardCharsets.UTF_8), AuthResponse.class);
        }
    }


    @Override
    public boolean validate(MojangAccount acc) throws IOException {
        ValidateRequest validate = new ValidateRequest(acc.getAccessToken(), acc.getClientToken());
        try (CloseableHttpResponse res = execute(newGsonRequest(validateURL, "POST", validate, null))) {
            return checkStatus(res, 204);
        }
    }

    @Override
    public RefreshResponse refresh(MojangAccount acc) throws IOException {
        try (CloseableHttpResponse res = execute(newGsonRequest(refreshURL, "POST",
                new RefreshRequest(acc.getAccessToken(), acc.getClientToken(), null, true), null))) {
            checkStatus(res, 200);
            return gson.fromJson(IOUtils.toString(res.getEntity().getContent(), StandardCharsets.UTF_8), RefreshResponse.class);
        }
    }

    @Override
    public boolean signout(MojangAccount acc) throws IOException {
        try (CloseableHttpResponse res =
                     execute(newGsonRequest(signoutURL, "POST", new SignoutRequest(acc.getEmail(), acc.getPassword()), null))) {
            return checkStatus(res, 204);
        }
    }

    @Override
    public boolean invalidate(MojangAccount acc) throws IOException {
        try (CloseableHttpResponse res =
                     execute(newGsonRequest(invalidateURL, "POST", new InvalidateRequest(acc.getAccessToken(), acc.getClientToken()), null))) {
            return checkStatus(res, 204);
        }
    }

    @Override
    public boolean setName(MojangAccount acc, String name) throws IOException {
        String nameURL = "https://api.mojang.com/user/profile/" + acc.getId() + "/name";
        try (CloseableHttpResponse res =
                     execute(newGsonRequest(nameURL, "POST", new NameRequest(name, acc.getPassword()), acc.getAccessToken()))) {
            return checkStatus(res, 204);
        }
    }

    @Override
    public boolean setPassword(MojangAccount acc, String password) throws IOException {
        try (CloseableHttpResponse res =
                     execute(newGsonRequest(passwordURL, "PUT", new PasswordRequest(password, acc.getPassword()), acc.getAccessToken()))) {
            return checkStatus(res, 204);
        }
    }

    // You know the future?
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public Collection<NameHistory> getNameHistory(MojangAccount acc) throws IOException {
        try (CloseableHttpResponse res =
                     execute(RequestBuilder.get().setUri("https://api.mojang.com/user/profiles/" + acc.getId() + "/names"))) {
            checkStatus(res, 200);
            Type type = new TypeToken<Collection<NameHistory>>() {}.getType();
            return gson.fromJson(IOUtils.toString(res.getEntity().getContent(), StandardCharsets.UTF_8), type);
        }
    }

    @Override
    public BriefProfile getProfileAt(MojangAccount acc, Date date) throws IOException {
        RequestBuilder builder = RequestBuilder.get().setUri("https://api.mojang.com/users/profiles/minecraft/" + acc.getName())
                .addParameter("at", Long.toString(date.getTime() / 1000));
        try (CloseableHttpResponse res =
                     execute(builder)) {
            checkStatus(res, 200);
            return gson.fromJson(IOUtils.toString(res.getEntity().getContent(), StandardCharsets.UTF_8), BriefProfile.class);
        }
    }
}
