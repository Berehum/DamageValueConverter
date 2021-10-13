package io.github.berehum.damagevalueconverter.panels;

import io.github.berehum.damagevalueconverter.MainApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class FileListPanel extends JPanel implements ActionListener {

    private final MainApplication application;

    private JPopupMenu popup;

    protected JList<String> list;
    protected JButton addButton;
    protected JButton removeButton;


    public FileListPanel(MainApplication application) {
        this.application = application;
        initBoard();
    }

    private void initBoard() {

        popup = new JPopupMenu("edit");
        JMenuItem copyToClipboard = new JMenuItem("Copy to clipboard");
        copyToClipboard.addActionListener(e -> {
            StringSelection stringSelection = new StringSelection(list.getSelectedValue());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        });
        JMenuItem copy = new JMenuItem("Copy");
        copy.addActionListener(e -> application.getFileSelectorPanel().textField.setText(list.getSelectedValue()));
        popup.add(copy);
        popup.add(copyToClipboard);

        list = new JList<>(new DefaultListModel<>());
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON3) return;
                popup.show(list, e.getX(), e.getY());
            }
        });

        addButton = new JButton("Add");
        addButton.addActionListener(this);

        removeButton = new JButton("Remove");
        removeButton.addActionListener(this);

        add(new JScrollPane(list));
        add(addButton);
        add(removeButton);
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
            String path = application.getFileSelectorPanel().textField.getText();
            if (!addPath(path)) return;
            application.getFileSelectorPanel().textField.setText(null);
        }
    }
}
