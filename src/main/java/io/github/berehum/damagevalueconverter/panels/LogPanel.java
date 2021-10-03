package io.github.berehum.damagevalueconverter.panels;

import javax.swing.*;
import javax.swing.text.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LogPanel extends JPanel {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    protected JTextArea textArea;

    public LogPanel() {
        initBoard();
    }

    private void initBoard() {

        textArea = new JTextArea(20, 50);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        add(new JScrollPane(textArea));
    }

    public void log(String msg) {
        LocalTime time = LocalTime.now();
        textArea.append(time.format(formatter) + ": " + msg + "\n");
    }

}
