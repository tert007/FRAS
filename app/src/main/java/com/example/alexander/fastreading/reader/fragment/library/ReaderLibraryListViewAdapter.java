package com.example.alexander.fastreading.reader.fragment.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.entity.BookDescription;

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

        BookDescription currentBookDescription = getItem(position);

        TextView textView = (TextView) convertView.findViewById(R.id.reader_library_list_view_item_title_text_view);
        textView.setText(currentBookDescription.getTitle());

        ImageView imageView = (ImageView) convertView.findViewById(R.id.reader_library_list_view_item_image_view);

        Bitmap bookCoverBitmap = BitmapFactory.decodeFile(currentBookDescription.getCoverImagePath());

        if (bookCoverBitmap == null){
            imageView.setImageResource(R.drawable.book_without_title);
        } else {
            imageView.setImageBitmap(bookCoverBitmap);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.onBookClick(getItem(position));
            }
        });

        return convertView;
    }


}
