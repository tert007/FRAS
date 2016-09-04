package com.example.alexander.fastreading.reader;

import android.os.SystemClock;
import android.util.Log;

import com.example.alexander.fastreading.reader.bookparser.BookParserException;

import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Alexander on 26.08.2016.
 */
public class XmlHelper {

    public static Document getXmlFromFile(File file) throws BookParserException {
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
}
