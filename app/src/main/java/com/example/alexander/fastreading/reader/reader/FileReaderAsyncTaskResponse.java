package com.example.alexander.fastreading.reader.reader;

import java.util.List;

/**
 * Created by Alexander on 22.08.2016.
 */
public interface FileReaderAsyncTaskResponse {
    //Делегирет лист глав книги из ScrollFileReadingAsyncTask в Activity
    void onFileReadingPostExecute(List<CharSequence> chapters);
}
