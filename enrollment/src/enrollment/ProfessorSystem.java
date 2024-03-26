package enrollment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class ProfessorSystem {
    private final Connection conn;

    public ProfessorSystem(Connection conn) {
        this.conn = conn;
    }

    public List<Professor> getAllProfessors() {
        List<Professor> professors = new ArrayList<>();
        try {
            String query = "SELECT * FROM Professor WHERE ProfessorStatus = 'Active'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Professor professor = new Professor();
                professor.setId(rs.getInt("ProfessorId"));
                professor.setName(rs.getString("ProfessorName"));
                professor.setNumber(rs.getString("ProfessorNumber"));
                professor.setStatus(rs.getString("ProfessorStatus"));
                professors.add(professor);
            }

            stmt.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return professors;
    }

    public void displayProfessors() {
        List<Professor> professors = getAllProfessors();
        System.out.println("Name\t\tNumber");
        for (Professor professor : professors) {
            System.out.println(professor.getName() + "\t\t" + professor.getNumber());
        }
    }

    public void createProfessor(String name, String number) {
        try {
            String query = "INSERT INTO Professor (ProfessorName, ProfessorNumber) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, number);

            int rowsInserted = stmt.executeUpdate();
            System.out.println(rowsInserted + " record(s) inserted");

            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void updateProfessor(int id, Scanner scanner) {
        try {
            String query = "UPDATE Professor SET ProfessorName = ?, ProfessorNumber = ? WHERE ProfessorId = ? AND ProfessorStatus = 'Active'";
            PreparedStatement stmt = conn.prepareStatement(query);

            System.out.print("Enter new professor name (or press Enter to skip): ");
            String newName = scanner.nextLine();
            System.out.print("Enter new professor number (or press Enter to skip): ");
            String newNumber = scanner.nextLine();

            stmt.setString(1, newName.isEmpty() ? null : newName);
            stmt.setString(2, newNumber.isEmpty() ? null : newNumber);
            stmt.setInt(3, id);

            int rowsUpdated = stmt.executeUpdate();
            System.out.println(rowsUpdated + " record(s) updated");

            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void deleteProfessor(int id) {
        try {
            String query = "UPDATE Professor SET ProfessorStatus = 'Inactive' WHERE ProfessorId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);

            int rowsDeleted = stmt.executeUpdate();
            System.out.println(rowsDeleted + " record(s) deleted");

            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void undoDeleteProfessor(int id) {
        try {
            String query = "UPDATE Professor SET ProfessorStatus = 'Active' WHERE ProfessorId = ? AND ProfessorStatus = 'Inactive'";
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