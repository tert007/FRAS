package com.example.alexander.fastreading.reader.library.fragment.fileexplorer;

import com.example.alexander.fastreading.reader.entity.BookDescription;

/**
 * Created by Alexander on 30.09.2016.
 */
public interface ReaderFileExplorerBookAddAsyncTaskResponse {
    void bookResponse(BookDescription bookDescription, boolean bookHasBeenAdded);
}
