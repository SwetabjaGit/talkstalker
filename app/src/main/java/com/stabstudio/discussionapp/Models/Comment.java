package com.stabstudio.discussionapp.Models;

import org.joda.time.DateTime;



public class Comment {

    private String id;
    private String discussion_id;
    private String author;
    private String text;
    private String timestamp;

    public Comment(){
    }

    public Comment(String id, String discussion_id, String author, String text, String timestamp){
        this.id = id;
        this.discussion_id = discussion_id;
        this.author = author;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDiscussion_id() {
        return discussion_id;
    }

    public void setDiscussion_id(String discussion_id) {
        this.discussion_id = discussion_id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
