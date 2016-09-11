package com.example.alexander.fastreading.reader.fragment.description;

import com.example.alexander.fastreading.reader.entity.BookDescription;

/**
 * Created by Alexander on 19.08.2016.
 */
public interface ReaderReadBookResponse {
    void onReadBookClick(BookDescription bookDescription, boolean itsScrollReading, boolean itsFastReading);
}
