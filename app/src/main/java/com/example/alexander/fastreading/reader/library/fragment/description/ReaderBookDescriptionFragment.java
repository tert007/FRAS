package com.example.alexander.fastreading.reader.library.fragment.description;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

    private TextView bookTitleResultTextView;
    private TextView bookAuthorResultTextView;
    private TextView bookLanguageResultTextView;
    private TextView bookFilePathResultTextView;

    private TextView bookTitleTextView;
    private TextView bookAuthorTextView;
    private TextView bookLanguageTextView;

    private ImageView bookCoverImageView;

    private Button startPages;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reader_book_description_fragment, container, false);

        bookTitleResultTextView = (TextView) view.findViewById(R.id.reader_book_description_title_result_text_view);
        bookTitleTextView = (TextView) view.findViewById(R.id.reader_book_description_title_text_view);

        bookAuthorResultTextView = (TextView) view.findViewById(R.id.reader_book_description_author_result_text_view);
        bookAuthorTextView = (TextView) view.findViewById(R.id.reader_book_description_author_text_view);

        bookLanguageResultTextView = (TextView) view.findViewById(R.id.reader_book_description_language_result_text_view);
        bookLanguageTextView = (TextView) view.findViewById(R.id.reader_book_description_language_text_view);

        bookFilePathResultTextView = (TextView) view.findViewById(R.id.reader_book_description_book_path_result_text_view);

        bookCoverImageView = (ImageView) view.findViewById(R.id.reader_book_description_cover_image_view);

        startPages  = (Button) view.findViewById(R.id.reader_book_description_pages_reading_button);

        final BookDescription bookDescription = (BookDescription) getArguments().getParcelable("book_description");

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(bookDescription.getTitle());

        if (bookDescription.getType().equals(FileHelper.EPUB) || bookDescription.getType().equals(FileHelper.FB2) ) {

            Bitmap bookCoverBitmap = BitmapFactory.decodeFile(bookDescription.getCoverImagePath());

            if (bookCoverBitmap == null){
                bookCoverImageView.setImageResource(R.drawable.book_without_title);
            } else {
                bookCoverImageView.setImageBitmap(bookCoverBitmap);
            }

            bookTitleResultTextView.setText(bookDescription.getTitle());
            bookAuthorResultTextView.setText(bookDescription.getAuthor());
            bookLanguageResultTextView.setText(bookDescription.getLanguage());

        } else {
            //TXT File
            bookCoverImageView.setImageResource(R.drawable.book_without_title);

            bookTitleResultTextView.setVisibility(View.GONE);
            bookAuthorResultTextView.setVisibility(View.GONE);
            bookLanguageResultTextView.setVisibility(View.GONE);
            bookTitleTextView.setVisibility(View.GONE);
            bookAuthorTextView.setVisibility(View.GONE);
            bookLanguageTextView.setVisibility(View.GONE);
        }

        bookFilePathResultTextView.setText(bookDescription.getFilePath());

        startPages.setOnClickListener(new View.OnClickListener() {
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