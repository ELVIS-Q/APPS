package com.personal.firebaseapp;

public class SmsMessageData {
    public String sender;
    public String message;
    public String link;
    public String timestamp;

    public SmsMessageData() {} // Requerido por Firebase

    public SmsMessageData(String sender, String message, String link, String timestamp) {
        this.sender = sender;
        this.message = message;
        this.link = link;
        this.timestamp = timestamp;
    }
}
