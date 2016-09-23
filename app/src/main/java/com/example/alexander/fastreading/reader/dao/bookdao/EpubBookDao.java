package com.example.alexander.fastreading.reader.dao.bookdao;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.entity.HtmlTag;
import com.example.alexander.fastreading.reader.XmlHelper;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDao;
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDaoFactory;

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

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Alexander on 07.09.2016.
 */
public class EpubBookDao implements BookDao {

    private final static List<String> supportedNestedTags;
    private final static Map<String, Float> headers;

    private final static Pattern xmlPattern = Pattern.compile(".+\\.x?html");

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
    private final String booksLibraryPath;
    private final String unzipTempPath;

    public EpubBookDao(Context context) {
        bookDescriptionDao = BookDescriptionDaoFactory.getDaoFactory(context).getBookDescriptionDao();
        unzipTempPath = context.getApplicationInfo().dataDir + File.separator + "temp";
        booksLibraryPath = context.getApplicationInfo().dataDir + File.separator + "books";

        File bookLibraryDirectory = new File(booksLibraryPath);
        bookLibraryDirectory.mkdir();
    }

    @Override
    public BookDescription addBook(String filePath) throws BookParserException {
        BookDescription bookDescription = createBookDescription(filePath);

        long id = bookDescriptionDao.addBookDescription(bookDescription);
        if (id == -1)
            return null;

        bookDescription.setId(id);

        bookDescription = updateBookDescriptionToEpub(bookDescription);
        bookDescriptionDao.updateBookDescription(bookDescription);

        return bookDescription;
    }

    private BookDescription createBookDescription(String filePath) {
        BookDescription bookDescription = new BookDescription();

        bookDescription.setFilePath(filePath);
        bookDescription.setType(FileHelper.getFileExtension(filePath));

        return bookDescription;
    }

    private BookDescription updateBookDescriptionToEpub(BookDescription bookDescription) throws BookParserException {
        //Когда у нас есть основные данные о книги мы должны добавить специфические данные и сохранить их на диск
        //А также сохранить сам текст в нужном нам формате в папку с приложением
        try {
            FileHelper.unZip(bookDescription.getFilePath(), unzipTempPath);

            List<File> files = FileHelper.getFilesCollection(unzipTempPath);

            List<String> bookChaptersPaths = Collections.emptyList();
            String coverPath = null;

            //Основная инфа о книге (автор, название...)
            for (File file : files) {
                if (file.getName().toLowerCase().equals("content.opf")) {
                    Document contentOpf = XmlHelper.getXmlFromFileWithSpaceTrim(file);

                    bookDescription.setTitle(getBookTitle(contentOpf));
                    bookDescription.setLanguage(getBookLanguage(contentOpf));
                    bookDescription.setAuthor(getBookAuthor(contentOpf));

                    coverPath = getCoverPath(contentOpf);
                }

                //Пути к файлам с главами книг
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

            Document xml = XmlHelper.convertBookToXml(htmlTagsList);

            ///data/.../books/1
            String bookDirectoryPath = booksLibraryPath + File.separator + bookDescription.getId();
            File bookDirectory = new File(bookDirectoryPath);
            bookDirectory.mkdir();

            String saveFilePath = bookDirectoryPath + File.separator + "content.xml";

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(xml);
            StreamResult streamResult =  new StreamResult(new File(saveFilePath));
            transformer.transform(source, streamResult);

            if (coverPath != null){
                String coverName = new File(coverPath).getName();
                String saveCoverPath = bookDirectoryPath + File.separator + coverName;

                for (File file : files) {
                    if (file.getName().equals(coverName)){
                        FileHelper.copyFile(file, new File(saveCoverPath));
                        bookDescription.setCoverImagePath(saveCoverPath);
                        break;
                    }
                }
            }

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
            Matcher matcher = xmlPattern.matcher(contentPath);

            if (matcher.find()){
                contentPath = matcher.group();
                int index;
                if ((index = contentPath.lastIndexOf('/')) > -1){
                    contentPath = contentPath.substring(index + 1);
                }

                if (!bookChaptersContentPath.contains(contentPath)){
                    bookChaptersContentPath.add(contentPath);
                }
            }
        }
        return bookChaptersContentPath;
    }

    private List<HtmlTag> parseChapter(Document document) {
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

    private String getBookTitle(Document document) {
        return document.getElementsByTagName("dc:title").item(0).getTextContent();
    }

    private String getBookLanguage(Document document) {
        return document.getElementsByTagName("dc:language").item(0).getTextContent();
    }

    private String getBookAuthor(Document document) {
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

            if (element.getAttribute("id").equals(coverId)){
                coverFilePath = element.getAttribute("href");
            }

        }
        return coverFilePath;
    }

    @Override
    public void removeBook(BookDescription bookDescription) {
        String directoryPath = booksLibraryPath + File.separator + bookDescription.getId();
        FileHelper.removeDirectory(new File(directoryPath));

        bookDescriptionDao.removeBookDescription(bookDescription.getId());
    }

    @Override
    public CharSequence getScrollText(BookDescription bookDescription) throws BookParserException {
        String directoryPath = booksLibraryPath + File.separator + bookDescription.getId();
        String saveFilePath = directoryPath + File.separator + "content.xml";

        Document document = XmlHelper.getXmlFromFile(new File(saveFilePath));
        List<List<HtmlTag>> book = XmlHelper.readBookFromXml(document);

        return convertToScroll(book);
    }

    @Override
    public List<CharSequence> getChaptersText(BookDescription bookDescription) throws BookParserException {
        String directoryPath = booksLibraryPath + File.separator + bookDescription.getId();
        String saveFilePath = directoryPath + File.separator + "content.xml";

        Document document = XmlHelper.getXmlFromFile(new File(saveFilePath));
        List<List<HtmlTag>> book = XmlHelper.readBookFromXml(document);

        return convertToChapters(book);
    }

    private CharSequence convertToScroll(List<List<HtmlTag>> pages) {
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

    public List<CharSequence> convertToChapters(List<List<HtmlTag>> pages) {
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
