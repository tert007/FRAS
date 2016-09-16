package com.example.alexander.fastreading.reader.dao.bookdao;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;

import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.XmlHelper;
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDao;
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDaoFactory;
import com.example.alexander.fastreading.reader.entity.BookChapter;
import com.example.alexander.fastreading.reader.entity.BookContent;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.entity.HtmlTag;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexander on 14.09.2016.
 */
public class Fb2BookDao implements BookDao {

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

    private BookDescriptionDao bookDescriptionDao;

    public Fb2BookDao(Context context) {
        bookDescriptionDao = BookDescriptionDaoFactory.getDaoFactory(context).getBookDescriptionDao();
    }

    @Override
    public BookDescription addBook(String filePath) throws BookParserException {
        BookDescription bookDescription = createBookDescription(filePath);

        long id = bookDescriptionDao.addBookDescription(bookDescription);
        bookDescription.setId(id);

        bookDescription = updateBookDescriptionToFb2(bookDescription);
        bookDescriptionDao.updateBookDescription(bookDescription);

        return bookDescription;
    }

    private BookDescription createBookDescription(String filePath) {
        BookDescription bookDescription = new BookDescription();

        bookDescription.setFilePath(filePath);
        bookDescription.setType(FileHelper.getFileExtension(filePath));

        return bookDescription;
    }

    private BookDescription updateBookDescriptionToFb2(BookDescription bookDescription) throws BookParserException  {
        Log.d("parse start", "Start");

        Document bookDocument = XmlHelper.getXmlFromFile(new File(bookDescription.getFilePath()));

        Element description = (Element) bookDocument.getElementsByTagName("description").item(0);
        Element titleInfo = (Element) description.getElementsByTagName("title-info").item(0);

        String bookTitle = getBookTitle(titleInfo);
        String bookLanguage = getBookLanguage(titleInfo);
        List<String> authors = getBookAuthors(titleInfo);

        bookDescription.setTitle(bookTitle);
        bookDescription.setLanguage(bookLanguage);
        bookDescription.setAuthor(authors.get(0)); ////////FIX

        Log.d("parse end", "end");

        return bookDescription;
    }

    private String getBookTitle(Element titleInfo) {
        return titleInfo.getElementsByTagName("book-title").item(0).getTextContent();
    }

    private String getBookLanguage(Element titleInfo) {
        return titleInfo.getElementsByTagName("lang").item(0).getTextContent();
    }

    private List<String> getBookAuthors(Element titleInfo) {
        NodeList authors = titleInfo.getElementsByTagName("author");

        List<String> result = new ArrayList<>(authors.getLength());

        for (int i = 0; i < authors.getLength(); i++) {
            NodeList authorsTags = authors.item(i).getChildNodes();

            String firstName = null;
            String middleName = null;
            String lastName = null;

            String nickName = null;

            for (int j = 0; j < authorsTags.getLength(); j++) {
                String nodeName = authorsTags.item(j).getNodeName();

                switch (nodeName) {
                    case "first-name":
                        firstName = authorsTags.item(j).getTextContent();
                        break;
                    case "middle-name":
                        middleName = authorsTags.item(j).getTextContent();
                        break;
                    case "last-name":
                        lastName = authorsTags.item(j).getTextContent();
                        break;
                    case "nickname":
                        nickName = authorsTags.item(j).getTextContent();
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

        NodeList bodyTags = bookDocument.getElementsByTagName("body");
        for (int i = 0; i < bodyTags.getLength(); i++) {
            bookContent.addBookChapter(parseBody(bodyTags.item(i)));
        }

        return bookContent;
    }

    private BookChapter parseBody(Node body) {
        NodeList bodyChildList = body.getChildNodes();

        BookChapter bookBodyChapter = new BookChapter();

        for (int i = 0; i < bodyChildList.getLength(); i++) {
            String tagName = bodyChildList.item(i).getNodeName();

            switch (tagName){
                case TITLE_TAG:
                    CharSequence title = parseTag(bodyChildList.item(i));
                    bookBodyChapter.setTitle(title);
                    break;
                case EPIGRAPH_TAG:
                    CharSequence epigraph = parseTag(bodyChildList.item(i));
                    bookBodyChapter.setEpigraph(epigraph);
                    break;
                case SECTION_TAG:
                    bookBodyChapter.addChildChapter(parseSection(bodyChildList.item(i)));
                    break;
            }
        }


        return bookBodyChapter;
    }

    private BookChapter parseSection(Node section) {
        NodeList nestedTags = section.getChildNodes();

        BookChapter bookChapter = new BookChapter();
        SpannableStringBuilder builder = new SpannableStringBuilder();

        for (int i = 0; i < nestedTags.getLength(); i++) {
            String tagName = nestedTags.item(i).getNodeName();

            switch (tagName){
                case TITLE_TAG:
                    CharSequence title = parseTag(nestedTags.item(i));
                    bookChapter.setTitle(title);
                    break;
                case EPIGRAPH_TAG:
                    CharSequence epigraph = parseTag(nestedTags.item(i));
                    bookChapter.setEpigraph(epigraph);
                    break;
                case SECTION_TAG:
                    bookChapter.addChildChapter(parseSection(nestedTags.item(i)));
                    break;
                default:
                    builder.append(parseTag(nestedTags.item(i)));
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
            //Если это самый внутренни тег и при этом не "левый" \n
            if (!tag.getNodeValue().trim().isEmpty()){
                return tag.getNodeValue();
            } else {
                return "";
            }
        }

        SpannableStringBuilder builder = new SpannableStringBuilder();

        NodeList nestedTags = tag.getChildNodes();
        for (int i = 0; i < nestedTags.getLength(); i++) {
            builder.append(parseTag(nestedTags.item(i)));
        }

        switch (tag.getNodeName()) {
            case TITLE_TAG:
                builder.setSpan(new RelativeSizeSpan(TITLE_FONT_HEIGHT),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case SUBTITLE_TAG:
                builder.setSpan(new RelativeSizeSpan(SUBTITLE_FONT_HEIGHT),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append("\n\n");
                break;
            case PARAGRAPH_TAG:
                builder.append("\n\n");
                break;
            case EPIGRAPH_TAG:
                builder.append("\n");
                break;
            case POEM_TAG:
                builder.setSpan(new StyleSpan(Typeface.ITALIC),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case STANZA_TAG:
                builder.append("\n");
                break;
            case V_TAG:
                builder.append("\n");
                break;
            case CITE_TAG:
                builder.setSpan(new StyleSpan(Typeface.ITALIC),
                        0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case TEXT_AUTHOR_TAG:
                builder.append("\n");
                break;
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
    public void removeBook(BookDescription bookDescription) {

    }

    @Override
    public CharSequence getScrollText(BookDescription bookDescription) throws BookParserException {
        Log.d("scroll start", "Start");

        Document bookDocument = XmlHelper.getXmlFromFile(new File(bookDescription.getFilePath()));

        BookContent bookContent = parseBook(bookDocument);

        CharSequence reslt = bookContent.getScrollContent();

        Log.d("scroll end", "end");

        return reslt;
    }

    @Override
    public List<CharSequence> getChaptersText(BookDescription bookDescription) throws BookParserException {
        Document bookDocument = XmlHelper.getXmlFromFile(new File(bookDescription.getFilePath()));

        BookContent bookContent = parseBook(bookDocument);

        return bookContent.getChaptersText();
    }
}
