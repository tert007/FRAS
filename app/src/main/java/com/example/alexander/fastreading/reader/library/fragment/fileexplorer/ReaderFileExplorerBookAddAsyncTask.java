package com.example.alexander.fastreading.reader.library.fragment.fileexplorer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.dao.BookController;
import com.example.alexander.fastreading.reader.dao.bookdao.BookHasBeenAddedException;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.dao.bookdao.BookParserException;

/**
 * Created by Alexander on 04.09.2016.
 */
    public class ReaderFileExplorerBookAddAsyncTask extends AsyncTask<String, Void, BookDescription> {

        public ReaderFileExplorerBookAddAsyncTaskResponse delegate;

        private Context context;
        private ProgressDialog progressDialog;

        private boolean bookHasBeenAdded;

        public ReaderFileExplorerBookAddAsyncTask(Context context) {
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
            BookController bookController = new BookController(context);
            try {
                return bookController.addBook(filePath);
            } catch (BookHasBeenAddedException e){
                bookHasBeenAdded = true;
                return bookController.findBookDescription(filePath);
            } catch (BookParserException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(BookDescription bookDescription) {
            super.onPostExecute(bookDescription);
            progressDialog.dismiss();

            delegate.bookResponse(bookDescription, bookHasBeenAdded);
        }
    }

