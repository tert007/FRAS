package com.example.alexander.fastreading.reader.bookparser;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alexander on 03.09.2016.
 */public class BookDescription implements Parcelable {

    private long id;

    private float progress;

    private String title;
    private String author;
    private String language;

    private String type;

    private String filePath;

    private boolean itsFavorite;

    private String coverImageName;

    public BookDescription(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean itsFavorite() {
        return itsFavorite;
    }

    public void setFavorite(boolean favorite) {
        this.itsFavorite = favorite;
    }

    public String getCoverImageName() {
        return coverImageName;
    }

    public void setCoverImageName(String coverImageName) {
        this.coverImageName = coverImageName;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    protected BookDescription(Parcel in) {
        id = in.readLong();
        progress = in.readFloat();
        title = in.readString();
        author = in.readString();
        language = in.readString();
        type = in.readString();
        filePath = in.readString();
        itsFavorite = in.readInt() == 1;
        coverImageName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeFloat(progress);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(language);
        dest.writeString(type);
        dest.writeString(filePath);
        dest.writeInt(itsFavorite ? 1 : 0);
        dest.writeString(coverImageName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BookDescription> CREATOR = new Parcelable.Creator<BookDescription>() {
        @Override
        public BookDescription createFromParcel(Parcel in) {
            return new BookDescription(in);
        }

        @Override
        public BookDescription[] newArray(int size) {
            return new BookDescription[size];
        }
    };
}
