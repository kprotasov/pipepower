package com.cornford.games.pipepower.storevalues;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.cornford.games.pipepower.R;
import com.cornford.games.pipepower.SoundValueContract;
import com.cornford.games.pipepower.SoundValueDbHelper;
import com.cornford.games.pipepower.SoundValueEntity;

import java.util.ArrayList;

/**
 * Created by kprotasov on 18.04.2016.
 */
public class SoundValuesActivity extends Activity {

    private static final int MAX_COUNT = 100;

    private ArrayList<SoundValueEntity> soundList = new ArrayList<>();
    private SoundValuesAdapter adapter;
    private ListView listView;

    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound_list_view);

        listView = (ListView) findViewById(R.id.listView);
        adapter = new SoundValuesAdapter(this, R.layout.sound_value_item, soundList);
        listView.setAdapter(adapter);
        readFromDatabase();
    }

    private void readFromDatabase(){
        soundList.clear();
        final SoundValueDbHelper dbHelper = new SoundValueDbHelper(this);
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        final String[] projection = {SoundValueContract.SoundValueEntry._ID,
                SoundValueContract.SoundValueEntry.COLUMN_NAME_DATE,
                SoundValueContract.SoundValueEntry.COLUMN_NAME_SOUND_VALUE};
        final String sorting = SoundValueContract.SoundValueEntry.COLUMN_NAME_DATE + " DESC";
        final Cursor cursor = db.query(SoundValueContract.SoundValueEntry.TABLE_NAME,
                projection, null, null, null, null, sorting);
        cursor.moveToFirst();
        int count = 0;
        while(!cursor.isAfterLast()){
            final SoundValueEntity entity = cursorToSoundValueEntity(cursor);
            if (count <= MAX_COUNT) {
                soundList.add(entity);
            }else{
                dbHelper.deleteRow(db, entity);
            }
            count ++;
            cursor.moveToNext();
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private SoundValueEntity cursorToSoundValueEntity(final Cursor cursor){
        SoundValueEntity entity = new SoundValueEntity();
        final long timestamp = cursor.getLong(cursor.getColumnIndex(SoundValueContract.SoundValueEntry.COLUMN_NAME_DATE));
        final int value = cursor.getInt(cursor.getColumnIndex(SoundValueContract.SoundValueEntry.COLUMN_NAME_SOUND_VALUE));
        entity.setTimestamp(timestamp);
        entity.setValue(value);
        Log.v("SoundValueEntity", "read new value timestamp " + timestamp + " value " + value);
        return entity;
    }

}
