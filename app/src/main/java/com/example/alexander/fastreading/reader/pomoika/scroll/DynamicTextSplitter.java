package com.example.alexander.fastreading.reader.pomoika.scroll;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * Created by Alexander on 17.09.2016.
 */
public class DynamicTextSplitter {
    private final StaticLayout staticLayout;

    private final int pageHeight;
    private final CharSequence text;


    private final int backDownloadLimit;
    private final int forwardDownloadLimit;

    private int startOffset;


    public DynamicTextSplitter(CharSequence text,TextPaint textPaint, int pageWidth, int pageHeight) {
        this.text = text;
        this.pageHeight = pageHeight;

        this.backDownloadLimit = pageHeight;
        this.forwardDownloadLimit = pageHeight * 8;

        this.staticLayout = new StaticLayout(text, textPaint, pageWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);
    }

    public DynamicTextSplitter(CharSequence text, TextPaint textPaint, int pageWidth, int pageHeight, float lineSpacingMultiplier, int lineSpacingExtra) {
        this.text = text;
        this.pageHeight = pageHeight;

        this.backDownloadLimit = pageHeight;
        this.forwardDownloadLimit = pageHeight * 8;

        this.staticLayout = new StaticLayout(text, textPaint, pageWidth, Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineSpacingExtra, false);
    }

    public CharSequence getShortText(int charOffset) {

        int centerLine = staticLayout.getLineForOffset(charOffset);
        int centerLineVerticalOffset = staticLayout.getLineBottom(centerLine);

        int startLine = staticLayout.getLineForVertical(centerLineVerticalOffset);
        int endLine = staticLayout.getLineForVertical(centerLineVerticalOffset + 9 * pageHeight);

        int startIndex = staticLayout.getLineStart(startLine);
        int endIndex = staticLayout.getLineEnd(endLine);

        return text.subSequence(startIndex, endIndex);
    }
}
