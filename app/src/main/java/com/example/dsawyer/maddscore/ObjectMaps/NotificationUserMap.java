package com.example.dsawyer.maddscore.ObjectMaps;

import com.example.dsawyer.maddscore.Objects.Notification;

public class NotificationUserMap {
    private Notification notification;

    /*** User Fields ***/
    private String name, username, PhotoUrl, squadName;

    public NotificationUserMap(Notification notification, String name, String username, String photoUrl) {
        this.notification = notification;
        this.name = name;
        this.username = username;
        PhotoUrl = photoUrl;
    }

    public String getSquadName() {
        return squadName;
    }

    public void setSquadName(String squadName) {
        this.squadName = squadName;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
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
}
