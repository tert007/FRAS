package com.example.alexander.fastreading.shulte;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.alexander.fastreading.R;

import java.util.List;

/**
 * Created by Alexander on 26.07.2016.
 */
public class GridAdapter extends ArrayAdapter<String> {
    public View.OnClickListener delegate;

    public GridAdapter(Context context, int textViewResourceId, List<String> items) {
        super(context, textViewResourceId, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.shulte_main_grid_item, parent, false);
        }

        //SquareLayout squareLayout = (SquareLayout) convertView.findViewById(R.id.squre);

        TextView textView = (TextView) convertView.findViewById(R.id.shulte_grid_item_text_view);
        textView.setText(getItem(position));

        return (convertView);
    }
}