package com.example.alexander.fastreading.reader.reader;

import com.example.alexander.fastreading.reader.entity.BookContent;

import java.util.List;

/**
 * Created by Alexander on 22.08.2016.
 */
public interface FileReaderAsyncTaskResponse {
    void onFileReadingPostExecute(BookContent bookContent);
}
