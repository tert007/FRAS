package com.example.alexander.fastreading.reader.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.FileHelper;

import java.io.File;
import java.io.IOException;

/**
 * Created by Alexander on 04.08.2016.
 */
public class ReaderMainFragment extends Fragment {

    //private ReaderLinearLayout readerLinearLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reader_main_txt_fragment, container, false);

        final TextView textView = (TextView) view.findViewById(R.id.reader_main_txt_fragment_text_view);
        String filePath = getArguments().getString("file_path");

        try {
            String textFromFile = FileHelper.getTextFromFile(new File(filePath));
            textView.setText(textFromFile);

            textView.setOnClickListener(new View.OnClickListener() {
                private boolean itsScrolling;

                @Override
                public void onClick(View v) {
                    //if (itsScrolling){
                        textView.scrollBy(0, +20);
                    //}
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
















        /*
        try {

            String filePath = getArguments().getString("file_path");

            readerLinearLayout = (ReaderLinearLayout) view.findViewById(R.id.reader_main_book_text_view);
            String str = FileHelper.getTextFromFile(new File(filePath));

            readerLinearLayout.addText(str);

            //WebView webView = (WebView) view.findViewById(R.id.web_view);
            //readerLinearLayout.setText(htm);
        } catch (IOException e) {
            e.printStackTrace();
        }

    */
/*
        try {
            //String html = FileHelper.getTextFromFile(new File(getActivity().getApplicationInfo().dataDir + File.separator + "test1/OEBPS/Text/section2.xhtml"));

            //String pattern = "(?s)<head>.*</head>";
            //String cssPath = getActivity().getApplicationInfo().dataDir + File.separator + "test1/OEBPS/Styles/main.css";



            //String  a = "<head><link rel=\"stylesheet\" type=\"text/css\" href=\"cssPat\" /></head>";
            //String s = html.replaceAll(pattern, a);
            //webView.loadUrl("file:///" + getActivity().getApplicationInfo().dataDir + File.separator + "test1/OEBPS/Text/section4.xhtml");
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
        return view;
    }
}
