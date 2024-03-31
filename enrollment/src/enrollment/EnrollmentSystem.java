package enrollment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.*;

class EnrollmentSystem {
    private final Connection conn;

    public EnrollmentSystem(Connection conn) {
        this.conn = conn;
    }

    public List<Enrollment> getAllEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();
        try {
            String query = "SELECT * FROM Enrollment";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Enrollment enrollment = new Enrollment();
                enrollment.setId(rs.getInt("EnrollNo"));
                enrollment.setStudentNo(rs.getInt("StudentNo"));
                enrollment.setCourseId(rs.getInt("CourseId"));
                enrollment.setSubjectId(rs.getInt("SubjectId"));
                enrollments.add(enrollment);
            }

            stmt.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return enrollments;
    }

    public void displayEnrollments() {
        List<Enrollment> enrollments = getAllEnrollments();
        System.out.println("Enroll No\tStudent No\tCourse ID\tSubject ID");
        for (Enrollment enrollment : enrollments) {
            System.out.println(enrollment.getId() + "\t\t" + enrollment.getStudentNo() + "\t\t" + enrollment.getCourseId() + "\t\t" + enrollment.getSubjectId());
        }
    }

    public void createEnrollment(int studentNo, int courseId, int subjectId) {
        try {
            String query = "INSERT INTO Enrollment (StudentNo, CourseId, SubjectId) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, studentNo);
            stmt.setInt(2, courseId);
            stmt.setInt(3, subjectId);

            int rowsInserted = stmt.executeUpdate();
            System.out.println(rowsInserted + " record(s) inserted");

            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void updateEnrollment(int id, Scanner scanner) {
        try {
            String query = "UPDATE Enrollment SET StudentNo = ?, CourseId = ?, SubjectId = ? WHERE EnrollNo = ?";
            PreparedStatement stmt = conn.prepareStatement(query);

            System.out.print("Enter new student number: ");
            int newStudentNo = scanner.nextInt();
            scanner.nextLine(); 
            System.out.print("Enter new course ID: ");
            int newCourseId = scanner.nextInt();
            scanner.nextLine(); 
            System.out.print("Enter new subject ID: ");
            int newSubjectId = scanner.nextInt();
            scanner.nextLine(); 

            stmt.setInt(1, newStudentNo);
            stmt.setInt(2, newCourseId);
            stmt.setInt(3, newSubjectId);
            stmt.setInt(4, id);

            int rowsUpdated = stmt.executeUpdate();
            System.out.println(rowsUpdated + " record(s) updated");

            stmt.close();
        } catch (SQLException e) {
            System.out.println("An error occurred while updating enrollment: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter valid numbers.");
        }
    }

    public void deleteEnrollment(int id) {
        try {
            String query = "DELETE FROM Enrollment WHERE EnrollNo = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);

            int rowsDeleted = stmt.executeUpdate();
            System.out.println(rowsDeleted + " record(s) deleted");

            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void undoEnrollment(int toUpdate) {
        try {
            String query = "UPDATE Enrollment SET enrollment_status = 'Active' WHERE enrollNo = ? AND enrollment_status = 'Inactive'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, toUpdate);

            int rowsUpdated = stmt.executeUpdate();
            System.out.println(rowsUpdated + " record(s) updated");

            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    public void enrollInSubject(int studentNo, String subjectName) {
        
        int courseId = retrieveCourseId(subjectName);
        int subjectId = retrieveSubjectId(subjectName);

        if (courseId == -1 || subjectId == -1) {
            System.out.println("Subject " + subjectName + " not found.");
            return;
        }

        
        createEnrollment(studentNo, courseId, subjectId);
        System.out.println("Enrollment in subject " + subjectName + " successful!");
    }
    
    public int retrieveCourseId(String subjectName) {
        int courseId = -1; 
        try {
            String query = "SELECT CourseId FROM course WHERE CourseName = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, subjectName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                courseId = rs.getInt("CourseId");
            }
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println("Error retrieving courseId: " + e.getMessage());
        }
        return courseId;
    }

    public int retrieveSubjectId(String subjectName) {
        int subjectId = -1; 
        try {
            String query = "SELECT SubjectId FROM subject WHERE SubjectName = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, subjectName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                subjectId = rs.getInt("SubjectId");
            }
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println("Error retrieving subjectId: " + e.getMessage());
        }
        return subjectId;
    }

}
