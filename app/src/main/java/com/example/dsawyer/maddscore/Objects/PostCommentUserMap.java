package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class PostCommentUserMap implements Parcelable {

    private PostComment postComment;
    private String name, username, PhotoUrl;

    public PostCommentUserMap() {  }

    public PostCommentUserMap(PostComment postComment) {
        this.postComment = postComment;
    }

    protected PostCommentUserMap(Parcel in) {
        name = in.readString();
        username = in.readString();
        PhotoUrl = in.readString();
    }

    public static final Creator<PostCommentUserMap> CREATOR = new Creator<PostCommentUserMap>() {
        @Override
        public PostCommentUserMap createFromParcel(Parcel in) {
            return new PostCommentUserMap(in);
        }

        @Override
        public PostCommentUserMap[] newArray(int size) {
            return new PostCommentUserMap[size];
        }
    };

    public PostComment getPostComment() {
        return postComment;
    }

    public void setPostComment(PostComment postComment) {
        this.postComment = postComment;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(PhotoUrl);
    }
}
