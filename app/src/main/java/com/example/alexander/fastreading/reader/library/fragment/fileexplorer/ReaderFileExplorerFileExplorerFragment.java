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
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDao;
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDaoFactory;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.library.ReaderBookDescriptionResponse;

import java.io.File;
import java.util.List;

/**
 * Created by Alexander on 03.08.2016.
 */
public class ReaderFileExplorerFileExplorerFragment extends Fragment implements
        ReaderFileExplorerOnFileClickResponse, ReaderBookDescriptionResponse {

    public ReaderBookDescriptionResponse delegate;

    private ReaderFileExplorerListAdapter adapter;
    private ListView listView;

    private File path = new File("/");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reader_file_explorer_fragment, container, false);

        listView = (ListView) view.findViewById(R.id.reader_file_explorer_list_view);

        update();

        return view;
    }

    public boolean onBackPressed() {
        //Когда файл последний возвращает true
        if (path.getParentFile() == null){
            return true;
        } else {
            path = path.getParentFile();

            update();

            return false;
        }
    }

    @Override
    public void onFileClick(File file) {
        if (file.isDirectory()){
            path = file;
            update();
        } else {
            BookDescriptionDao bookDescriptionDao = BookDescriptionDaoFactory.getDaoFactory(getActivity()).getBookDescriptionDao();
            final BookDescription bookDescription = bookDescriptionDao.findBookDescription(file.getPath());

            if (bookDescription == null) {
                ReaderFileExplorerBookAddAsyncTask bookAddAsyncTask = new ReaderFileExplorerBookAddAsyncTask(getActivity());
                bookAddAsyncTask.delegate = this;

                bookAddAsyncTask.execute(file.getPath());
            } else {
                Snackbar.make(getView(), getString(R.string.book_in_library), Snackbar.LENGTH_LONG).setAction(getString(R.string.open_book), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delegate.bookResponse(bookDescription);
                    }
                }).show();
            }
        }
    }

    private void update(){
        List<File> files = FileHelper.readerFileFilter(path.listFiles());

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(path.getPath());

        adapter = new ReaderFileExplorerListAdapter(getActivity(), R.id.reader_file_explorer_text_view, files);
        adapter.delegate = this;

        listView.setAdapter(adapter);
    }

    //Добавление книги, а также нажатие на ту что уже добавлена
    @Override
    public void bookResponse(BookDescription bookDescription) {
        if (bookDescription == null) {
            Snackbar.make(getView(), R.string.file_reading_error, Snackbar.LENGTH_LONG).show();
        } else {
            delegate.bookResponse(bookDescription);
        }
    }
}
