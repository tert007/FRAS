package com.example.alexander.fastreading.guessnumber;

import android.os.AsyncTask;

import java.util.Random;

/**
 * Created by Alexander on 23.07.2016.
 */
public class GuessNumberAsyncTask extends AsyncTask<Integer, String, String> {
        public Response delegate;
        //public TextView textView;

        @Override
        protected String doInBackground(Integer... params) {
            int numbersCount = params[0];
            int[] numbers = new int[numbersCount];

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Random random = new Random();

            for (int i = 0; i < numbersCount; i++){
                numbers[i] = random.nextInt(10);
                publishProgress(arrayToString(numbers));
            }

            return arrayToString(numbers);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            delegate.onResponse(result);
        }

        /*
        @Override
        protected void onProgressUpdate(String... values) {

            super.onProgressUpdate(values);
            textView.setText(values[0]);

        }
        */

    private String arrayToString(int[] numbers) {
        String string = new String();
        for (int i = 0; i < numbers.length; i++){
            string += String.valueOf(numbers[i]);
        }

        return string;
    }
}
