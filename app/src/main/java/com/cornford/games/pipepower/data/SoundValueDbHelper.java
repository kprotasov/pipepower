package com.cornford.games.pipepower.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kprotasov on 09.04.2016.
 */
public class SoundValueDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SoundValueReader.db";

    public SoundValueDbHelper(final Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(final SQLiteDatabase db){
        db.execSQL(SoundValueContract.DataBaseCreator.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion){
        db.execSQL(SoundValueContract.DataBaseCreator.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(final SQLiteDatabase db, final int oldVersion, final int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }

    public void deleteRow(final SQLiteDatabase db, final SoundValueEntity entity){
        final String[] whereArgs = {String.valueOf(entity.getTimestamp())};
        db.delete(SoundValueContract.SoundValueEntry.TABLE_NAME, SoundValueContract.SoundValueEntry.COLUMN_NAME_DATE + "=?", whereArgs);
    }

}
