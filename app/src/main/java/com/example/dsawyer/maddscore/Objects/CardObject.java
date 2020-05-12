package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

public class CardObject implements Parcelable {

    private String creatorID, cardID, courseID;
    private long dateCreated;
    private HashMap<String, Boolean> users;
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Integer> pars;

    public CardObject() {}

    public CardObject(String creatorID,
                      String cardID,
                      String courseID,
                      ArrayList<Integer> pars,
                      long dateCreated,
                      HashMap<String, Boolean> users,
                      ArrayList<Player> players) {
        this.creatorID = creatorID;
        this.cardID = cardID;
        this.courseID = courseID;
        this.pars = pars;
        this.dateCreated = dateCreated;
        this.users = users;
        this.players = players;
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setHoleUpdateScores(null);
        }
    }

    protected CardObject(Parcel in) {
        creatorID = in.readString();
        cardID = in.readString();
        courseID = in.readString();
        dateCreated = in.readLong();
        players = in.createTypedArrayList(Player.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(creatorID);
        dest.writeString(cardID);
        dest.writeString(courseID);
        dest.writeLong(dateCreated);
        dest.writeTypedList(players);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CardObject> CREATOR = new Creator<CardObject>() {
        @Override
        public CardObject createFromParcel(Parcel in) {
            return new CardObject(in);
        }

        @Override
        public CardObject[] newArray(int size) {
            return new CardObject[size];
        }
    };

    public ArrayList<Integer> getPars() {
        return pars;
    }

    public void setPars(ArrayList<Integer> pars) {
        this.pars = pars;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getCardID() {
        return cardID;
    }

    public void setCardID(String cardID) {
        this.cardID = cardID;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public HashMap<String, Boolean> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, Boolean> users) {
        this.users = users;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }
}
