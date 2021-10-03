package io.github.berehum.damagevalueconverter.panels;

import io.github.berehum.damagevalueconverter.JsonUtils;
import io.github.berehum.damagevalueconverter.MainApplication;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class FileSelectorPanel extends JPanel implements ActionListener {

    private final MainApplication application;

    protected JTextField textField;
    protected JFileChooser fileChooser;

    protected JButton selectFileButton;
    protected JButton convertButton;


    public FileSelectorPanel(MainApplication application) {
        this.application = application;
        initBoard();
    }

    private void initBoard() {

        fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setDialogTitle("Select .json file");
        fileChooser.setAcceptAllFileFilterUsed(true);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".json", "json");
        fileChooser.addChoosableFileFilter(filter);

        JLabel label = new JLabel("Path to .json file");
        textField = new JTextField(20);

        selectFileButton = new JButton("Select File");
        selectFileButton.addActionListener(this);

        convertButton = new JButton("Convert Files");
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
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                if (fileChooser.getSelectedFile().isFile()) {
                    application.log("You selected the File: " + fileChooser.getSelectedFile().getName());
                    textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        }

        if (convertButton.equals(e.getSource())) {
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
        }
    }


}
