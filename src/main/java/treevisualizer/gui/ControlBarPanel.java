package treevisualizer.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ControlBarPanel extends JPanel {
    private JLabel stepLabel;
    private JProgressBar progressBar;
    private JButton resetBtn, backBtn, playPauseBtn, forwardBtn, endBtn;
    private JButton undoBtn, redoBtn;
    private JSlider speedSlider;
    private JLabel statusLabel;
    private VisualizationScreen screen;
    private Timer animationTimer;
    private boolean isPlaying = false;
    private int[] speedDelays = {800, 400, 200};

    public ControlBarPanel(VisualizationScreen screen) {
        this.screen = screen;
        setBackground(new Color(40, 40, 40));
        setBorder(new EmptyBorder(8, 10, 8, 10));
        setLayout(new BorderLayout(10, 0));
        buildUI();
    }

    private void buildUI() {
        // Left: step info
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);

        stepLabel = new JLabel("Step: 0/0");
        stepLabel.setForeground(Color.WHITE);
        stepLabel.setFont(new Font("Arial", Font.BOLD, 13));
        leftPanel.add(stepLabel);

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(140, 16));
        progressBar.setStringPainted(false);
        progressBar.setForeground(new Color(70, 130, 180));
        progressBar.setBackground(new Color(60, 60, 60));
        leftPanel.add(progressBar);

        add(leftPanel, BorderLayout.WEST);

        // Center: playback controls
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        centerPanel.setOpaque(false);

        resetBtn = createControlButton("|◀", "Reset to beginning");
        backBtn = createControlButton("◀", "Step backward");
        playPauseBtn = createControlButton("▶", "Play/Pause animation");
        forwardBtn = createControlButton("▶|", "Step forward");
        endBtn = createControlButton("▶|▶", "Go to end");

        resetBtn.addActionListener(e -> { stopAnimation(); screen.resetAnimation(); });
        backBtn.addActionListener(e -> { stopAnimation(); screen.stepBackward(); });
        playPauseBtn.addActionListener(e -> togglePlayPause());
        forwardBtn.addActionListener(e -> { stopAnimation(); screen.stepForward(); });
        endBtn.addActionListener(e -> { stopAnimation(); screen.goToEnd(); });

        centerPanel.add(resetBtn);
        centerPanel.add(backBtn);
        centerPanel.add(playPauseBtn);
        centerPanel.add(forwardBtn);
        centerPanel.add(endBtn);

        statusLabel = new JLabel("  Ready");
        statusLabel.setForeground(new Color(150, 150, 150));
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        centerPanel.add(statusLabel);

        add(centerPanel, BorderLayout.CENTER);

        // Right: speed + undo/redo
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);

        JLabel speedLabel = new JLabel("Speed:");
        speedLabel.setForeground(Color.WHITE);
        speedLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        rightPanel.add(speedLabel);

        speedSlider = new JSlider(0, 2, 0);
        speedSlider.setPreferredSize(new Dimension(80, 20));
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setSnapToTicks(true);
        speedSlider.setOpaque(false);
        rightPanel.add(speedSlider);

        JLabel[] speedLabels = {new JLabel("1x"), new JLabel("2x"), new JLabel("4x")};
        for (JLabel sl : speedLabels) {
            sl.setForeground(new Color(180, 180, 180));
            sl.setFont(new Font("Arial", Font.PLAIN, 11));
        }
        rightPanel.add(speedLabels[0]);
        rightPanel.add(speedLabels[1]);
        rightPanel.add(speedLabels[2]);

        undoBtn = createSmallButton("Undo");
        redoBtn = createSmallButton("Redo");
        undoBtn.addActionListener(e -> screen.undo());
        redoBtn.addActionListener(e -> screen.redo());
        rightPanel.add(undoBtn);
        rightPanel.add(redoBtn);

        add(rightPanel, BorderLayout.EAST);
    }

    private JButton createControlButton(String text, String tooltip) {
        JButton btn = new JButton(text);
        btn.setToolTipText(tooltip);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(new Color(70, 70, 80));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(46, 32));
        return btn;
    }

    private JButton createSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setBackground(new Color(80, 80, 100));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(55, 28));
        return btn;
    }

    public void updateProgress(int current, int total) {
        stepLabel.setText("Step: " + current + "/" + total);
        if (total > 0) {
            progressBar.setValue((int) ((current * 100.0) / total));
        } else {
            progressBar.setValue(0);
        }
    }

    public void setStatus(String status) {
        statusLabel.setText("  " + status);
    }

    private void togglePlayPause() {
        if (isPlaying) {
            stopAnimation();
        } else {
            startAnimation();
        }
    }

    public void startAnimation() {
        if (animationTimer != null) animationTimer.stop();
        int delay = speedDelays[speedSlider.getValue()];
        animationTimer = new Timer(delay, e -> {
            boolean hasMore = screen.stepForward();
            if (!hasMore) {
                stopAnimation();
            }
        });
        animationTimer.start();
        isPlaying = true;
        playPauseBtn.setText("⏸");
        setStatus("Playing...");
    }

    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
        isPlaying = false;
        playPauseBtn.setText("▶");
        setStatus("Ready");
    }

    public boolean isPlaying() { return isPlaying; }
}
