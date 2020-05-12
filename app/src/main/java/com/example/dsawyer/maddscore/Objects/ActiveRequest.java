package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class ActiveRequest implements Parcelable {

    private String sender, receiver, notificationID;
    private long date;
    private int notificationType;

    public ActiveRequest() {}

    public ActiveRequest(String sender, String receiver, long date, String notificationID, int notificationType) {
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.notificationID = notificationID;
        this.notificationType = notificationType;
    }

    protected ActiveRequest(Parcel in) {
        sender = in.readString();
        receiver = in.readString();
        notificationID = in.readString();
        date = in.readLong();
        notificationType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sender);
        dest.writeString(receiver);
        dest.writeString(notificationID);
        dest.writeLong(date);
        dest.writeInt(notificationType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ActiveRequest> CREATOR = new Creator<ActiveRequest>() {
        @Override
        public ActiveRequest createFromParcel(Parcel in) {
            return new ActiveRequest(in);
        }

        @Override
        public ActiveRequest[] newArray(int size) {
            return new ActiveRequest[size];
        }
    };

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }
}
