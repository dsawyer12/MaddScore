package com.example.dsawyer.maddscore.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostComment {

    private String userID, username, name, photoID, commentBody, postID, commentID;
    private long commentDate;
    private CircleImageView userIMG;

    public PostComment() {
    }

    public PostComment(String userID,
                       String username,
                       String name,
                       String photoID,
                       String commentBody,
                       long commentDate,
                       String postID,
                       CircleImageView userIMG) {
        this.userID = userID;
        this.username = username;
        this.name = name;
        this.photoID = photoID;
        this.commentBody = commentBody;
        this.commentDate = commentDate;
        this.postID = postID;
        this.userIMG = userIMG;
    }

    public String getPhotoID() {
        return photoID;
    }

    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public long getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(long commentDate) {
        this.commentDate = commentDate;
    }

    public CircleImageView getUserIMG() {
        return userIMG;
    }

    public void setUserIMG(CircleImageView userIMG) {
        this.userIMG = userIMG;
    }
}
