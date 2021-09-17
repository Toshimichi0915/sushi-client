package net.sushiclient.client.account.responses;

public class BriefProfile {
    private String name;
    private String id;

    public BriefProfile() {
    }

    public BriefProfile(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
