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

public class MainPanel extends JPanel implements ActionListener {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final JsonUtils jsonUtils;

    private JPopupMenu popup;
    private JFileChooser fileChooser;

    private JTextArea console;
    private JList<String> list;
    private JButton addButton;
    private JButton removeButton;

    private JTextField pathField;
    private JButton selectFileButton;
    private JButton convertButton;


    private boolean converting = false;

    public MainPanel(JsonUtils jsonUtils) {
        this.jsonUtils = jsonUtils;
        init();
    }

    private void init() {

        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        //
        c.weightx = 0.5;
        c.weighty = 0.25;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.PAGE_START;
        c.gridwidth = 4;

        console = new JTextArea(20, 0);
        console.setEditable(false);
        console.setLineWrap(true);
        DefaultCaret caret = (DefaultCaret) console.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        add(new JScrollPane(console), c);

        //
        c = new GridBagConstraints();
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.gridy = 1;
        c.gridx = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;

        list = new JList<>(new DefaultListModel<>());
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON3) return;
                popup.show(list, e.getX(), e.getY());
            }
        });

        add(new JScrollPane(list), c);

        //

        c = new GridBagConstraints();
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.gridy = 2;
        c.gridx = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;

        addButton = new JButton("Add");
        addButton.addActionListener(this);
        add(addButton, c);

        //
        c = new GridBagConstraints();
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.gridy = 2;
        c.gridx = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_END;

        removeButton = new JButton("Remove");
        removeButton.addActionListener(this);
        add(removeButton, c);

        //

        c = new GridBagConstraints();
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.gridy = 1;
        c.gridx = 1;
        c.anchor = GridBagConstraints.PAGE_END;

        JLabel label = new JLabel("Path to .json file");
        add(label, c);

        //

        c = new GridBagConstraints();
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.gridy = 2;
        c.gridx = 1;
        c.anchor = GridBagConstraints.PAGE_START;

        pathField = new JTextField(20);
        add(pathField, c);

        //

        c = new GridBagConstraints();
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.gridy = 2;
        c.gridx = 2;
        c.anchor = GridBagConstraints.PAGE_START;

        selectFileButton = new JButton("Select File(s)");
        selectFileButton.addActionListener(this);
        add(selectFileButton, c);

        //

        c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.gridy = 2;
        c.gridx = 3;
        c.anchor = GridBagConstraints.PAGE_START;

        convertButton = new JButton("Convert File(s)");
        convertButton.addActionListener(this);
        add(convertButton, c);

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

}
