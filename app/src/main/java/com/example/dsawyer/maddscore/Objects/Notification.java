package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Notification implements Parcelable {
    public static final int FRIEND_REQUEST = 1;
    public static final int SQUAD_INVITE = 2;
    public static final int SQUAD_REQUEST = 3;
    public static final int MESSAGE = 4;

    private String notificationID, senderID, squadID, receiverID, heading, snippet;
    private long dateSent;
    private int notificationType;

    public Notification() {}

    /**   constructor for friend request   **/
    public Notification(String notificationID,
                        String senderID,
                        String receiverID,
                        int notificationType,
                        long dateSent) {
        setNotificationID(notificationID);
        setSenderID(senderID);
        setReceiverID(receiverID);
        setNotificationType(notificationType);
        setDateSent(dateSent);
    }

    /***    constructor for squad invite  **/
    public Notification(String notificationID,
                        String senderID,
                        String squadID,
                        String receiverID,
                        int notificationType,
                        long dateSent) {
        setNotificationID(notificationID);
        setSenderID(senderID);
        setSquadID(squadID);
        setReceiverID(receiverID);
        setNotificationType(notificationType);
        setDateSent(dateSent);
    }

    /***    constructor for message  **/
    public Notification(String notificationID,
                        String senderID,
                        String receiverID,
                        int notificationType,
                        long dateSent,
                        String snippet) {
        setNotificationID(notificationID);
        setSenderID(senderID);
        setReceiverID(receiverID);
        setNotificationType(notificationType);
        setDateSent(dateSent);
        setSnippet(snippet);
    }

    protected Notification(Parcel in) {
        notificationID = in.readString();
        senderID = in.readString();
        squadID = in.readString();
        receiverID = in.readString();
        heading = in.readString();
        snippet = in.readString();
        dateSent = in.readLong();
        notificationType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(notificationID);
        dest.writeString(senderID);
        dest.writeString(squadID);
        dest.writeString(receiverID);
        dest.writeString(heading);
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

    public String getSquadID() {
        return squadID;
    }

    public void setSquadID(String squadID) {
        this.squadID = squadID;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
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
