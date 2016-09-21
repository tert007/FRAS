package com.example.alexander.fastreading.reader;

import android.text.Spannable;
import android.text.Spanned;

import com.example.alexander.fastreading.reader.dao.bookdao.BookParserException;
import com.example.alexander.fastreading.reader.entity.BookChapter;
import com.example.alexander.fastreading.reader.entity.BookContent;
import com.example.alexander.fastreading.reader.entity.HtmlTag;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Alexander on 26.08.2016.
 */
public class XmlHelper {

    public static Document getXmlFromFileWithSpaceTrim(File file) throws BookParserException {
        //Использует удаление лишних пробелов в xml документе
        try {
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(file), "utf8");
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(removeSpaces(streamReader).getBytes());

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document document = documentBuilder.parse(byteArrayInputStream);
            document.getDocumentElement().normalize();
            return document;
        } catch (Exception e) {
            throw new BookParserException(e);
        }
    }

    public static Document getXmlFromFile(File file) throws BookParserException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document document = documentBuilder.parse(file);
            document.getDocumentElement().normalize();
            return document;
        } catch (Exception e) {
            throw new BookParserException(e);
        }
    }

    private static String removeSpaces(Reader inputReader) throws BookParserException {
        BufferedReader reader = new BufferedReader(inputReader);
        StringBuilder result = new StringBuilder();
        try {
            String line;
            while ( (line = reader.readLine() ) != null)
                result.append(line.trim());
            return result.toString();
        } catch (IOException e) {
            throw new BookParserException(e);
        }
    }

    public static Document convertBookToXml(BookContent bookContent) throws BookParserException {
        //goToXmlParser
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document document = docBuilder.newDocument();

            Element rootElement = document.createElement("book");
            document.appendChild(rootElement);

            List<BookChapter> chapters = bookContent.getBookChapterList();
            int chaptersCount = chapters.size();

            for (int i = 0; i < chaptersCount; i++) {
                Element chapterElement = document.createElement("chapter");
                rootElement.appendChild(chapterElement);

                Spanned title = (Spanned) chapters.get(i).getTitle();
                Spanned content = (Spanned) chapters.get(i).getContent();

                if (title != null) {

                    Element titleElement = document.createElement("title");
                    titleElement.setTextContent(title.toString());

                    chapterElement.appendChild(titleElement);

                    Object[] spans = title.getSpans(0, title.length(), Objects.class);
                    int spansCount = spans.length;

                    if (spansCount > 0){
                        Element titleElementSpans = document.createElement("title-span");

                        for (int j = 0; j < spansCount; j++) {
                            Object a = (spans[j].getClass());
                        }
                    }

                }

                if (content != null) {
                    Element contentElement = document.createElement("content");
                    contentElement.setTextContent(content.toString());
                }





                //chapterElement.appendChild(tagElement);

            }


            return document;

        } catch (Exception e){
            throw new BookParserException(e);
        }
    }

    public static Document convertBookToXml(List<List<HtmlTag>> book) throws BookParserException {
        //goToXmlParser
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document document = docBuilder.newDocument();

            Element rootElement = document.createElement("book");
            document.appendChild(rootElement);

            for (List<HtmlTag> chapter : book) {
                Element chapterElement = document.createElement("chapter");
                rootElement.appendChild(chapterElement);

                for (HtmlTag htmlTag : chapter) {
                    Element tagElement = document.createElement(htmlTag.getTagName());
                    tagElement.setTextContent(htmlTag.getTagContent());

                    chapterElement.appendChild(tagElement);
                }
            }
            return document;

        } catch (Exception e){
            throw new BookParserException(e);
        }
    }

    public static List<List<HtmlTag>> readBookFromXml(Document document){
        NodeList chapterList = document.getElementsByTagName("chapter");

        List<List<HtmlTag>> result = new ArrayList<>(chapterList.getLength());

        for (int i = 0; i < chapterList.getLength(); i++) {
            NodeList htmlTagList = chapterList.item(i).getChildNodes(); // Мб к елементу

            List<HtmlTag> chapter = new ArrayList<>(htmlTagList.getLength());
            for (int j = 0; j < htmlTagList.getLength(); j++) {
                String nodeName = htmlTagList.item(j).getNodeName();
                String nodeValue = htmlTagList.item(j).getTextContent();

                chapter.add(new HtmlTag(nodeName, nodeValue));
            }

            result.add(chapter);
        }
        return result;
    }

}
