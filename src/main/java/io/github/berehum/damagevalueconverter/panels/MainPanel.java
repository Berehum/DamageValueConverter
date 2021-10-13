package io.github.berehum.damagevalueconverter.panels;

import io.github.berehum.damagevalueconverter.JsonUtils;
import io.github.berehum.damagevalueconverter.MainApplication;

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

    private JPopupMenu popup;
    protected JFileChooser fileChooser;

    private JTextArea console;
    private JList<String> list;
    private JButton addButton;
    private JButton removeButton;

    private JTextField pathField;
    private JButton selectFileButton;
    private JButton convertButton;


    private boolean converting = false;

    public MainPanel() {
        init();
    }

    private void init() {
        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        //
        constraints.fill = GridBagConstraints.HORIZONTAL;

        console = new JTextArea(20, 50);
        console.setEditable(false);
        console.setLineWrap(true);
        DefaultCaret caret = (DefaultCaret) console.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        add(new JScrollPane(console), constraints);

        //


        list = new JList<>(new DefaultListModel<>());
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON3) return;
                popup.show(list, e.getX(), e.getY());
            }
        });

        add(new JScrollPane(list));

        //

        addButton = new JButton("Add");
        addButton.addActionListener(this);
        add(addButton);

        //

        removeButton = new JButton("Remove");
        removeButton.addActionListener(this);
        add(removeButton);

        //

        JLabel label = new JLabel("Path to .json file");
        add(label);

        //

        pathField = new JTextField(20);
        add(pathField);

        //

        selectFileButton = new JButton("Select File(s)");
        selectFileButton.addActionListener(this);
        add(selectFileButton);

        //

        convertButton = new JButton("Convert File(s)");
        convertButton.addActionListener(this);
        add(convertButton);

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
                application.log("You added the file: " + file.getName());
            }

        }

        if (convertButton.equals(e.getSource())) {
            if (converting) return;
            String buttonText = convertButton.getText();
            convertButton.setText("Converting..");
            converting = true;
            JsonUtils jsonUtils = application.getJsonUtils();
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
