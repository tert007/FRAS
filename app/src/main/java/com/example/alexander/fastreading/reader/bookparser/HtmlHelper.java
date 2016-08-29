package com.example.alexander.fastreading.reader.bookparser;

import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 23.08.2016.
 */
public class HtmlHelper {
    public final static String PARAGRAPH_TAG = "p";
    public final static String HEAD_1_TAG = "h1";
    public final static String HEAD_2_TAG = "h2";
    public final static String HEAD_3_TAG = "h3";
    public final static String HEAD_4_TAG = "h4";
    public final static String HEAD_5_TAG = "h5";
    public final static String HEAD_6_TAG = "h6";
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
            default:
                paragraph = "<" + htmlTag.getTagName() + ">" + htmlTag.getTagContent() + "</" + htmlTag.getTagName() + ">";
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
            default:
                paragraph = "<" + htmlTag.getTagName() + ">" + htmlTag.getTagContent() + "</" + htmlTag.getTagName() + ">";
                break;
        }

        return paragraph;
    }

    public static Spanned convertHtmlPageToSpanned(List<HtmlTag> htmlPage) {
        StringBuilder result = new StringBuilder();


        for (int i = 0; i < htmlPage.size(); i++) {
            result.append(convertHtmlTagToString(htmlPage.get(i)));
        }

        return Html.fromHtml(result.toString());
    }
}
