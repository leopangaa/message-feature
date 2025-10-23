package com.messagefeature.model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue {
    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();

    public void sendMessage(Message message) {
        queue.offer(message);
    }

    public Message receiveMessage() throws InterruptedException {
        return queue.take();
    }

    public int size() {
        return queue.size();
    }
}
