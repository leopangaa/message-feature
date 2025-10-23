package chatdemo.model;

import java.util.LinkedList;
import java.util.Queue;

public class MessageQueue {
    private final Queue<Message> queue = new LinkedList<>();

    public synchronized void send(Message message) {
        queue.add(message);
        notifyAll();
    }

    public synchronized Message receive() {
        while (queue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                return null;
            }
        }
        return queue.poll();
    }
}
