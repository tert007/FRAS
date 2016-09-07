package com.example.alexander.fastreading.reader.fragment.fileexplorer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.bookparser.BookDescription;

import java.io.File;

/**
 * Created by Alexander on 03.08.2016.
 */
public class ReaderFileExplorerFileExplorerFragment extends Fragment implements ReaderFileExplorerOnFileClickResponse,
        ReaderFileExplorerBookAddResponse {

    public ReaderFileExplorerBookAddResponse delegate;

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
            ReaderBookAddAsyncTask readerBookAddAsyncTask = new ReaderBookAddAsyncTask(getActivity());
            readerBookAddAsyncTask.delegate = this;

            readerBookAddAsyncTask.execute(file.getPath());
        }
    }

    private void update(){
        File[] files = FileHelper.readerFileFilter(path.listFiles());

        adapter = new ReaderFileExplorerListAdapter(getActivity(), R.id.reader_file_explorer_text_view, files);
        adapter.delegate = this;

        listView.setAdapter(adapter);
    }

    @Override
    public void bookAddPostExecute(BookDescription bookDescription) {
        delegate.bookAddPostExecute(bookDescription);
    }
}
