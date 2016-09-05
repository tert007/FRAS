package com.example.alexander.fastreading.reader.fragment.fileexplorer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.alexander.fastreading.reader.bookparser.BookDescription;
import com.example.alexander.fastreading.reader.bookparser.tmp.BookParserException;
import com.example.alexander.fastreading.reader.bookparser.tmp.BookParserFactory;
import com.example.alexander.fastreading.reader.dao.BookHuiVRotDao;

/**
 * Created by Alexander on 04.09.2016.
 */
    public class BookAddAsyncTask extends AsyncTask<String, Void, Long> {

        private Context context;
        private ProgressDialog progressDialog;

        public BookAddAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Black Sabbath: what can't you see,i'm no a stranger to love");
            progressDialog.show();
        }

        @Override
        protected Long doInBackground(String... params) {
            String filePath = params[0];
            try {
               return new BookHuiVRotDao(context).addBook(filePath);
            } catch (BookParserException e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(Long id) {
            super.onPostExecute(id);
            progressDialog.dismiss();
        }
    }

