package com.example.alexander.fastreading.reader.bookparser;

/**
 * Created by Alexander on 24.08.2016.
 */
public class HtmlTag {
    private String tagName;
    private String tagContent;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagContent() {
        return tagContent;
    }

    public void setTagContent(String tagContent) {
        this.tagContent = tagContent;
    }

    public HtmlTag(String tagName, String tagContent) {
        this.tagName = tagName;
        this.tagContent = tagContent;
    }
}
