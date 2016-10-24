package com.example.alexander.fastreading.reader.library.fragment.description;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.reader.ReaderActivity;

/**
 * Created by Alexander on 07.08.2016.
 */
public class ReaderBookDescriptionFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.reader_book_description_fragment, container, false);

        final View authorView = view.findViewById(R.id.reader_description_author_view);
        final View languageView = view.findViewById(R.id.reader_description_language_view);

        final TextView bookTitleResultTextView = (TextView) view.findViewById(R.id.reader_book_description_title_result_text_view);
        final TextView bookAuthorResultTextView = (TextView) authorView.findViewById(R.id.reader_book_description_author_result_text_view);
        final TextView bookLanguageResultTextView = (TextView) languageView.findViewById(R.id.reader_book_description_language_result_text_view);
        final TextView bookFilePathResultTextView = (TextView) view.findViewById(R.id.reader_book_description_book_path_result_text_view);
        final ImageView bookCoverImageView = (ImageView) view.findViewById(R.id.reader_book_description_cover_image_view);

        final Button startReadingButton = (Button) view.findViewById(R.id.reader_book_description_pages_reading_button);

        final BookDescription bookDescription = (BookDescription) getArguments().getParcelable("book_description");

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(bookDescription.getTitle());

        if (bookDescription.getType().equals(FileHelper.TXT)) {
            authorView.setVisibility(View.GONE);
            languageView.setVisibility(View.GONE);

            bookCoverImageView.setImageResource(R.drawable.reader_book_without_title);
        } else {
            Bitmap bookCoverBitmap = BitmapFactory.decodeFile(bookDescription.getCoverImagePath());

            if (bookCoverBitmap == null){
                bookCoverImageView.setImageResource(R.drawable.reader_book_without_title);
            } else {
                bookCoverImageView.setImageBitmap(bookCoverBitmap);
            }

            bookAuthorResultTextView.setText(bookDescription.getAuthor());
            bookLanguageResultTextView.setText(bookDescription.getLanguage());
        }

        bookTitleResultTextView.setText(bookDescription.getTitle());
        bookFilePathResultTextView.setText(bookDescription.getFilePath());

        startReadingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReaderActivity.class);
                intent.putExtra("book_description", bookDescription);

                startActivity(intent);
            }
        });

        return view;
    }
}