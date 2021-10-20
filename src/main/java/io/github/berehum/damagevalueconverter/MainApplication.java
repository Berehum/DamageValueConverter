package io.github.berehum.damagevalueconverter;

import io.github.berehum.damagevalueconverter.panels.*;

import javax.swing.*;
import java.awt.*;

public class MainApplication extends JFrame implements Logger{

    private MainPanel mainPanel;
    private JsonUtils jsonUtils;
    private Dimension dimension;

    public MainApplication() {
        jsonUtils = new JsonUtils(this);
        initUI();
    }

    private void initUI() {
        mainPanel = new MainPanel(jsonUtils);
        add(mainPanel);

        setTitle("Damage Value Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        dimension = getSize();
        setMinimumSize(dimension);
        log("Started program");

    }


    public void log(String msg) {
        mainPanel.log(msg);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApplication mainApplication = new MainApplication();
            mainApplication.setVisible(true);
        });

    }

    public JsonUtils getJsonUtils() {
        return jsonUtils;
    }

}
