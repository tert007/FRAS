package com.example.alexander.fastreading.reader.fragment.pages;

import android.text.Layout;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.example.alexander.fastreading.reader.bookparser.HtmlHelper;
import com.example.alexander.fastreading.reader.bookparser.HtmlTag;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexander on 23.08.2016.
 */
public class PageSplitter {
    private final TextPaint textPaint;
    private final int pageWidth;
    private final int pageHeight;
    private final float lineSpacingMultiplier;
    private final int lineSpacingExtra;

    private List<Spanned> pages;
    private List<HtmlTag> page;

    public PageSplitter(TextPaint textPaint, int pageWidth, int pageHeight) {
        this.textPaint = textPaint;
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.lineSpacingMultiplier = 1;
        this.lineSpacingExtra = 0;
    }

    public PageSplitter(TextPaint textPaint, int pageWidth, int pageHeight, float lineSpacingMultiplier, int lineSpacingExtra) {
        this.textPaint = textPaint;
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.lineSpacingMultiplier = lineSpacingMultiplier;
        this.lineSpacingExtra = lineSpacingExtra;
    }

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
}
