package com.example.alexander.fastreading.reader.fragment.pages;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.UnderlineSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexander on 28.08.2016.
 */
public class MyWordSelector {

    private Spanned page;

    private final static String regExp = "\\b([\\w]+)\\b";

    private final Pattern pattern;
    private Matcher matcher;

    public MyWordSelector(Spanned page){
        this.page = page;
        this.pattern = Pattern.compile(regExp);
        this.matcher = pattern.matcher(page.toString());
    }


    public Spanned getNextSelectedWord(){
        /*
        SpannableString spannableString = null;
        String pageWithoutStyles = page.toString();

        if (i == splitte.length - 1){
            return null;
        }

        int index;
        if ((index = pageWithoutStyles.indexOf(splitte[i], startIndex)) != -1){
            startIndex = index;
            endIndex = index + splitte[i].length() - 1;
            i++;

            spannableString = new SpannableString(page);
            spannableString.setSpan(new UnderlineSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

*/


        if (matcher.find()){
            SpannableString spannableString = null;

            spannableString = new SpannableString(page);
            spannableString.setSpan(new UnderlineSpan(), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            return spannableString;
        }


        return null;
    }
}
