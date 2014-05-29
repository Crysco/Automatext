package com.automatext.crysco.app;

import android.os.Parcel;
import android.os.Parcelable;

public class Reply implements Parcelable {

    private String title;
    private String startTime;
    private String endTime;
    private String content;
    private String days;
    private int active;
    private int silence;
    private long id;

    public Reply() {
        title = "";
        startTime = "";
        endTime = "";
        content = "";
        days = "";
        active = 0;
        silence = 1;
        id = 0;
    }

    public Reply(String title, String startTime, String endTime, String content, String days, int active, int silence, long id) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.id = id;
        this.content = content;
        this.active = active;
        this.silence = silence;
        this.days = days;
    }

    public Reply(Parcel source) {
        title = source.readString();
        startTime = source.readString();
        endTime = source.readString();
        content = source.readString();
        active = source.readInt();
        silence = source.readInt();
        days = source.readString();
        id = source.readLong();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDays() {
        return this.days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public int getActive() {
        return this.active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getSilence() {
        return this.silence;
    }

    public void setSilence(int checked) {
        this.silence = checked;
    }

    public long getID() {
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
        parcel.writeString(title);
        parcel.writeString(startTime);
        parcel.writeString(endTime);
        parcel.writeString(content);
        parcel.writeInt(active);
        parcel.writeInt(silence);
        parcel.writeString(days);
        parcel.writeLong(id);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Reply createFromParcel(Parcel in) {
            return new Reply(in);
        }

        public Reply[] newArray(int size) {
            return new Reply[size];
        }
    };
}