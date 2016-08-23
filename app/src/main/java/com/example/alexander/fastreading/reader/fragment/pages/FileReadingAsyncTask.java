package com.example.alexander.fastreading.reader.fragment.pages;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.FileHelper;

import java.io.File;
import java.io.IOException;

/**
 * Created by Alexander on 22.08.2016.
 */
public class FileReadingAsyncTask extends AsyncTask<String, Void, String> {

    public FileReaderAsyncTaskResponse delegate;

    private Context context;
    private ProgressDialog progressDialog;

    public FileReadingAsyncTask(Context context) {
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
    protected String doInBackground(String... params) {
        String filePath = params[0];
        String textFromFile;

        try {
            textFromFile = FileHelper.getTextFromFile(new File(filePath));
        } catch (IOException e) {
            return null;
        }

        return textFromFile;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressDialog.dismiss();
        delegate.onFileReadingPostExecute(s);
    }

}
