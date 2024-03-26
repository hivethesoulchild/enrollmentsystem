package enrollment;


import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class UsersSystem {
    private Connection conn;
    private List<Users> users = new ArrayList<>();

    public UsersSystem(Connection conn) {
        this.conn = conn;
    }

    public void loadUsers() {
        try {
            String query = "SELECT * FROM users WHERE status = 'Active'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Users user = new Users();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                // Assuming roles are stored as strings in the database
                Users.Role role = Users.Role.valueOf(rs.getString("role"));
                user.setRole(role);
                // Assuming status is stored as string in the database
                Users.Status status = Users.Status.valueOf(rs.getString("status"));
                user.setStatus(status);
                users.add(user);
            }
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void displayUsers() {
        loadUsers();
        System.out.println("ID\tUsername\tRole\tStatus");
        users.forEach(u -> System.out.println(u.getId() + "\t" + u.getUsername() + "\t" + u.getRole() + "\t" + u.getStatus()));
    }

    public void addUser(String username, String password, Users.Role role) {
        try {
            String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role.toString());
            stmt.executeUpdate();
            System.out.println("User added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding user: " + e.getMessage());
        }
    }


    public void updateUser(int userId, String fieldToUpdate, String value) {
        try {
            String query = "UPDATE users SET " + fieldToUpdate + " = ? WHERE id = ? AND status = 'Active'";
            PreparedStatement stmt = conn.prepareStatement(query);
            
            // Set the appropriate value based on the fieldToUpdate parameter
            stmt.setString(1, value);
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User updated successfully.");
            } else {
                System.out.println("Failed to update user.");
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void deleteUser(int userId) {
        try {
            String query = "UPDATE users SET status = 'Inactive' WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User deleted successfully.");
            } else {
                System.out.println("Failed to delete user.");
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void undoUser(int userId) {
        try {
            String query = "UPDATE users SET status = 'Active' WHERE id = ? AND status = 'Inactive'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User status updated successfully.");
            } else {
                System.out.println("Failed to update user status.");
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}

