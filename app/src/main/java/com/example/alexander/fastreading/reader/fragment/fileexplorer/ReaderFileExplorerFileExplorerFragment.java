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

import java.io.File;

/**
 * Created by Alexander on 03.08.2016.
 */
public class ReaderFileExplorerFileExplorerFragment extends Fragment implements ReaderFileExplorerOnClickResponse {

    public ReaderFileExplorerOnClickResponse delegate;

    private FileExplorerListAdapter adapter;
    private ListView listView;

    private File path = new File("/");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

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
    public void fileOnClick(File file) {
        if (file.isDirectory()){
            path = file;
            update();
        } else {
            BookAddAsyncTask bookAddAsyncTask = new BookAddAsyncTask(getActivity());
            bookAddAsyncTask.execute(file.getPath());

            //delegate.fileOnClick(file);
        }
    }

    private void update(){
        File[] files = FileHelper.readerFileFilter(path.listFiles());

        adapter = new FileExplorerListAdapter(getActivity(), R.id.reader_file_explorer_text_view, files);
        adapter.delegate = this;

        listView.setAdapter(adapter);
    }
}
