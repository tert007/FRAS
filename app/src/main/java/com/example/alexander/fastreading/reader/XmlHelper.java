package com.example.alexander.fastreading.reader;

import com.example.alexander.fastreading.reader.dao.bookdao.BookParserException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Alexander on 26.08.2016.
 */
public class XmlHelper {

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

    public static Document getXmlFromFile(InputStream inputStream) throws BookParserException {
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


}
