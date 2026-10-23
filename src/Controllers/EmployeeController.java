package Controllers;

import models.Employee;
import java.io.*;
import java.util.ArrayList;

public class EmployeeController {
    private final File file;
    private final FileIO fio;

    public EmployeeController() {
        this(new File(new File(System.getProperty("user.home"), ".employee-desk"), "employees.txt"));
    }

    public EmployeeController(File file) {
        this.file = file;
        this.fio = new FileIO();
    }

    public File getDataFile() {
        return file;
    }

    public boolean insertEmployee(Employee emp) throws IOException {
        validateEmployee(emp);
        ArrayList<Employee> employees = getAllEmployee();
        if (findEmployee(employees, emp.getEmployeeId()) != null) return false;
        employees.add(emp);
        write(employees);
        return true;
    }

    public boolean updateEmployee(Employee emp) throws IOException {
        validateEmployee(emp);
        ArrayList<Employee> employees = getAllEmployee();
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getEmployeeId().equalsIgnoreCase(emp.getEmployeeId())) {
                employees.set(i, emp);
                write(employees);
                return true;
            }
        }
        return false;
    }

    public boolean deleteEmployee(String employeeId) throws IOException {
        ArrayList<Employee> employees = getAllEmployee();
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getEmployeeId().equalsIgnoreCase(employeeId)) {
                employees.remove(i);
                write(employees);
                return true;
            }
        }
        return false;
    }

    public Employee searchEmployee(String employeeId) throws IOException {
        return findEmployee(getAllEmployee(), employeeId);
    }

    private Employee findEmployee(ArrayList<Employee> employees, String employeeId) {
        if (employeeId == null) return null;
        for (Employee emp : employees) {
            if (emp.getEmployeeId().equalsIgnoreCase(employeeId.trim())) return emp;
        }
        return null;
    }

    public ArrayList<Employee> getAllEmployee() throws IOException {
        ArrayList<Employee> employees = new ArrayList<>();
        String[] data = fio.readFile(file.getAbsolutePath());
        for (int i = 0; i < data.length; i++) {
            Employee emp = Employee.formEmployee(data[i]);
            if (emp == null) {
                throw new IOException("Invalid employee record at line " + (i + 1) + ".");
            }
            validateEmployee(emp);
            if (findEmployee(employees, emp.getEmployeeId()) != null) {
                throw new IOException("Duplicate employee ID in saved data: " + emp.getEmployeeId());
            }
            employees.add(emp);
        }
        return employees;
    }

    public int importEmployees(File selectedFile) throws IOException {
        if (selectedFile.getCanonicalFile().equals(file.getCanonicalFile())) {
            return 0;
        }
        ArrayList<Employee> employees = getAllEmployee();
        String[] data = fio.readFile(selectedFile.getAbsolutePath());
        if (!selectedFile.isFile()) throw new FileNotFoundException("File not found.");
        int added = 0;
        for (int i = 0; i < data.length; i++) {
            Employee emp = Employee.formEmployee(data[i]);
            if (emp == null) throw new IOException("Invalid record in imported file at line " + (i + 1));
            validateEmployee(emp);
            if (findEmployee(employees, emp.getEmployeeId()) == null) {
                employees.add(emp);
                added++;
            }
        }
        if (added > 0) write(employees);
        return added;
    }

    private void validateEmployee(Employee emp) {
        if (emp == null || blank(emp.getEmployeeId()) || blank(emp.getName()) ||
                blank(emp.getDepartment()) || blank(emp.getDesignation())) {
            throw new IllegalArgumentException("ID, name, department and position are required.");
        }
        if (!Double.isFinite(emp.getSalary()) || emp.getSalary() < 0) {
            throw new IllegalArgumentException("Salary must be a valid number, zero or greater.");
        }
        String[] values = {emp.getEmployeeId(), emp.getName(), emp.getDepartment(),
                           emp.getDesignation(), emp.getEmail(), emp.getPhoneNo()};
        for (String value : values) {
            if (value != null && (value.contains("\t") || value.contains("\n") || value.contains("\r"))) {
                throw new IllegalArgumentException("Fields cannot contain tabs or line breaks.");
            }
        }
    }

    private boolean blank(String text) {
        return text == null || text.trim().isEmpty();
    }

    public void write(ArrayList<Employee> employees) throws IOException {
        ArrayList<String> data = new ArrayList<>();
        for (Employee emp : employees) {
            validateEmployee(emp);
            data.add(emp.toStringEmployee());
        }
        fio.writeFile(file.getAbsolutePath(), data.toArray(new String[0]));
    }
}
