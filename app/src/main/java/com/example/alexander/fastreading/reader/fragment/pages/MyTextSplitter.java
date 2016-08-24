package com.example.alexander.fastreading.reader.fragment.pages;

import android.text.Layout;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Alexander on 23.08.2016.
 */
public class MyTextSplitter {

    private final TextPaint textPaint;
    private final int pageWidth;
    private final int pageHeight;
    private final float lineSpacingMultiplier;
    private final int lineSpacingExtra;

    //private int currentPageHeight;

    private List<List<HtmlTag>> pages;
    private List<HtmlTag> page;

    /*
    private final List<CharSequence> pages = new ArrayList<CharSequence>();
    private SpannableStringBuilder currentLine = new SpannableStringBuilder();
    private SpannableStringBuilder currentPage = new SpannableStringBuilder();

    /*
    private int currentLineHeight;
    private int pageContentHeight;

    private int currentLineWidth;
    private int textLineHeight;
    */
    public MyTextSplitter(TextPaint textPaint, int pageWidth, int pageHeight) {
        this.textPaint = textPaint;
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.lineSpacingMultiplier = 1;
        this.lineSpacingExtra = 0;
    }

    public MyTextSplitter(TextPaint textPaint, int pageWidth, int pageHeight, float lineSpacingMultiplier, int lineSpacingExtra) {
        this.textPaint = textPaint;
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.lineSpacingMultiplier = lineSpacingMultiplier;
        this.lineSpacingExtra = lineSpacingExtra;
    }

    public List<List<HtmlTag>> getPages(List<HtmlTag> html){
        //Делит один большой html на мелкие
        pages = new ArrayList<>();
        page = new ArrayList<>();

        //currentPageHeight = 0;

        for (int i = 0; i < html.size(); i++) {
            //Spanned textSpanned = HtmlHelper.convertHtmlTagToSpanned(html.get(i));
            page.add(html.get(i));

            Spanned textSpanned = HtmlHelper.convertHtmlPageToSpanned(page);

            StaticLayout tempLayout = new StaticLayout(textSpanned, textPaint, pageWidth, Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineSpacingExtra, false);
            int staticLayoutHeight = tempLayout.getHeight();

            if (staticLayoutHeight < pageHeight) {
                if (i == html.size() - 1){
                    pages.add(page);
                }
            } else {
                page.remove(page.size() - 1);
                paragraphSplitter(html.get(i));
            }
        }

        return pages;
    }

    private void paragraphSplitter(HtmlTag htmlTag){
        String tagName = htmlTag.getTagName();
        String[] words = htmlTag.getTagContent().split("\\s");

        String previousParagraph = "";
        String newParagraph = "";

        for (int i = 0; i < words.length; i++) {
            if (i == 0){
                newParagraph += words[i];
            } else {
                newParagraph += " " + words[i];
            }

            HtmlTag tempHtmlTag = new HtmlTag(tagName, newParagraph);
            page.add(tempHtmlTag);

            Spanned textWithPartOfParagraph = HtmlHelper.convertHtmlPageToSpanned(page);

            StaticLayout tempLayout = new StaticLayout(textWithPartOfParagraph, textPaint, pageWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);
            int staticLayoutHeight = tempLayout.getHeight();

            if (staticLayoutHeight < pageHeight) {
                page.remove(page.size() - 1);
                previousParagraph = newParagraph;
            } else {
                page.remove(page.size() - 1);

                //Если было добавлено хоть одно слово
                if (i != 0){
                    page.add(new HtmlTag(tagName, previousParagraph));
                }

                pages.add(page);

                String textForNextPage = "";
                for (int j = i; j < words.length; j++){
                    if (j == i){
                        textForNextPage += words[j];
                    } else {
                        textForNextPage += " " + words[j];
                    }
                }

                //Новая страница
                page = new ArrayList<>();

                HtmlTag tagForNextPage = new HtmlTag(tagName, textForNextPage);
                Spanned tagForNextPageSpanned = HtmlHelper.convertHtmlTagToSpanned(tagForNextPage);

                tempLayout = new StaticLayout(tagForNextPageSpanned, textPaint, pageWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);
                staticLayoutHeight = tempLayout.getHeight();
                if (staticLayoutHeight < pageHeight) {
                    page.add(tagForNextPage);

                    return;
                } else {
                    paragraphSplitter(tagForNextPage); /////////СТРЁМНО
                }
            }
        }
    }

    /*
    private void paragraphSplitter(String paragraph){
        String[] words = paragraph.split("\\s");

        for (int i = 0; i < words.length; i++) {
            StaticLayout tempLayout = new StaticLayout(words[i], textPaint, pageWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);


            int lineCount = tempLayout.getLineCount();
            float textWidth =0;
            for(int i=0 ; i < lineCount ; i++){
                textWidth += tempLayout.getLineWidth(i);
            }

        //    appendWord(words[i] + " ", textPaint);
        }
    }
    /*
    private void check(){

        int textWidth = (int) Math.ceil(textPaint.measureText(appendedText));
        if (currentLineWidth + textWidth >= pageWidth) {
            checkForPageEnd();
            appendLineToPage(textLineHeight);
        }
        appendTextToLine(appendedText, textPaint, textWidth);

    }











    public void append(String text, TextPaint textPaint) {
        textLineHeight = (int) Math.ceil(textPaint.getFontMetrics(null) * lineSpacingMultiplier + lineSpacingExtra);


        String[] paragraphs = text.split("\n", -1);
        int i;
        for (i = 0; i < paragraphs.length - 1; i++) {
            appendText(paragraphs[i], textPaint);
            appendNewLine();
        }
        appendText(paragraphs[i], textPaint);
    }

    private void appendText(String text, TextPaint textPaint) {
        String[] words = text.split(" ", -1);
        int i;
        for (i = 0; i < words.length - 1; i++) {
            appendWord(words[i] + " ", textPaint);
        }
        appendWord(words[i], textPaint);
    }

    private void appendNewLine() {
        currentLine.append("\n");
        checkForPageEnd();
        appendLineToPage(textLineHeight);
    }

    private void checkForPageEnd() {
        if (pageContentHeight + currentLineHeight > pageHeight) {
            pages.add(currentPage);
            currentPage = new SpannableStringBuilder();
            pageContentHeight = 0;
        }
    }

    private void appendWord(String appendedText, TextPaint textPaint) {
        int textWidth = (int) Math.ceil(textPaint.measureText(appendedText));
        if (currentLineWidth + textWidth >= pageWidth) {
            checkForPageEnd();
            appendLineToPage(textLineHeight);
        }
        appendTextToLine(appendedText, textPaint, textWidth);
    }

    private void appendLineToPage(int textLineHeight) {
        currentPage.append(currentLine);
        pageContentHeight += currentLineHeight;

        currentLine = new SpannableStringBuilder();
        currentLineHeight = textLineHeight;
        currentLineWidth = 0;
    }

    private void appendTextToLine(String appendedText, TextPaint textPaint, int textWidth) {
        currentLineHeight = Math.max(currentLineHeight, textLineHeight);
        currentLine.append(renderToSpannable(appendedText, textPaint));
        currentLineWidth += textWidth;
    }

    public List<CharSequence> getPages() {
        List<CharSequence> copyPages = new ArrayList<CharSequence>(pages);
        SpannableStringBuilder lastPage = new SpannableStringBuilder(currentPage);
        if (pageContentHeight + currentLineHeight > pageHeight) {
            copyPages.add(lastPage);
            lastPage = new SpannableStringBuilder();
        }
        lastPage.append(currentLine);
        copyPages.add(lastPage);
        return copyPages;
    }

    private SpannableString renderToSpannable(String text, TextPaint textPaint) {
        SpannableString spannable = new SpannableString(text);

        if (textPaint.isFakeBoldText()) {
            spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, spannable.length(), 0);
        }
        return spannable;
    }
    */
}
