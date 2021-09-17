package net.sushiclient.client.account.responses;

public class NameHistory {
    private String name;
    private long changedToAt;

    public NameHistory() {
    }

    public NameHistory(String name, long changedToAt) {
        this.name = name;
        this.changedToAt = changedToAt;
    }

    public String getName() {
        return name;
    }

    public long getChangedToAt() {
        return changedToAt;
    }
}
