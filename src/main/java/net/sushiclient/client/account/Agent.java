package net.sushiclient.client.account;

public class Agent {
    private String name;
    private int version;

    public Agent() {
    }

    public Agent(String name, int version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }
}
