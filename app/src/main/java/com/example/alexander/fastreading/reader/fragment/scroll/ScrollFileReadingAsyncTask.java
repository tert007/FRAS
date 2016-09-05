package com.example.alexander.fastreading.reader.fragment.scroll;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.bookparser.tmp.BookParserException;
import com.example.alexander.fastreading.reader.bookparser.tmp.BookParserFactory;

/**
 * Created by Alexander on 27.08.2016.
 */
public class ScrollFileReadingAsyncTask extends AsyncTask<String, Void, CharSequence> {

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
        progressDialog.setMessage(context.getString(R.string.file_opening));
        progressDialog.show();
    }

    @Override
    protected CharSequence doInBackground(String... params) {
        String filePath = params[0];
        try {
            return BookParserFactory.getScrollText(filePath);
        } catch (BookParserException e){
            return null;
        }
    }

    @Override
    protected void onPostExecute(CharSequence text) {
        super.onPostExecute(text);
        progressDialog.dismiss();
        delegate.onFileReadingPostExecute(text);
    }
}
