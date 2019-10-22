package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Notification implements Parcelable {
    public static final int FRIEND_REQUEST = 1;
    public static final int SQUAD_INVITE = 2;
    public static final int SQUAD_REQUEST = 3;
    public static final int MESSAGE = 4;

    private String notificationID, sender, senderName, senderPhotoID, squadID, squadName, receiver, head, snippet;
    private long dateSent;
    private int notificationType;

    public Notification() {}

    /**   constructor for friend request   **/
    public Notification(String notificationID,
                        String sender,
                        String senderName,
                        String senderPhotoID,
                        String receiver,
                        int notificationType,
                        long dateSent) {
        setNotificationID(notificationID);
        setSender(sender);
        setSenderName(senderName);
        setSenderPhotoID(senderPhotoID);
        setReceiver(receiver);
        setNotificationType(notificationType);
        setDateSent(dateSent);
    }

    /***    constructor for squad invite  **/
    public Notification(String notificationID,
                        String sender,
                        String senderName,
                        String senderPhotoID,
                        String squadID,
                        String squadName,
                        String receiver,
                        int notificationType,
                        long dateSent) {
        setNotificationID(notificationID);
        setSender(sender);
        setSenderName(senderName);
        setSenderPhotoID(senderPhotoID);
        setSquadID(squadID);
        setSquadName(squadName);
        setReceiver(receiver);
        setNotificationType(notificationType);
        setDateSent(dateSent);
    }

    /***    constructor for message  **/
    public Notification(String notificationID,
                        String sender,
                        String senderName,
                        String senderPhotoID,
                        String receiver,
                        int notificationType,
                        long dateSent,
                        String snippet) {
        setNotificationID(notificationID);
        setSender(sender);
        setSenderName(senderName);
        setSenderPhotoID(senderPhotoID);
        setReceiver(receiver);
        setNotificationType(notificationType);
        setDateSent(dateSent);
        setSnippet(snippet);
    }

    protected Notification(Parcel in) {
        notificationID = in.readString();
        sender = in.readString();
        senderName = in.readString();
        senderPhotoID = in.readString();
        squadID = in.readString();
        squadName = in.readString();
        receiver = in.readString();
        head = in.readString();
        snippet = in.readString();
        dateSent = in.readLong();
        notificationType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(notificationID);
        dest.writeString(sender);
        dest.writeString(senderName);
        dest.writeString(senderPhotoID);
        dest.writeString(squadID);
        dest.writeString(squadName);
        dest.writeString(receiver);
        dest.writeString(head);
        dest.writeString(snippet);
        dest.writeLong(dateSent);
        dest.writeInt(notificationType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getSenderPhotoID() {
        return senderPhotoID;
    }

    public void setSenderPhotoID(String senderPhotoID) {
        this.senderPhotoID = senderPhotoID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSquadID() {
        return squadID;
    }

    public void setSquadID(String squadID) {
        this.squadID = squadID;
    }

    public String getSquadName() {
        return squadName;
    }

    public void setSquadName(String squadName) {
        this.squadName = squadName;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

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

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public long getDateSent() {
        return dateSent;
    }

    public void setDateSent(long dateSent) {
        this.dateSent = dateSent;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }
}
