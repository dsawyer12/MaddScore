package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class UserStats implements Parcelable {

    private String userID, bestRoundCourse;
    private long bestRoundDate;
    private int numRounds, bestRoundScore, holesThrown,
            holeInOnes, eagles, pars, birdies, bogies, doublePlus, eagleAces;
    private double scoreAVG;

    public UserStats(){}

    public UserStats(String userID) {
        this.userID = userID;
    }

    protected UserStats(Parcel in) {
        userID = in.readString();
        bestRoundCourse = in.readString();
        bestRoundDate = in.readLong();
        numRounds = in.readInt();
        bestRoundScore = in.readInt();
        holesThrown = in.readInt();
        holeInOnes = in.readInt();
        eagles = in.readInt();
        pars = in.readInt();
        birdies = in.readInt();
        bogies = in.readInt();
        doublePlus = in.readInt();
        eagleAces = in.readInt();
        scoreAVG = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(bestRoundCourse);
        dest.writeLong(bestRoundDate);
        dest.writeInt(numRounds);
        dest.writeInt(bestRoundScore);
        dest.writeInt(holesThrown);
        dest.writeInt(holeInOnes);
        dest.writeInt(eagles);
        dest.writeInt(pars);
        dest.writeInt(birdies);
        dest.writeInt(bogies);
        dest.writeInt(doublePlus);
        dest.writeInt(eagleAces);
        dest.writeDouble(scoreAVG);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserStats> CREATOR = new Creator<UserStats>() {
        @Override
        public UserStats createFromParcel(Parcel in) {
            return new UserStats(in);
        }

        @Override
        public UserStats[] newArray(int size) {
            return new UserStats[size];
        }
    };

    public int getEagleAces() {
        return eagleAces;
    }

    public void setEagleAces(int eagleAces) {
        this.eagleAces = eagleAces;
    }

    public int getEagles() {
        return eagles;
    }

    public void setEagles(int eagles) {
        this.eagles = eagles;
    }

    public int getPars() {
        return pars;
    }

    public void setPars(int pars) {
        this.pars = pars;
    }

    public int getHolesThrown() {
        return holesThrown;
    }

    public void setHolesThrown(int holesThrown) {
        this.holesThrown = holesThrown;
    }

    public int getBirdies() {
        return birdies;
    }

    public void setBirdies(int birdies) {
        this.birdies = birdies;
    }

    public int getBogies() {
        return bogies;
    }

    public void setBogies(int bogies) {
        this.bogies = bogies;
    }

    public int getDoublePlus() {
        return doublePlus;
    }

    public void setDoublePlus(int doublePlus) {
        this.doublePlus = doublePlus;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getBestRoundCourse() {
        return bestRoundCourse;
    }

    public void setBestRoundCourse(String bestRoundCourse) {
        this.bestRoundCourse = bestRoundCourse;
    }

    public long getBestRoundDate() {
        return bestRoundDate;
    }

    public void setBestRoundDate(long bestRoundDate) {
        this.bestRoundDate = bestRoundDate;
    }

    public double getScoreAVG() {
        return scoreAVG;
    }

    public void setScoreAVG(double scoreAVG) {
        this.scoreAVG = scoreAVG;
    }

    public int getNumRounds() {
        return numRounds;
    }

    public void setNumRounds(int numRounds) {
        this.numRounds = numRounds;
    }

    public int getHoleInOnes() {
        return holeInOnes;
    }

    public void setHoleInOnes(int holeInOnes) {
        this.holeInOnes = holeInOnes;
    }

    public int getBestRoundScore() {
        return bestRoundScore;
    }

    public void setBestRoundScore(int bestRoundScore) {
        this.bestRoundScore = bestRoundScore;
    }
}
