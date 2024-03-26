package enrollment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class SubjectSystem {
    private final Connection conn;

    public SubjectSystem(Connection conn) {
        this.conn = conn;
    }

    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        try {
            String query = "SELECT * FROM Subject WHERE SubjectStatus = 'Active'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Subject subject = new Subject();
                subject.setId(rs.getInt("SubjectId"));
                subject.setName(rs.getString("SubjectName"));
                subject.setProfessorId(rs.getInt("ProfessorId"));
                subject.setStatus(rs.getString("SubjectStatus"));
                subjects.add(subject);
            }

            stmt.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return subjects;
    }

    public void displaySubjects() {
        List<Subject> subjects = getAllSubjects();
        System.out.println("Name\t\tProfessor ID");
        for (Subject subject : subjects) {
            System.out.println(subject.getName() + "\t\t" + subject.getProfessorId());
        }
    }

    public void createSubject(String name, int professorId) {
        try {
            String query = "INSERT INTO Subject (SubjectName, ProfessorId) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setInt(2, professorId);

            int rowsInserted = stmt.executeUpdate();
            System.out.println(rowsInserted + " record(s) inserted");

            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void updateSubject(int id, Scanner scanner) {
        try {
            String query = "UPDATE Subject SET SubjectName = ?, ProfessorId = ? WHERE SubjectId = ? AND SubjectStatus = 'Active'";
            PreparedStatement stmt = conn.prepareStatement(query);

            System.out.print("Enter new subject name (or press Enter to skip): ");
            String newName = scanner.nextLine();
            System.out.print("Enter new professor ID (or press Enter to skip): ");
            String newProfessorIdStr = scanner.nextLine();
            int newProfessorId = 0;
            if (!newProfessorIdStr.isEmpty()) {
                newProfessorId = Integer.parseInt(newProfessorIdStr);
            }

            stmt.setString(1, newName.isEmpty() ? null : newName);
            stmt.setInt(2, newProfessorId);
            stmt.setInt(3, id);

            int rowsUpdated = stmt.executeUpdate();
            System.out.println(rowsUpdated + " record(s) updated");

            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void deleteSubject(int id) {
        try {
            String query = "UPDATE Subject SET SubjectStatus = 'Inactive' WHERE SubjectId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);

            int rowsDeleted = stmt.executeUpdate();
            System.out.println(rowsDeleted + " record(s) deleted");

            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void undoDeleteSubject(int id) {
        try {
            String query = "UPDATE Subject SET SubjectStatus = 'Active' WHERE SubjectId = ? AND SubjectStatus = 'Inactive'";
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
