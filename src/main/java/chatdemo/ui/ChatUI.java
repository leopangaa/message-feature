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
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        try {
            logWriter = new FileWriter("chat_log.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        chatPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0,
                        new Color(230, 245, 230),
                        0, getHeight(),
                        new Color(210, 235, 210)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(240, 250, 240));

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(180, 210, 180);
                this.trackColor = new Color(230, 245, 230);
            }
        });
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            }
        };
        inputPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        inputPanel.setBackground(Color.WHITE);

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 220, 190), 1),
                new EmptyBorder(8, 12, 8, 12)));
        inputField.setBackground(new Color(250, 250, 250));
        inputField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    sendMessage();
                    startSim();
                }
            }
        });

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        sendButton.setBackground(new Color(60, 150, 90));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setOpaque(true);
        sendButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 255, 245));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            }
        };
        topPanel.setBorder(new EmptyBorder(8, 16, 8, 16));
        topPanel.setBackground(new Color(245, 255, 245));

        startButton = new JButton("Start Group Chat");
        stopButton = new JButton("Stop Group Chat");
        for (JButton btn : new JButton[] { startButton, stopButton }) {
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setBackground(new Color(60, 150, 90));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setOpaque(true);
            btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        }
        topPanel.add(startButton);
        topPanel.add(stopButton);
        add(topPanel, BorderLayout.NORTH);

        sendButton.addActionListener(e -> startSim());
        sendButton.addActionListener(e -> sendMessage());
        startButton.addActionListener(e -> startSim());
        stopButton.addActionListener(e -> stopSim());

        addClickAnimation(sendButton, new Color(60, 150, 90));
        addClickAnimation(startButton, new Color(60, 150, 90));
        addClickAnimation(stopButton, new Color(60, 150, 90));

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
        if (text.isEmpty())
            return;
        queue.send(new Message("You", text, true));
        inputField.setText("");
    }

    private void startSim() {
        users.clear();
        String[] botNames = { "Aila", "Aira" };
        for (String name : botNames) {
            UserThread u = new UserThread(name, queue);
            users.add(u);
            u.start();
        }
    }

    private void stopSim() {
        for (UserThread u : users)
            u.stopUser();
        users.clear();
    }

    private void addMessageBubble(Message msg) {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(5, 10, 5, 10));

        JPanel bubble = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (msg.isUser()) {
                    g2.setColor(new Color(190, 235, 180));
                } else {
                    g2.setColor(Color.WHITE);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(new Color(200, 220, 200));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            }
        };
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
        bubble.setOpaque(false);
        bubble.setBorder(new EmptyBorder(10, 14, 10, 14));
        bubble.setMaximumSize(new Dimension(420, Integer.MAX_VALUE));

        JLabel sender = new JLabel(msg.getSender());
        sender.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        sender.setForeground(msg.isUser() ? new Color(34, 102, 34) : new Color(60, 130, 60));

        JLabel text = new JLabel("<html><body style='width:280px;'>" + msg.getText() + "</body></html>");
        text.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        text.setForeground(new Color(25, 25, 25));
        text.setBorder(new EmptyBorder(4, 0, 4, 0));

        JLabel time = new JLabel(msg.getTime());
        time.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        time.setForeground(new Color(110, 130, 110));
        time.setBorder(new EmptyBorder(2, 0, 0, 0));

        bubble.add(sender);
        bubble.add(text);
        bubble.add(time);

        if (msg.isUser()) {
            container.add(bubble, BorderLayout.EAST);
        } else {
            container.add(bubble, BorderLayout.WEST);
        }

        chatPanel.add(container);
        chatPanel.add(Box.createVerticalStrut(6));
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
        } catch (IOException ignored) {
        }
    }

    private void addClickAnimation(JButton button, Color normalColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                button.setBackground(normalColor.darker());
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                Timer timer = new Timer(15, null);
                final float[] brightness = { 0f };
                timer.addActionListener(ae -> {
                    brightness[0] += 0.1f;
                    if (brightness[0] >= 1f) {
                        button.setBackground(normalColor);
                        timer.stop();
                    } else {
                        int r = (int) (normalColor.getRed() * (1 - brightness[0])
                                + normalColor.darker().getRed() * brightness[0]);
                        int g = (int) (normalColor.getGreen() * (1 - brightness[0])
                                + normalColor.darker().getGreen() * brightness[0]);
                        int b = (int) (normalColor.getBlue() * (1 - brightness[0])
                                + normalColor.darker().getBlue() * brightness[0]);
                        button.setBackground(new Color(r, g, b));
                    }
                });
                timer.start();
            }
        });
    }

}