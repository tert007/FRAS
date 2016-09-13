package com.example.alexander.fastreading.reader.fragment.pages;

import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import com.example.alexander.fastreading.reader.entity.HtmlTag;

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

        List<CharSequence> result = new ArrayList<>();

        for (CharSequence chapter : chapters) {
            //int currentPage = 1;

            int usedHeight = 0;

            int startIndex = 0;
            int endIndex = 0;

            StaticLayout tempLayout = new StaticLayout(chapter, textPaint, pageWidth, Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineSpacingExtra, false);
            int lastLineIndex = tempLayout.getLineCount() - 1;

            //Log.d("width", String.valueOf(tempLayout.getWidth()));
            //Log.d("sub", chapter.subSequence(tempLayout.getLineStart(6), tempLayout.getLineEnd(6)).toString());

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
            /*
            //int lastLineIndex = tempLayout.getLineForVertical(tempLayout.getHeight());

            int currentLineIndex;
            while ((currentLineIndex = tempLayout.getLineForVertical(pageHeight * currentPage)) != lastLineIndex) {
                currentLineIndex--;
                //tempLayout.getLineTop(currentLineIndex);

                endIndex = tempLayout.getLineVisibleEnd(currentLineIndex);
                result.add(chapter.subSequence(startIndex, endIndex));
                startIndex = endIndex;

                currentPage++;
            }

            result.add(chapter.subSequence(startIndex, chapter.length()));

            /*
            for (int i = 0; i < lineCount; i++) {
                if (i == lineCount - 1) {
                    //Последнюю страницу можно добавить и не запоненную :)
                    result.add(chapter.subSequence(startIndex, chapter.length()));
                    break;
                }

                if (tempLayout.getLineBottom(i) < pageHeight * currentPage) {
                    lastLine = i;
                } else {
                    currentPage++;

                    lastLine--;

                    //if (itsSpanned) {
                    //    int countRelativeSpans = ((Spanned) chapter).getSpans(startIndex, tempLayout.getLineEnd(lastLine), RelativeSizeSpan.class).length;
                    //    lastLine -= countRelativeSpans * 5;
                    //}


                }
                */


        }
        return result;
    }
}
