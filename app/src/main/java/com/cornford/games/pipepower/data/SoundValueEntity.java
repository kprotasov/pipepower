package com.cornford.games.pipepower.data;

/**
 * Created by kprotasov on 12.04.2016.
 */
public class SoundValueEntity {

    private long timestamp;
    private int value;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public int getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }
}
