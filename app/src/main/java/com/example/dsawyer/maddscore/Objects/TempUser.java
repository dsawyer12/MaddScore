package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class TempUser implements Parcelable, Comparable<User>{


    private String tempID, creatorID, name, username, PhotoUrl;
    private int numRounds, bestScore;
    private tempCourse bestRound;
    long registerDate;

    public TempUser(String tempID, String creatorID, String name, String username, String PhotoUrl,
                     long registerDate){
        this.tempID = tempID;
        this.creatorID = creatorID;
        this.name = name;
        this.username = username;
        this.PhotoUrl = PhotoUrl;
        this.registerDate = registerDate;
    }

    protected TempUser(Parcel in) {
        tempID = in.readString();
        creatorID = in.readString();
        name = in.readString();
        username = in.readString();
        PhotoUrl = in.readString();
        numRounds = in.readInt();
        bestScore = in.readInt();
        bestRound = in.readParcelable(tempCourse.class.getClassLoader());
        registerDate = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tempID);
        dest.writeString(creatorID);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(PhotoUrl);
        dest.writeInt(numRounds);
        dest.writeInt(bestScore);
        dest.writeParcelable(bestRound, flags);
        dest.writeLong(registerDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TempUser> CREATOR = new Creator<TempUser>() {
        @Override
        public TempUser createFromParcel(Parcel in) {
            return new TempUser(in);
        }

        @Override
        public TempUser[] newArray(int size) {
            return new TempUser[size];
        }
    };

    public long getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(long registerDate) {
        this.registerDate = registerDate;
    }

    public String getTempID() {
        return tempID;
    }

    public void setTempID(String tempID) {
        this.tempID = tempID;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

    public int getNumRounds() {
        return numRounds;
    }

    public void setNumRounds(int numRounds) {
        this.numRounds = numRounds;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public tempCourse getBestRound() {
        return bestRound;
    }

    public void setBestRound(tempCourse bestRound) {
        this.bestRound = bestRound;
    }

    @Override
    public int compareTo(@NonNull User o) {
        return 0;
    }
}


