package com.example.alexander.fastreading.reader.fragment.library;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.BookDescription;

import java.util.List;

/**
 * Created by Alexander on 04.09.2016.
 */
public class ReaderLibraryListViewAdapter extends ArrayAdapter<BookDescription> {

    public ReaderLibraryOnBookClickResponse delegate;

    public ReaderLibraryListViewAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ReaderLibraryListViewAdapter(Context context, int resource, List<BookDescription> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.reader_library_list_view_item, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.reader_library_list_view_item_title_text_view);
        textView.setText(getItem(position).getFilePath());
        //ImageView imageView = (ImageView) convertView.findViewById(R.id.reader_file_explorer_image_view);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.onBookClick(getItem(position));
            }
        });

        return convertView;
    }


}
