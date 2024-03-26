package enrollment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class CourseSystem {
    private final Connection conn;

    public CourseSystem(Connection conn) {
        this.conn = conn;
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        try {
            String query = "SELECT * FROM Course WHERE CourseStatus = 'Active'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("CourseId"));
                course.setName(rs.getString("CourseName"));
                course.setStatus(rs.getString("CourseStatus"));
                courses.add(course);
            }

            stmt.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return courses;
    }

    public void displayCourses() {
        List<Course> courses = getAllCourses();
        System.out.println("Name");
        for (Course course : courses) {
            System.out.println(course.getName());
        }
    }

    public void createCourse(String name) {
        try {
            String query = "INSERT INTO Course (CourseName) VALUES (?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);

            int rowsInserted = stmt.executeUpdate();
            System.out.println(rowsInserted + " record(s) inserted");

            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void updateCourse(int id, Scanner scanner) {
        try {
            String query = "UPDATE Course SET CourseName = ? WHERE CourseId = ? AND CourseStatus = 'Active'";
            PreparedStatement stmt = conn.prepareStatement(query);

            System.out.print("Enter new course name: ");
            String newName = scanner.nextLine();

            stmt.setString(1, newName);
            stmt.setInt(2, id);

            int rowsUpdated = stmt.executeUpdate();
            System.out.println(rowsUpdated + " record(s) updated");

            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void deleteCourse(int id) {
        try {
            String query = "UPDATE Course SET CourseStatus = 'Inactive' WHERE CourseId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);

            int rowsDeleted = stmt.executeUpdate();
            System.out.println(rowsDeleted + " record(s) deleted");

            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void undoDeleteCourse(int id) {
        try {
            String query = "UPDATE Course SET CourseStatus = 'Active' WHERE CourseId = ? AND CourseStatus = 'Inactive'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);

            int rowsUpdated = stmt.executeUpdate();
            System.out.println(rowsUpdated + " record(s) restored");

            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
