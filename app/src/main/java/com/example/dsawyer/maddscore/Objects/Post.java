package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Post implements Parcelable {

    private String postID, creatorID, courseLocation, postBody, playTime;
    private long dateCreated;
    private HashMap<String, Boolean> comments;
    private HashMap<String, Boolean> userLikesList;

    public Post() {  }

    public Post(String postID,
                String creatorID,
                String courseLocation,
                long dateCreated,
                String postBody,
                String playTime,
               HashMap<String, Boolean> comments,
                HashMap<String,
                        Boolean> userLikesList) {

        this.postID = postID;
        this.creatorID = creatorID;
        this.courseLocation = courseLocation;
        this.dateCreated = dateCreated;
        this.postBody = postBody;
        this.playTime = playTime;
        this.comments = comments;
        this.userLikesList = userLikesList;
    }

    protected Post(Parcel in) {
        postID = in.readString();
        creatorID = in.readString();
        courseLocation = in.readString();
        postBody = in.readString();
        playTime = in.readString();
        dateCreated = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(postID);
        dest.writeString(creatorID);
        dest.writeString(courseLocation);
        dest.writeString(postBody);
        dest.writeString(playTime);
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

    public String getCourseLocation() {
        return courseLocation;
    }

    public void setCourseLocation(String postLocation) {
        this.courseLocation = postLocation;
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

    public String getPlayTime() {
        return playTime;
    }

    public void setPlayTime(String postTime) {
        this.playTime = postTime;
    }

    public int getLikes() {
        if (getUserLikesList() != null)
            return userLikesList.size();
        else return 0;
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
