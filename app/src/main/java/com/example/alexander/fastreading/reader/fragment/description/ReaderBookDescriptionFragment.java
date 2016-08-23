package com.example.alexander.fastreading.reader.fragment.description;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.BookReaderHelper;
import com.example.alexander.fastreading.reader.book.Book;
import com.example.alexander.fastreading.reader.book.EpubBook;
import com.example.alexander.fastreading.reader.bookparser.BookParserException;

import java.io.File;

/**
 * Created by Alexander on 07.08.2016.
 */
public class ReaderBookDescriptionFragment extends Fragment {

    public ReaderScrollReadBookResponse scrollDelegate;
    public ReaderPagesReadBookResponse pagesDelegate;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reader_book_description_fragment, container, false);

        String filePath = getArguments().getString("file_path");

        /*
        try {
            FileHelper.unZip(filePath, getActivity().getApplicationInfo().dataDir + File.separator + "test1");
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("ER", e.getMessage());
        }

        */

        try {
            final Book book = BookReaderHelper.getBook(new File(filePath));

            ImageView coverImageView = (ImageView) view.findViewById(R.id.reader_book_description_cover_image_view);
            TextView bookPathTextView = (TextView) view.findViewById(R.id.reader_book_description_book_path_text_view);

            Button startReadingButton = (Button) view.findViewById(R.id.stard_read_button);
            Button button = (Button) view.findViewById(R.id.stard_pages_read_button);

            if (book instanceof EpubBook) {
                EpubBook epubBook = (EpubBook) book;

                Bitmap bitmap = epubBook.getCover();
                if (bitmap == null){
                    coverImageView.setImageResource(R.drawable.book_without_title);
                } else {
                    coverImageView.setImageBitmap(bitmap);
                }

                TextView bookTitleTextView = (TextView) view.findViewById(R.id.reader_book_description_title_text_view);
                TextView bookAuthorTextView = (TextView) view.findViewById(R.id.reader_book_description_author_text_view);
                TextView bookLanguageTextView = (TextView) view.findViewById(R.id.reader_book_description_language_text_view);

                startReadingButton.setVisibility(View.INVISIBLE);

                bookTitleTextView.setText(getString(R.string.book_title) + " " +epubBook.getTitle());
                bookAuthorTextView.setText(getString(R.string.author) + " " +epubBook.getAuthor());
                bookLanguageTextView.setText(getString(R.string.language) + " " +epubBook.getLanguage());

                startReadingButton.setVisibility(View.INVISIBLE);
                startReadingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scrollDelegate.onScrollReadBookClick(book);
                    }
                });
            } else {
                //TXT File
                coverImageView.setImageResource(R.drawable.book_without_title);

                startReadingButton.setVisibility(View.VISIBLE);
                startReadingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scrollDelegate.onScrollReadBookClick(book);
                    }
                });

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pagesDelegate.onPagesReadBookClick(book);
                    }
                });
            }

            //Для любой книги
            bookPathTextView.setText(getString(R.string.file_path) + " " + filePath);
        } catch (BookParserException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return view;
    }
}