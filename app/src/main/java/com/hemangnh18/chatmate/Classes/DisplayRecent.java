package com.hemangnh18.chatmate.Classes;

public class DisplayRecent {


    String id;
    String message;
    String time;

    public String getBase64() {
        return base64;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    String base64,username;
    Boolean seen;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public DisplayRecent() {
    }

    public DisplayRecent(String id, String message, String time, Boolean seen) {
        this.id = id;
        this.message = message;
        this.time = time;
        this.seen = seen;
    }
}
