package chatdemo;

import chatdemo.model.MessageQueue;
import chatdemo.ui.ChatUI;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MessageQueue queue = new MessageQueue();
            new ChatUI(queue).setVisible(true);
        });
    }
}
