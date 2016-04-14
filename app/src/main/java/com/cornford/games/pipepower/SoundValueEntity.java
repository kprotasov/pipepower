package com.cornford.games.pipepower;

/**
 * Created by kprotasov on 12.04.2016.
 */
public class SoundValueEntity {

    private long timestamp;
    private String value;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
}
