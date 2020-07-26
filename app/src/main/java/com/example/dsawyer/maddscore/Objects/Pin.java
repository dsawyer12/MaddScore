package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Pin implements Parcelable {

    public Pin() {  }

    private String pinID, postID, pinAuthor, snippet;
    private long pinDate;
    private Post post;


    protected Pin(Parcel in) {
        pinID = in.readString();
        postID = in.readString();
        pinAuthor = in.readString();
        snippet = in.readString();
        pinDate = in.readLong();
        post = in.readParcelable(Post.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pinID);
        dest.writeString(postID);
        dest.writeString(pinAuthor);
        dest.writeString(snippet);
        dest.writeLong(pinDate);
        dest.writeParcelable(post, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Pin> CREATOR = new Creator<Pin>() {
        @Override
        public Pin createFromParcel(Parcel in) {
            return new Pin(in);
        }

        @Override
        public Pin[] newArray(int size) {
            return new Pin[size];
        }
    };

    public String getPinID() {
        return pinID;
    }

    public void setPinID(String pinID) {
        this.pinID = pinID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPinAuthor() {
        return pinAuthor;
    }

    public void setPinAuthor(String pinAuthor) {
        this.pinAuthor = pinAuthor;
    }

    public long getPinDate() {
        return pinDate;
    }

    public void setPinDate(long pinDate) {
        this.pinDate = pinDate;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }
}
