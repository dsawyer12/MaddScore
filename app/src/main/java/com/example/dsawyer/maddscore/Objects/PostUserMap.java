package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class PostUserMap implements Parcelable {
    /*** POST FIELDS ***/
    private Post post;

    /*** USER FIELDS ***/
    private String name, username, PhotoUrl;
    private int squad_rank;

    public PostUserMap() {  }

    public PostUserMap(Post post) {
        this.post = post;
    }

    public PostUserMap(Post post, String name, String username, String photoUrl) {
        this.post = post;
        this.name = name;
        this.username = username;
        PhotoUrl = photoUrl;
    }

    protected PostUserMap(Parcel in) {
        post = in.readParcelable(Post.class.getClassLoader());
        name = in.readString();
        username = in.readString();
        PhotoUrl = in.readString();
        squad_rank = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(post, flags);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(PhotoUrl);
        dest.writeInt(squad_rank);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PostUserMap> CREATOR = new Creator<PostUserMap>() {
        @Override
        public PostUserMap createFromParcel(Parcel in) {
            return new PostUserMap(in);
        }

        @Override
        public PostUserMap[] newArray(int size) {
            return new PostUserMap[size];
        }
    };

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

    public int getSquad_rank() {
        return squad_rank;
    }

    public void setSquad_rank(int squad_rank) {
        this.squad_rank = squad_rank;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
