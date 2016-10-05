package com.example.alexander.fastreading.reader.dao.bookdao;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.util.Base64;
import android.util.Log;

import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.JsonHelper;
import com.example.alexander.fastreading.reader.XmlHelper;
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDao;
import com.example.alexander.fastreading.reader.entity.BookChapter;
import com.example.alexander.fastreading.reader.entity.Fb2BookChapter;
import com.example.alexander.fastreading.reader.entity.BookContent;
import com.example.alexander.fastreading.reader.entity.BookDescription;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Alexander on 29.09.2016.
 */
public class Fb2BookDao extends AbstractBookDao {

    public final static String TITLE_TAG = "title";
    public final static String SUBTITLE_TAG = "subtitle";

    public final static String SECTION_TAG = "section";
    public final static String CITE_TAG = "cite";
    public final static String EMPTY_LINE_TAG = "empty-line";

    public final static String STRONG_TAG = "strong"; //b
    public final static String EMPHASIS_TAG = "emphasis"; //i

    public final static String TEXT_AUTHOR_TAG = "text-author";
    public final static String EPIGRAPH_TAG = "epigraph";
    public final static String PARAGRAPH_TAG = "p";

    public final static String SUB_TAG = "sub";
    public final static String SUP_TAG = "sup";
    public final static String STRIKE_THROUGH_TAG = "strikethrough";

    public final static String POEM_TAG = "poem";
    public final static String STANZA_TAG = "stanza";
    public final static String V_TAG = "v";

    public final static float TITLE_FONT_HEIGHT = 1.5f;
    public final static float SUBTITLE_FONT_HEIGHT = 1.2f;
    public final static float SUB_FONT_HEIGHT = 0.5f;

    private final String booksLibraryPath;

    public Fb2BookDao(BookDescriptionDao bookDescriptionDao, Context context) {
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

        bookDescription.setId(id);
        bookDescription.setFilePath(filePath);
        bookDescription.setType(FileHelper.getFileExtension(filePath));

        Document bookDocument = XmlHelper.getXmlFromFile(new File(filePath));

        Element description = (Element) bookDocument.getElementsByTagName("description").item(0);
        Element titleInfo = (Element) description.getElementsByTagName("title-info").item(0);

        String bookTitle = getBookTitle(titleInfo);
        String bookLanguage = getBookLanguage(titleInfo);
        List<String> authors = getBookAuthors(titleInfo);

        bookDescription.setTitle(bookTitle);
        bookDescription.setLanguage(bookLanguage);
        bookDescription.setAuthor(authors.get(0)); ////////FIX

        String saveDirectoryPath = booksLibraryPath + File.separator + id;

        bookDescription.setCoverImagePath(getCoverImagePath(bookDocument, saveDirectoryPath));
        JsonHelper.saveBook(bookDescription, parseBook(bookDocument), saveDirectoryPath);

        bookDescriptionDao.addBookDescription(bookDescription);

        return bookDescription;
    }

    private String getCoverImagePath(Document bookDocument, String directoryPath) throws BookParserException {
        NodeList coverPageTags = bookDocument.getElementsByTagName("coverpage");
        if (coverPageTags.getLength() == 0)
            return null;

        NodeList coverPageNestedTags = coverPageTags.item(0).getChildNodes();
        int coverPageNestedTagsCount= coverPageNestedTags.getLength();

        Element imageTag = null;

        for (int i = 0; i < coverPageNestedTagsCount; i++) {
            if (coverPageNestedTags.item(i).getNodeType() == Node.ELEMENT_NODE){
                imageTag = (Element) coverPageNestedTags.item(i);
                break;
            }
        }

        if (imageTag == null)
            return null;


        NamedNodeMap imageAttributes = imageTag.getAttributes();
        int imageAttributesCount = imageAttributes.getLength();

        String coverId = null;

        for (int i = 0; i < imageAttributesCount; i++) {
            if (imageAttributes.item(i).getNodeName().indexOf("href") > 0) {
                coverId = imageAttributes.item(i).getNodeValue();
                break;
            }
        }

        if (coverId == null)
            return null;

        if (coverId.charAt(0) == '#') {
            coverId = coverId.substring(1, coverId.length());
        }
        //Найдено имя картнки-обложки (id)

        NodeList binaryTags = bookDocument.getElementsByTagName("binary");
        int binaryTagsCount = binaryTags.getLength();

        Log.d("b-count", String.valueOf(binaryTagsCount));

        for (int i = 0; i < binaryTagsCount; i++) {
            Element binaryTag = (Element) binaryTags.item(i);

            String attribute = binaryTag.getAttribute("id");
            if (attribute != null){
                if (attribute.equals(coverId)){
                    try {
                        String binary = binaryTag.getTextContent();
                        //binary = binary.replace("\n", "");

                        byte[] imageByte = Base64.decode(binary, Base64.DEFAULT);
                        String imagePath = directoryPath + File.separator + coverId;

                        File imageFile = new File(imagePath);
                        imageFile.getParentFile().mkdirs();
                        imageFile.createNewFile();

                        FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                        fileOutputStream.write(imageByte);
                        fileOutputStream.close();

                        return imagePath;
                    } catch (IOException e) {
                        throw new BookParserException(e);
                    }
                }
            }
        }
        return null;
    }

    private String getBookTitle(Element titleInfo) {
        return titleInfo.getElementsByTagName("book-title").item(0).getTextContent();
    }

    private String getBookLanguage(Element titleInfo) {
        return titleInfo.getElementsByTagName("lang").item(0).getTextContent();
    }

    private List<String> getBookAuthors(Element titleInfo) {
        NodeList authorTags = titleInfo.getElementsByTagName("author");
        int authorTagsCount = authorTags.getLength();

        List<String> result = new ArrayList<>(authorTags.getLength());

        for (int i = 0; i < authorTagsCount; i++) {
            NodeList authorNestedTags = authorTags.item(i).getChildNodes();
            int authorNestedTagsCount = authorNestedTags.getLength();

            String firstName = null;
            String middleName = null;
            String lastName = null;

            String nickName = null;

            for (int j = 0; j < authorNestedTagsCount; j++) {
                Node currentAuthorNestedTag = authorNestedTags.item(j);
                String nodeName = currentAuthorNestedTag.getNodeName();

                switch (nodeName) {
                    case "first-name":
                        firstName = currentAuthorNestedTag.getTextContent();
                        break;
                    case "middle-name":
                        middleName = currentAuthorNestedTag.getTextContent();
                        break;
                    case "last-name":
                        lastName = currentAuthorNestedTag.getTextContent();
                        break;
                    case "nickname":
                        nickName = currentAuthorNestedTag.getTextContent();
                        break;
                }
            }

            if (firstName != null) {
                if (middleName != null){
                    result.add(firstName + ' ' + middleName + ' ' + lastName);
                } else {
                    result.add(firstName + ' ' + lastName);
                }
            } else {
                result.add(nickName);
            }
        }
        return result;
    }

    private BookContent parseBook(Document bookDocument) {
        BookContent bookContent = new BookContent();

        int chaptersCount = bookDocument.getElementsByTagName("section").getLength();

        NodeList bodyTags = bookDocument.getElementsByTagName("body");
        int bodyTagsCount = bodyTags.getLength();

        List<BookChapter> bookChapterList = new ArrayList<>(chaptersCount + bodyTagsCount);

        for (int i = 0; i < bodyTagsCount; i++) {
            NodeList sectionTags = ((Element) bodyTags.item(i)).getElementsByTagName("section");
            int sectionTagsCount = sectionTags.getLength();

            bookChapterList.add(parseBody(bodyTags.item(i)));

            for (int j = 0; j < sectionTagsCount; j++) {
                bookChapterList.add(parseSection(sectionTags.item(j)));
            }
        }

        bookContent.setBookChapterList(bookChapterList);

        return bookContent;
    }

    private Fb2BookChapter parseBody(Node body) {
        Fb2BookChapter bookChapter = new Fb2BookChapter();

        NodeList bodyNestedTags = body.getChildNodes();
        int bodyNestedTagsCount = bodyNestedTags.getLength();

        for (int i = 0; i < bodyNestedTagsCount; i++) {
            Node currentTag = bodyNestedTags.item(i);
            String currentTagName = currentTag.getNodeName();

            switch (currentTagName){
                case TITLE_TAG:
                    CharSequence title = parseTag(currentTag);
                    bookChapter.setTitle(title);
                    break;
                case EPIGRAPH_TAG:
                    CharSequence epigraph = parseTag(currentTag);
                    bookChapter.setContent(epigraph);
                    break;
            }
        }

        return bookChapter;
    }

    private Fb2BookChapter parseSection(Node section) {
        NodeList sectionNestedTags = section.getChildNodes();
        int sectionNestedTagsCount = sectionNestedTags.getLength();

        Fb2BookChapter bookChapter = new Fb2BookChapter();
        SpannableStringBuilder builder = new SpannableStringBuilder();

        for (int i = 0; i < sectionNestedTagsCount; i++) {
            Node currentTag = sectionNestedTags.item(i);
            String currentTagName = currentTag.getNodeName();

            switch (currentTagName){
                case TITLE_TAG:
                    CharSequence title = parseTag(currentTag);
                    bookChapter.setTitle(title);
                    break;
                case EPIGRAPH_TAG:
                    builder.append(parseTag(currentTag));
                    break;
                case SECTION_TAG:
                    //do nothing
                    break;
                default:
                    builder.append(parseTag(currentTag));
                    break;
            }
        }

        if (builder.length() > 0) {
            bookChapter.setContent(builder);
        }

        return bookChapter;
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
            case TITLE_TAG: {
                builder.setSpan(new RelativeSizeSpan(TITLE_FONT_HEIGHT),
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
            case SUBTITLE_TAG:  {
                builder.setSpan(new RelativeSizeSpan(SUBTITLE_FONT_HEIGHT),
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
            case PARAGRAPH_TAG: {
                int builderLength = builder.length();
                if (builderLength < 1) {
                    break;
                }

                if (!(builder.charAt(builderLength - 1) == '\n')) {
                    builder.append("\n\n");
                }
                break;
            }
            case EPIGRAPH_TAG: {
                int builderLength = builder.length();
                if (builderLength < 1) {
                    break;
                }

                if (!(builder.charAt(builderLength - 1) == '\n')) {
                    builder.append("\n");
                }
                break;
            }
            case POEM_TAG: {
                int builderLength = builder.length();
                if (builderLength < 1) {
                    break;
                }

                if (!(builder.charAt(builderLength - 1) == '\n')) {
                    builder.append("\n\n");
                }
                break;
            }
            case STANZA_TAG: {
                int builderLength = builder.length();
                if (builderLength < 1) {
                    break;
                }

                builder.setSpan(new StyleSpan(Typeface.ITALIC),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                if (!(builder.charAt(builderLength - 1) == '\n')) {
                    builder.append("\n\n");
                }
                break;
            }
            case V_TAG: {
                int builderLength = builder.length();
                if (builderLength < 1) {
                    break;
                }

                if (!(builder.charAt(builderLength - 1) == '\n')) {
                    builder.append("\n");
                }
                break;
            }
            case CITE_TAG:
                builder.setSpan(new StyleSpan(Typeface.ITALIC),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case TEXT_AUTHOR_TAG:{
                int builderLength = builder.length();
                if (builderLength < 1) {
                    break;
                }

                if (!(builder.charAt(builderLength - 1) == '\n')) {
                    builder.append("\n");
                }
                break;
            }
            case EMPHASIS_TAG:
                builder.setSpan(new StyleSpan(Typeface.ITALIC),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case STRONG_TAG:
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
            case EMPTY_LINE_TAG:
                builder.append("\n");
                break;
        }

        return builder;
    }

    @Override
    public BookContent getBookContent(BookDescription bookDescription) throws BookParserException {
        String directoryPath = booksLibraryPath + File.separator + bookDescription.getId();
        return JsonHelper.readBook(bookDescription, directoryPath);
    }

    @Override
    public void removeBook(long id) {
        bookDescriptionDao.removeBookDescription(id);
        FileHelper.removeDirectory(new File(booksLibraryPath + File.separator + String.valueOf(id)));
    }
}
