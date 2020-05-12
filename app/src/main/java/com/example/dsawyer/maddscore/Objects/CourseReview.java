package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class CourseReview implements Parcelable {

    private String courseId, reviewerId, review;
    private long date;
    private double rating;

    public CourseReview() {}

    public CourseReview(String courseID, String reviewerId, long date, double rating, String review) {
        this.courseId = courseID;
        this.reviewerId = reviewerId;
        this.date = date;
        this.rating = rating;
        this.review = review;
    }

    protected CourseReview(Parcel in) {
        courseId = in.readString();
        reviewerId = in.readString();
        review = in.readString();
        date = in.readLong();
        rating = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(courseId);
        dest.writeString(reviewerId);
        dest.writeString(review);
        dest.writeLong(date);
        dest.writeDouble(rating);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CourseReview> CREATOR = new Creator<CourseReview>() {
        @Override
        public CourseReview createFromParcel(Parcel in) {
            return new CourseReview(in);
        }

        @Override
        public CourseReview[] newArray(int size) {
            return new CourseReview[size];
        }
    };

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
