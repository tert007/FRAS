package com.example.alexander.fastreading.reader.dao;

import android.content.Context;

import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.XmlHelper;
import com.example.alexander.fastreading.reader.bookparser.BookDescription;
import com.example.alexander.fastreading.reader.bookparser.tmp.BookParserException;
import com.example.alexander.fastreading.reader.dao.bookdescription.BookDescriptionDao;
import com.example.alexander.fastreading.reader.dao.bookdescription.BookDescriptionDaoFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Alexander on 05.09.2016.
 */
public class BookHuiVRotDao implements BookDao {

    private BookDescriptionDao bookDescriptionDao;

    public BookHuiVRotDao(Context context) {
        this.bookDescriptionDao = BookDescriptionDaoFactory.getDaoFactory(context).getBookDescriptionDao();
    }

    @Override
    public long addBook(String filePath) throws BookParserException {
        //Проверка
        BookDescription bookDescription = parseBook(filePath);

        long id = bookDescriptionDao.addBookDescription(bookDescription);

        String fileExtension = FileHelper.getFileExtension(filePath);
        switch (fileExtension){
            case FileHelper.EPUB:
                bookDescription = epubBookParse(filePath, bookDescription);
                return bookDescriptionDao.updateBookDescription(bookDescription);
        }

        return id;
    }

    private BookDescription parseBook(String filePath) throws BookParserException {
        BookDescription bookDescription = new BookDescription();

        bookDescription.setFilePath(filePath);
        bookDescription.setType(FileHelper.getFileExtension(filePath));
        bookDescription.setProgress(0f);
        bookDescription.setFavorite(false);

        return bookDescription;
    }

    @Override
    public void removeBook(long id) throws BookParserException {

    }

    @Override
    public List<BookDescription> getBookDescriptions() throws BookParserException {
        return bookDescriptionDao.getBookDescriptions();
    }



    @Override
    public CharSequence getScrollText(int bookId) throws BookParserException {
        return null;
    }

    @Override
    public List<CharSequence> getPagesText(int bookId) throws BookParserException {
        return null;
    }

    private BookDescription epubBookParse(String filePath, BookDescription bookDescription) throws BookParserException {
        try {
            FileHelper.unZip(filePath, SettingsManager.getTempPath());

            List<File> files = FileHelper.getFilesCollection(SettingsManager.getTempPath());

            for (File file : files) {
                if (file.getName().toLowerCase().equals("content.opf")) {
                    Document contentOpf = XmlHelper.getXmlFromFile(file);

                    bookDescription.setTitle(getBookTitle(contentOpf));
                    bookDescription.setLanguage(getBookLanguage(contentOpf));
                    bookDescription.setAuthor(getBookAuthor(contentOpf));

                    String coverPath = getCoverPath(contentOpf);
                    if (coverPath != null){
                        bookDescription.setCoverImageName(FileHelper.getFileName(coverPath));
                    }

                    //Save Text

                    return bookDescription;
                }
            }

            return null;
        } catch (IOException e){
            throw new BookParserException(e);
        }
    }



    private static String getBookTitle(Document document) throws BookParserException{
        return document.getElementsByTagName("dc:title").item(0).getTextContent();
    }

    private static String getBookLanguage(Document document) throws BookParserException {
        return document.getElementsByTagName("dc:language").item(0).getTextContent();
    }

    private static String getBookAuthor(Document document) throws BookParserException{
        return document.getElementsByTagName("dc:creator").item(0).getTextContent();
    }

    private static String getCoverPath(Document document) throws BookParserException{
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
            /*
            if (element != null && element.getAttribute("id").equals(coverId)){
                coverFilePath = element.getAttribute("href");
            }
            */
            if (element.getAttribute("id").equals(coverId)){
                coverFilePath = element.getAttribute("href");
            }

        }
        return coverFilePath;
    }
}
