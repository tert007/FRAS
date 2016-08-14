package com.example.alexander.fastreading.reader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexander.fastreading.R;

import java.io.File;

/**
 * Created by Alexander on 03.08.2016.
 * Адаптер, позволяет создать список файлов на устройве
 */
public class FileExplorerListAdapter extends ArrayAdapter<File> {

    public ReaderFileExplorerOnClickResponse delegate;

    public FileExplorerListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public FileExplorerListAdapter(Context context, int resource, File[] items) {
        super(context, resource, items);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.reader_file_explorer_list_item, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.reader_file_explorer_image_view);

        String fileExtension = FileHelper.getFileExtension(getItem(position));
        if (fileExtension != null){
            switch (fileExtension){
                case FileHelper.TXT:
                    imageView.setImageResource(R.drawable.text_document);
                    break;
                case FileHelper.EPUB:
                    imageView.setImageResource(R.drawable.book);
                    break;
            }
        } else {
            imageView.setImageResource(R.drawable.folder);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.reader_file_explorer_text_view);
        textView.setText(getItem(position).getName());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.fileOnClick(getItem(position));
            }
        });

        return convertView;
    }


}