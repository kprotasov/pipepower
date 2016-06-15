package com.cornford.games.pipepower;

/**
 * Created by kprotasov on 15.06.2016.
 */
public class Constants {

    private static final long MILLISECONDS = 1000;

    public static final long RECORDING_TIME_FREE = 15 * MILLISECONDS;
    public static final long RECORDING_TIME_PAID = 60 * MILLISECONDS;

    private static final int APP_TYPE_FREE = 1001;
    private static final int APP_TYPE_PAID = 1002;

    private static final int CURRENT_APP_TYPE = APP_TYPE_FREE;

    public static final long getRecordingLength() {
        return CURRENT_APP_TYPE == APP_TYPE_FREE ? RECORDING_TIME_FREE : RECORDING_TIME_PAID;
    }

}
