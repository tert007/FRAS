package com.example.alexander.fastreading.reader.fragment.pages;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.bookparser.BookParserFactory;
import com.example.alexander.fastreading.reader.bookparser.HtmlHelper;
import com.example.alexander.fastreading.reader.bookparser.HtmlTag;
import com.example.alexander.fastreading.reader.bookparser.trash.Book;

import java.util.List;

/**
 * Created by Alexander on 22.08.2016.
 */
public class PagesFileReadingAsyncTask extends AsyncTask<String, Void, List<HtmlTag> > {

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
    protected List<HtmlTag> doInBackground(String... params) {
        String filePath = params[0];

        try {
            return BookParserFactory.getHtmlTagsText(filePath);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<HtmlTag> tagList) {
        super.onPostExecute(tagList);
        //EXCEPTION (NULL)
        progressDialog.dismiss();
        delegate.onFileReadingPostExecute(tagList);
    }
}
