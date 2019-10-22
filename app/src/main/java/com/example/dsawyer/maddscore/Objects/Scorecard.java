package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

public class Scorecard implements Parcelable {
    private static final String TAG = "TAG";

    private String creatorID, cardID;
    private Course course;
    private int currentHole;
    private long dateCreated;
    private CardObject cardObject;
    private ArrayList<Integer> pars;
    private ArrayList<User> users;
    private ArrayList<Player> players;

    public Scorecard() {}

    public Scorecard(String creatorID,
                     String cardID,
                     Course course,
                     long dateCreated,
                     ArrayList<Integer> pars,
                     ArrayList<User> users,
                     ArrayList<Player> players) {
        this.creatorID = creatorID;
        this.cardID = cardID;
        this.course = course;
        this.dateCreated = dateCreated;
        this.currentHole = 0;
        this.pars = pars;
        this.users = users;
        this.players = players;
    }

    /****** USED WHEN RETRIEVING SCORECARDS. LIST OF USERS IS ADDED LATER. ******/

    public Scorecard(CardObject cardObject,
                     Course course) {
        this.cardObject = cardObject;
        this.course = course;
        this.currentHole = 0;
    }


    protected Scorecard(Parcel in) {
        creatorID = in.readString();
        cardID = in.readString();
        course = in.readParcelable(Course.class.getClassLoader());
        currentHole = in.readInt();
        dateCreated = in.readLong();
        cardObject = in.readParcelable(CardObject.class.getClassLoader());
        users = in.createTypedArrayList(User.CREATOR);
        players = in.createTypedArrayList(Player.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(creatorID);
        dest.writeString(cardID);
        dest.writeParcelable(course, flags);
        dest.writeInt(currentHole);
        dest.writeLong(dateCreated);
        dest.writeParcelable(cardObject, flags);
        dest.writeTypedList(users);
        dest.writeTypedList(players);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Scorecard> CREATOR = new Creator<Scorecard>() {
        @Override
        public Scorecard createFromParcel(Parcel in) {
            return new Scorecard(in);
        }

        @Override
        public Scorecard[] newArray(int size) {
            return new Scorecard[size];
        }
    };

    public CardObject getCardObject() {
        return cardObject;
    }

    public void setCardObject(CardObject cardObject) {
        this.cardObject = cardObject;
    }

    /******    Standard methods    ******/

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
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

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getCurrentHole() {
        return currentHole;
    }

    public void setCurrentHole(int currentHole) {
        this.currentHole = currentHole;
    }

    public ArrayList<Integer> getPars() {
        return pars;
    }

    public void setPars(ArrayList<Integer> pars) {
        this.pars = pars;
    }

    /******    Custom methods    ******/

    public void addPlayers(ArrayList<User> newPlayers) {
        users.addAll(newPlayers);
        for (int i = 0; i < newPlayers.size(); i++) {
            players.add(new Player(newPlayers.get(i).getUserID(), getCourse().getNumHoles()));
        }
    }

    public void removePlayers(ArrayList<User> users) {

        Iterator<Player> iterator;

        for (int i = 0; i < users.size(); i++) {
            iterator = getPlayers().iterator();
            while (iterator.hasNext()) {
                Player player = iterator.next();

                if (player.getUserId().equals(users.get(i).getUserID()))
                    iterator.remove();
            }
        }
        this.users.removeAll(users);
    }

    public void addUser(User user) {
        if (getUsers() != null)
            users.add(user);
        else {
            users = new ArrayList<>();
            users.add(user);
        }
    }

    public void addPars(int position, int par){
        pars.add(position, par);
    }

    public int getPar(int position){
        return pars.get(position);
    }

    public void setpar(int currentHole, int par) {
        pars.set(currentHole, par);
    }

    public void updatePar(int position, int par) {
        Log.d(TAG, "updatePar: ");
        pars.set(position, par);
    }

}
