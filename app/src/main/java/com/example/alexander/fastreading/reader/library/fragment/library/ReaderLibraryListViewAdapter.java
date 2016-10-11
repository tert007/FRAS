package com.example.alexander.fastreading.reader.library.fragment.library;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.library.ReaderBookDescriptionResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Alexander on 04.09.2016.
 */
public class ReaderLibraryListViewAdapter extends ArrayAdapter<BookDescription> {

    public ReaderBookDescriptionResponse bookClickDelegate;
    public ReaderLibraryRemoveBookOnClickResponse removeBookDelegate;

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

        final BookDescription currentBookDescription = getItem(position);

        TextView textView = (TextView) convertView.findViewById(R.id.reader_library_list_view_item_title_text_view);
        textView.setText(currentBookDescription.getTitle());

        TextView progressTextView = (TextView) convertView.findViewById(R.id.reader_library__list_view_item_progress_result_text_view);
        String progress = String.valueOf(currentBookDescription.getProgress()) + '%';
        progressTextView.setText(progress);

        TextView bookCoverImageView = (TextView) convertView.findViewById(R.id.reader_library_list_view_item_cover_text_view);

        char firstLetter;
        if (currentBookDescription.getTitle().length() > 0) {
            firstLetter = currentBookDescription.getTitle().charAt(0);
        } else {
            firstLetter = '-';
        }

        GradientDrawable gradientDrawable = ((GradientDrawable)bookCoverImageView.getBackground());
        int color = ColourGenerator.getRandomColor(firstLetter);
        gradientDrawable.setColor(color);

        bookCoverImageView.setText(String.valueOf(firstLetter));

        ImageView removeImageView = (ImageView) convertView.findViewById(R.id.reader_library_list_view_item_remove_image_view);
        removeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeBookDelegate.onBookRemoveClick(currentBookDescription);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookClickDelegate.bookResponse(getItem(position));
            }
        });

        return convertView;
    }
}