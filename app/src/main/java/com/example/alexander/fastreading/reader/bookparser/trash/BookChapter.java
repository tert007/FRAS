package com.example.alexander.fastreading.reader.bookparser.trash;

import com.example.alexander.fastreading.reader.bookparser.HtmlTag;

import java.util.List;

/**
 * Created by Alexander on 10.08.2016.
 */
public class BookChapter {
    ///Глава книги . сожержит страницы . страницы теги
    private int index;
    private String title;
    private List<HtmlTag> tags;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<HtmlTag> getTags() {
        return tags;
    }

    public void setTags(List<HtmlTag> tags) {
        this.tags = tags;
    }
}
