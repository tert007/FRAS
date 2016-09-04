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
import com.example.alexander.fastreading.reader.bookparser.trash.BookReaderHelper;
import com.example.alexander.fastreading.reader.bookparser.trash.Book;
import com.example.alexander.fastreading.reader.bookparser.trash.EpubBook;
import com.example.alexander.fastreading.reader.bookparser.BookParserException;

import java.io.File;

/**
 * Created by Alexander on 07.08.2016.
 */
public class ReaderBookDescriptionFragment extends Fragment {

    public ReaderScrollReadBookResponse scrollDelegate;
    public ReaderPagesReadBookResponse pagesDelegate;

    private TextView bookTitleResultTextView;
    private TextView bookAuthorResultTextView;
    private TextView bookLanguageResultTextView;
    private TextView bookFilePathResultTextView;

    private TextView bookTitleTextView;
    private TextView bookAuthorTextView;
    private TextView bookLanguageTextView;

    private ImageView bookCoverImageView;

    private Button startScroll;
    private Button startFastScroll;
    private Button startPages;
    private Button startFastPages;

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

        startScroll = (Button) view.findViewById(R.id.reader_book_description_scroll_reading_button);
        startFastScroll = (Button) view.findViewById(R.id.reader_book_description_fast_scroll_reading_button);
        startPages  = (Button) view.findViewById(R.id.reader_book_description_pages_reading_button);
        startFastPages  = (Button) view.findViewById(R.id.reader_book_description_fast_pages_reading_button);

        //Далее будет по другому
        String filePath = getArguments().getString("file_path");

        try {
            final Book book = BookReaderHelper.getBook(new File(filePath));

            if (book instanceof EpubBook) {
                EpubBook epubBook = (EpubBook) book;

                Bitmap bitmap = epubBook.getCover();

                if (bitmap == null){
                    bookCoverImageView.setImageResource(R.drawable.book_without_title);
                } else {
                    bookCoverImageView.setImageBitmap(bitmap);
                }

                bookTitleResultTextView.setText(((EpubBook) book).getTitle());
                bookAuthorResultTextView.setText(((EpubBook) book).getAuthor());
                bookLanguageResultTextView.setText(((EpubBook) book).getLanguage());


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

            bookFilePathResultTextView.setText(book.getFilePath());

            startScroll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scrollDelegate.onScrollReadBookClick(book);
                }
            });
            startFastScroll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scrollDelegate.onScrollReadBookClick(book);
                }
            });
            startPages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pagesDelegate.onPagesReadBookClick(book);
                }
            });
            startFastPages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pagesDelegate.onPagesReadBookClick(book);
                }
            });

        } catch (BookParserException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return view;
    }
}