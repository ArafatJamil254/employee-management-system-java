package models;

public class User {
    protected String userId;
    protected String name;
    protected String email;
    protected String phoneNo;

    public User() {
    }

    public User(String userId, String name, String email, String phoneNo) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
    }

    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

    public String getUserId() { return this.userId; }
    public String getName() { return this.name; }
    public String getEmail() { return this.email; }
    public String getPhoneNo() { return this.phoneNo; }
}
