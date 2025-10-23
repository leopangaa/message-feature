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
                    "grpwork daw sa ppl ngayon",
                    "check niyo docs",
                    "okay pano hatian sa parts?",
                    "pili na lang kayo kung ano gusto niyo",
                    "akin na lang matitira",
                    "ako sa A at B",
                    "go ako sa last part",
                    "sige ako na sa natira"
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
