package com.example.dsawyer.maddscore.Objects;

public class PostComment {

    private String userID, postID, commentID, commentBody;
    private long commentDate;

    public PostComment() {  }

    public PostComment(String userID,
                       String postID,
                       String commentID,
                       String commentBody,
                       long commentDate) {
        this.userID = userID;
        this.postID = postID;
        this.commentID = commentID;
        this.commentBody = commentBody;
        this.commentDate = commentDate;
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
}
