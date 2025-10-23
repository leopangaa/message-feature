package chatdemo.ui;

import chatdemo.model.Message;
import chatdemo.model.MessageQueue;
import chatdemo.threads.UserThread;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ChatUI extends JFrame {
    private JPanel chatPanel;
    private JTextField inputField;
    private JButton sendButton, startButton, stopButton;
    private MessageQueue queue;
    private ArrayList<UserThread> users = new ArrayList<>();
    private JScrollPane scrollPane;
    private FileWriter logWriter;

    public ChatUI(MessageQueue queue) {
        this.queue = queue;
        setTitle("Principles of Programming Group Chat");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        try {
            logWriter = new FileWriter("chat_log.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(248, 249, 250));
        
        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBlockIncrement(64);
        
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(220, 220, 220);
                this.trackColor = new Color(248, 249, 250);
            }
        });
        
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputPanel.setBackground(new Color(255, 255, 255));

        inputField = new JTextField();
        inputField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    sendMessage();
                    startSim();
                }
            }
        });

        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(0, 153, 255));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel();
        startButton = new JButton("Start Group Chat");
        stopButton = new JButton("Stop Group Chat");
        topPanel.add(startButton);
        topPanel.add(stopButton);
        add(topPanel, BorderLayout.NORTH);

        sendButton.addActionListener(e -> startSim());
        sendButton.addActionListener(e -> sendMessage());
        startButton.addActionListener(e -> startSim());
        stopButton.addActionListener(e -> stopSim());

        new Thread(() -> {
            while (true) {
                Message msg = queue.receive();
                if (msg != null) {
                    SwingUtilities.invokeLater(() -> addMessageBubble(msg));
                    writeToLog(msg.toString());
                }
            }
        }).start();
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;
        queue.send(new Message("You", text, true));
        inputField.setText("");
    }

    private void startSim() {
        users.clear();
        String[] botNames = {"Aila", "Aira"};
        for (String name : botNames) {
            UserThread u = new UserThread(name, queue);
            users.add(u);
            u.start();
        }
    }

    private void stopSim() {
        for (UserThread u : users) u.stopUser();
        users.clear();
    }

    private void addMessageBubble(Message msg) {
        JPanel bubble = new JPanel();
        bubble.setLayout(new BorderLayout(5, 2));
        bubble.setOpaque(false);
        bubble.setBorder(new EmptyBorder(5, 10, 5, 10));

        // Create header panel for sender and time
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel sender = new JLabel(msg.getSender());
        sender.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JLabel time = new JLabel(msg.getTime());
        time.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        time.setForeground(Color.GRAY);

        JLabel text = new JLabel("<html><body style='display:inline-block; min-width:60px; max-width:250px;'>" + msg.getText() + "</body></html>");
        text.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(4, 12, 4, 12)
        ));
        text.setOpaque(true);
        text.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        int minWidth = 60;
        int maxWidth = 250;
        int bubbleWidth = Math.max(minWidth, Math.min(maxWidth, msg.getText().length() * 8 + 40));
        text.setPreferredSize(new Dimension(bubbleWidth, text.getPreferredSize().height));

        if (msg.isUser()) {
            headerPanel.add(time, BorderLayout.WEST);
            headerPanel.add(sender, BorderLayout.EAST);
            text.setBackground(new Color(0, 132, 255));
            text.setForeground(Color.WHITE);
            bubble.add(text, BorderLayout.EAST);
        } else {
            headerPanel.add(sender, BorderLayout.WEST);
            headerPanel.add(time, BorderLayout.EAST);
            text.setBackground(new Color(240, 240, 240));
            text.setForeground(Color.BLACK);
            bubble.add(text, BorderLayout.WEST);
        }

        bubble.add(headerPanel, BorderLayout.NORTH);
        chatPanel.add(bubble);
        refresh();
    }

    private void refresh() {
        chatPanel.revalidate();
        chatPanel.repaint();
        JScrollBar bar = scrollPane.getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
    }

    private void writeToLog(String line) {
        try {
            if (logWriter != null) {
                logWriter.write(line + "\n");
                logWriter.flush();
            }
        } catch (IOException ignored) {}
    }
}
