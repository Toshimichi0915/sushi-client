package net.sushiclient.client.utils.player;

import net.minecraft.util.Session;

public class SessionUtils {

    private static Session session;

    public static Session getSession() {
        return session;
    }

    public static void setSession(Session session) {
        SessionUtils.session = session;
    }
}
