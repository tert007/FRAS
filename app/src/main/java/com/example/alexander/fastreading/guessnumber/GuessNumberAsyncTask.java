package com.example.alexander.fastreading.guessnumber;

import android.os.AsyncTask;

import java.util.Random;

/**
 * Created by Alexander on 23.07.2016.
 */
public class GuessNumberAsyncTask extends AsyncTask<Integer, String, int[]> {
        public Response delegate;

        @Override
        protected int[] doInBackground(Integer... params) {
            int numbersCount = params[0];
            int[] numbers = new int[numbersCount];

            Random random = new Random();

            for (int i = 0; i < numbersCount; i++){
                numbers[i] = random.nextInt(10);
            }

            return numbers;
        }

        @Override
        protected void onPostExecute(int[] result) {
            super.onPostExecute(result);
            delegate.onResponse(result);
        }

}
