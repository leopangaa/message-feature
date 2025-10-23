package com.messagefeature.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private final String sender;
    private final String text;
    private final String time;

    public Message(String sender, String text) {
        this.sender = sender;
        this.text = text;
        this.time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "[" + time + "] " + sender + ": " + text;
    }
}
