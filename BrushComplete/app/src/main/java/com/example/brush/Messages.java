package com.example.brush;

public class Messages {

    public String date, time, type, message, from;

    public Messages(){

    }

    public Messages(String date, String time, String type, String message, String from) {

        this.date = date;
        this.time = time;
        this.type = type;
        this.message = message;
        this.from = from;
    }

    public String getdate() {

        return date;
    }

    public void setdate(String date) {

        this.date = date;
    }

    public String gettime() {

        return time;
    }

    public void settime(String time) {

        this.time = time;
    }

    public String gettype() {

        return type;
    }

    public void settype(String type) {

        this.type = type;
    }

    public String getmessage() {

        return message;
    }

    public void setmessage(String message) {

        this.message = message;
    }

    public String getfrom() {

        return from;
    }

    public void setfrom(String from) {

        this.from = from;
    }
}

