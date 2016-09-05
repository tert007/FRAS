package com.example.alexander.fastreading.reader.bookparser.tmp;

import android.graphics.Typeface;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.HtmlTag;
import com.example.alexander.fastreading.reader.XmlHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexander on 26.08.2016.
 */
public class MyEpubParser implements MyBookParser {

    private final static List<String> supportedNestedTags;
    private final static Map<String, Float> headers;

    private final static Pattern pattern = Pattern.compile("\\w+\\.x?html");

    public final static String HEAD_1_TAG = "h1";
    public final static String HEAD_2_TAG = "h2";
    public final static String HEAD_3_TAG = "h3";
    public final static String HEAD_4_TAG = "h4";
    public final static String HEAD_5_TAG = "h5";
    public final static String HEAD_6_TAG = "h6";
    public final static String NEW_LINE_TAG = "br";

    static {
        supportedNestedTags = new ArrayList<>();
        supportedNestedTags.add("i");
        supportedNestedTags.add("b");
        supportedNestedTags.add("pre");
        supportedNestedTags.add("em");
        supportedNestedTags.add("strong");
        supportedNestedTags.add("sup");
        supportedNestedTags.add("sub");
        supportedNestedTags.add("a");
        supportedNestedTags.add("br");

        headers = new HashMap<>();
        headers.put(HEAD_1_TAG, 1.5f);
        headers.put(HEAD_2_TAG, 1.4f);
        headers.put(HEAD_3_TAG, 1.3f);
        headers.put(HEAD_4_TAG, 1.2f);
        headers.put(HEAD_5_TAG, 1.1f);
        headers.put(HEAD_6_TAG, 1f);
    }

    private static MyEpubParser instance = new MyEpubParser();
    private MyEpubParser() {
    }

    public static MyEpubParser getInstance() {
        return instance;
    }

    @Override
    public CharSequence getScrollText(String filePath) throws BookParserException{
        return convertToScroll(getHtmlTags(filePath));
    }

    @Override
    public List<CharSequence> getPagesText(String filePath) throws BookParserException {
        return convertToChapters(getHtmlTags(filePath));
    }

    public List<List<HtmlTag>> getHtmlTags(String filePath) throws BookParserException {
        try {
            //Каждая глава книги лежит в отдельном листе
            FileHelper.unZip(filePath, SettingsManager.getTempPath());
            List<File> files = FileHelper.getFilesCollection(SettingsManager.getTempPath());

            List<String> bookChaptersPaths = Collections.emptyList();

            for (File file : files) {
                if (file.getName().toLowerCase().equals("toc.ncx")) {
                    Document docNcx = XmlHelper.getXmlFromFile(file);
                    bookChaptersPaths = getBookChaptersPaths(docNcx);
                    break;
                }
            }

            List<List<HtmlTag>> result = new ArrayList<>(bookChaptersPaths.size());

            for (String bookChapterPath : bookChaptersPaths) {
                for (File file : files) {
                    if (file.getName().toLowerCase().equals(bookChapterPath)) {
                        Document chapter = XmlHelper.getXmlFromFile(file);
                        List<HtmlTag> tempChapter = parseChapter(chapter);
                        if (!tempChapter.isEmpty())
                            result.add(tempChapter);
                        break;
                    }
                }
            }
            return result;
        } catch (IOException | NullPointerException e){
            throw new BookParserException(e);
        }
    }


    public static CharSequence convertToScroll(List<List<HtmlTag>> pages) {
        SpannableStringBuilder chapter = new SpannableStringBuilder();

        for (List<HtmlTag> htmlPage : pages) {

            for (HtmlTag htmlTag : htmlPage) {
                if (htmlTag.getTagName().equals(NEW_LINE_TAG)) {
                    chapter.append("\n");
                    break;
                }

                int spanStartPosition = chapter.length();
                int spanEndPosition = spanStartPosition + htmlTag.getTagContent().length();
                chapter.append(htmlTag.getTagContent());
                if (headers.containsKey(htmlTag.getTagName())){
                    chapter.setSpan(new RelativeSizeSpan(headers.get(htmlTag.getTagName())),
                            spanStartPosition, spanEndPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    chapter.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                            spanStartPosition, spanEndPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    chapter.setSpan(new StyleSpan(Typeface.BOLD),
                            spanStartPosition, spanEndPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                chapter.append("\n\n");
            }
        }

        return chapter;
    }

    public static List<CharSequence> convertToChapters(List<List<HtmlTag>> pages) {
        List<CharSequence> result = new ArrayList<>(pages.size());

        for (List<HtmlTag> htmlPage : pages) {
            SpannableStringBuilder chapter = new SpannableStringBuilder();
            for (HtmlTag htmlTag : htmlPage) {
                if (htmlTag.getTagName().equals(NEW_LINE_TAG)) {
                    chapter.append("\n");
                    break;
                }

                int spanStartPosition = chapter.length();
                int spanEndPosition = spanStartPosition + htmlTag.getTagContent().length();
                chapter.append(htmlTag.getTagContent());
                if (headers.containsKey(htmlTag.getTagName())){
                    chapter.setSpan(new RelativeSizeSpan(headers.get(htmlTag.getTagName())),
                            spanStartPosition, spanEndPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    chapter.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                            spanStartPosition, spanEndPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    chapter.setSpan(new StyleSpan(Typeface.BOLD),
                            spanStartPosition, spanEndPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                chapter.append("\n\n");
            }
            result.add(chapter);
        }

        return result;
    }

    private List<String> getBookChaptersPaths(Document docNcx) {
        NodeList navPoints = docNcx.getElementsByTagName("content");
        List<String> bookChaptersContentPath = new ArrayList<>(navPoints.getLength());

        for (int i = 0; i < navPoints.getLength(); i++) {
            String contentPath = ((Element) navPoints.item(i)).getAttribute("src");
            Matcher matcher = pattern.matcher(contentPath);
            matcher.find();
            ///Новый способ ищем только теи content
            contentPath = matcher.group();
            if (!bookChaptersContentPath.contains(contentPath)){
                bookChaptersContentPath.add(contentPath);
            }
        }
        return bookChaptersContentPath;
    }

    public List<HtmlTag> parseChapter(Document document) {
        Node body = document.getElementsByTagName("body").item(0);
        NodeList nodeList = ((Element)body).getElementsByTagName("*");

        List<HtmlTag> chapter = new ArrayList<>(nodeList.getLength());

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).hasChildNodes()) {
                NodeList childNodes = nodeList.item(i).getChildNodes();
                boolean flag = true;
                for (int j = 0; j < childNodes.getLength(); j++) {
                    if (childNodes.item(j).getNodeType() != Node.TEXT_NODE && !supportedNestedTags.contains(childNodes.item(j).getNodeName())) {
                        flag = false;
                    }
                }

                if (flag) {
                    if (!supportedNestedTags.contains(nodeList.item(i).getNodeName())){
                        String nodeName = nodeList.item(i).getNodeName();
                        String nodeValue = nodeList.item(i).getTextContent();

                        if(!nodeValue.replaceAll("\\s+", "").isEmpty()){ // удаление пустых тегов
                            chapter.add(new HtmlTag(nodeName, nodeValue));
                        }
                    }
                }
            }
        }

        return chapter;
    }
}
