package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable {
    public static final Integer TYPE_ONE = 0;
    public static final Integer TYPE_TWO = 1;

    private String sender, reciever, messageBody;
    private long date;
    private int type;

    public Message() {}

    public Message(String sender, String receiver, long date, String messageBody) {
        this.sender = sender;
        this.reciever = receiver;
        this.date = date;
        this.messageBody = messageBody;
    }


    protected Message(Parcel in) {
        sender = in.readString();
        reciever = in.readString();
        messageBody = in.readString();
        date = in.readLong();
        type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sender);
        dest.writeString(reciever);
        dest.writeString(messageBody);
        dest.writeLong(date);
        dest.writeInt(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
