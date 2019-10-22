package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Post implements Parcelable {

    private String postKey, userId, courseLocation, postBody, playTime;
    private int likes;
    private long dateCreated;
    private HashMap<String, Boolean> comments;
    private HashMap<String, Boolean> userlist;

    public Post(){

    }

    public Post(String postKey,
                String userId,
                String courseLocation,
                long dateCreated,
                String postBody,
                String playTime,
                int likes,
               HashMap<String, Boolean> comments,
                HashMap<String,
                        Boolean> userlist) {

        this.postKey = postKey;
        this.userId = userId;
        this.courseLocation = courseLocation;
        this.dateCreated = dateCreated;
        this.postBody = postBody;
        this.playTime = playTime;
        this.likes = likes;
        this.comments = comments;
        this.userlist = userlist;
    }

    protected Post(Parcel in) {
        postKey = in.readString();
        userId = in.readString();
        courseLocation = in.readString();
        postBody = in.readString();
        playTime = in.readString();
        likes = in.readInt();
        dateCreated = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(postKey);
        dest.writeString(userId);
        dest.writeString(courseLocation);
        dest.writeString(postBody);
        dest.writeString(playTime);
        dest.writeInt(likes);
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

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        if (getUserlist() != null){
            return userlist.size();
        }else{
            return 0;
        }
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public HashMap<String, Boolean> getComments() {
        return comments;
    }

    public void setComments(HashMap<String, Boolean> comments) {
        this.comments = comments;
    }

    public HashMap<String, Boolean> getUserlist() {
        return userlist;
    }

    public void setUserlist(HashMap<String, Boolean> userlist) {
        this.userlist = userlist;
    }


    public void addComment(String commentID, boolean b) {
        comments.put(commentID, b);
    }
}
