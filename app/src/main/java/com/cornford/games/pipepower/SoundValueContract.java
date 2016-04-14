package com.cornford.games.pipepower;

import android.provider.BaseColumns;

/**
 * Created by kprotasov on 09.04.2016.
 */
public class SoundValueContract {

    public SoundValueContract(){}

    public static abstract class SoundValueEntry implements BaseColumns{
        public static final String TABLE_NAME = "sound_value_entry";
        public static final String COLUMN_NAME_SOUND_VALUE = "column_name_sound_value";
        public static final String COLUMN_NAME_DATE = "column_name_date";
    }

    public static abstract class DataBaseCreater{
        private static final String TYPE_LONG = " LONG";
        private static final String TYPE_TEXT = " TEXT";
        private static final String COMMA_SEP = ",";
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + SoundValueEntry.TABLE_NAME + " (" +
                        SoundValueEntry._ID + " INTEGER_PRIMARY_KEY," +
                        SoundValueEntry.COLUMN_NAME_SOUND_VALUE + TYPE_TEXT + COMMA_SEP +
                        SoundValueEntry.COLUMN_NAME_DATE + TYPE_LONG + " )";
        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + SoundValueEntry.TABLE_NAME;
    }

}
