package net.sushiclient.client.events.input;

public class KeyPressEvent extends KeyEvent {

    private final char key;

    public KeyPressEvent(int keyCode, char key) {
        super(keyCode);
        this.key = key;
    }

    public char getKey() {
        return key;
    }
}
