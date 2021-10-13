package io.github.berehum.damagevalueconverter;

import io.github.berehum.damagevalueconverter.panels.*;

import javax.swing.*;
import java.awt.*;

public class MainApplication extends JFrame {

    private MainPanel mainPanel;
    private JsonUtils jsonUtils;

    public MainApplication() {
        jsonUtils = new JsonUtils(this);
        initUI();
    }

    private void initUI() {
        add(mainPanel);
        getContentPane().add(BorderLayout.NORTH, new LogPanel());
        getContentPane().add(BorderLayout.CENTER, new FileListPanel(this));
        getContentPane().add(BorderLayout.SOUTH, new FileSelectorPanel(this));

        setTitle("Damage Value Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        //setSize(new Dimension(600, 600));
        pack();
        log("Started program");

    }


    public void log(String msg) {
        getLogPanel().log(msg);
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
