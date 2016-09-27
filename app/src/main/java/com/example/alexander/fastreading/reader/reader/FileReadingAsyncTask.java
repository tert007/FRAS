package com.example.alexander.fastreading.reader.reader;

import android.content.Context;
import android.os.AsyncTask;

import com.example.alexander.fastreading.reader.dao.bookdao.BookDao;
import com.example.alexander.fastreading.reader.dao.bookdao.BookDaoFactory;
import com.example.alexander.fastreading.reader.dao.bookdao.BookParserException;
import com.example.alexander.fastreading.reader.entity.BookDescription;

import java.util.List;

/**
 * Created by Alexander on 22.08.2016.
 */
public class FileReadingAsyncTask extends AsyncTask<BookDescription, Void, List<CharSequence>> {

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
    protected List<CharSequence> doInBackground(BookDescription... params) {
        BookDescription bookDescription = params[0];

        try {
            BookDao bookDao = new BookDaoFactory(context).getBookDao(bookDescription.getType());
            return bookDao.getChaptersText(bookDescription);
        } catch (BookParserException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<CharSequence> chapters) {
        super.onPostExecute(chapters);
        //progressDialog.dismiss();

        delegate.onFileReadingPostExecute(chapters);
    }
}
