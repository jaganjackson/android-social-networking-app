package com.example.socialnetwork;

public class Comments {
    public String comment, username, date, time;

    public Comments(){

    }

    public Comments(String comment, String username, String date, String time) {
        this.comment = comment;
        this.username = username;
        this.date = date;
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
