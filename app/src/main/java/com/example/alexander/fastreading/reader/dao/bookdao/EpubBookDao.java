package com.example.alexander.fastreading.reader.dao.bookdao;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.UnderlineSpan;

import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.JsonHelper;
import com.example.alexander.fastreading.reader.XmlHelper;
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDao;
import com.example.alexander.fastreading.reader.entity.BookChapter;
import com.example.alexander.fastreading.reader.entity.EpubBookChapter;
import com.example.alexander.fastreading.reader.entity.BookContent;
import com.example.alexander.fastreading.reader.entity.BookDescription;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Alexander on 30.09.2016.
 */
public class EpubBookDao extends AbstractBookDao {

    private final static Pattern xmlPattern = Pattern.compile(".+\\.x?html");

    private final static String EMPTY_LINE_TAG = "br";

    private final static String STRONG_TAG_1 = "strong"; //b
    private final static String STRONG_TAG_2 = "b";

    private final static String EMPHASIS_TAG_1 = "em"; //i
    private final static String EMPHASIS_TAG_2 = "i";

    private final static String STRIKE_THROUGH_TAG = "strike"; //Перечеркнутый
    private final static String UNDERLINE_TAG = "u"; //Подчеркнутый

    private final static String SUB_TAG = "sub";
    private final static String SUP_TAG = "sup";

    private final static String PARAGRAPH_TAG_1 = "p";
    private final static String PARAGRAPH_TAG_2 = "div";

    //LI - СДЕЛАТЬ

    private final static String HEAD_1_TAG = "h1";
    private final static String HEAD_2_TAG = "h2";
    private final static String HEAD_3_TAG = "h3";
    private final static String HEAD_4_TAG = "h4";
    private final static String HEAD_5_TAG = "h5";
    private final static String HEAD_6_TAG = "h6";

    public final static float SUB_FONT_HEIGHT = 0.5f;

    private final static Map<String, Float> headers;

    static {
        headers = new HashMap<>();

        headers.put(HEAD_1_TAG, 1.5f);
        headers.put(HEAD_2_TAG, 1.4f);
        headers.put(HEAD_3_TAG, 1.3f);
        headers.put(HEAD_4_TAG, 1.2f);
        headers.put(HEAD_5_TAG, 1.1f);
        headers.put(HEAD_6_TAG, 1f);
    }

    private final String booksLibraryPath;

    public EpubBookDao(BookDescriptionDao bookDescriptionDao, Context context) {
        super(bookDescriptionDao);
        booksLibraryPath = context.getApplicationInfo().dataDir + File.separator + "books";
    }

    @Override
    public BookDescription addBook(String filePath) throws BookParserException, BookHasBeenAddedException {
        if (bookDescriptionDao.findBookDescription(filePath) != null) {
            throw new BookHasBeenAddedException("The book has been added");
        }

        long id = bookDescriptionDao.getNextItemId();

        BookDescription bookDescription = new BookDescription();

        ZipFile zipFile = null;

        try {
            zipFile = new ZipFile(filePath);

            List<? extends ZipEntry> zipEntries = Collections.list(zipFile.entries());

            List<String> bookChaptersPaths = Collections.emptyList();
            String coverPath = null;

            for (ZipEntry zipEntry : zipEntries) {
                String fileName = new File(zipEntry.getName()).getName();

                //Основная инфа о книге (автор, название...)
                if (fileName.toLowerCase().equals("content.opf")) {
                    Document contentOpf = XmlHelper.getXmlFromFile(zipFile.getInputStream(zipEntry));

                    bookDescription.setTitle(getBookTitle(contentOpf));
                    bookDescription.setLanguage(getBookLanguage(contentOpf));
                    bookDescription.setAuthor(getBookAuthor(contentOpf));

                    coverPath = getCoverPath(contentOpf);
                }
            }

            if (coverPath != null) {
                String coverName = new File(coverPath).getName();
                String imagePath = booksLibraryPath + File.separator + id + File.separator + coverName;

                File imageFile = new File(imagePath);
                imageFile.getParentFile().mkdirs();

                for (ZipEntry zipEntry : zipEntries) {
                    String fileName = new File(zipEntry.getName()).getName();

                    if (fileName.equals(coverName)){
                        FileHelper.copyFile(zipFile.getInputStream(zipEntry), imageFile);
                        bookDescription.setCoverImagePath(imagePath);
                        break;
                    }
                }
            }
            zipFile.close();

        } catch (IOException e){
            throw new BookParserException(e);
        }

        bookDescription.setId(id);
        bookDescription.setFilePath(filePath);
        bookDescription.setType(FileHelper.getFileExtension(filePath));

        String directoryPath = booksLibraryPath + File.separator + id;

        BookContent bookContent = parseBook(filePath);
        JsonHelper.saveBook(bookDescription, bookContent, directoryPath);

        bookDescriptionDao.addBookDescription(bookDescription);

        return bookDescription;
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
    public BookContent getBookContent(BookDescription bookDescription) throws BookParserException {
        String directoryPath = booksLibraryPath + File.separator + bookDescription.getId();
        return JsonHelper.readBook(bookDescription, directoryPath);
    }

    private BookContent parseBook(String filePath) throws BookParserException {
        try {
            ZipFile zipFile = new ZipFile(filePath);
            List<? extends ZipEntry> zipEntries = Collections.list(zipFile.entries());
            List<NavigationPoint> navigationPoints = Collections.emptyList();

            for (ZipEntry zipEntry : zipEntries) {
                String fileName = new File(zipEntry.getName()).getName();

                //Основная инфа о книге (автор, название...)
                if (fileName.toLowerCase().equals("toc.ncx")) {
                    Document docNcx = XmlHelper.getXmlFromFile(zipFile.getInputStream(zipEntry));
                    navigationPoints = getBookNavigationPoints(docNcx);
                    break;
                }
            }

            BookContent bookContent = new BookContent();
            List<BookChapter> bookChapters = new LinkedList<>();

            for (int i = 0; i < navigationPoints.size(); i++) {
                String bookChapterId = navigationPoints.get(i).getBookChapterId();
                String bookChapterPath = navigationPoints.get(i).getBookChapterPath();
                String bookChapterTitle = navigationPoints.get(i).getBookChapterTitle();

                for (ZipEntry zipEntry : zipEntries) {
                    String fileName = new File(zipEntry.getName()).getName();

                    if (bookChapterPath.equals(fileName)) {
                        if (bookChapterId == null) {
                            Document chapterDocument = XmlHelper.getXmlFromFile(zipFile.getInputStream(zipEntry));
                            CharSequence bookChapterContent = parseChapter(chapterDocument);

                            if (! bookChapterContent.toString().trim().isEmpty())
                                bookChapters.add(new EpubBookChapter(bookChapterTitle, bookChapterContent));
                            break;
                        } else {
                            List<String> ids = new ArrayList<>();
                            List<String> titles = new ArrayList<>();

                            ids.add(bookChapterId);
                            titles.add(bookChapterTitle);

                            for (int j = i + 1; j < navigationPoints.size(); j++) {
                                if (bookChapterPath.equals(navigationPoints.get(j).getBookChapterPath())) {
                                    ids.add(navigationPoints.get(j).getBookChapterId());
                                    titles.add(navigationPoints.get(j).getBookChapterTitle());
                                    i++;
                                }
                            }

                            String html = FileHelper.getTextFromFile(zipFile.getInputStream(zipEntry));
                            Document chapterDocument = XmlHelper.getXmlFromFile(zipFile.getInputStream(zipEntry));

                            List<BookChapter> combineChapters = parseCombineChapter(chapterDocument, html, ids, titles);
                            bookChapters.addAll(combineChapters);
                        }
                    }
                }
            }

            bookContent.setBookChapterList(bookChapters);
            return bookContent;
        } catch (IOException e) {
            throw new BookParserException(e);
        }
    }

    private List<BookChapter> parseCombineChapter (Document combineChapterDocument, String html, List<String> ids, List<String> titles) throws BookParserException {
        //Парсим страницы в которых одновременно несколько глав
        CharSequence combineChapter = parseChapter(combineChapterDocument);

        String bodyRegex = "<body[\\s\\S]+</body>";

        Pattern bodyPattern = Pattern.compile(bodyRegex);
        Matcher matcher = bodyPattern.matcher(html);

        String body = html;

        if (matcher.find()) {
            body = html.substring(matcher.start(), matcher.end());
            body = body.replaceAll("\n", ""); ///   \r
        }

        int chaptersCount = ids.size();
        int[] startIndexes = new int[chaptersCount];
        List<BookChapter> bookChapters = new ArrayList<>(chaptersCount);

        for (int i = 0; i < chaptersCount; i++) {
            StringBuilder request = new StringBuilder();
            request.append("id=\"");
            request.append(ids.get(i));
            request.append("\"");
            //request.append("[\\s\\S]");

            Pattern pattern = Pattern.compile(request.toString());
            matcher = pattern.matcher(body);

            if (matcher.find()) {
                int bodyLength = body.length();

                int startIndex = 0;
                int endIndex = bodyLength;

                boolean itsTag = false;

                for (int j = matcher.start(); j < bodyLength; j++) {
                    if (body.charAt(j) == '>') {
                        startIndex = j + 1;
                        break;
                    }
                }

                for (int j = startIndex; j < bodyLength; j++) {
                    if (body.charAt(j) == '<') {
                        itsTag = true;
                    } else if (body.charAt(j) == '>') {
                        if (itsTag)
                            itsTag = false;
                    } else {
                        if (!itsTag) {
                            startIndex = j;
                            break;
                        }
                    }
                }

                for (int j = startIndex; j < bodyLength; j++) {
                    if (body.charAt(j) == '<') {
                        endIndex = j - 1;
                        break;
                    }
                }

                String partOfContent = body.substring(startIndex, endIndex + 1).trim(); //ТО, ЧТО ЕСТЬ В ГЛАВЕ
                body = body.substring(endIndex + 1);

                Pattern combinePattern = Pattern.compile(partOfContent);
                Matcher combineMatcher = combinePattern.matcher(combineChapter);

                if (i > 0) {
                    if (combineMatcher.find(startIndexes[i - 1])) {
                        startIndexes[i] = combineMatcher.start();
                    }
                } else {
                    if (combineMatcher.find()) {
                        startIndexes[i] = combineMatcher.start();
                    }
                }
            }
        }

        for (int i = 0; i < chaptersCount - 1; i++) {
            CharSequence title = titles.get(i);
            CharSequence chapter = combineChapter.subSequence(startIndexes[i], startIndexes[i + 1]);

            if (! chapter.toString().trim().isEmpty())
                bookChapters.add(new EpubBookChapter(title, chapter));
        }
        CharSequence title = titles.get(chaptersCount - 1);
        CharSequence chapter = combineChapter.subSequence(startIndexes[chaptersCount - 1], combineChapter.length());

        if (! chapter.toString().trim().isEmpty())
            bookChapters.add(new EpubBookChapter(title, chapter));

        return bookChapters;
    }

    private class NavigationPoint {
        private String bookChapterTitle;
        private String bookChapterPath;
        private String bookChapterId;

        public NavigationPoint(String bookChapterTitle, String bookChapterPath) {
            this.bookChapterTitle = bookChapterTitle;
            this.bookChapterPath = bookChapterPath;
        }

        public NavigationPoint(String bookChapterTitle, String bookChapterPath, String bookChapterId) {
            this.bookChapterTitle = bookChapterTitle;
            this.bookChapterPath = bookChapterPath;
            this.bookChapterId = bookChapterId;
        }

        public String getBookChapterTitle() {
            return bookChapterTitle;
        }

        public String getBookChapterPath() {
            return bookChapterPath;
        }

        public String getBookChapterId() {
            return bookChapterId;
        }
    }

    private List<NavigationPoint> getBookNavigationPoints(Document docNcx) {
        NodeList navPoints = docNcx.getElementsByTagName("navPoint");
        int navPointsCount = navPoints.getLength();

        List<String> bookChapterContentPaths = new ArrayList<>(navPointsCount);
        List<String> bookChapterTitles = new ArrayList<>(navPointsCount);
        List<String> bookChapterId = new ArrayList<>(navPointsCount);

        for (int i = 0; i < navPointsCount; i++) {
            NodeList navPointChildNodes = navPoints.item(i).getChildNodes();
            int navPointChildNodesCount = navPointChildNodes.getLength();

            String contentPath = null;
            String contentTitle = null;
            String contentId = null;

            for (int j = 0; j < navPointChildNodesCount; j++) {
                if (navPointChildNodes.item(j).getNodeName().equals("content")) {

                    contentPath = ((Element) navPointChildNodes.item(j)).getAttribute("src");
                    Matcher matcher = xmlPattern.matcher(contentPath);
                    ////РАссмотреть случай когда # в имени, а не в конце...
                    int sharpIndex = contentPath.lastIndexOf('#');
                    if (sharpIndex > 0) {
                        contentId = contentPath.substring(sharpIndex + 1);
                    }

                    if (matcher.find()){
                        contentPath = matcher.group();
                        int index;
                        if ((index = contentPath.lastIndexOf('/')) > -1){
                            contentPath = contentPath.substring(index + 1);
                        }
                    }
                }

                if (navPointChildNodes.item(j).getNodeName().equals("navLabel")) {
                    NodeList navLabelChildNodes = navPointChildNodes.item(j).getChildNodes();
                    int navLabelChildNodesCount = navLabelChildNodes.getLength();

                    for (int k = 0; k < navLabelChildNodesCount; k++) {
                        if (navLabelChildNodes.item(k).getNodeName().equals("text")){
                            contentTitle = navLabelChildNodes.item(k).getTextContent();
                            break;
                        }
                    }
                }
            }

            bookChapterContentPaths.add(contentPath);
            bookChapterTitles.add(contentTitle);
            bookChapterId.add(contentId);
        }

        List<NavigationPoint> navigationPoints = new ArrayList<>(navPointsCount);
        for (int i = 0; i < navPointsCount; i++) {
            navigationPoints.add(new NavigationPoint(bookChapterTitles.get(i), bookChapterContentPaths.get(i), bookChapterId.get(i)));
        }

        return navigationPoints;
    }

    private CharSequence parseChapter(Document bookDocument) {
        Node body = bookDocument.getElementsByTagName("body").item(0);

        return parseBody(body);
    }

    private CharSequence parseBody(Node body) {
        NodeList bodyChildNotes = body.getChildNodes();
        int bodyChildNotesCount = bodyChildNotes.getLength();

        SpannableStringBuilder builder = new SpannableStringBuilder();

        for (int i = 0; i < bodyChildNotesCount; i++) {
            Node currentTag = bodyChildNotes.item(i);

            if (currentTag.getNodeType() == Node.ELEMENT_NODE) {
                builder.append(parseTag(currentTag));
            }
        }

        return builder;
    }

    private CharSequence parseTag(Node tag) {

        if (tag.getNodeType() == Node.TEXT_NODE){
            String tagNodeValue = tag.getNodeValue().trim();
            //Если это самый внутренни тег и при этом не "левый" \n
            if (!tagNodeValue.isEmpty()){
                return tagNodeValue;
            } else {
                return "";
            }
        }

        SpannableStringBuilder builder = new SpannableStringBuilder();

        NodeList nestedTags = tag.getChildNodes();
        int nestedTagsCount = nestedTags.getLength();

        for (int i = 0; i < nestedTagsCount; i++) {
            builder.append(parseTag(nestedTags.item(i)));
        }

        switch (tag.getNodeName()) {
            case HEAD_1_TAG: {
                builder.setSpan(new RelativeSizeSpan(headers.get(HEAD_1_TAG)),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                int builderLength = builder.length();
                if (builderLength < 1) {
                    break;
                }

                if (!(builder.charAt(builderLength - 1) == '\n')) {
                    builder.append("\n\n");
                }

                break;
            }
            case HEAD_2_TAG: {
                builder.setSpan(new RelativeSizeSpan(headers.get(HEAD_2_TAG)),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                int builderLength = builder.length();
                if (builderLength < 1) {
                    break;
                }

                if (!(builder.charAt(builderLength - 1) == '\n')) {
                    builder.append("\n\n");
                }

                break;
            }
            case HEAD_3_TAG:
                builder.setSpan(new RelativeSizeSpan(headers.get(HEAD_3_TAG)),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case HEAD_4_TAG:
                builder.setSpan(new RelativeSizeSpan(headers.get(HEAD_4_TAG)),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case HEAD_5_TAG:
                builder.setSpan(new RelativeSizeSpan(headers.get(HEAD_5_TAG)),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case HEAD_6_TAG:
                builder.setSpan(new RelativeSizeSpan(headers.get(HEAD_6_TAG)),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case PARAGRAPH_TAG_1: {
                int builderLength = builder.length();
                if (builderLength < 1) {
                    break;
                }

                if (!(builder.charAt(builderLength - 1) == '\n')) {
                    builder.append("\n\n");
                }
                break;
            }
            case PARAGRAPH_TAG_2: {
                int builderLength = builder.length();
                if (builderLength < 1) {
                    break;
                }

                if (!(builder.charAt(builderLength - 1) == '\n')) {
                    builder.append("\n\n");
                }
                break;
            }
            case EMPHASIS_TAG_1:
                builder.setSpan(new StyleSpan(Typeface.ITALIC),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case EMPHASIS_TAG_2:
                builder.setSpan(new StyleSpan(Typeface.ITALIC),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case STRONG_TAG_1:
                builder.setSpan(new StyleSpan(Typeface.BOLD),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case STRONG_TAG_2:
                builder.setSpan(new StyleSpan(Typeface.BOLD),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case SUB_TAG:
                builder.setSpan(new RelativeSizeSpan(SUB_FONT_HEIGHT),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new SubscriptSpan(),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case SUP_TAG:
                builder.setSpan(new RelativeSizeSpan(SUB_FONT_HEIGHT),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new SuperscriptSpan(),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case STRIKE_THROUGH_TAG:
                builder.setSpan(new StrikethroughSpan(),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case UNDERLINE_TAG:
                builder.setSpan(new UnderlineSpan(),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case EMPTY_LINE_TAG:
                builder.append("\n");
                break;
        }

        return builder;
    }

    @Override
    public void removeBook(long id) {
        bookDescriptionDao.removeBookDescription(id);
        FileHelper.removeDirectory(new File(booksLibraryPath + File.separator + String.valueOf(id)));
    }
}
