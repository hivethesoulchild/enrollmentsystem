package enrollment;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class StudentSystem {
    private Connection conn;
    private List<Student> students = new ArrayList<>();

    public StudentSystem(Connection conn) {
        this.conn = conn;
    }

    public void loadStudent() {
        try {
            String query = "SELECT * FROM Student where StudentStatus = 'Active'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Student st = new Student();
                st.setId(rs.getInt("StudentNo"));
                st.setName(rs.getString("StudentName"));
                st.setAddress(rs.getString("StudentAddress"));
                st.setNumber(rs.getInt("StudentNumber"));
                st.setStatus(rs.getString("StudentStatus"));
                students.add(st);
            }
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void searchStudent(String search) {
        try {
            String query = "SELECT * FROM Student where StudentNo Like ? or StudentName Like ? or StudentAddress Like ? or StudentNumber Like ? and StudentStatus = 'Active' ";
            PreparedStatement stmt = conn.prepareStatement(query);
            for (int i = 1; i <= 4; i++) {
                stmt.setString(i, "%" + search + "%");
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Student st = new Student();
                st.setId(rs.getInt("StudentNo"));
                st.setName(rs.getString("StudentName"));
                st.setAddress(rs.getString("StudentAddress"));
                st.setNumber(rs.getInt("StudentNumber"));
                st.setStatus(rs.getString("StudentStatus"));
                students.add(st);
            }
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void displayStudent() {
        loadStudent();
        System.out.println("Name\t\tAddress\t\tNumber");
        students.forEach(n -> System.out.println(n.getName() + "\t\t" + n.getAddress() + "\t\t" + n.getNumber()));
    }

    public void displayStudent(String search) {
        searchStudent(search);
        System.out.println("Name\t\tAddress\t\tNumber");
        students.forEach(n -> System.out.println(n.getName() + "\t\t" + n.getAddress() + "\t\t" + n.getNumber()));
    }

    public void addStudent(String name, String address, long number) {
        try {
            String query = "Insert INTO Student ( StudentName, StudentAddress, StudentNumber ) values ( ?, ?, ? )";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setLong(3, number);

            int i = stmt.executeUpdate();
            System.out.println(i + " records inserted");

            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void updateStudent(int toUpdate, String fieldToUpdate, String value) {
        try {
            String query = "UPDATE Student SET " + fieldToUpdate + " = ? WHERE StudentNo = ? AND StudentStatus = 'Active'";
            PreparedStatement stmt = conn.prepareStatement(query);
            
            // Set the appropriate value based on the fieldToUpdate parameter
            switch (fieldToUpdate) {
                case "StudentName":
                case "StudentAddress":
                    stmt.setString(1, value);
                    break;
                case "StudentNumber":
                    stmt.setLong(1, Long.parseLong(value));
                    break;
                default:
                    System.out.println("Invalid field to update.");
                    return;
            }
            
            stmt.setInt(2, toUpdate);

            int i = stmt.executeUpdate();
            System.out.println(i + " record(s) updated");

            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    public boolean studentExists(int studentNo) {
        for (Student student : students) {
            if (student.getId() == studentNo) {
                return true; // Student exists
            }
        }
        return false; // Student does not exist
    }

    public void deleteStudent(int toUpdate) {
        try {
            String query = "UPDATE Student SET StudentStatus = 'Inactive' WHERE StudentNo = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, toUpdate);

            int i = stmt.executeUpdate();
            System.out.println(i + " records deleted");

            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void undoStudent(int toUpdate) {
        try {
            String query = "UPDATE Student SET StudentStatus = 'Active' WHERE StudentNo = ? and StudentStatus = 'Inactive'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, toUpdate);

            int i = stmt.executeUpdate();
            System.out.println(i + " records updated");

            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
