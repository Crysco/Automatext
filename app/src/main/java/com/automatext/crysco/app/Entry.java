package com.automatext.crysco.app;

import android.os.Parcel;
import android.os.Parcelable;

public class Entry implements Parcelable {

    private String contact;
    private String date;
    private String time;
    private String content;
    private int frequency;
    private long id;

    public Entry() {
        contact = "";
        date = "";
        time = "";
        content = "";
        frequency = 0;
        id = 0;
    }

    public Entry(String contact, String date, String time, String content, int frequency, long id) {
        this.contact = contact;
        this.date = date;
        this.time = time;
        this.id = id;
        this.content = content;
        this.frequency = frequency;
    }

    public Entry(Parcel source) {
        contact = source.readString();
        date = source.readString();
        time = source.readString();
        content = source.readString();
        frequency = source.readInt();
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String title) {
        this.contact = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(contact);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(content);
        parcel.writeInt(frequency);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Entry createFromParcel(Parcel in) {
            return new Entry(in);
        }

        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };
}
