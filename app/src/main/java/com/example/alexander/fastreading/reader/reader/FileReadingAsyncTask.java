package com.example.alexander.fastreading.reader.reader;

import android.content.Context;
import android.os.AsyncTask;

import com.example.alexander.fastreading.reader.dao.BookController;
import com.example.alexander.fastreading.reader.dao.bookdao.BookParserException;
import com.example.alexander.fastreading.reader.entity.BookContent;
import com.example.alexander.fastreading.reader.entity.BookDescription;

import java.util.List;

/**
 * Created by Alexander on 22.08.2016.
 */
public class FileReadingAsyncTask extends AsyncTask<BookDescription, Void, BookContent> {

    public FileReaderAsyncTaskResponse delegate;

    private Context context;
    //private ProgressDialog progressDialog;

    public FileReadingAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //progressDialog = new ProgressDialog(context);
        //progressDialog.setMessage(context.getString(R.string.file_opening_message));
        //progressDialog.show();
    }

    @Override
    protected BookContent doInBackground(BookDescription... params) {
        BookDescription bookDescription = params[0];

        try {
            BookController bookController = new BookController(context);
            return bookController.getBookContent(bookDescription);
        } catch (BookParserException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(BookContent bookContent) {
        super.onPostExecute(bookContent);
        //progressDialog.dismiss();

        delegate.onFileReadingPostExecute(bookContent);
    }
}
