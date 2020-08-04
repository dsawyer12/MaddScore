package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;
import java.util.HashMap;

public class User implements
        Parcelable,
        Comparator<User>, Comparable<User> {

    private String creator, userID, name, email, phoneNumber, username, PhotoUrl, squad, squadRankImg;
    private HashMap<String, Boolean> friends = new HashMap<>();
    private boolean registered, completeAccount;
    private long registerDate;
    private int squadRank;

    public User() {}

    /*****   Registered user constructor   *****/
    public User(String userID,
                String name,
                String email,
                Boolean registered,
                long registerDate) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.registered = registered;
        this.registerDate = registerDate;
    }

    /*****   tempUser constructor    *****/
    public User(Boolean registered,
                String creator,
                String userID,
                String name,
                String username,
                long registerDate) {
        this.registered = registered;
        this.creator = creator;
        this.userID = userID;
        this.name = name;
        this.username = username;
        this.registerDate = registerDate;
    }

    protected User(Parcel in) {
        creator = in.readString();
        userID = in.readString();
        name = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        username = in.readString();
        PhotoUrl = in.readString();
        squad = in.readString();
        squadRankImg = in.readString();
        registered = in.readByte() != 0;
        completeAccount = in.readByte() != 0;
        registerDate = in.readLong();
        squadRank = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(creator);
        dest.writeString(userID);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(phoneNumber);
        dest.writeString(username);
        dest.writeString(PhotoUrl);
        dest.writeString(squad);
        dest.writeString(squadRankImg);
        dest.writeByte((byte) (registered ? 1 : 0));
        dest.writeByte((byte) (completeAccount ? 1 : 0));
        dest.writeLong(registerDate);
        dest.writeInt(squadRank);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public boolean isCompleteAccount() {
        return completeAccount;
    }

    public void setCompleteAccount(boolean completeAccount) {
        this.completeAccount = completeAccount;
    }

    public long getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(long registerDate) {
        this.registerDate = registerDate;
    }

    public String getCreator() {
            return creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        public boolean isRegistered() {
            return registered;
        }

        public void setRegistered(boolean registered) {
            this.registered = registered;
        }

        public HashMap<String, Boolean> getFriends() {
        return friends;
    }

        public void setFriends(HashMap<String, Boolean> friends) {
        this.friends = friends;
    }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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

        public String getSquad() {
            return squad;
        }

        public void setSquad(String squad) {
            this.squad = squad;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

    public int getSquadRank() {
        return squadRank;
    }

    public void setSquadRank(int squadRank) {
        this.squadRank = squadRank;
    }

    public String getSquadRankImg() {
        return squadRankImg;
    }

    public void setSquadRankImg(String squadRankImg) {
        this.squadRankImg = squadRankImg;
    }

    @Override
    public int compare(User user1, User user2) {
        return user1.getSquad().compareTo(user2.getSquad());
    }

    @Override
    public int compareTo(User user) {
        return (this.squadRank - user.getSquadRank());
    }
}


