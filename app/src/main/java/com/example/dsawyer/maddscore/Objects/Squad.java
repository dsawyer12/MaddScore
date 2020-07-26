package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Squad implements Parcelable {

    private String creatorId, squadName, squadID, publicID, description, pinID;
    private long dateCreated;
    private int privacyLevel;

    private HashMap<String, Boolean> memberList = new HashMap<>();
    private HashMap<String, Boolean> proprietors = new HashMap<>();
    private HashMap<String, Boolean> VIPs = new HashMap<>();

    public Squad(){}

    // squadId, currentUser.getUid(), squad_name, squad_description, privacy_level, date, squadMemberList)

    public Squad(String squadID,
                 String creatorId,
                 String squadName,
                 String description,
                 int privacyLevel,
                 long date) {
        this.squadID = squadID;
        this.creatorId = creatorId;
        this.squadName = squadName;
        this.description = description;
        this.privacyLevel = privacyLevel;
        this.dateCreated = date;

        this.memberList.put(creatorId, true);
        proprietors.put(creatorId, true);
    }


    protected Squad(Parcel in) {
        creatorId = in.readString();
        squadName = in.readString();
        squadID = in.readString();
        publicID = in.readString();
        description = in.readString();
        pinID = in.readString();
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
        dest.writeString(pinID);
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

    public String getPinID() {
        return pinID;
    }

    public void setPinID(String pinID) {
        this.pinID = pinID;
    }

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

    public HashMap<String, Boolean> getMemberList() {
        return memberList;
    }

    public void setMemberList(HashMap<String, Boolean> memberList) {
        this.memberList = memberList;
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
        memberList.put(userUID, true);
    }

    public void removeMember(String userUID){
        memberList.remove(userUID);
    }

    public Boolean getMember(String userUID){
        return memberList.get(userUID);
    }
}
