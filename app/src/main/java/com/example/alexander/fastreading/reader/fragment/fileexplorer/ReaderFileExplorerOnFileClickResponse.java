package com.example.alexander.fastreading.reader.fragment.fileexplorer;

import java.io.File;

/**
 * Created by Alexander on 03.08.2016.
 *
 */
public interface ReaderFileExplorerOnFileClickResponse {
    //Делегирует нажатие на файл или папку из адаптера во фрагмент
    void onFileClick(File file);
}
