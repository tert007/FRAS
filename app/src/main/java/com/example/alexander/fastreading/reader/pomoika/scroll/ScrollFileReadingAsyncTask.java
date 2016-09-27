package com.example.alexander.fastreading.reader.pomoika.scroll;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.dao.bookdao.BookParserException;
import com.example.alexander.fastreading.reader.dao.bookdao.BookDao;
import com.example.alexander.fastreading.reader.dao.bookdao.BookDaoFactory;

/**
 * Created by Alexander on 27.08.2016.
 */
public class ScrollFileReadingAsyncTask extends AsyncTask<BookDescription, Void, CharSequence> {

    public ScrollFileReadingAsyncTaskResponse delegate;

    private Context context;
    private ProgressDialog progressDialog;

    public ScrollFileReadingAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.file_opening_message));
        progressDialog.show();
    }

    @Override
    protected CharSequence doInBackground(BookDescription... params) {
        BookDescription bookDescription = params[0];
        try {
            BookDao bookDao = new BookDaoFactory(context).getBookDao(bookDescription.getType());

            return bookDao.getScrollText(bookDescription);

        } catch (BookParserException e){
            return null;
        }
    }

    @Override
    protected void onPostExecute(CharSequence result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        delegate.fileReadingPostExecute(result);
    }
}
