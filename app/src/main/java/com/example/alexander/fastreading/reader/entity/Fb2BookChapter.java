package com.example.alexander.fastreading.reader.entity;

import android.text.SpannableStringBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alexander on 15.09.2016.
 */
public class Fb2BookChapter implements BookChapter {
    private CharSequence title;
    private CharSequence content;

    public Fb2BookChapter() {
    }

    public Fb2BookChapter(CharSequence title, CharSequence content) {
        this.title = title;
        this.content = content;
    }

    public CharSequence getTitle() {
        return title;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    public CharSequence getContent() {
        return content;
    }

    public void setContent(CharSequence content) {
        this.content = content;
    }

    @Override
    public CharSequence getBookChapter() {
        final SpannableStringBuilder builder = new SpannableStringBuilder();

        if (title != null) {
            builder.append(title);
        }

        if (content != null) {
            builder.append(content);
        }

        int builderLength = builder.length();
        for (int i = builderLength - 1; i >= 0; i--) {
            if (builder.charAt(i) != '\n') {
                return builder.subSequence(0 , i + 1);
            }
        }

        return builder;
    }
}
