package com.messagefeature.main;

import com.messagefeature.model.MessageQueue;
import com.messagefeature.ui.ChatWindow;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        MessageQueue queue = new MessageQueue();

        SwingUtilities.invokeLater(() -> {
            ChatWindow window = new ChatWindow(queue);
            window.setVisible(true);
        });
    }
}
