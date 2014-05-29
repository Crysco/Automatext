package com.automatext.crysco.app;

import android.os.Parcel;
import android.os.Parcelable;

public class Entry implements Parcelable {

    private String name;
    private String number;
    private String date;
    private String time;
    private String content;
    private int frequency;
    private long id;

    public Entry() {
        name = "";
        number = "";
        date = "";
        time = "";
        content = "";
        frequency = 0;
        id = 0;
    }

    public Entry(String name, String number, String date, String time, String content, int frequency, long id) {
        this.name = name;
        this.number = number;
        this.date = date;
        this.time = time;
        this.id = id;
        this.content = content;
        this.frequency = frequency;
    }

    public Entry(Parcel source) {
        name = source.readString();
        number = source.readString();
        date = source.readString();
        time = source.readString();
        content = source.readString();
        frequency = source.readInt();
        id = source.readLong();
    }

    public String getName() {
        return name;
    }

    public void setName(String title) {
        this.name = title;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public long getID() {
        return this.id;
    }

    public void setID(long id) {
        this.id = id;
    }

    public static class EntryParser {

        public static String parseNumber(String contact) {
            String number = "";
            int start = contact.indexOf('#');
            start++;
            do {
                number += contact.charAt(start);
                start++;
            } while (start != contact.length());

            return number;
        }

        public static String parseName(String contact) {
            String name = "";
            int start = 0;
            do {
                name += contact.charAt(start);
                start++;
            } while (start != contact.indexOf('#'));

            return name;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(number);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(content);
        parcel.writeInt(frequency);
        parcel.writeLong(id);
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
