package com.stabstudio.discussionapp.Models;

/**
 * Created by HP on 24-05-2017.
 */

public class Places {

    private String id;
    private String address;

    public Places(){

    }

    public Places(String id, String address){
        this.id = id;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
