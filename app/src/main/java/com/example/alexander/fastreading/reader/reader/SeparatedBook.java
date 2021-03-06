package com.example.alexander.fastreading.reader.reader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 04.10.2016.
 */
public class SeparatedBook {

    private List<String> titles;
    private List<List<CharSequence>> chapters;

    private final int size;

    public SeparatedBook(final List<String> titles, final List<List<CharSequence>> chapters) {
        final List<String> trimTitles = new ArrayList<>(titles.size());

        for (String title : titles) {
            String bufTitle = title.trim().replaceAll("[\\s]+", " ");
            if (bufTitle.isEmpty()) {
                bufTitle = "...";
            }

            trimTitles.add(bufTitle);
        }

        this.titles = trimTitles;
        this.chapters = chapters;

        int size = 0;
        for (List<CharSequence> chapter: chapters) {
            size += chapter.size();
        }

        this.size = size;
    }

    public String getTitle(int pageIndex) {
        int pageCount = 0;
        int lastTitleIndex = 0;

        for (List<CharSequence> chapter: chapters) {
            pageCount += chapter.size();

            if (pageIndex >= pageCount) {
                lastTitleIndex++;
            } else {
                return titles.get(lastTitleIndex);
            }
        }

        return null;
    }

    public int getPageIndexByTitle(int titleIndex) {
        int pageIndex = 0;

        for (int i = 0; i < titleIndex; i++) {
            pageIndex += chapters.get(i).size();
        }

        return pageIndex;
    }

    public List<String> getTitles() {
        return titles;
    }

    public CharSequence getPage(int pageIndex) {
        int offsetIndex = pageIndex;

        for (List<CharSequence> chapter: chapters) {
            int chapterPagesCount = chapter.size();

            if (offsetIndex < chapterPagesCount) {
                return chapter.get(offsetIndex);
            } else {
                offsetIndex -= chapterPagesCount;
            }
        }

        return null;
    }

    public int size() {
        return size;
    }
}
