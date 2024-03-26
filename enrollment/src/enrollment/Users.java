package enrollment;

public class Users {
    private int id;
    private String username;
    private String password;
    private Role role;
    private Status status;
    
    public enum Role {
        Admin,
        Professor,
        Student
    }
    
    public enum Status {
        Active,
        Inactive
    }
    
    
    // Getters
    public int getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public Role getRole() {
        return role;
    }
    
    public Status getStatus() {
        return status;
    }
    
    // Setters
    public void setId(int id) {
        this.id = id;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
}
