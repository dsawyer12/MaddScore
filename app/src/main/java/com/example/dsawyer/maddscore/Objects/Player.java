package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Player implements Parcelable, Comparable<Player>{

    private String userId;
    private int endScore, total;
    private ArrayList<Integer> holeScores = new ArrayList<>();
    private ArrayList<Integer> holeUpdateScores = new ArrayList<>();

    public Player() {}

    public Player(String userId, int numHoles) {
        this.userId = userId;
        this.endScore = 0;
        this.total = 0;

        for (int i = 0; i < numHoles; i++) {
            addHoleScore(i, 0);
            addHoleUpdateScore(i, 0);
        }
    }

    protected Player(Parcel in) {
        userId = in.readString();
        endScore = in.readInt();
        total = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeInt(endScore);
        dest.writeInt(total);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    /*************  Standard getters and setters    *******************/

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getEndScore() {
        return endScore;
    }

    public void setEndScore(int endScore) {
        this.endScore = endScore;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<Integer> getHoleScores() {
        return holeScores;
    }

    public void setHoleScores(ArrayList<Integer> holeScores) {
        this.holeScores = holeScores;
    }

    public ArrayList<Integer> getHoleUpdateScores() {
        return holeUpdateScores;
    }

    public void setHoleUpdateScores(ArrayList<Integer> holeUpdateScores) {
        this.holeUpdateScores = holeUpdateScores;
    }

    /*************    Custom Methods    *******************/

    public void addHoleScore(int position, int holeScore) {
        holeScores.add(position, holeScore);
    }

    public void addHoleUpdateScore(int position, int updateHoleScore) {
        holeUpdateScores.add(position, updateHoleScore);
    }

    public void updateHoleScore(int position, int holeScore) {
        holeScores.set(position, holeScore);
    }

    public void updateHoleUpdateScore(int position, int updateHoleScore) {
        holeUpdateScores.set(position, updateHoleScore);
    }

    public int getHoleScore(int position) {
        return holeScores.get(position);
    }


    @Override
    public int compareTo(Player p) {
        return this.getTotal() - p.getTotal();
    }
}
