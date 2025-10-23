package chatdemo.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private String sender;
    private String text;
    private boolean isUser;
    private long timestamp;

    public Message(String sender, String text, boolean isUser) {
        this.sender = sender;
        this.text = text;
        this.isUser = isUser;
        this.timestamp = System.currentTimeMillis();
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public boolean isUser() {
        return isUser;
    }

    public String getTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date(timestamp));
    }

    @Override
    public String toString() {
        return "[" + getTime() + "] " + sender + ": " + text;
    }
}
