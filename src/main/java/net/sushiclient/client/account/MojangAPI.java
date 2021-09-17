package net.sushiclient.client.account;

import net.sushiclient.client.account.requests.RefreshResponse;
import net.sushiclient.client.account.responses.AuthResponse;
import net.sushiclient.client.account.responses.BriefProfile;
import net.sushiclient.client.account.responses.NameHistory;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

public interface MojangAPI {

    AuthResponse auth(MojangAccount acc) throws IOException;

    boolean validate(MojangAccount acc) throws IOException;

    RefreshResponse refresh(MojangAccount acc) throws IOException;

    boolean signout(MojangAccount acc) throws IOException;

    boolean invalidate(MojangAccount acc) throws IOException;

    boolean setName(MojangAccount acc, String name) throws IOException;

    boolean setPassword(MojangAccount acc, String password) throws IOException;

    Collection<NameHistory> getNameHistory(MojangAccount acc) throws IOException;

    BriefProfile getProfileAt(MojangAccount acc, Date date) throws IOException;

}
