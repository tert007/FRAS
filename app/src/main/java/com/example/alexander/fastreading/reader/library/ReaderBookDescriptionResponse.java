package com.example.alexander.fastreading.reader.library;

import com.example.alexander.fastreading.reader.entity.BookDescription;

/**
 * Created by Alexander on 24.09.2016.
 */
public interface ReaderBookDescriptionResponse {
    //ПОзволяет передватаь bookDescription при создании или открытие уже созданных книг
    void bookResponse(BookDescription bookDescription);
}
