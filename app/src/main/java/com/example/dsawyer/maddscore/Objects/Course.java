package com.example.dsawyer.maddscore.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;

public class Course implements Parcelable, ClusterItem, Comparable<Course> {

    private String courseId, name, description, address;
    private Double latitude, longitude, rating;
    private Integer numRatings, numHoles;
    private ArrayList<String> contributors;
    private float distance;
    private int line;
    private String customCourseCreator;

    public Course() {}

    public Course(String courseId, String name, String address, int numHoles, Double latitude, Double longitude) {
        this.courseId = courseId;
        this.name = name;
        this.address = address;
        this.numHoles = numHoles;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /*****  constructor for custom course   *****/

    public Course(String customCourseCreator, String courseId, String name, int numHoles) {
        this.customCourseCreator = customCourseCreator;
        this.courseId = courseId;
        this.name = name;
        this.numHoles = numHoles;
        this.latitude = 0.0;
        this.longitude = 0.0;
    }

    public Course(String name) {
        this.name = name;
    }
    public Course(String name, int line) {
        this.name = name;
        this.line = line;
    }

    public Course(double lat, double lon, String title, String address, String courseId) {
        this.latitude = lat;
        this.longitude = lon;
        this.name = title;
        this.address = address;
        this.courseId = courseId;
    }

    protected Course(Parcel in) {
        courseId = in.readString();
        name = in.readString();
        description = in.readString();
        address = in.readString();
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readDouble();
        }
        if (in.readByte() == 0) {
            numRatings = null;
        } else {
            numRatings = in.readInt();
        }
        if (in.readByte() == 0) {
            numHoles = null;
        } else {
            numHoles = in.readInt();
        }
        contributors = in.createStringArrayList();
        distance = in.readFloat();
        line = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(courseId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(address);
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(rating);
        }
        if (numRatings == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(numRatings);
        }
        if (numHoles == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(numHoles);
        }
        dest.writeStringList(contributors);
        dest.writeFloat(distance);
        dest.writeInt(line);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSnippet() {
        return address;
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    @Override
    public int compareTo(Course o) {
        float compare = o.getDistance();
        return (int)(this.distance - compare);
    }

    public String getCustomCourseCreator() {
        return customCourseCreator;
    }

    public void setCustomCourseCreator(String customCourseCreator) {
        this.customCourseCreator = customCourseCreator;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(Integer numRatings) {
        this.numRatings = numRatings;
    }

    public Integer getNumHoles() {
        return numHoles;
    }

    public void setNumHoles(Integer numHoles) {
        this.numHoles = numHoles;
    }

    public ArrayList<String> getContributors() {
        return contributors;
    }

    public void setContributors(ArrayList<String> contributors) {
        this.contributors = contributors;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public static Creator<Course> getCREATOR() {
        return CREATOR;
    }
}
