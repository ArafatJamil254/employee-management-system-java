import UI.EmployeeManagementUI;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;

public class EmployeeUITest {
    private static void check(boolean condition, String description) {
        if (!condition) throw new AssertionError(description);
    }

    private static Object field(EmployeeManagementUI app, String name) throws Exception {
        Field field = app.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(app);
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("user.home", Files.createTempDirectory("employee-desk-ui").toString());
        final Throwable[] issue = {null};
        SwingUtilities.invokeAndWait(() -> {
            try {
                EmployeeManagementUI app = new EmployeeManagementUI();
                ((JTextField) field(app, "idField")).setText("E100");
                ((JTextField) field(app, "nameField")).setText("Imran Hossain");
                ((JTextField) field(app, "departmentField")).setText("Finance");
                ((JTextField) field(app, "designationField")).setText("Analyst");
                ((JTextField) field(app, "emailField")).setText("imran@example.test");
                ((JTextField) field(app, "salaryField")).setText("42500.50");
                ((JButton) field(app, "addButton")).doClick();
                DefaultTableModel model = (DefaultTableModel) field(app, "tableModel");
                check(model.getRowCount() == 1, "Add should populate the table");
                JTable table = (JTable) field(app, "employeeTable");
                table.setRowSelectionInterval(0, 0);
                check(!((JTextField) field(app, "idField")).isEditable(), "ID should be locked during edit");
                ((JTextField) field(app, "nameField")).setText("Imran H.");
                ((JButton) field(app, "updateButton")).doClick();
                check(model.getRowCount() == 1 && model.getValueAt(0, 1).equals("Imran H."),
                      "Save edit should update the row");
                ((JTextField) field(app, "searchField")).setText("not-a-match");
                check(model.getRowCount() == 0, "Search should filter results");
                ((JTextField) field(app, "searchField")).setText("finance");
                check(model.getRowCount() == 1, "Search by department should find result");
                File storage = new File(new File(System.getProperty("user.home"), ".employee-desk"), "employees.txt");
                check(storage.isFile(), "GUI should save data");
                ((JFrame) field(app, "frame")).dispose();
                System.out.println("PASS: Add, select, edit, live search, saved data, GUI smoke checks");
            } catch (Throwable e) {
                issue[0] = e;
                for (java.awt.Frame frame : java.awt.Frame.getFrames()) frame.dispose();
            }
        });
        if (issue[0] != null) throw new RuntimeException(issue[0]);
    }
}
