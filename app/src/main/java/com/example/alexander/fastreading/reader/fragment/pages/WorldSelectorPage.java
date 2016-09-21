package com.example.alexander.fastreading.reader.fragment.pages;

/**
 * Created by Alexander on 21.09.2016.
 */
public class WorldSelectorPage {
    private CharSequence page;
    private int selectedWordLength;

    public WorldSelectorPage(CharSequence page, int selectedWordLength) {
        this.page = page;
        this.selectedWordLength = selectedWordLength;
    }

    public CharSequence getPage() {
        return page;
    }

    public void setPage(CharSequence page) {
        this.page = page;
    }

    public int getSelectedWordLength() {
        return selectedWordLength;
    }

    public void setSelectedWordLength(int selectedWordLength) {
        this.selectedWordLength = selectedWordLength;
    }
}
