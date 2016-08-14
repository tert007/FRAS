package com.example.alexander.fastreading.reader;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReaderLinearLayout extends LinearLayout {
    //Пока не используем!!111111


    private int textSize = 16;
    private List<String> pages;

    private int pageCount;
    private int currentPage;

    public void setTextSize(int textSize){
        this.textSize = textSize;
    }

    public int getTextSize(){
        return textSize;
    }

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private final DisplayMetrics displayMetrics = new DisplayMetrics();

    public ReaderLinearLayout(Context context) {
        super(context);
    }

    public ReaderLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public String addText(String text) {
        this.text = text;
        String inputText = text;

        int screenHeight = getScreenHeight();
        int textHeight = getTextHeight(inputText);

        if (textHeight == 0){
            pageCount = 1;
        } else {
            pageCount = (int)Math.ceil((double) textHeight / (double) screenHeight);
        }

        pages = new ArrayList<>(pageCount);
        int lengthOnePage = (int)Math.ceil((double)inputText.length() / (double)pageCount);
        for (int i = 0; i < pageCount; i++){
            if (i == pageCount - 1){ //Последняя страница
                pages.add(inputText.substring(i * lengthOnePage, inputText.length()));
            } else {
                pages.add(inputText.substring(i * lengthOnePage, (i + 1) * lengthOnePage));
            }
        }

/*
        while (textHeight > screenHeight) {
            int heightRatio =  textHeight / screenHeight;

            if (heightRatio > 1){
                text = text.substring(0, text.length() / heightRatio);
            } else {
                text = text.substring(0, text.length() - 1);
            }

            textHeight = getTextHeight(text);
        }
*/
        final TextView textView = new TextView(getContext());

        textView.setText(pages.get(currentPage));
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(textSize);
        textView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == event.ACTION_DOWN){
                    int x = (int)event.getX();
                    //int y = (int)event.getY();

                    if (x > (getScreenWidth() / 2)) {
                        if (currentPage < pageCount - 1){
                            currentPage++;
                            textView.setText(pages.get(currentPage));
                        }
                    } else {
                        if (currentPage > 0){
                            currentPage--;
                            textView.setText(pages.get(currentPage));
                        }
                    }
                }
                return false;
            }
        });
        addView(textView);

        return inputText.subSequence(text.length(), inputText.length()).toString();
    }

    public int getTextHeight(String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextSize(textSize);
        TextPaint textPaint = textView.getPaint();

        return new StaticLayout(text, textPaint, getScreenWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true).getHeight();
    }

    public int getScreenHeight() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.heightPixels/* - getPaddingTop() - getPaddingBottom()*/;
    }

    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.widthPixels/* - getPaddingLeft() - getPaddingRight() */;
    }
}