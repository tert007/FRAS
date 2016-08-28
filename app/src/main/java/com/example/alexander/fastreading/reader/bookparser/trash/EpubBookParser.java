package com.example.alexander.fastreading.reader.bookparser.trash;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.bookparser.BookParserException;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

/**
 * Created by Alexander on 05.08.2016.
 */
public class EpubBookParser implements BookParser {




    private ZipFile zipFile;
    ArrayList<? extends ZipEntry> zipEntries;

    private Document getXmlFromStream(InputStream inputStream) throws BookParserException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document document = documentBuilder.parse(inputStream);
            document.getDocumentElement().normalize();

            return document;
        } catch (Exception e) {
            throw new BookParserException(e);
        }
    }

    private String getBookTitle(Document document) throws BookParserException{
        return document.getElementsByTagName("dc:title").item(0).getTextContent();
    }

    private String getBookLanguage(Document document) throws BookParserException{
        return document.getElementsByTagName("dc:language").item(0).getTextContent();
    }

    private String getBookAuthor(Document document) throws BookParserException{
        return document.getElementsByTagName("dc:creator").item(0).getTextContent();
    }

    private String getCoverPath(Document document) throws BookParserException{
        String coverId = null;
        String coverFilePath = null;

        NodeList itemNodes = null;

        //Парсинг content.opf и поиск id обложки (В манифесте уже есть путь)
        itemNodes = document.getElementsByTagName("meta");

        for (int i = 0; i < itemNodes.getLength(); i++){
            Element metaElement = (Element)itemNodes.item(i);

            // Если этот мета-тег описывает обложку
            if (metaElement.getAttribute("name").equals("cover")){
                coverId = metaElement.getAttribute("content");
            }
        }

        if (coverId == null){
            return null;
        }

        //Поиск по id пути
        itemNodes = document.getElementsByTagName("item");
        for (int i = 0; i < itemNodes.getLength(); i++){
            Element element = (Element) itemNodes.item(i);

            //Пример  <item id="title.jpg" href="Images/title.jpg" media-type="image/jpeg"/>
            //Проверка на то, что узел имеет вид <item id="coverId"
            if (element != null && element.getAttribute("id").equals(coverId)){
                coverFilePath = element.getAttribute("href");
            }

        }
        return coverFilePath;
    }

    private Map<String, String> parseXml(Document document){
        NodeList bodyNodes = document.getElementsByTagName("body");
        Node bodyTag = bodyNodes.item(0);

        NodeList childNodes = bodyTag.getChildNodes();

        Map<String, String> tagMap = new HashMap<>(childNodes.getLength());
        //НЕ ГОВНО ЛИ?! Мапа вроде не подходит
        for (int i = 0; i < childNodes.getLength(); i++){
            String nodeName = childNodes.item(i).getNodeName();
            String nodeValue = childNodes.item(i).getTextContent();

            tagMap.put(nodeName, nodeValue);
        }

        return tagMap;
    }

    private List<BookChapter> getNavigationPoints(Document document) throws BookParserException {
        NodeList navPoints = document.getElementsByTagName("navPoint");

        List<BookChapter> bookChapters = new ArrayList<>(navPoints.getLength());

        for (int i = 0; i < navPoints.getLength(); i++){
            BookChapter navigationPoint = new BookChapter();

            //Каждый узел в документе
            Node nodePoint = navPoints.item(i);

            //Номер главы в книге по счету
            String index = ((Element) nodePoint).getAttribute("playOrder");
            navigationPoint.setIndex(Integer.valueOf(index));

            NodeList childNodes = nodePoint.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++){
                String nodeName = childNodes.item(j).getNodeName();

                if (nodeName.equals("navLabel")){
                    Node navLabel = childNodes.item(j);

                    NodeList navLabelChildNodes = navLabel.getChildNodes();
                    for (int k = 0; k < navLabelChildNodes.getLength(); k++) {
                        if (navLabelChildNodes.item(k).getNodeName().equals("text")) {
                            //Название главы в книге
                            navigationPoint.setTitle(navLabelChildNodes.item(k).getTextContent());
                            break;
                        }
                    }
                }

                if (childNodes.item(j).getNodeName().equals("content")){
                    String textContentPath = ((Element) childNodes.item(j)).getAttribute("src");

                    for (ZipEntry entry : zipEntries) {
                        String entryName = (new File(entry.getName())).getName();

                        if (entryName.equals(textContentPath)){
                            try {
                                String text = FileHelper.getTextFromFile(zipFile.getInputStream(entry));
                                //navigationPoint.setText(text);
                            } catch (IOException e) {
                                throw new BookParserException(e);
                            }
                        }
                    }
                }

            }
            bookChapters.add(navigationPoint);
        }

        return bookChapters;
    }
    @Override
    public Book getBook(File fileBook) throws BookParserException  {
        EpubBook book = new EpubBook();


        try {
            zipFile =  new ZipFile(fileBook);
            zipEntries = Collections.list(zipFile.entries());

            String coverPath = null;
            Bitmap coverBitmap = null;

            for (ZipEntry entry : zipEntries) {
                String fileName = new File(entry.getName()).getName();
                String parentPathName = new File(entry.getName()).getParent();

                if (fileName.toLowerCase().equals("content.opf")) {
                    Document document = getXmlFromStream(zipFile.getInputStream(entry));

                    String title = getBookTitle(document);
                    String author = getBookAuthor(document);
                    String language = getBookLanguage(document);

                    //fixxxxxxxxxxeeeeeeeeeeeeeee
                    if (parentPathName != null){
                        coverPath = parentPathName + "/" + getCoverPath(document);
                    } else {
                        coverPath = getCoverPath(document);
                    }

                    book.setTitle(title);
                    book.setAuthor(author);
                    book.setLanguage(language);
                }

                if (fileName.toLowerCase().equals("toc.ncx")) {
                    //Document document = getXmlFromStream(zipFile.getInputStream(entry));

                    //book.setBookChapters(getBookChapters(document));
                }

                if (entry.getName().equals(coverPath)){
                    //Пробуем найти обложку за первый проход цикла

                    coverBitmap = BitmapFactory.decodeStream(zipFile.getInputStream(entry));
                    //break;
                }
            }

            if (coverPath != null && coverBitmap == null){
                //Если за первый проход пропустили файл с обложкой

                for (ZipEntry entry : zipEntries) {
                    if (entry.getName().equals(coverPath)){
                        coverBitmap = BitmapFactory.decodeStream(zipFile.getInputStream(entry));
                        break;
                    }
                }
            }

            book.setCover(coverBitmap);
            book.setFilePath(fileBook.getPath());

            return book;
        } catch (IOException e){
            throw new BookParserException(e);
        }
    }
}
