package com.example.alexander.fastreading.reader.fragment.pages;

/**
 * Created by Alexander on 22.08.2016.
 */
public interface FileReaderAsyncTaskResponse {
    //Делегирет загруженный текст из FileReadingAsyncTask в PagesFragment
    void onFileReadingPostExecute(String response);
}
