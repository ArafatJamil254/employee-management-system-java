package models;

public class Employee extends User {
    private String department;
    private String designation;
    private double salary;

    public Employee() {
        super();
    }

    public Employee(String employeeId, String name, String department,
                    String designation, String email, String phoneNo, double salary) {
        super(employeeId, name, email, phoneNo);
        this.department = department;
        this.designation = designation;
        this.salary = salary;
    }

    public void setDepartment(String department) { this.department = department; }
    public void setDesignation(String designation) { this.designation = designation; }
    public void setSalary(double salary) { this.salary = salary; }

    public String getDepartment() { return this.department; }
    public String getDesignation() { return this.designation; }
    public double getSalary() { return this.salary; }
    public String getEmployeeId() { return this.userId; }

    public String toStringEmployee() {
        return userId + "\t" + name + "\t" + department + "\t" + designation + "\t"
             + email + "\t" + phoneNo + "\t" + salary;
    }

    public static Employee formEmployee(String line) {
        try {
            String[] data = line.split("\t", -1);
            if (data.length == 7) {
                return new Employee(data[0], data[1], data[2], data[3],
                                    data[4], data[5], Double.parseDouble(data[6]));
            }

            // Original coursework format: unused password and security fields are ignored.
            data = line.split(",", -1);
            if (data.length == 11) {
                return new Employee(data[0], data[1], "General", data[9],
                                    data[4], data[5], Double.parseDouble(data[10]));
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }
}
