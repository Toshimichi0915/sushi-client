package net.sushiclient.client.account.requests;

import net.sushiclient.client.account.Agent;

public class AuthRequest {
    private final String captcha;
    private final String captchaSupported;
    private final String username;
    private final String password;
    private final Agent agent;
    private final boolean requestUser;

    public AuthRequest(String captcha, String captchaSupported, String username, String password, Agent agent, boolean requestUser) {
        this.captcha = captcha;
        this.captchaSupported = captchaSupported;
        this.username = username;
        this.password = password;
        this.agent = agent;
        this.requestUser = requestUser;
    }

    public String getCaptcha() {
        return captcha;
    }

    public String getCaptchaSupported() {
        return captchaSupported;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Agent getAgent() {
        return agent;
    }

    public boolean isRequestUser() {
        return requestUser;
    }
}
