package io.github.berehum.damagevalueconverter.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import io.github.berehum.damagevalueconverter.utils.JsonUtils;
import io.github.berehum.damagevalueconverter.models.Logger;

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

public class MainApplication extends JFrame implements ActionListener, Logger {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final JsonUtils jsonUtils;

    private JPanel mainPanel;
    private JTextArea console;
    private JList<String> list;
    private JButton addButton;
    private JButton removeButton;
    private JTextField pathField;
    private JButton convertButton;
    private JButton selectFileButton;

    private JPopupMenu popup;
    private JFileChooser fileChooser;

    private JMenuBar menuBar;

    private boolean converting = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApplication mainApplication = new MainApplication();
            mainApplication.setVisible(true);
        });

    }

    //@todo fix growing console

    public MainApplication() {
        jsonUtils = new JsonUtils(this);

        $$$setupUI$$$();
        setTitle("Damage Value Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 550));
        setLocationRelativeTo(null);
        init();
        pack();
        log("Started program");
    }

    public void init() {
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
                listModel.removeElementAt(index - removedValues);
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

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 6, new Insets(10, 10, 10, 10), -1, -1));
        mainPanel.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        addButton = new JButton();
        addButton.setSelected(false);
        addButton.setText("Add");
        panel1.add(addButton, new GridConstraints(0, 3, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectFileButton = new JButton();
        selectFileButton.setText("Select File(s)");
        panel1.add(selectFileButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pathField = new JTextField();
        pathField.setEditable(true);
        pathField.setEnabled(true);
        pathField.setHorizontalAlignment(10);
        panel1.add(pathField, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(500, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Path to .json file");
        label1.setVerticalAlignment(3);
        panel1.add(label1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        mainPanel.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setBackground(new Color(-14605013));
        panel2.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        console = new JTextArea();
        console.setColumns(0);
        console.setEditable(false);
        console.setLineWrap(true);
        scrollPane1.setViewportView(console);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 2, new Insets(10, 10, 10, 10), -1, -1));
        mainPanel.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel3.add(scrollPane2, new GridConstraints(0, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        list.setBackground(new Color(-985857));
        list.setLayoutOrientation(0);
        list.setSelectionMode(2);
        list.putClientProperty("List.isFileList", Boolean.FALSE);
        scrollPane2.setViewportView(list);
        convertButton = new JButton();
        convertButton.setText("Convert");
        panel3.add(convertButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeButton = new JButton();
        removeButton.setText("Remove");
        removeButton.setMnemonic('R');
        removeButton.setDisplayedMnemonicIndex(0);
        panel3.add(removeButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        label1.setLabelFor(pathField);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
