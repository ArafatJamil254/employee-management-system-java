import Controllers.EmployeeController;
import models.Employee;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class EmployeeControllerTest {
    private static int checks = 0;

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) throws Exception {
        Path folder = Files.createTempDirectory("employee-desk-tests");
        File data = folder.resolve("new-folder/employees.txt").toFile();
        EmployeeController controller = new EmployeeController(data);

        check(controller.getAllEmployee().isEmpty(), "New data file should be empty");
        Employee first = new Employee("E001", "Samira Karim", "Design", "Designer",
                                      "samira@example.test", "01234567890", 60000);
        check(controller.insertEmployee(first), "Add should succeed");
        check(!controller.insertEmployee(new Employee("e001", "Duplicate", "Sales", "Assistant",
                                                      "", "", 20000)), "Duplicate ID should be rejected");
        check(data.exists(), "Data folder should be created automatically");
        check(controller.searchEmployee("e001") != null, "ID search should ignore letter case");
        check(new EmployeeController(data).getAllEmployee().size() == 1, "Data should survive restarting the controller");

        Employee edited = new Employee("E001", "Samira K.", "Operations", "Coordinator",
                                       "samira@example.test", "01234567890", 68000.50);
        check(controller.updateEmployee(edited), "Edit should succeed");
        check(controller.searchEmployee("E001").getSalary() == 68000.50, "Edited salary should persist");
        check(!controller.updateEmployee(new Employee("E404", "Nobody", "HR", "Recruiter", "", "", 1)),
              "Editing an unknown ID should fail");

        boolean invalidRejected = false;
        try {
            controller.insertEmployee(new Employee("E002", "", "HR", "Recruiter", "", "", 30000));
        } catch (IllegalArgumentException expected) {
            invalidRejected = true;
        }
        check(invalidRejected, "Blank required fields should be rejected");
        invalidRejected = false;
        try {
            controller.insertEmployee(new Employee("E002", "Valid", "HR", "Recruiter", "", "", Double.NaN));
        } catch (IllegalArgumentException expected) {
            invalidRejected = true;
        }
        check(invalidRejected, "NaN salary should be rejected");

        Path old = folder.resolve("original-records.txt");
        Files.write(old, ("U001,John Doe,Male,30,john@example.test,01234567890,Address,secret-question,secret-password,Manager,50000.0\n")
                .getBytes(StandardCharsets.UTF_8));
        check(controller.importEmployees(old.toFile()) == 1, "Legacy record should import");
        check(controller.importEmployees(old.toFile()) == 0, "Reimport should skip duplicates");
        Employee oldEmployee = controller.searchEmployee("U001");
        check(oldEmployee.getDepartment().equals("General"), "Legacy department should default to General");
        String saved = new String(Files.readAllBytes(data.toPath()), StandardCharsets.UTF_8);
        check(!saved.contains("secret-password") && !saved.contains("secret-question"),
              "Legacy passwords and security answers must not be saved");

        Path invalid = folder.resolve("broken.txt");
        Files.write(invalid, "invalid line\n".getBytes(StandardCharsets.UTF_8));
        boolean importRejected = false;
        try {
            controller.importEmployees(invalid.toFile());
        } catch (IOException expected) {
            importRejected = true;
        }
        check(importRejected, "Invalid import should fail with a clear error");
        check(controller.getAllEmployee().size() == 2, "Invalid import must not alter saved data");
        check(!controller.deleteEmployee("E999"), "Unknown deletion should do nothing");
        check(controller.deleteEmployee("E001"), "Delete should succeed");
        check(controller.getAllEmployee().size() == 1, "Deletion must persist");

        Path corrupt = folder.resolve("corrupt.txt");
        Files.write(corrupt, "broken data\n".getBytes(StandardCharsets.UTF_8));
        EmployeeController badController = new EmployeeController(corrupt.toFile());
        boolean badRead = false;
        try {
            badController.insertEmployee(new Employee("E123", "Name", "IT", "Tech", "", "", 1));
        } catch (IOException expected) {
            badRead = true;
        }
        check(badRead, "Corrupt data must not be overwritten by inserting a record");
        check(new String(Files.readAllBytes(corrupt), StandardCharsets.UTF_8).equals("broken data\n"),
              "Corrupt original file should be untouched");
        System.out.println("PASS: " + checks + " controller and file-storage checks");
    }
}
