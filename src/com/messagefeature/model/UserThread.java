package com.messagefeature.model;

import java.util.Random;

public class UserThread extends Thread {
    private final String userName;
    private final MessageQueue queue;
    private volatile boolean running = true;
    private final Random random = new Random();
    private static final String[] SAMPLE_TEXTS = {
        "Hello!", "How's it going?", "Anyone here?", "Nice to meet you.", "That's cool.",
        "I agree.", "Ping!", "What's up?", "Good job!", "See you later."
    };

    public UserThread(String userName, MessageQueue queue) {
        super("UserThread-" + userName);
        this.userName = userName;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (running && !Thread.currentThread().isInterrupted()) {
                String text = SAMPLE_TEXTS[random.nextInt(SAMPLE_TEXTS.length)];
                queue.sendMessage(new Message(userName, text));
                Thread.sleep(500 + random.nextInt(1500));
            }
        } catch (InterruptedException e) {
        }
    }

    public void stopRunning() {
        running = false;
        this.interrupt();
    }

    public String getUserName() {
        return userName;
    }
}
