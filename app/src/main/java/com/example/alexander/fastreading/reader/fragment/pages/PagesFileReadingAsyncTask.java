package com.example.alexander.fastreading.reader.fragment.pages;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.alexander.fastreading.R;

import java.util.List;

/**
 * Created by Alexander on 22.08.2016.
 */
public class PagesFileReadingAsyncTask extends AsyncTask<String, Void, List<CharSequence> > {

    public PagesFileReaderAsyncTaskResponse delegate;

    private Context context;
    private ProgressDialog progressDialog;

    public PagesFileReadingAsyncTask(Context context) {
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
    protected List<CharSequence> doInBackground(String... params) {
        String filePath = params[0];

        try {
            return null;// new BookDaoFactory(context).getBookDao(filePath).getChaptersText();
        } catch (Exception e) { //
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<CharSequence> chapters) {
        super.onPostExecute(chapters);
        //EXCEPTION (NULL)
        progressDialog.dismiss();
        delegate.onFileReadingPostExecute(chapters);
    }
}
