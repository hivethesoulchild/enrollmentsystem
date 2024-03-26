package enrollment;

import java.sql.*;
import java.util.Scanner;

public class Login {

	public static void main(String args[]) {
		try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/students", "host",
				"password123")) {
			Scanner scanner = new Scanner(System.in);
			boolean loggedIn = false;

			while (!loggedIn) {
				System.out.print("Enter username: ");
				String username = scanner.nextLine();

				System.out.print("Enter password: ");
				String password = scanner.nextLine();

				// Create a statement
				String sql = "SELECT * FROM login WHERE Username = ? AND Password = ?";
				try (PreparedStatement statement = connection.prepareStatement(sql)) {
					statement.setString(1, username);
					statement.setString(2, password);

					// Execute the query
					ResultSet resultSet = statement.executeQuery();

					if (resultSet.next()) {
						System.out.println("Login successful!");
						// You can do further actions here after successful login
						loggedIn = true; // Exit the loop
					} else {
						System.out.println("Invalid username or password! Please try again.");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
