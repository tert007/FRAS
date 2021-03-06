package com.example.alexander.fastreading.shulte;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.app.SettingsManager;

import java.util.List;

/**
 * Created by Alexander on 26.07.2016.
 */
public class ShulteGridAdapter extends ArrayAdapter<String> {

    public TextViewOnTouchListener delegate;

    public ShulteGridAdapter(Context context, int textViewResourceId, List<String> items) {
        super(context, textViewResourceId, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.shulte_main_grid_item, parent, false);
        }

        final TextView textView = (TextView) convertView.findViewById(R.id.shulte_grid_item_text_view);

        switch (SettingsManager.getShulteComplexity()) {
            case 5:
                textView.setTextSize(35);
                break;
            case 6:
                textView.setTextSize(30);
                break;
            default:
                textView.setTextSize(25);
                break;
        }

        if (SettingsManager.isShulteColored()) {
            textView.setTextColor(ShulteColourGenerator.getColour());
        }
        textView.setText(getItem(position));

        if (! SettingsManager.isShulteEyeMode()) {
            textView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return delegate.textViewOnTouch(textView, motionEvent);
                }
            });
        }

        return convertView;
    }
}