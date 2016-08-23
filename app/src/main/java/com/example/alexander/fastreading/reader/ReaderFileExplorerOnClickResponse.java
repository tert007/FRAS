package com.example.alexander.fastreading.reader;

import java.io.File;

/**
 * Created by Alexander on 03.08.2016.
 *
 */
public interface ReaderFileExplorerOnClickResponse {
    //Делегирует нажатие на файл или папку из адаптера во фрагмент
    void fileOnClick(File file);
}
