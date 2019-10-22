package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Squad implements Parcelable {

    private String creatorId, squadName, squadID, publicID, description;
    private long dateCreated;
    private int privacyLevel;

    private HashMap<String, Boolean> userList = new HashMap<>();
    private HashMap<String, Boolean> proprietors = new HashMap<>();
    private HashMap<String, Boolean> VIPs = new HashMap<>();

    public Squad(){}

    public Squad(String squadID,
                 String publicID,
                 String squadName,
                 String creatorId,
                 long dateString,
                 String description,
                 int privacyLevel,
                 HashMap<String, Boolean> users) {
        setSquadID(squadID);
        setPublicID(publicID);
        setSquadName(squadName);
        setCreatorId(creatorId);
        setDateCreated(dateString);
        setDescription(description);
        setPrivacyLevel(privacyLevel);
        setUserList(users);
        userList.put(creatorId, true);
        proprietors.put(creatorId, true);
    }


    protected Squad(Parcel in) {
        creatorId = in.readString();
        squadName = in.readString();
        squadID = in.readString();
        publicID = in.readString();
        description = in.readString();
        dateCreated = in.readLong();
        privacyLevel = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(creatorId);
        dest.writeString(squadName);
        dest.writeString(squadID);
        dest.writeString(publicID);
        dest.writeString(description);
        dest.writeLong(dateCreated);
        dest.writeInt(privacyLevel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Squad> CREATOR = new Creator<Squad>() {
        @Override
        public Squad createFromParcel(Parcel in) {
            return new Squad(in);
        }

        @Override
        public Squad[] newArray(int size) {
            return new Squad[size];
        }
    };

    /**********     sntadard getters and setters        **********/

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrivacyLevel() {
        return privacyLevel;
    }

    public void setPrivacyLevel(int privacyLevel) {
        this.privacyLevel = privacyLevel;
    }

    public String getPublicID() {
        return publicID;
    }

    public void setPublicID(String publicID) {
        this.publicID = publicID;
    }

    public String getSquadName() {
        return squadName;
    }

    public void setSquadName(String squadName) {
        this.squadName = squadName;
    }

    public String getSquadID() {
        return squadID;
    }

    public void setSquadID(String squadID) {
        this.squadID = squadID;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public HashMap<String, Boolean> getUserList() {
        return userList;
    }

    public void setUserList(HashMap<String, Boolean> userList) {
        this.userList = userList;
    }

    public HashMap<String, Boolean> getProprietors() {
        return proprietors;
    }

    public void setProprietors(HashMap<String, Boolean> proprietors) {
        this.proprietors = proprietors;
    }

    public HashMap<String, Boolean> getVIPs() {
        return VIPs;
    }

    public void setVIPs(HashMap<String, Boolean> VIPs) {
        this.VIPs = VIPs;
    }

    /**********         custom methods      **********/

    public void addMember(String userUID){
        userList.put(userUID, true);
    }

    public void removeMember(String userUID){
        userList.remove(userUID);
    }

    public Boolean getMember(String userUID){
        return userList.get(userUID);
    }
}
