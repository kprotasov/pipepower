package com.cornford.games.pipepower.storevalues;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.cornford.games.pipepower.SoundValueEntity;

import java.util.ArrayList;

/**
 * Created by kprotasov on 16.04.2016.
 */
public class SoundValuesAdapter extends ArrayAdapter<SoundValueEntity> {

    public SoundValuesAdapter(final Context context, final int textViewResourcesId, final ArrayList<SoundValueEntity> objects){
        super(context, textViewResourcesId, objects);
    }

    static class ViewHolder {

    }

}
