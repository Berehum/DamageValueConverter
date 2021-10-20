package io.github.berehum.damagevalueconverter.panels;

import io.github.berehum.damagevalueconverter.JsonUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Test extends JFrame implements ActionListener, Logger {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final JsonUtils jsonUtils;

    private JPanel testPanel;
    private JTextArea console;
    private JList<String> list;
    private JButton addButton;
    private JButton removeButton;
    private JTextField pathField;
    private JButton convertButton;
    private JButton selectFileButton;

    private JPopupMenu popup;
    private JFileChooser fileChooser;

    private boolean converting = false;

    public static void main(String[] args) {
        Test test = new Test();
    }

    public Test() {
        jsonUtils = new JsonUtils(this);

        setTitle("Damage Value Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(500, 550));
        add(testPanel);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
        init();
    }

    public void init() {
        //

        console.setEditable(false);
        console.setLineWrap(true);
        DefaultCaret caret = (DefaultCaret) console.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        //
        addButton.addActionListener(this);
        removeButton.addActionListener(this);
        selectFileButton.addActionListener(this);
        convertButton.addActionListener(this);

        // Invisible objects

        popup = new JPopupMenu("edit");
        JMenuItem copyToClipboard = new JMenuItem("Copy to clipboard");
        copyToClipboard.addActionListener(e -> {
            StringSelection stringSelection = new StringSelection(list.getSelectedValue());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        });
        JMenuItem copy = new JMenuItem("Copy");
        copy.addActionListener(e -> pathField.setText(list.getSelectedValue()));
        popup.add(copy);
        popup.add(copyToClipboard);

        //

        fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Select .json file");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".json", "json");
        fileChooser.setFileFilter(filter);
    }

    public void log(String msg) {
        LocalTime time = LocalTime.now();
        console.append(time.format(formatter) + ": " + msg + "\n");
    }

    public boolean addPath(String path) {
        File file = new File(path);
        if (!file.exists() || !file.getName().endsWith(".json")) return false;
        DefaultListModel<String> listModel = (DefaultListModel<String>) list.getModel();
        if (listModel.contains(path)) return false;
        listModel.addElement(path);
        return true;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (removeButton.equals(e.getSource())) {
            DefaultListModel<String> listModel = (DefaultListModel<String>) list.getModel();
            int removedValues = 0;
            for (int index : list.getSelectedIndices()) {
                listModel.removeElementAt(index-removedValues);
                removedValues++;
            }
        }

        if (addButton.equals(e.getSource())) {
            String path = pathField.getText();
            if (!addPath(path)) return;
            pathField.setText(null);
        }

        if (selectFileButton.equals(e.getSource())) {
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue != JFileChooser.APPROVE_OPTION) return;

            for (File file : fileChooser.getSelectedFiles()) {
                if (!file.isFile()) continue;
                if (!addPath(file.getAbsolutePath())) continue;
                log("You added the file: " + file.getName());
            }

        }

        if (convertButton.equals(e.getSource())) {
            if (converting) return;
            String buttonText = convertButton.getText();
            convertButton.setText("Converting..");
            converting = true;
            DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
            List<String> newList = new ArrayList<>();
            for (int i = 0; i < model.getSize(); i++) {
                String path = model.get(i);
                int result = jsonUtils.convert(path);
                if (result != JsonUtils.SUCCEEDED) {
                    newList.add(path);
                }
            }
            model.clear();
            model.addAll(newList);
            converting = false;
            convertButton.setText(buttonText);
        }
    }

    private void createUIComponents() {
        list = new JList<>(new DefaultListModel<>());
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON3) return;
                popup.show(list, e.getX(), e.getY());
            }
        });
    }
}
