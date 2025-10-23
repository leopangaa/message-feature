package com.messagefeature.ui;

import com.messagefeature.model.Message;
import com.messagefeature.model.MessageQueue;
import com.messagefeature.model.UserThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatWindow extends JFrame {
    private final MessageQueue messageQueue;
    private final JTextArea chatArea = new JTextArea();
    private final JTextField inputField = new JTextField();
    private final JButton sendButton = new JButton("Send");
    private final JButton addUserButton = new JButton("Add Simulated User");
    private final JButton startSimButton = new JButton("Start Simulation");
    private final JButton stopSimButton = new JButton("Stop Simulation");
    private final DefaultListModel<String> usersListModel = new DefaultListModel<>();
    private final JList<String> usersList = new JList<>(usersListModel);

    private final List<UserThread> userThreads = Collections.synchronizedList(new ArrayList<>());
    private Thread consumerThread;
    private final AtomicBoolean simRunning = new AtomicBoolean(false);
    private int userCounter = 1;

    public ChatWindow(MessageQueue messageQueue) {
        super("Concurrent Chat Simulation");
        this.messageQueue = messageQueue;
        initComponents();
        setupLayout();
        setupListeners();
        setSize(800, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        usersList.setVisibleRowCount(8);
    }

    private void setupLayout() {
        JPanel left = new JPanel(new BorderLayout(6, 6));
        left.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(6, 6));
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        left.add(bottomPanel, BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout(6, 6));
        right.setPreferredSize(new Dimension(240, 0));
        right.add(new JLabel("Simulated Users:"), BorderLayout.NORTH);
        right.add(new JScrollPane(usersList), BorderLayout.CENTER);

        JPanel controls = new JPanel(new GridLayout(0, 1, 6, 6));
        controls.add(addUserButton);
        controls.add(startSimButton);
        controls.add(stopSimButton);

        right.add(controls, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout(6, 6));
        getContentPane().add(left, BorderLayout.CENTER);
        getContentPane().add(right, BorderLayout.EAST);

        JPanel top = new JPanel(new BorderLayout());
        JLabel info = new JLabel("Concurrent Chat Simulation — message passing demo");
        top.add(info, BorderLayout.WEST);
        getContentPane().add(top, BorderLayout.NORTH);
    }

    private void setupListeners() {
        sendButton.addActionListener((ActionEvent e) -> {
            String text = inputField.getText().trim();
            if (text.isEmpty()) return;
            String sender = usersList.getSelectedValue() != null ? usersList.getSelectedValue() : "You";
            messageQueue.sendMessage(new Message(sender, text));
            inputField.setText("");
        });

        inputField.addActionListener((ActionEvent e) -> sendButton.doClick());

        addUserButton.addActionListener((ActionEvent e) -> {
            String userName = "User" + userCounter++;
            usersListModel.addElement(userName);
            appendToChatArea("System: Added simulated user '" + userName + "'");
        });

        startSimButton.addActionListener((ActionEvent e) -> startSimulation());

        stopSimButton.addActionListener((ActionEvent e) -> stopSimulation());

        usersList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    String selected = usersList.getSelectedValue();
                    if (selected != null && !isUserThreadRunning(selected)) {
                        startUserThread(selected);
                    } else if (selected != null) {
                        appendToChatArea("System: Thread for " + selected + " already running.");
                    }
                }
            }
        });
    }

    private void appendToChatArea(String text) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(text + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    private boolean isUserThreadRunning(String userName) {
        synchronized (userThreads) {
            return userThreads.stream().anyMatch(t -> t.getUserName().equals(userName) && t.isAlive());
        }
    }

    private void startUserThread(String name) {
        UserThread ut = new UserThread(name, messageQueue);
        userThreads.add(ut);
        ut.start();
        appendToChatArea("System: Started thread for " + name);
    }

    private void startSimulation() {
        if (simRunning.get()) {
            appendToChatArea("System: Simulation already running.");
            return;
        }
        simRunning.set(true);
        consumerThread = new Thread(() -> {
            while (simRunning.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    Message msg = messageQueue.receiveMessage();
                    appendToChatArea(msg.toString());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "ConsumerThread");
        consumerThread.start();

        List<String> listSnapshot = Collections.list(usersListModel.elements());
        for (String user : listSnapshot) {
            if (!isUserThreadRunning(user)) {
                startUserThread(user);
            }
        }

        appendToChatArea("System: Simulation started. Consumer running.");
    }

    private void stopSimulation() {
        if (!simRunning.get()) {
            appendToChatArea("System: Simulation is not running.");
            return;
        }

        synchronized (userThreads) {
            for (UserThread ut : userThreads) {
                ut.stopRunning();
            }
            userThreads.clear();
        }

        simRunning.set(false);
        if (consumerThread != null) {
            consumerThread.interrupt();
        }

        appendToChatArea("System: Simulation stopped. All user threads terminated.");
    }
}
