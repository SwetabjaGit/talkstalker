package com.stabstudio.discussionapp.Models;


import java.util.Map;

public class User {

    private String id;
    private String place_id;
    private String photoUrl;
    private String first_name;
    private String last_name;
    private String phoneNo;
    private String notificationToken;
    private Boolean placeSet;

    public User(){
    }

    public User(String id, String place_id, String photoUrl, String first_name, String last_name, String phoneNo, String notificationToken){
        this.id = id;
        this.place_id = place_id;
        this.photoUrl = photoUrl;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phoneNo = phoneNo;
        this.notificationToken = notificationToken;
        this.placeSet = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }

    public Boolean getPlaceSet() {
        return placeSet;
    }

    public void setPlaceSet(Boolean placeSet) {
        this.placeSet = placeSet;
    }
}
