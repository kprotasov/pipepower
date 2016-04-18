package com.cornford.games.pipepower.storevalues;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cornford.games.pipepower.MainActivity;
import com.cornford.games.pipepower.R;
import com.cornford.games.pipepower.SoundValueEntity;

import java.util.ArrayList;
import java.util.Date;
import static com.cornford.games.pipepower.MainActivity.*;

/**
 * Created by kprotasov on 16.04.2016.
 */
public class SoundValuesAdapter extends ArrayAdapter<SoundValueEntity> {

    private final LayoutInflater inflater;
    private final int textViewResourcesId;

    public SoundValuesAdapter(final Context context, final int textViewResourcesId, final ArrayList<SoundValueEntity> objects){
        super(context, textViewResourcesId, objects);
        this.inflater = LayoutInflater.from(context);
        this.textViewResourcesId = textViewResourcesId;
    }

    static class ViewHolder {
        TextView value;
        TextView levelText;
        TextView date;
    }
    ViewHolder holder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null){
            convertView = inflater.inflate(textViewResourcesId, parent, false);
            holder = new ViewHolder();
            holder.value = (TextView)convertView.findViewById(R.id.value);
            holder.levelText = (TextView)convertView.findViewById(R.id.levelText);
            holder.date = (TextView)convertView.findViewById(R.id.date);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        final SoundValueEntity item = getItem(position);
        holder.value.setText(String.valueOf(item.getValue()));
        setWarningLevel(item.getValue(), holder.value, holder.levelText);
        String dateString= DateFormat.format("dd/mm/yyyy", new Date(item.getTimestamp())).toString();
        holder.date.setText(dateString);
        return convertView;
    }

    public void setWarningLevel(int dbLevel, final TextView valueTextView, final TextView levelTextView){
        if (dbLevel >= 0 && dbLevel <= DB_LEVEL_LOW){
            levelTextView.setText(R.string.low_level);
            valueTextView.setTextColor(getContext().getResources().getColor(R.color.low_level_color));
            levelTextView.setTextColor(getContext().getResources().getColor(R.color.low_level_color));
        }else if (dbLevel > DB_LEVEL_LOW && dbLevel <= DB_LEVEL_MIDDLE){
            levelTextView.setText(R.string.middle_level);
            valueTextView.setTextColor(getContext().getResources().getColor(R.color.middle_level_color));
            levelTextView.setTextColor(getContext().getResources().getColor(R.color.middle_level_color));
        }else if (dbLevel > DB_LEVEL_MIDDLE && dbLevel <= DB_LEVEL_HIGH){
            valueTextView.setTextColor(getContext().getResources().getColor(R.color.low_high_level_color));
            levelTextView.setTextColor(getContext().getResources().getColor(R.color.low_high_level_color));
            levelTextView.setText(R.string.low_high_level);
        }else if (dbLevel > DB_LEVEL_HIGH && dbLevel < DB_LEVEL_VERY_HIGH) {
            valueTextView.setTextColor(getContext().getResources().getColor(R.color.high_level_color));
            levelTextView.setTextColor(getContext().getResources().getColor(R.color.high_level_color));
            levelTextView.setText(R.string.high_level);
        }else if (dbLevel > DB_LEVEL_VERY_HIGH){
            valueTextView.setTextColor(getContext().getResources().getColor(R.color.very_high_color));
            levelTextView.setTextColor(getContext().getResources().getColor(R.color.very_high_color));
            levelTextView.setText(R.string.very_high_level);
        }
    }
}
