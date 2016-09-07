package com.example.alexander.fastreading.reader.dao;

import android.content.Context;
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
import com.example.alexander.fastreading.reader.bookparser.BookDescription;
import com.example.alexander.fastreading.reader.bookparser.tmp.BookParserException;
import com.example.alexander.fastreading.reader.dao.bookdescription.BookDescriptionDao;
import com.example.alexander.fastreading.reader.dao.bookdescription.BookDescriptionDaoFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Alexander on 05.09.2016.
 */
public class DatabaseBookDao implements BookDao {

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


    private BookDescriptionDao bookDescriptionDao;

    public DatabaseBookDao(Context context) {
        this.bookDescriptionDao = BookDescriptionDaoFactory.getDaoFactory(context).getBookDescriptionDao();
    }

    @Override
    public BookDescription addBook(String filePath) throws BookParserException {
        //Проверка что такой книги больше нет
        BookDescription bookDescription = parseBook(filePath);

        long id = bookDescriptionDao.addBookDescription(bookDescription);
        bookDescription.setId(id);

        String fileExtension = FileHelper.getFileExtension(filePath);
        switch (fileExtension){
            case FileHelper.EPUB:
                bookDescription = updateToEpub(filePath, bookDescription);
                bookDescriptionDao.updateBookDescription(bookDescription);
                break;
        }

        return bookDescription;
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
    public CharSequence getScrollText(long bookId) throws BookParserException {
        BookDescription bookDescription = bookDescriptionDao.findBookDescription(bookId);

        String directoryPath = SettingsManager.getBookLibraryPath() + File.separator + bookDescription.getId();
        String saveFilePath = directoryPath + "content.xml";

        Document document = XmlHelper.getXmlFromFile(new File(saveFilePath));
        List<List<HtmlTag>> book = XmlHelper.readEpubFromXml(document);

        return convertToScroll(book);
    }

    @Override
    public List<CharSequence> getChaptersText(long bookId) throws BookParserException {
        return null;
    }

    private BookDescription updateToEpub(String filePath, BookDescription bookDescription) throws BookParserException {
        //Когда у нас есть основные данные о книги мы должны добавить специфические данные и сохранить их на диск
        try {
            FileHelper.unZip(filePath, SettingsManager.getTempPath());

            List<File> files = FileHelper.getFilesCollection(SettingsManager.getTempPath());

            List<String> bookChaptersPaths = Collections.emptyList();

            //Основная инфа о книге (автор, название...)
            for (File file : files) {
                if (file.getName().toLowerCase().equals("content.opf")) {
                    Document contentOpf = XmlHelper.getXmlFromFileWithSpaceTrim(file);

                    bookDescription.setTitle(getBookTitle(contentOpf));
                    bookDescription.setLanguage(getBookLanguage(contentOpf));
                    bookDescription.setAuthor(getBookAuthor(contentOpf));

                    String coverPath = getCoverPath(contentOpf);
                    if (coverPath != null){
                        bookDescription.setCoverImageName(FileHelper.getFileName(coverPath));
                    }
                }

                //Инфа о главах
                if (file.getName().toLowerCase().equals("toc.ncx")) {
                    Document docNcx = XmlHelper.getXmlFromFileWithSpaceTrim(file);
                    bookChaptersPaths = getBookChaptersPaths(docNcx);
                    break;
                }

            }

            //Сами главы
            List<List<HtmlTag>> htmlTagsList = new ArrayList<>(bookChaptersPaths.size());

            for (String bookChapterPath : bookChaptersPaths) {
                for (File file : files) {
                    if (file.getName().equals(bookChapterPath)) {
                        Document chapter = XmlHelper.getXmlFromFileWithSpaceTrim(file);
                        List<HtmlTag> tempChapter = parseChapter(chapter);
                        if (!tempChapter.isEmpty())
                            htmlTagsList.add(tempChapter);
                        break;
                    }
                }
            }

            Document xml = XmlHelper.convertEpubToXml(htmlTagsList);

            String directoryPath = SettingsManager.getBookLibraryPath() + File.separator + bookDescription.getId();
            new File(directoryPath).mkdir();

            String saveFilePath = directoryPath + "content.xml";

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(xml);
            StreamResult streamResult =  new StreamResult(new File(saveFilePath));
            transformer.transform(source, streamResult);

            return bookDescription;
        } catch (Exception e){
            throw new BookParserException(e);
        }
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
}
