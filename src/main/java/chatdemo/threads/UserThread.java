package chatdemo.threads;

import chatdemo.model.Message;
import chatdemo.model.MessageQueue;

public class UserThread extends Thread {
    private final String name;
    private final MessageQueue queue;
    private boolean running = true;

    public UserThread(String name, MessageQueue queue) {
        this.name = name;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            int msgCount = 0;
            while (running && msgCount < 8) {
                Thread.sleep(1000 + (int) (Math.random() * 2000));
                String[] phrases = {
                    "TANGINA NI MONA!",
                    "naka ai nanaman assignment amp",
                    "HAHAHAHAHAHA",
                    "ANLALA",
                    "sinusumpa ko na yan siya",
                    "kaantok naman",
                    "nanghula siya ng grade",
                    "order mcdo"
                };
                String msg = phrases[(int) (Math.random() * phrases.length)];
                queue.send(new Message(name, msg, false));
                msgCount++;
            }
        } catch (InterruptedException ignored) {}
    }

    public void stopUser() {
        running = false;
        interrupt();
    }
}
