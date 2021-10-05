package io.github.berehum.damagevalueconverter.panels;

import io.github.berehum.damagevalueconverter.JsonUtils;
import io.github.berehum.damagevalueconverter.MainApplication;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSelectorPanel extends JPanel implements ActionListener {

    private final MainApplication application;

    protected JFileChooser fileChooser;

    protected JTextField textField;
    protected JButton selectFileButton;
    protected JButton convertButton;


    private boolean converting = false;


    public FileSelectorPanel(MainApplication application) {
        this.application = application;
        initBoard();
    }

    private void initBoard() {

        fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Select .json file");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".json", "json");
        fileChooser.setFileFilter(filter);

        JLabel label = new JLabel("Path to .json file");
        textField = new JTextField(20);

        selectFileButton = new JButton("Select File(s)");
        selectFileButton.addActionListener(this);

        convertButton = new JButton("Convert File(s)");
        convertButton.addActionListener(this);

        add(label);
        add(textField);
        add(selectFileButton);
        add(convertButton);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (selectFileButton.equals(e.getSource())) {
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue != JFileChooser.APPROVE_OPTION) return;

            for (File file : fileChooser.getSelectedFiles()) {
                if (!file.isFile()) continue;
                if (!application.getFileListPanel().addPath(file.getAbsolutePath())) continue;
                application.log("You added the file: " + file.getName());
            }

        }

        if (convertButton.equals(e.getSource())) {
            if (converting) return;
            String buttonText = convertButton.getText();
            convertButton.setText("Converting..");
            converting = true;
            JsonUtils jsonUtils = application.getJsonUtils();
            DefaultListModel<String> model = (DefaultListModel<String>) application.getFileListPanel().list.getModel();
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
