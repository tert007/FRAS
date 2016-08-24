package com.example.alexander.fastreading.reader.fragment.pages;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 23.08.2016.
 */
public class HtmlHelper {
    public final static String PARAGRAPH_TAG = "p";
    public final static String NEW_LINE_TAG = "br";

    public static List<HtmlTag> convertTextToHtmlPage(String text) {
        //Позволяет порезать текст на абзацы, один отступ после абзаца игнорируется, так как тег p его сам сделает
        String[] lines = text.split("\\n");

        List<HtmlTag> tags = new ArrayList<>(lines.length);

        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();
            if (lines[i].isEmpty()) {
                if (i > 0 && !lines[i - 1].isEmpty()){
                    continue;
                }
                tags.add(new HtmlTag(NEW_LINE_TAG, null));
            } else {
                tags.add(new HtmlTag(PARAGRAPH_TAG, lines[i]));
            }
        }

        return tags;
    }

    public static Spanned convertHtmlTagToSpanned(HtmlTag htmlTag) {
        String paragraph = null;

        switch (htmlTag.getTagName()) {
            case NEW_LINE_TAG:
                paragraph = "<" + NEW_LINE_TAG + ">";
                break;
            case PARAGRAPH_TAG:
                paragraph = "<" + PARAGRAPH_TAG + ">" + htmlTag.getTagContent() + "</" + PARAGRAPH_TAG + ">";
                break;
        }

        return Html.fromHtml(paragraph);
    }

    private static String convertHtmlTagToString(HtmlTag htmlTag) {
        String paragraph = null;

        switch (htmlTag.getTagName()) {
            case NEW_LINE_TAG:
                paragraph = "<" + NEW_LINE_TAG + ">";
                break;
            case PARAGRAPH_TAG:
                paragraph = "<" + PARAGRAPH_TAG + ">" + htmlTag.getTagContent() + "</" + PARAGRAPH_TAG + ">";
                break;
        }

        return paragraph;
    }

    public static Spanned convertHtmlPageToSpanned(List<HtmlTag> htmlPage) {
        String result = "";

        for (int i = 0; i < htmlPage.size(); i++) {
            result += convertHtmlTagToString(htmlPage.get(i));
        }

        return Html.fromHtml(result);
    }
}
