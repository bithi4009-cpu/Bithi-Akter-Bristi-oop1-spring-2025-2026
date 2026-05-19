package gui;

import entity.Player;
import fileio.SportsClubFileIO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;

public class PlayerGUI extends JFrame {

    private JTextField idField;
    private JTextField nameField;
    private JTextField ageField;
    private JTextField sportsTypeField;
    private JTextField searchField;

    private JTable table;
    private DefaultTableModel tableModel;

    public PlayerGUI() {
        setTitle("Sports Club Management System");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Player Details"));

        formPanel.add(new JLabel("Player ID:"));
        idField = new JTextField(8);
        formPanel.add(idField);

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField(12);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Age:"));
        ageField = new JTextField(4);
        formPanel.add(ageField);

        formPanel.add(new JLabel("Sports Type:"));
        sportsTypeField = new JTextField(10);
        formPanel.add(sportsTypeField);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Panel"));

        searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        searchPanel.add(new JLabel("Search by ID/Name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton viewAllBtn = new JButton("View All");
        JButton clearBtn = new JButton("Clear");

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(viewAllBtn);
        buttonPanel.add(clearBtn);

        String[] columns = { "Player ID", "Name", "Age", "Sports Type" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(22);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Player Records"));

        mainContainer.add(formPanel);
        mainContainer.add(Box.createVerticalStrut(10));
        mainContainer.add(searchPanel);
        mainContainer.add(Box.createVerticalStrut(10));
        mainContainer.add(buttonPanel);
        mainContainer.add(Box.createVerticalStrut(15));
        mainContainer.add(scrollPane);

        add(mainContainer);

        addBtn.addActionListener(e -> addPlayer());
        updateBtn.addActionListener(e -> updatePlayer());
        deleteBtn.addActionListener(e -> deletePlayer());
        searchBtn.addActionListener(e -> searchPlayer());

        viewAllBtn.addActionListener(e -> {
            searchField.setText("");
            viewAll();
        });

        clearBtn.addActionListener(e -> clearFields());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                idField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
                nameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
                ageField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
                sportsTypeField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
            }
        });

        try {
            SportsClubFileIO.createFileIfNotExists();
        } catch (IOException ex) {
            showError("Error creating file: " + ex.getMessage());
        }

        viewAll();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private boolean isValidId(String id) {
        if (id.isEmpty()) {
            showError("Player ID is required!");
            return false;
        }
        if (!id.matches("\\d{8}")) {
            showError("Player ID must be exactly 8 digits.");
            return false;
        }
        return true;
    }

    private boolean isValidAllFields(String id, String name, String age, String sportsType) {
        if (name.isEmpty() || age.isEmpty() || sportsType.isEmpty()) {
            showError("All fields are required!");
            return false;
        }
        if (!isValidId(id))
            return false;

        if (name.contains(",") || age.contains(",") || sportsType.contains(",")) {
            showError("Commas are not allowed in any field!");
            return false;
        }
        try {
            Integer.parseInt(age);
        } catch (NumberFormatException ex) {
            showError("Age must be a number!");
            return false;
        }
        return true;
    }

    private void addPlayer() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String sportsType = sportsTypeField.getText().trim();

        if (!isValidAllFields(id, name, ageStr, sportsType))
            return;

        if (SportsClubFileIO.idExists(id)) {
            showError("Duplicate ID! A player with ID " + id + " already exists.");
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            SportsClubFileIO.addPlayer(new Player(id, name, age, sportsType));
            showInfo("Player added successfully!");
            clearFields();
            viewAll();
        } catch (IOException ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    private void updatePlayer() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String sportsType = sportsTypeField.getText().trim();

        if (!isValidAllFields(id, name, ageStr, sportsType))
            return;

        try {
            int age = Integer.parseInt(ageStr);
            boolean updated = SportsClubFileIO.updatePlayer(new Player(id, name, age, sportsType));

            if (updated) {
                showInfo("Player updated successfully!");
                clearFields();
                viewAll();
            } else {
                showError("Player ID not found!");
            }
        } catch (IOException ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    private void deletePlayer() {
        String id = idField.getText().trim();
        if (!isValidId(id))
            return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete player ID: " + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION)
            return;

        try {
            boolean deleted = SportsClubFileIO.deletePlayer(id);
            if (deleted) {
                showInfo("Player deleted successfully!");
                clearFields();
                viewAll();
            } else {
                showError("Player ID not found!");
            }
        } catch (IOException ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    private void searchPlayer() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            showError("Enter ID or Name to search!");
            return;
        }

        Object[][] results = SportsClubFileIO.searchPlayers(keyword);
        tableModel.setRowCount(0);

        for (int i = 0; i < results.length; i++) {
            tableModel.addRow(results[i]);
        }

        if (results.length == 0)
            showInfo("No matching player found.");
    }

    private void viewAll() {
        Object[][] rows = SportsClubFileIO.getAllPlayers();
        tableModel.setRowCount(0);
        for (int i = 0; i < rows.length; i++) {
            if (rows[i][0] != null)
                tableModel.addRow(rows[i]);
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        ageField.setText("");
        sportsTypeField.setText("");
        searchField.setText("");
        table.clearSelection();
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}