package net.sushiclient.client;

import java.util.List;

public interface Profiles {

    List<String> getAll();

    Profile load(String name);

    Profile clone(String old, String name);

    boolean remove(String name);

    String getName(Profile profile);
}
