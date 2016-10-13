package com.example.alexander.fastreading.reader;

import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.UnderlineSpan;

import com.example.alexander.fastreading.reader.dao.bookdao.BookParserException;
import com.example.alexander.fastreading.reader.entity.BookChapter;
import com.example.alexander.fastreading.reader.entity.BookContent;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.entity.EpubBookChapter;
import com.example.alexander.fastreading.reader.entity.Fb2BookChapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 03.10.2016.
 */
public class JsonHelper {

    private static final String BOOK_CONTENT_DIRECTORY_NAME = "book_content";

    public static void saveBook(BookDescription bookDescription, BookContent bookContent, String bookIdDirectoryPath) throws BookParserException {
        try {
            List<BookChapter> bookChapters = bookContent.getBookChapterList();

            List<JSONObject> jsonChapters = convertChaptersToJson(bookDescription, bookChapters);

            String savePath = bookIdDirectoryPath + File.separator + bookDescription.getType();
            saveBookChapters(jsonChapters, savePath);
        } catch (JSONException e) {
            throw new BookParserException(e);
        }
    }

    private static void saveBookChapters(List<JSONObject> jsonChapters, String directoryPath) throws BookParserException {
        try {
            File directory = new File(directoryPath);
            directory.mkdirs();

            int jsonChaptersCount = jsonChapters.size();

            for (int i = 0; i < jsonChaptersCount; i++) {
                String savePath = directoryPath + File.separator + i + ".json";

                File file = new File(savePath);
                file.createNewFile();

                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(jsonChapters.get(i).toString());
                fileWriter.close();
            }
        } catch (IOException e) {
            throw new BookParserException(e);
        }
    }
    /*
    private static void saveJson(JSONObject jsonObject, String directoryPath) throws BookParserException {
        try {
            String savePth = directoryPath + File.separator + FILE_NAME;

            File file = new File(savePth);
            file.getParentFile().mkdirs();
            file.createNewFile();

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonObject.toString());
            fileWriter.close();
        } catch (IOException e) {
            throw new BookParserException(e);
        }
    }
/*
    private static JSONObject convertBookContentToJson(BookDescription bookDescription, BookContent bookContent) throws JSONException {
            JSONObject bookContentJsonObject = new JSONObject();
            bookContentJsonObject.put("book_type", bookDescription.getType());

            JSONArray bookChapterJsonArray = new JSONArray();
            List<BookChapter> chapters = bookContent.getBookChapterList();

            for (BookChapter chapter : chapters) {
                JSONObject chapterJsonObject = new JSONObject();

                JSONArray titleSpanJsonArray = new JSONArray();

                CharSequence title = chapter.getTitle();
                if (title == null) {
                    chapterJsonObject.put("title", "");
                    chapterJsonObject.put("title_spans", titleSpanJsonArray);
                } else {
                    if (bookDescription.getType().equals(FileHelper.FB2)) {
                        Spanned spannedTitle = (Spanned) title;

                        chapterJsonObject.put("title", spannedTitle.toString());

                        Object[] spans = spannedTitle.getSpans(0, spannedTitle.length(), Object.class);


                        for (Object span : spans) {
                            titleSpanJsonArray.put(convertSpanToJson(spannedTitle, span));
                        }

                        chapterJsonObject.put("title_spans", titleSpanJsonArray);
                    } else {
                        chapterJsonObject.put("title", title.toString());
                        //chapterJsonObject.put("title_spans", titleSpanJsonArray);
                    }
                }

                JSONArray chapterSpanJsonArray = new JSONArray();

                CharSequence content = chapter.getContent();
                if (content == null) {
                    chapterJsonObject.put("content", "");
                    chapterJsonObject.put("content_spans", chapterSpanJsonArray);
                } else {
                    Spanned spannedContent = (Spanned) content;

                    chapterJsonObject.put("content", spannedContent.toString());

                    Object[] spans = spannedContent.getSpans(0, spannedContent.length(), Object.class);

                    for (Object span : spans) {
                        chapterSpanJsonArray.put(convertSpanToJson(spannedContent, span));
                    }

                    chapterJsonObject.put("content_spans", chapterSpanJsonArray);
                }

                bookChapterJsonArray.put(chapterJsonObject);
            }

            bookContentJsonObject.put("chapters", bookChapterJsonArray);

            return bookContentJsonObject;
    }
*/
    private static List<JSONObject> convertChaptersToJson(BookDescription bookDescription, List<BookChapter> chapters) throws JSONException {
        final List<JSONObject> jsonChapters = new ArrayList<>(chapters.size());

        for (BookChapter chapter : chapters) {
            final JSONObject chapterJsonObject = new JSONObject();

            final JSONArray titleSpanJsonArray = new JSONArray();
            final JSONArray chapterSpanJsonArray = new JSONArray();

            CharSequence title = chapter.getTitle();
            if (title == null) {
                chapterJsonObject.put("title", "");
                chapterJsonObject.put("title_spans", titleSpanJsonArray);
            } else {
                if (bookDescription.getType().equals(FileHelper.FB2)) {
                    Spanned spannedTitle = (Spanned) title;

                    chapterJsonObject.put("title", spannedTitle.toString());

                    Object[] spans = spannedTitle.getSpans(0, spannedTitle.length(), Object.class);


                    for (Object span : spans) {
                        titleSpanJsonArray.put(convertSpanToJson(spannedTitle, span));
                    }

                    chapterJsonObject.put("title_spans", titleSpanJsonArray);
                } else {
                    chapterJsonObject.put("title", title.toString());
                    //chapterJsonObject.put("title_spans", titleSpanJsonArray);
                }
            }

            CharSequence content = chapter.getContent();
            if (content == null) {
                chapterJsonObject.put("content", "");
                chapterJsonObject.put("content_spans", chapterSpanJsonArray);
            } else {
                Spanned spannedContent = (Spanned) content;

                chapterJsonObject.put("content", spannedContent.toString());

                Object[] spans = spannedContent.getSpans(0, spannedContent.length(), Object.class);

                for (Object span : spans) {
                    chapterSpanJsonArray.put(convertSpanToJson(spannedContent, span));
                }

                chapterJsonObject.put("content_spans", chapterSpanJsonArray);
            }

            jsonChapters.add(chapterJsonObject);
        }

        return jsonChapters;
    }

    private static JSONObject convertSpanToJson(Spanned spanned, Object span) throws JSONException {
            final JSONObject jsonObject = new JSONObject();

            final Class spanClass = span.getClass();

            jsonObject.put("span_class", spanClass.toString());
            jsonObject.put("start_index", spanned.getSpanStart(span));
            jsonObject.put("end_index", spanned.getSpanEnd(span));

            if (spanClass == AlignmentSpan.Standard.class) {
                AlignmentSpan.Standard alignmentSpan = (AlignmentSpan.Standard) span;
                jsonObject.put("alignment", alignmentSpan.getAlignment().name());
            } else if (spanClass == RelativeSizeSpan.class) {
                RelativeSizeSpan relativeSizeSpan = (RelativeSizeSpan) span;
                jsonObject.put("relative_size", relativeSizeSpan.getSizeChange());
            } else if (spanClass == StyleSpan.class) {
                StyleSpan styleSpan = (StyleSpan) (span);
                jsonObject.put("style_type", styleSpan.getStyle());
            }
            /*else if (spanClass == SubscriptSpan.class) {
                //
            } else if (spanClass == SuperscriptSpan.class) {
                //
            } else if (span == StrikethroughSpan.class) {
                //
            } else if (span == UnderlineSpan.class) {
                //
            }*/

            return jsonObject;
    }

    public static BookContent readBook(BookDescription bookDescription, String bookIdDirectoryPath) throws BookParserException {
        try {
            String bookType = bookDescription.getType();
            String chaptersDirectoryPath = bookIdDirectoryPath + File.separator + bookType;

            File chaptersDirectory = new File(chaptersDirectoryPath);
            File[] chapterFiles = chaptersDirectory.listFiles();

            List<BookChapter> bookChapters = new ArrayList<>(chapterFiles.length);

            for (File chapterFile : chapterFiles) {
                StringBuilder jsonStringBuilder = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(chapterFile)));
                String line;

                while ((line = br.readLine()) != null) {
                    jsonStringBuilder.append(line);
                }
                br.close();

                final JSONObject jsonChapter = new JSONObject(jsonStringBuilder.toString());
                bookChapters.add(parseChapter(jsonChapter, bookType));
            }

            BookContent bookContent = new BookContent();
            bookContent.setBookChapterList(bookChapters);

            return bookContent;
        } catch (IOException | JSONException e) {
            throw new BookParserException(e);
        }
    }

    private static BookChapter parseChapter(JSONObject chapter, String bookType) throws JSONException, BookParserException {
        switch (bookType) {
            case FileHelper.FB2: {
                final SpannableString spannedTitle = new SpannableString(chapter.getString("title"));
                final SpannableString spannedContent = new SpannableString(chapter.getString("content"));

                final JSONArray titleSpansJsonArray = chapter.getJSONArray("title_spans");
                final int titleSpansJsonArrayLength = titleSpansJsonArray.length();

                for (int i = 0; i < titleSpansJsonArrayLength; i++) {
                    setSpan(spannedTitle, (JSONObject) titleSpansJsonArray.get(i));
                }

                final JSONArray contentSpansJsonArray = chapter.getJSONArray("content_spans");
                final int contentSpansJsonArrayLength = contentSpansJsonArray.length();

                for (int i = 0; i < contentSpansJsonArrayLength; i++) {
                    setSpan(spannedContent, (JSONObject) contentSpansJsonArray.get(i));
                }

                return new Fb2BookChapter(spannedTitle, spannedContent);
            }
            case FileHelper.EPUB: {
                final CharSequence title = chapter.getString("title");
                final SpannableString spannedContent = new SpannableString(chapter.getString("content"));

                final JSONArray contentSpansJsonArray = chapter.getJSONArray("content_spans");
                final int contentSpansJsonArrayLength = contentSpansJsonArray.length();

                for (int i = 0; i < contentSpansJsonArrayLength; i++) {
                    setSpan(spannedContent, (JSONObject) contentSpansJsonArray.get(i));
                }

                return new EpubBookChapter(title, spannedContent);
            }
            default:
                throw new BookParserException("Unsupported epub_book format");
        }
    }

    private static void setSpan(final SpannableString spannableString, final JSONObject span) throws JSONException {
        final String className = span.getString("span_class");
        int startIndex = span.getInt("start_index");
        int endIndex = span.getInt("end_index");

        if (className.equals(AlignmentSpan.Standard.class.toString())) {
            Layout.Alignment alignment = Layout.Alignment.valueOf(span.getString("alignment"));

            spannableString.setSpan(new AlignmentSpan.Standard(alignment), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return;
        }

        if (className.equals(RelativeSizeSpan.class.toString())) {
            float relativeSize = (float) span.getDouble("relative_size");

            spannableString.setSpan(new RelativeSizeSpan(relativeSize), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return;
        }

        if (className.equals(StyleSpan.class.toString())) {
            int styleType =  span.getInt("style_type");

            spannableString.setSpan(new StyleSpan(styleType), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return;
        }

        if (className.equals(SubscriptSpan.class.toString())) {
            spannableString.setSpan(new SubscriptSpan(), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return;
        }

        if (className.equals(SuperscriptSpan.class.toString())) {
            spannableString.setSpan(new SuperscriptSpan(), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return;
        }

        if (className.equals(StrikethroughSpan.class.toString())) {
            spannableString.setSpan(new StrikethroughSpan(), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return;
        }

        if (className.equals(UnderlineSpan.class.toString())) {
            spannableString.setSpan(new UnderlineSpan(), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return;
        }
    }
}
