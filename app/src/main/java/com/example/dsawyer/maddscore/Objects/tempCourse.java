package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class tempCourse implements Parcelable {

    private String courseID, name, creator;
    private int numRounds, numHoles;
    private Boolean favorite;
    private long registerDate;

    public tempCourse(){}

    public tempCourse(String UID,
                      String courseID,
                      String name,
                      int numHoles,
                      Boolean favorite,
                      long registerDate){
        creator = UID;
        this.name = name;
        this.courseID = courseID;
        this.numHoles = numHoles;
        this.numRounds = 0;
        this.favorite = favorite;
        this.registerDate = registerDate;
    }

    protected tempCourse(Parcel in) {
        courseID = in.readString();
        name = in.readString();
        creator = in.readString();
        numRounds = in.readInt();
        numHoles = in.readInt();
        byte tmpFavorite = in.readByte();
        favorite = tmpFavorite == 0 ? null : tmpFavorite == 1;
        registerDate = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(courseID);
        dest.writeString(name);
        dest.writeString(creator);
        dest.writeInt(numRounds);
        dest.writeInt(numHoles);
        dest.writeByte((byte) (favorite == null ? 0 : favorite ? 1 : 2));
        dest.writeLong(registerDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<tempCourse> CREATOR = new Creator<tempCourse>() {
        @Override
        public tempCourse createFromParcel(Parcel in) {
            return new tempCourse(in);
        }

        @Override
        public tempCourse[] newArray(int size) {
            return new tempCourse[size];
        }
    };

    public long getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(long registerDate) {
        this.registerDate = registerDate;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public Boolean getFavorite(){
        return favorite;
    }

    public void setFavorite(Boolean favorite){
        this.favorite = favorite;
    }

    public void setCreator(String creator){
        this.creator = creator;
    }
    public String getCreator(){
        return creator;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setNumHoles(int numHoles){
        this.numHoles = numHoles;
    }

    public int getNumHoles(){
        return numHoles;
    }

    public void setNumRounds(int numRounds){
        this.numRounds = numRounds;
    }

    public int getNumRounds(){
        return numRounds;
    }
}
