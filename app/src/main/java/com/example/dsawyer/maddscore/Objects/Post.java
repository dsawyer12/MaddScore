package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Post implements Parcelable {

    private String postID, creatorID, postBody;
    private long dateCreated;
    private HashMap<String, Boolean> comments;
    private HashMap<String, Boolean> userLikesList;

    public Post() {  }

    public Post(String postID,
                String creatorID,
                long dateCreated,
                String postBody,
                HashMap<String, Boolean> comments,
                HashMap<String, Boolean> userLikesList) {

        this.postID = postID;
        this.creatorID = creatorID;
        this.dateCreated = dateCreated;
        this.postBody = postBody;
        this.comments = comments;
        this.userLikesList = userLikesList;
    }

    protected Post(Parcel in) {
        postID = in.readString();
        creatorID = in.readString();
        postBody = in.readString();
        dateCreated = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(postID);
        dest.writeString(creatorID);
        dest.writeString(postBody);
        dest.writeLong(dateCreated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getPostBody() {
        return postBody;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public HashMap<String, Boolean> getComments() {
        return comments;
    }

    public void setComments(HashMap<String, Boolean> comments) {
        this.comments = comments;
    }

    public HashMap<String, Boolean> getUserLikesList() {
        return userLikesList;
    }

    public void setUserLikesList(HashMap<String, Boolean> userLikesList) {
        this.userLikesList = userLikesList;
    }


    public void addComment(String commentID, boolean b) {
        comments.put(commentID, b);
    }
}
