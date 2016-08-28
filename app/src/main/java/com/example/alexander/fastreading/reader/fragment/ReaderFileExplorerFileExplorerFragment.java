package com.example.alexander.fastreading.reader.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.ReaderFileExplorerOnClickResponse;

import java.io.File;

/**
 * Created by Alexander on 03.08.2016.
 */
public class ReaderFileExplorerFileExplorerFragment extends Fragment implements ReaderFileExplorerOnClickResponse {

    public ReaderFileExplorerOnClickResponse delegate;

    private FileExplorerListAdapter adapter;
    private ListView listView;

    public void setStartDirectoryPath(File startDirectoryPath) {
        this.startDirectoryPath = startDirectoryPath;
    }

    private File startDirectoryPath = new File("/");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reader_file_explorer_fragment, container, false);

        File[] files = FileHelper.readerFileFilter(startDirectoryPath.listFiles());

        adapter = new FileExplorerListAdapter(getActivity(), R.id.reader_file_explorer_text_view, files);
        adapter.delegate = this;

        listView = (ListView) view.findViewById(R.id.reader_file_explorer_list_view);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void fileOnClick(File file) {
        delegate.fileOnClick(file);
    }
}
