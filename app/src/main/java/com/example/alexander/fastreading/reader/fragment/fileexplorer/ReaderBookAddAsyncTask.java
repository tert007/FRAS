package com.example.alexander.fastreading.reader.fragment.fileexplorer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.dao.bookdao.BookDao;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.dao.bookdao.BookDaoFactory;
import com.example.alexander.fastreading.reader.dao.bookdao.BookParserException;

/**
 * Created by Alexander on 04.09.2016.
 */
    public class ReaderBookAddAsyncTask extends AsyncTask<String, Void, BookDescription> {

        public ReaderFileExplorerBookAddResponse delegate;

        private Context context;
        private ProgressDialog progressDialog;

        public ReaderBookAddAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getString(R.string.add_book_message));
            progressDialog.show();
        }

        @Override
        protected BookDescription doInBackground(String... params) {
            String filePath = params[0];
            try {
                BookDao bookDao = new BookDaoFactory(context).getBookDao(FileHelper.getFileExtension(filePath));
                return bookDao.addBook(filePath);
            } catch (BookParserException e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(BookDescription bookDescription) {
            super.onPostExecute(bookDescription);
            progressDialog.dismiss();

            delegate.bookAddPostExecute(bookDescription);
        }
    }

