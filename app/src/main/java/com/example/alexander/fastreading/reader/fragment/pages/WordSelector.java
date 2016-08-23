package com.example.alexander.fastreading.reader.fragment.pages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexander on 22.08.2016.
 */
public class WordSelector {
    private String inputText;

    private int index;

    private final static String regExp = "\\b([\\w]+)\\b";
    private final static String startTag = "<font color='red'>";
    private final static String finishTag = "</font>";

    private final static String START_PARAGRAPH_TAG = "<p>";
    private final static String FINISH_PARAGRAPH_TAG = "</p>";

    private final static String NEW_LINE_TAG = "<br>";

    private Pattern pattern;
    private Matcher matcher;

    public WordSelector(String inputText) {
        this.inputText = inputText;

        pattern = Pattern.compile(regExp);
        matcher = pattern.matcher(inputText);
    }

    public String getNextSelectedWord(){
        if (matcher.find()){

            String match = matcher.group();
            index = inputText.indexOf(match, index);

            String firstPart = inputText.subSequence(0, index).toString();
            String middlePart = startTag + match + finishTag;
            String lastPart = inputText.subSequence(index + match.length(), inputText.length()).toString();

            String result = firstPart + middlePart + lastPart;
            String[] lines = result.split("\n");

            result = "";

            for (int i = 0; i < lines.length; i++) {
                lines[i] = lines[i].trim();
                if (lines[i].isEmpty()) {
                    //lines[i] = NEW_LINE_TAG;
                } else {
                    lines[i] = START_PARAGRAPH_TAG + lines[i] + FINISH_PARAGRAPH_TAG;
                }

                result += lines[i];
            }

            return result;
        } else {
            return null;
        }
    }
}
