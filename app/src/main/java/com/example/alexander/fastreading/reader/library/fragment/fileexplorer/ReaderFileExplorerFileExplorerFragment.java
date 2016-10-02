package com.example.alexander.fastreading.reader.library.fragment.fileexplorer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.dao.BookController;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.library.ReaderBookDescriptionResponse;

import java.io.File;
import java.util.List;

/**
 * Created by Alexander on 03.08.2016.
 */
public class ReaderFileExplorerFileExplorerFragment extends Fragment implements
        ReaderFileExplorerBookAddAsyncTaskResponse,
        ReaderFileExplorerOnFileClickResponse,
        ReaderBookDescriptionResponse {

    public ReaderBookDescriptionResponse delegate;

    private BookController bookController;

    private ReaderFileExplorerListAdapter adapter;
    private ListView listView;

    private File path = new File("/");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reader_file_explorer_fragment, container, false);

        bookController = new BookController(getActivity());

        listView = (ListView) view.findViewById(R.id.reader_file_explorer_list_view);

        listViewUpdate();

        return view;
    }

    public boolean onBackPressed() {
        //Когда файл последний возвращает true
        if (path.getParentFile() == null){
            return true;
        } else {
            path = path.getParentFile();

            listViewUpdate();

            return false;
        }
    }

    @Override
    public void onFileClick(File file) {
        if (file.isDirectory()){
            path = file;
            listViewUpdate();
        } else {
            ReaderFileExplorerBookAddAsyncTask bookAddAsyncTask = new ReaderFileExplorerBookAddAsyncTask(getActivity());
            bookAddAsyncTask.delegate = this;

            bookAddAsyncTask.execute(file.getPath());
        }
    }

    //После добавления книги:
    //
    //null - Ошибка
    //bookHasBeenAdded - true -- уже такая была
    @Override
    public void bookResponse(final BookDescription bookDescription, boolean bookHasBeenAdded) {
        if (bookDescription == null) {
            Snackbar.make(getView(), R.string.file_reading_error, Snackbar.LENGTH_LONG).show();
            return;
        }

        if (bookHasBeenAdded) {
            Snackbar.make(getView(), R.string.book_has_been_added, Snackbar.LENGTH_LONG).
                    setAction(R.string.open_book, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            delegate.bookResponse(bookDescription);
                        }
                    }).
                    show();
            return;
        }

        delegate.bookResponse(bookDescription);
    }

    private void listViewUpdate(){
        List<File> files = FileHelper.readerFileFilter(path.listFiles());

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(path.getPath());

        adapter = new ReaderFileExplorerListAdapter(getActivity(), R.id.reader_file_explorer_text_view, files);
        adapter.delegate = this;

        listView.setAdapter(adapter);
    }

    //Добавление книги, а также нажатие на ту что уже добавлена
    @Override
    public void bookResponse(BookDescription bookDescription) {
        delegate.bookResponse(bookDescription);
    }
}
