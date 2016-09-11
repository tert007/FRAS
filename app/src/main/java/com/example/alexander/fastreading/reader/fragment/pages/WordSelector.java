package com.example.alexander.fastreading.reader.fragment.pages;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexander on 28.08.2016.
 */
public class WordSelector {

    private final static String regExp = "\\b([\\w]+)\\b";
    private final static Pattern pattern = Pattern.compile(regExp);

    private CharSequence page;

    private Matcher matcher;

    public WordSelector(CharSequence page){
        this.page = page;
        this.matcher = pattern.matcher(page.toString());
    }


    public CharSequence getNextSelectedWord(){
        if (matcher.find()){
            SpannableString spannableString;

            spannableString = new SpannableString(page);
            spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            return spannableString;
        }

        return null;
    }
}
