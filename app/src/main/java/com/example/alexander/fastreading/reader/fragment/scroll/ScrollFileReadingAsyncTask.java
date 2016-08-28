package com.example.alexander.fastreading.reader.fragment.scroll;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Spanned;
import android.widget.Toast;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.bookparser.BookParserException;
import com.example.alexander.fastreading.reader.bookparser.BookParserFactory;

/**
 * Created by Alexander on 27.08.2016.
 */
public class ScrollFileReadingAsyncTask extends AsyncTask<String, Void, Spanned> {

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
    protected Spanned doInBackground(String... params) {
        String filePath = params[0];
        try {
            return BookParserFactory.getScrollSpannedText(filePath);
        } catch (BookParserException e){
            return null;
        }
    }

    @Override
    protected void onPostExecute(Spanned spannedText) {
        super.onPostExecute(spannedText);
        progressDialog.dismiss();

        if (spannedText == null){
            Toast.makeText(context, "File reading error", Toast.LENGTH_SHORT).show();
            //FIX
        }

        delegate.onFileReadingPostExecute(spannedText);
    }
}
