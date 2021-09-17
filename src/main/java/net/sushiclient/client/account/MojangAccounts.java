package net.sushiclient.client.account;

import java.util.List;

public interface MojangAccounts {

    List<MojangAccount> getAll();

    boolean auth(MojangAccount acc);

    void add(MojangAccount acc);

    boolean remove(MojangAccount acc);

    void refreshAll();

    void load();

    void save();

    AccountStatus getAccountStatus(MojangAccount acc);
}
