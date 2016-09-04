package com.example.alexander.fastreading.reader.fragment.pages;

import android.text.Layout;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.example.alexander.fastreading.reader.HtmlTag;

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

    private List<Spanned> pages;
    private List<HtmlTag> page;

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

    public List<CharSequence> getPages(List<CharSequence> chapters){
        //Лист глав книги в лист страниц
        if (chapters.isEmpty())
            return Collections.emptyList();

        List<CharSequence> result = new ArrayList<>();

        int interlineSpacing = textPaint.getFontMetricsInt(null);

        for (CharSequence chapter : chapters) {
            int currentPage = 1;
            int lastLine = 0;

            int startIndex = 0;
            int endIndex = 0;

            StaticLayout tempLayout = new StaticLayout(chapter, textPaint, pageWidth, Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineSpacingExtra, false);
            int lineCount = tempLayout.getLineCount();

            for (int i = 0; i < lineCount; i++) {

                if (i == lineCount - 1) {
                    //Последнюю страницу можно добавить и не запоненную :)
                    result.add(chapter.subSequence(startIndex, chapter.length()));
                    break;
                }

                int a = tempLayout.getLineBottom(i);

                if (tempLayout.getLineBottom(i) < pageHeight * currentPage) {
                    lastLine = i;
                } else {
                    currentPage++;

                    endIndex = tempLayout.getLineEnd(lastLine);
                    result.add(chapter.subSequence(startIndex, endIndex));
                    startIndex = tempLayout.getLineStart(lastLine + 1);;

                    if (Character.isSpaceChar(chapter.charAt(startIndex))){
                        startIndex++;  //МБ исключение?
                    }
                }

            }


/*







            textHeight = tempLayout.getHeight();

            if (textHeight % pageHeight == 0){
                pagesCount = textHeight / pageHeight;
            } else {
                pagesCount = textHeight / pageHeight + 1;
            }

            int startIndex = 0;
            int endIndex = pageBaseLength;

            for (int i = 0; i < pagesCount; i++) {
                while (true){
                    CharSequence part = chapter.subSequence(startIndex, endIndex);
                    tempLayout = new StaticLayout(part, textPaint, pageWidth, Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineSpacingExtra, false);
                    textHeight = tempLayout.getHeight();

                    if (textHeight <= pageHeight){
                        if (Character.isSpaceChar(chapter.charAt(endIndex))) {
                            result.add(chapter.subSequence(startIndex, endIndex));

                            startIndex = endIndex + 1;
                            endIndex = startIndex + pageBaseLength;

                            break;
                        }
                    }


                    for (int j = endIndex - 1; j > startIndex - 1; j--) {
                        if (Character.isSpaceChar(chapter.charAt(j))){
                            endIndex = j;
                            break;
                        }
                    }
                }
            }*/
        }
        //SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(l);*/
        return result;
    }

    /*
    public List<Spanned> getPages(List<HtmlTag> html){
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
                    pages.add(textSpanned);
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

        if (tagName.equals(HtmlHelper.NEW_LINE_TAG)){
            pages.add(HtmlHelper.convertHtmlPageToSpanned(page));

            page = new ArrayList<>();
            //Или вообще не добавлять? (Расскоментить, если добавлять)
            //page.add(htmlTag);
            return;
        }

        String[] words = htmlTag.getTagContent().split("\\s");

        String previousParagraph = "";
        StringBuilder newParagraph = new StringBuilder();

        //int index = -1;

        for (int i = 0; i < words.length; i++) {
            if (i == 0){
                newParagraph.append(words[i]);
            } else {
                newParagraph.append(' ');
                newParagraph.append(words[i]);
            }

            HtmlTag tempHtmlTag = new HtmlTag(tagName, newParagraph.toString());
            page.add(tempHtmlTag);

            Spanned textWithPartOfParagraph = HtmlHelper.convertHtmlPageToSpanned(page);

            StaticLayout tempLayout = new StaticLayout(textWithPartOfParagraph, textPaint, pageWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);
            int staticLayoutHeight = tempLayout.getHeight();

            if (staticLayoutHeight < pageHeight) {
                page.remove(page.size() - 1);
                previousParagraph = newParagraph.toString();
            } else {
                page.remove(page.size() - 1);

                //Если было добавлено хоть одно слово
                if (i != 0){
                    page.add(new HtmlTag(tagName, previousParagraph));
                }

                pages.add(HtmlHelper.convertHtmlPageToSpanned(page));

                //Улучшить через subSequence
                StringBuilder textForNextPage = new StringBuilder();
                for (int j = i; j < words.length; j++){
                    if (j == i){
                        textForNextPage.append(words[j]);
                    } else {
                        textForNextPage.append(' ');
                        textForNextPage.append(words[j]);
                    }
                }

                //Новая страница
                page = new ArrayList<>();

                HtmlTag tagForNextPage = new HtmlTag(tagName, textForNextPage.toString());
                Spanned tagForNextPageSpanned = HtmlHelper.convertHtmlTagToSpanned(tagForNextPage);

                tempLayout = new StaticLayout(tagForNextPageSpanned, textPaint, pageWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);
                staticLayoutHeight = tempLayout.getHeight();
                if (staticLayoutHeight < pageHeight) {
                    page.add(tagForNextPage);

                    return;
                } else {
                    paragraphSplitter(tagForNextPage);

                    return;
                }
            }
        }
    }
    */
}
