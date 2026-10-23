package UI;

import Controllers.EmployeeController;
import models.Employee;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class EmployeeManagementUI {
    private final Color dark = new Color(35, 49, 70);
    private final Color orange = new Color(218, 146, 61);
    private final Color page = new Color(243, 246, 250);
    private final Color border = new Color(218, 225, 233);
    private final Color muted = new Color(108, 121, 140);
    private final Color ink = new Color(38, 53, 73);

    private JFrame frame;
    private JTextField idField, nameField, departmentField, designationField;
    private JTextField emailField, phoneField, salaryField, searchField;
    private JLabel countLabel, shownLabel, noteLabel, editorTitle, salaryTotalLabel;
    private JButton addButton, updateButton, deleteButton;
    private JComboBox<String> departmentFilter;
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private DepartmentChart departmentChart;
    private EmployeeController controller;
    private ArrayList<Employee> allEmployees = new ArrayList<>();
    private String selectedId;
    private boolean changingFilter;
    private final DecimalFormat salaryFormat = new DecimalFormat("#,##0.00");
    private final DecimalFormat tableSalaryFormat = new DecimalFormat("#,##0.##");

    public EmployeeManagementUI() {
        controller = new EmployeeController();
        frame = new JFrame("Employee Desk | Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1220, 790);
        frame.setMinimumSize(new Dimension(990, 640));
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(page);
        frame.setIconImage(makeIcon());
        frame.setLayout(new BorderLayout());

        frame.add(makeHeader(), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(18, 0));
        content.setBackground(page);
        content.setBorder(new EmptyBorder(19, 22, 21, 22));
        content.add(makeEditor(), BorderLayout.WEST);
        content.add(makeDirectory(), BorderLayout.CENTER);
        frame.add(content, BorderLayout.CENTER);

        loadEmployees();
        frame.setVisible(true);
    }

    private JPanel makeHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0)) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, dark, getWidth(), 0, new Color(54, 71, 93)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 13));
                for (int x = getWidth() - 260; x < getWidth() + 100; x += 38) {
                    g2.drawLine(x, 0, x - 70, getHeight());
                }
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 104));
        header.setBorder(new EmptyBorder(19, 25, 17, 28));

        JLabel mark = new JLabel("ED", SwingConstants.CENTER);
        mark.setPreferredSize(new Dimension(52, 52));
        mark.setBackground(orange);
        mark.setOpaque(true);
        mark.setForeground(dark);
        mark.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.add(mark, BorderLayout.WEST);

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 3));
        titles.setOpaque(false);
        JLabel title = new JLabel("Employee Desk");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("EMPLOYEE MANAGEMENT  /  STAFF DIRECTORY");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setForeground(new Color(188, 201, 217));
        titles.add(title);
        titles.add(subtitle);
        header.add(titles, BorderLayout.CENTER);

        countLabel = new JLabel("0 EMPLOYEES");
        countLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        countLabel.setForeground(new Color(239, 200, 137));
        countLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        countLabel.setBorder(new EmptyBorder(0, 12, 0, 0));
        header.add(countLabel, BorderLayout.EAST);
        return header;
    }

    private JPanel makeEditor() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(305, 0));
        card.setBorder(new LineBorder(border));

        JPanel heading = new JPanel(new BorderLayout(0, 5));
        heading.setBackground(Color.WHITE);
        heading.setBorder(new EmptyBorder(20, 21, 15, 21));
        editorTitle = new JLabel("Employee details");
        editorTitle.setFont(new Font("SansSerif", Font.BOLD, 19));
        editorTitle.setForeground(ink);
        heading.add(editorTitle, BorderLayout.NORTH);
        JLabel help = new JLabel("Add a new record or select a row to edit.");
        help.setForeground(muted);
        help.setFont(new Font("SansSerif", Font.PLAIN, 12));
        heading.add(help, BorderLayout.SOUTH);
        card.add(heading, BorderLayout.NORTH);

        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        fields.setBackground(Color.WHITE);
        fields.setBorder(new EmptyBorder(5, 21, 10, 21));

        idField = new JTextField();
        nameField = new JTextField();
        departmentField = new JTextField();
        designationField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        salaryField = new JTextField();
        fields.add(makeField("Employee ID *", idField));
        fields.add(makeField("Full name *", nameField));
        fields.add(makeField("Department *", departmentField));
        fields.add(makeField("Position *", designationField));
        fields.add(makeField("Email", emailField));
        fields.add(makeField("Phone", phoneField));
        fields.add(makeField("Salary *", salaryField));

        JScrollPane fieldScroll = new JScrollPane(fields);
        fieldScroll.setBorder(null);
        fieldScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        fieldScroll.getViewport().setBackground(Color.WHITE);
        fieldScroll.getVerticalScrollBar().setUnitIncrement(15);
        card.add(fieldScroll, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.setBackground(Color.WHITE);
        actions.setBorder(new EmptyBorder(10, 21, 19, 21));

        addButton = makeButton("Add employee", orange, dark, orange);
        addButton.addActionListener(e -> addEmployee());
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 39));
        actions.add(addButton);
        actions.add(Box.createVerticalStrut(8));

        JPanel secondary = new JPanel(new GridLayout(1, 3, 7, 0));
        secondary.setBackground(Color.WHITE);
        updateButton = makeButton("Save edit", Color.WHITE, ink, border);
        updateButton.addActionListener(e -> updateEmployee());
        deleteButton = makeButton("Delete", Color.WHITE, new Color(174, 73, 61), border);
        deleteButton.addActionListener(e -> deleteEmployee());
        JButton clearButton = makeButton("Clear", Color.WHITE, ink, border);
        clearButton.addActionListener(e -> clearFields());
        secondary.add(updateButton);
        secondary.add(deleteButton);
        secondary.add(clearButton);
        secondary.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        actions.add(secondary);
        actions.add(Box.createVerticalStrut(9));

        noteLabel = new JLabel("Choose a row to edit or delete.");
        noteLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        noteLabel.setForeground(muted);
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.add(noteLabel);
        card.add(actions, BorderLayout.SOUTH);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        return card;
    }

    private JPanel makeField(String labelText, JTextField field) {
        JPanel group = new JPanel(new BorderLayout(0, 5));
        group.setBackground(Color.WHITE);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        group.setPreferredSize(new Dimension(250, 60));
        group.setBorder(new EmptyBorder(0, 0, 9, 0));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(ink);
        group.add(label, BorderLayout.NORTH);
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setForeground(ink);
        field.setBackground(new Color(252, 253, 255));
        field.setBorder(new CompoundBorder(new LineBorder(border), new EmptyBorder(7, 9, 7, 9)));
        group.add(field, BorderLayout.CENTER);
        return group;
    }

    private JButton makeButton(String text, Color background, Color foreground, Color line) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setBorder(new LineBorder(line));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(110, 38));
        return button;
    }

    private JPanel makeDirectory() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(border));

        JPanel toolbar = new JPanel(new BorderLayout(0, 16));
        toolbar.setBorder(new EmptyBorder(21, 22, 18, 22));
        toolbar.setBackground(Color.WHITE);
        JPanel titleLine = new JPanel(new BorderLayout());
        titleLine.setBackground(Color.WHITE);
        JLabel heading = new JLabel("Staff directory");
        heading.setForeground(ink);
        heading.setFont(new Font("SansSerif", Font.BOLD, 19));
        titleLine.add(heading, BorderLayout.WEST);
        shownLabel = new JLabel("0 records");
        shownLabel.setForeground(muted);
        shownLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLine.add(shownLabel, BorderLayout.EAST);
        toolbar.add(titleLine, BorderLayout.NORTH);

        JPanel filters = new JPanel(new BorderLayout(10, 0));
        filters.setBackground(Color.WHITE);
        searchField = new JTextField() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(muted);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    g2.drawString("Search by ID, name or department...", 12, getHeight() / 2 + 4);
                    g2.dispose();
                }
            }
        };
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { searchField.repaint(); }
            public void focusLost(FocusEvent e) { searchField.repaint(); }
        });
        searchField.setToolTipText("Find an employee by ID, name, department, position or email");
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setBorder(new CompoundBorder(new LineBorder(border), new EmptyBorder(8, 10, 8, 10)));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });
        filters.add(searchField, BorderLayout.CENTER);

        departmentFilter = new JComboBox<>(new String[]{"All departments"});
        departmentFilter.setFont(new Font("SansSerif", Font.PLAIN, 12));
        departmentFilter.setPreferredSize(new Dimension(160, 37));
        departmentFilter.setBackground(Color.WHITE);
        departmentFilter.addActionListener(e -> {
            if (!changingFilter) applyFilter();
        });
        filters.add(departmentFilter, BorderLayout.EAST);
        toolbar.add(filters, BorderLayout.SOUTH);
        card.add(toolbar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"EMPLOYEE ID", "NAME", "DEPARTMENT", "POSITION", "SALARY"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
            public Class<?> getColumnClass(int column) { return column == 4 ? Double.class : String.class; }
        };
        employeeTable = new JTable(tableModel);
        employeeTable.setRowHeight(42);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setAutoCreateRowSorter(true);
        employeeTable.setShowGrid(false);
        employeeTable.setIntercellSpacing(new Dimension(0, 0));
        employeeTable.setFillsViewportHeight(true);
        employeeTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        employeeTable.setSelectionBackground(new Color(250, 235, 211));
        employeeTable.setSelectionForeground(ink);
        employeeTable.setBackground(Color.WHITE);
        employeeTable.getTableHeader().setReorderingAllowed(false);
        employeeTable.getTableHeader().setPreferredSize(new Dimension(0, 41));
        employeeTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, false, false, row, column);
                label.setOpaque(true);
                label.setBackground(new Color(234, 239, 246));
                label.setForeground(new Color(82, 100, 122));
                label.setFont(new Font("SansSerif", Font.BOLD, 10));
                label.setBorder(new EmptyBorder(0, 12, 0, 9));
                label.setHorizontalAlignment(column == 4 ? SwingConstants.RIGHT : SwingConstants.LEFT);
                return label;
            }
        });
        employeeTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                label.setOpaque(true);
                label.setBackground(isSelected ? new Color(250, 235, 211) : (row % 2 == 0 ? Color.WHITE : new Color(249, 251, 254)));
                label.setForeground(column == 0 ? new Color(84, 106, 135) : ink);
                label.setFont(new Font("SansSerif", column == 1 ? Font.BOLD : Font.PLAIN, 12));
                label.setBorder(new EmptyBorder(0, 12, 0, 9));
                label.setHorizontalAlignment(column == 4 ? SwingConstants.RIGHT : SwingConstants.LEFT);
                if (column == 4 && value instanceof Number) {
                    label.setText(tableSalaryFormat.format(((Number) value).doubleValue()));
                }
                return label;
            }
        });
        employeeTable.getColumnModel().getColumn(0).setPreferredWidth(105);
        employeeTable.getColumnModel().getColumn(1).setPreferredWidth(165);
        employeeTable.getColumnModel().getColumn(2).setPreferredWidth(115);
        employeeTable.getColumnModel().getColumn(3).setPreferredWidth(145);
        employeeTable.getColumnModel().getColumn(4).setPreferredWidth(112);
        employeeTable.getColumnModel().getColumn(4).setMinWidth(107);
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectEmployee();
        });

        JScrollPane tableScroll = new JScrollPane(employeeTable);
        tableScroll.setBorder(new EmptyBorder(0, 22, 0, 22));
        tableScroll.getViewport().setBackground(Color.WHITE);
        card.add(tableScroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(0, 12));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(13, 22, 19, 22));
        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setBackground(Color.WHITE);
        JLabel chartTitle = new JLabel("DEPARTMENT MIX");
        chartTitle.setForeground(muted);
        chartTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        top.add(chartTitle, BorderLayout.WEST);
        JPanel footerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footerActions.setBackground(Color.WHITE);
        salaryTotalLabel = new JLabel("Total salary: 0.00");
        salaryTotalLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        salaryTotalLabel.setForeground(ink);
        footerActions.add(salaryTotalLabel);
        JButton importButton = makeButton("Import old file", Color.WHITE, ink, border);
        importButton.setPreferredSize(new Dimension(128, 29));
        importButton.addActionListener(e -> importFile());
        footerActions.add(importButton);
        top.add(footerActions, BorderLayout.EAST);
        footer.add(top, BorderLayout.NORTH);
        departmentChart = new DepartmentChart();
        departmentChart.setPreferredSize(new Dimension(0, 70));
        footer.add(departmentChart, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    private void loadEmployees() {
        try {
            allEmployees = controller.getAllEmployee();
            refreshDepartments();
            applyFilter();
            countLabel.setText(allEmployees.size() + (allEmployees.size() == 1 ? " EMPLOYEE" : " EMPLOYEES"));
            departmentChart.setEmployees(allEmployees);
            double totalSalary = 0;
            for (Employee emp : allEmployees) totalSalary += emp.getSalary();
            salaryTotalLabel.setText("Total salary: " + salaryFormat.format(totalSalary));
        } catch (IOException | IllegalArgumentException e) {
            showError("Couldn't load employee data: " + e.getMessage());
        }
    }

    private void refreshDepartments() {
        String selected = (String) departmentFilter.getSelectedItem();
        ArrayList<String> departments = new ArrayList<>();
        for (Employee emp : allEmployees) {
            boolean exists = false;
            for (String dept : departments) {
                if (dept.equalsIgnoreCase(emp.getDepartment())) exists = true;
            }
            if (!exists) departments.add(emp.getDepartment());
        }
        Collections.sort(departments, String.CASE_INSENSITIVE_ORDER);
        changingFilter = true;
        departmentFilter.removeAllItems();
        departmentFilter.addItem("All departments");
        for (String dept : departments) departmentFilter.addItem(dept);
        if (selected != null) departmentFilter.setSelectedItem(selected);
        if (departmentFilter.getSelectedIndex() < 0) departmentFilter.setSelectedIndex(0);
        changingFilter = false;
    }

    private void applyFilter() {
        if (tableModel == null || searchField == null || departmentFilter == null) return;
        String search = searchField.getText().trim().toLowerCase();
        String dept = (String) departmentFilter.getSelectedItem();
        tableModel.setRowCount(0);
        int shown = 0;
        for (Employee emp : allEmployees) {
            String searchable = (emp.getEmployeeId() + " " + emp.getName() + " " +
                                 emp.getDepartment() + " " + emp.getDesignation() + " " +
                                 emp.getEmail() + " " + emp.getPhoneNo()).toLowerCase();
            boolean matches = searchable.contains(search);
            boolean byDepartment = dept == null || dept.equals("All departments") ||
                                   emp.getDepartment().equalsIgnoreCase(dept);
            if (matches && byDepartment) {
                tableModel.addRow(new Object[]{emp.getEmployeeId(), emp.getName(),
                    emp.getDepartment(), emp.getDesignation(), emp.getSalary()});
                shown++;
            }
        }
        shownLabel.setText("Showing " + shown + " of " + allEmployees.size());
    }

    private Employee getFormEmployee() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String department = departmentField.getText().trim();
        String designation = designationField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        if (id.isEmpty() || name.isEmpty() || department.isEmpty() || designation.isEmpty() ||
            salaryField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Please complete all fields marked with *.");
        }
        if (!email.isEmpty() && (!email.contains("@") || email.contains(" "))) {
            throw new IllegalArgumentException("Please enter a valid email address or leave it blank.");
        }
        double salary;
        try {
            salary = Double.parseDouble(salaryField.getText().trim().replace(",", ""));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Salary must be a valid number.");
        }
        return new Employee(id, name, department, designation, email, phone, salary);
    }

    private void addEmployee() {
        try {
            if (selectedId != null) {
                showError("Click Clear to add a new employee instead of editing a selected record.");
                return;
            }
            if (!controller.insertEmployee(getFormEmployee())) {
                showError("That employee ID already exists. Use a different ID.");
                return;
            }
            loadEmployees();
            clearFields();
            setNote("Employee added and saved.");
        } catch (IOException | IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void updateEmployee() {
        if (selectedId == null) return;
        try {
            Employee emp = getFormEmployee();
            if (!selectedId.equalsIgnoreCase(emp.getEmployeeId())) {
                showError("Employee ID cannot be changed while editing.");
                return;
            }
            if (!controller.updateEmployee(emp)) {
                showError("This employee no longer exists.");
                return;
            }
            loadEmployees();
            clearFields();
            setNote("Employee changes saved.");
        } catch (IOException | IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void deleteEmployee() {
        if (selectedId == null) return;
        int result = JOptionPane.showConfirmDialog(frame,
                "Delete employee " + selectedId + "? This can't be undone.",
                "Delete employee", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result != JOptionPane.YES_OPTION) return;
        try {
            if (controller.deleteEmployee(selectedId)) {
                loadEmployees();
                clearFields();
                setNote("Employee deleted.");
            }
        } catch (IOException e) {
            showError(e.getMessage());
        }
    }

    private void selectEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow < 0) {
            selectedId = null;
            updateButton.setEnabled(false);
            deleteButton.setEnabled(false);
            idField.setEditable(true);
            return;
        }
        int row = employeeTable.convertRowIndexToModel(selectedRow);
        String id = (String) tableModel.getValueAt(row, 0);
        for (Employee emp : allEmployees) {
            if (emp.getEmployeeId().equalsIgnoreCase(id)) {
                selectedId = emp.getEmployeeId();
                idField.setText(emp.getEmployeeId());
                nameField.setText(emp.getName());
                departmentField.setText(emp.getDepartment());
                designationField.setText(emp.getDesignation());
                emailField.setText(emp.getEmail());
                phoneField.setText(emp.getPhoneNo());
                salaryField.setText(salaryFormat.format(emp.getSalary()));
                idField.setEditable(false);
                editorTitle.setText("Edit employee");
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
                setNote("Editing " + emp.getEmployeeId() + ". Click Clear for a new record.");
                break;
            }
        }
    }

    private void clearFields() {
        employeeTable.clearSelection();
        selectedId = null;
        idField.setEditable(true);
        idField.setText("");
        nameField.setText("");
        departmentField.setText("");
        designationField.setText("");
        emailField.setText("");
        phoneField.setText("");
        salaryField.setText("");
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        editorTitle.setText("Employee details");
        setNote("Choose a row to edit or delete.");
    }

    private void importFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose an employee text file");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text files (*.txt)", "txt"));
        if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();
        try {
            int added = controller.importEmployees(file);
            loadEmployees();
            clearFields();
            setNote(added + " new employee(s) imported.");
            JOptionPane.showMessageDialog(frame, added + " new employee(s) imported.\nExisting IDs were skipped.",
                    "Import completed", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | IllegalArgumentException e) {
            showError("Import failed: " + e.getMessage());
        }
    }

    private void setNote(String message) {
        noteLabel.setText(message);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Employee Desk", JOptionPane.ERROR_MESSAGE);
    }

    private Image makeIcon() {
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(dark);
        g.fillRoundRect(0, 0, 64, 64, 16, 16);
        g.setColor(orange);
        g.fillRoundRect(8, 8, 48, 48, 11, 11);
        g.setColor(dark);
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        g.drawString("ED", 12, 42);
        g.dispose();
        return icon;
    }

    private class DepartmentChart extends JPanel {
        private ArrayList<Employee> employees = new ArrayList<>();
        private final Color[] chartColors = {
            new Color(216, 146, 68), new Color(78, 133, 157),
            new Color(119, 126, 157), new Color(172, 185, 197)
        };

        DepartmentChart() {
            setOpaque(true);
            setBackground(Color.WHITE);
        }

        void setEmployees(ArrayList<Employee> employees) {
            this.employees = new ArrayList<>(employees);
            repaint();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth() - 2;
            if (width <= 0) { g2.dispose(); return; }
            if (employees.isEmpty()) {
                g2.setColor(muted);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.drawString("Your department breakdown will appear here as you add employees.", 2, 33);
                g2.dispose();
                return;
            }

            Map<String, Integer> counts = new HashMap<>();
            for (Employee emp : employees) {
                String dept = emp.getDepartment();
                counts.put(dept, counts.getOrDefault(dept, 0) + 1);
            }
            ArrayList<Map.Entry<String, Integer>> sorted = new ArrayList<>(counts.entrySet());
            Collections.sort(sorted, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
                    return b.getValue().compareTo(a.getValue());
                }
            });
            int[] values = new int[4];
            String[] names = new String[4];
            for (int i = 0; i < sorted.size(); i++) {
                int group = Math.min(i, 3);
                values[group] += sorted.get(i).getValue();
                if (group < 3) names[group] = sorted.get(i).getKey();
                else names[3] = "Other";
            }
            int length = width;
            int x = 1;
            for (int i = 0; i < 4; i++) {
                if (values[i] == 0) continue;
                int span = i == 3 ? length - x + 1 : (int) Math.round(width * values[i] / (double) employees.size());
                g2.setColor(chartColors[i]);
                g2.fillRect(x, 4, Math.max(1, span), 12);
                x += span;
            }

            int labelX = 0;
            int labelY = 42;
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            for (int i = 0; i < 4; i++) {
                if (values[i] == 0) continue;
                String name = names[i] + " (" + values[i] + ")";
                int textWidth = g2.getFontMetrics().stringWidth(name);
                if (labelX + textWidth + 19 > width) break;
                g2.setColor(chartColors[i]);
                g2.fillRoundRect(labelX, labelY - 9, 9, 9, 3, 3);
                g2.setColor(muted);
                g2.drawString(name, labelX + 15, labelY);
                labelX += textWidth + 36;
            }
            g2.dispose();
        }
    }
}
