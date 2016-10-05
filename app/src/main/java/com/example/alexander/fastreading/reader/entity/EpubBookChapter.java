package com.example.alexander.fastreading.reader.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.util.Log;

/**
 * Created by Alexander on 01.10.2016.
 */
public class EpubBookChapter implements BookChapter {
    private CharSequence title;
    private CharSequence content;

    public EpubBookChapter() {
    }

    public EpubBookChapter(CharSequence title, CharSequence content) {
        this.title = title;
        this.content = content;
    }

    @Override
    public CharSequence getTitle() {
        return title;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    @Override
    public CharSequence getContent() {
        return content;
    }

    public void setContent(CharSequence content) {
        this.content = content;
    }

    @Override
    public CharSequence getBookChapter() {
        if (content != null) {
            int contentLength = content.length();
            for (int i = contentLength - 1; i >= 0; i--) {
                if (content.charAt(i) != '\n') {
                    return content.subSequence(0 , i + 1);
                }
            }

            return content;
        } else {
            return "";
        }
    }
}
