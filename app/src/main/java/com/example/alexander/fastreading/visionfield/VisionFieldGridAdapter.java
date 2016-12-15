package com.example.alexander.fastreading.visionfield;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.shulte.TextViewOnTouchListener;

/**
 * Created by Alexander on 26.07.2016.
 */
public class VisionFieldGridAdapter extends ArrayAdapter<String> {

    private static final int CENTER_ITEM_INDEX = 31;

    public TextViewOnTouchListener delegate;

    public VisionFieldGridAdapter(Context context, int textViewResourceId, String[] items) {
        super(context, textViewResourceId, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.vision_field_grid_view_item, parent, false);
        }

        final TextView textView = (TextView) convertView.findViewById(R.id.vision_field_grid_view_item_text_view);
        if (position == CENTER_ITEM_INDEX) {
            textView.setText("•");
            //textView.setBackgroundResource(R.drawable.vision_field_center_point_background);
        } else {
            textView.setText(getItem(position));
        }

        return convertView;
    }
}