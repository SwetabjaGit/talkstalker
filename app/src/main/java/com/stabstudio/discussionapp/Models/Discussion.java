package com.stabstudio.discussionapp.Models;

import org.joda.time.DateTime;

import java.util.ArrayList;


public class Discussion {

    private String id;
    private String place_id;
    private String user_id;
    private String subject;
    private String photoUrl;
    private String content;
    private String timestamp;
    private int likes;
    private int comments;
    //private ArrayList<Comment> commentsList = new ArrayList<>();

    public Discussion(){
    }

    public Discussion(String id, String place_id, String user_id, String subject, String photoUrl, String content, String timestamp, int likes, int comments){
        this.id = id;
        this.place_id = place_id;
        this.user_id = user_id;
        this.subject = subject;
        this.photoUrl = photoUrl;
        this.content = content;
        this.timestamp = timestamp;
        this.likes = likes;
        this.comments = comments;
    }

    /*public Discussion(String id, String place_id, String user_id, String subject, String photoUrl, String content, String timestamp, int likes, int comments, ArrayList<Comment> commentsList){
        this.id = id;
        this.place_id = place_id;
        this.user_id = user_id;
        this.subject = subject;
        this.photoUrl = photoUrl;
        this.content = content;
        this.timestamp = timestamp;
        this.likes = likes;
        this.comments = comments;
        this.commentsList = commentsList;
    }*/

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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    /*public ArrayList<Comment> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(ArrayList<Comment> commentsList) {
        this.commentsList = commentsList;
    }*/

    public void incrementLike(){
        this.likes++;
    }

    public void decrementLike(){
        this.likes--;
    }

}
