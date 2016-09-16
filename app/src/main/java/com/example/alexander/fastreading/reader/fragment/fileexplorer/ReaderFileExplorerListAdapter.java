package com.example.alexander.fastreading.reader.fragment.fileexplorer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.FileHelper;

import java.io.File;
import java.util.List;

/**
 * Created by Alexander on 03.08.2016.
 * Адаптер, позволяет создать список файлов на устройве
 */
public class ReaderFileExplorerListAdapter extends ArrayAdapter<File> {

    public ReaderFileExplorerOnFileClickResponse delegate;

    public ReaderFileExplorerListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ReaderFileExplorerListAdapter(Context context, int resource, List<File> items) {
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
                case FileHelper.FB2:
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
                delegate.onFileClick(getItem(position));
            }
        });

        return convertView;
    }


}