package com.example.alexander.fastreading.reader.fragment.pages;

import android.text.Spanned;

import com.example.alexander.fastreading.reader.bookparser.HtmlHelper;
import com.example.alexander.fastreading.reader.bookparser.HtmlTag;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexander on 22.08.2016.
 */
public class WordSelector {
    private List<HtmlTag> page;

    private int tagIndex = -1;
    private List<HtmlTag> newPage;
    private int index;

    private final static String regExp = "\\b([\\w]+)\\b";
    private final static String startTag = "<b>";
    private final static String finishTag = "</b>";

    private final Pattern pattern;
    private Matcher matcher;

    public WordSelector(List<HtmlTag> page){
        this.page = page;
        this.pattern = Pattern.compile(regExp);
    }


    public Spanned getNextSelectedWord(){
        newPage = new ArrayList<>(page);

        return selectNextWord();
    }

    private Spanned selectNextWord(){
        setTagIndex();
        if (tagIndex == page.size()){
            return null;
        }

        if (matcher == null)
            matcher = pattern.matcher(page.get(tagIndex).getTagContent());

        String result = find();
        if (result != null){
            newPage.set(tagIndex, new HtmlTag(page.get(tagIndex).getTagName(), result));
            return HtmlHelper.convertHtmlPageToSpanned(newPage);
        } else {
            matcher = null;
            return selectNextWord();
        }
    }

    private void setTagIndex(){
        if (matcher == null){
            while (tagIndex < page.size()){
                tagIndex++;
                if (tagIndex == page.size()) {
                    break;
                }
                if (page.get(tagIndex).getTagContent() == null){
                    continue;
                }
                break;
            }
        }
    }

    private String find() {

        if (matcher.find()){
            String match = matcher.group();

            index = page.get(tagIndex).getTagContent().indexOf(match, index);

            String firstPart = page.get(tagIndex).getTagContent().subSequence(0, index).toString();
            String middlePart = startTag + match + finishTag;
            String lastPart = page.get(tagIndex).getTagContent().subSequence(index + match.length(), page.get(tagIndex).getTagContent().length()).toString();

            return firstPart + middlePart + lastPart;
        } else {
            index = 0;
            return null;
        }
    }
}
