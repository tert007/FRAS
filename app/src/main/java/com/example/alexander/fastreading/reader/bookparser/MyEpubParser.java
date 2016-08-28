package com.example.alexander.fastreading.reader.bookparser;

import android.text.Spanned;

import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.reader.FileHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexander on 26.08.2016.
 */
public class MyEpubParser implements MyBookParser {
    private final static List<String> supportedNestedTags;
    private final static Pattern pattern = Pattern.compile("\\w+\\.x?html");

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
    }

    private static MyEpubParser instance = new MyEpubParser();
    private MyEpubParser() {
    }

    public static MyEpubParser getInstance() {
        return instance;
    }

    @Override
    public Spanned getScrollSpannedText(String filePath) throws BookParserException{
        return HtmlHelper.convertHtmlPageToSpanned(getHtmlTagsText(filePath));
    }
    @Override
    public List<HtmlTag> getHtmlTagsText(String filePath) throws BookParserException {
        try {
            FileHelper.unZip(filePath, SettingsManager.getTempPath());
            List<File> files = FileHelper.getFilesCollection(SettingsManager.getTempPath());
            List<String> bookChaptersPaths = null;

            for (File file : files) {
                if (file.getName().toLowerCase().equals("toc.ncx")) {
                    Document docNcx = XmlParser.getXmlFromFile(file);
                    bookChaptersPaths = getBookChaptersPaths(docNcx);
                    break;
                }
            }

            List<HtmlTag> result = new ArrayList<>(bookChaptersPaths.size());

            for (String bookChapterPath : bookChaptersPaths) {
                for (File file : files) {
                    if (file.getName().toLowerCase().equals(bookChapterPath)) {
                        Document chapter = XmlParser.getXmlFromFile(file);

                        result.addAll(parseChapter(chapter)); // Можно разделить каждая глава - отдельный лист
                        break;
                    }
                }
            }

            return result;
        } catch (IOException e){
            throw new BookParserException(e);
        }
    }

    private List<String> getBookChaptersPaths(Document docNcx) {
        NodeList navPoints = docNcx.getElementsByTagName("navPoint");
        List<String> bookChaptersContentPath = new ArrayList<>(navPoints.getLength());

        for (int i = 0; i < navPoints.getLength(); i++) {
            Node nodePoint = navPoints.item(i);
            NodeList childNodes = nodePoint.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                String nodeName = childNodes.item(j).getNodeName();

                if (nodeName.equals("content")) {
                    String contentPath = ((Element) childNodes.item(j)).getAttribute("src");
                    Matcher matcher = pattern.matcher(contentPath);
                    matcher.find();

                    contentPath = matcher.group();
                    if (!bookChaptersContentPath.contains(contentPath)){
                        bookChaptersContentPath.add(contentPath);
                    }
                }
            }
        }
        return bookChaptersContentPath;
    }

    public static List<HtmlTag> parseChapter(Document document) {
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

                        if(!nodeValue.replaceAll("\\s+", "").isEmpty()){
                            chapter.add(new HtmlTag(nodeName, nodeValue));
                        }
                    }
                }
            }

        }

        return chapter;
    }
}
