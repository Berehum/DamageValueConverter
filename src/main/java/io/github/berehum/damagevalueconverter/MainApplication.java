package io.github.berehum.damagevalueconverter;

import io.github.berehum.damagevalueconverter.panels.FileListPanel;
import io.github.berehum.damagevalueconverter.panels.FileSelectorPanel;
import io.github.berehum.damagevalueconverter.panels.LogPanel;

import javax.swing.*;
import java.awt.*;

public class MainApplication extends JFrame {

    private LogPanel logPanel;
    private FileListPanel fileListPanel;
    private FileSelectorPanel fileSelectorPanel;
    private JsonUtils jsonUtils;

    public MainApplication() {
        initUI();
    }

    private void initUI() {
        jsonUtils = new JsonUtils(this);
        logPanel = new LogPanel();
        fileListPanel = new FileListPanel(this);
        fileSelectorPanel = new FileSelectorPanel(this);

        getContentPane().add(BorderLayout.NORTH, logPanel);
        getContentPane().add(BorderLayout.CENTER, fileListPanel);
        getContentPane().add(BorderLayout.SOUTH, fileSelectorPanel);

        setTitle("Damage Value Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(new Dimension(600, 600));
        setResizable(false);
        log("Started program");

    }

    public void log(String msg) {
        logPanel.log(msg);
    }

    public LogPanel getLogPanel() {
        return logPanel;
    }

    public FileListPanel getFileListPanel() {
        return fileListPanel;
    }

    public FileSelectorPanel getFileSelectorPanel() {
        return fileSelectorPanel;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            MainApplication mainApplication = new MainApplication();
            mainApplication.setVisible(true);
        });

    }

    public JsonUtils getJsonUtils() {
        return jsonUtils;
    }

}
