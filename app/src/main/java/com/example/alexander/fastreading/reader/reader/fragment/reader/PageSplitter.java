package com.example.alexander.fastreading.reader.reader.fragment.reader;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alexander on 23.08.2016.
 */
public class PageSplitter {

    private final TextPaint textPaint;
    private final int pageWidth;
    private final int pageHeight;
    private final float lineSpacingMultiplier;
    private final float lineSpacingExtra;

    public PageSplitter(TextPaint textPaint, int pageWidth, int pageHeight) {
        this.textPaint = textPaint;
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.lineSpacingMultiplier = 1f;
        this.lineSpacingExtra = 0f;
    }

    public PageSplitter(TextPaint textPaint, int pageWidth, int pageHeight, float lineSpacingMultiplier, int lineSpacingExtra) {
        this.textPaint = textPaint;
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.lineSpacingMultiplier = lineSpacingMultiplier;
        this.lineSpacingExtra = lineSpacingExtra;
    }

    public List<CharSequence> getPages(List<CharSequence> chapters) {
        //Лист глав книги в лист страниц
        if (chapters.isEmpty())
            return Collections.emptyList();

        List<CharSequence> result = new ArrayList<>(); //FIX malloc every time

        for (CharSequence chapter : chapters) {

            int usedHeight = 0;

            int startIndex = 0;
            int endIndex = 0;

            StaticLayout tempLayout = new StaticLayout(chapter, textPaint, pageWidth, Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineSpacingExtra, false);
            int lastLineIndex = tempLayout.getLineCount() - 1;

            int selectedLine = 0;
            while (selectedLine != lastLineIndex) {
                int verticalOffset = usedHeight + pageHeight;

                selectedLine = tempLayout.getLineForVertical(verticalOffset);

                if (tempLayout.getLineBottom(selectedLine) > verticalOffset){
                    selectedLine--;
                }

                usedHeight = tempLayout.getLineBottom(selectedLine);

                endIndex = tempLayout.getLineEnd(selectedLine);
                result.add(chapter.subSequence(startIndex, endIndex));
                startIndex = endIndex;
            }
        }

        return result;
    }
}
