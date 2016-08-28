package com.example.alexander.fastreading.reader.fragment.pages;

import com.example.alexander.fastreading.reader.bookparser.HtmlTag;

import java.util.List;

/**
 * Created by Alexander on 22.08.2016.
 */
public interface PagesFileReaderAsyncTaskResponse {
    //Делегирет лист тегов из ScrollFileReadingAsyncTask в PagesFragment
    void onFileReadingPostExecute(List<HtmlTag> tagList);
}
