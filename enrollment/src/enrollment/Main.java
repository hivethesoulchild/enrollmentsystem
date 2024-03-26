package enrollment;

import java.sql.*;
import java.util.Scanner;

public class Main {

	public static void main(String args[]) {
		Connection conn = null;
		Scanner scanner = new Scanner(System.in);

		try {
			// Establish database connection
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/students", "host", "password123");

			System.out.print("Enter username: ");
			String username = scanner.nextLine();
			System.out.print("Enter password: ");
			String password = scanner.nextLine();

			// Check if the username and password are valid
			String role = authenticateUser(conn, username, password);

			if (role == null) {
				System.out.println("Invalid username or password.");
				return;
			}

			System.out.println("Login successful! Welcome, " + role + ".");

			// Create instances of the required systems
			StudentSystem ss = new StudentSystem(conn);
			EnrollmentSystem es = new EnrollmentSystem(conn);
			ProfessorSystem ps = new ProfessorSystem(conn);
			CourseSystem cs = new CourseSystem(conn);
			SubjectSystem sus = new SubjectSystem(conn);
			UsersSystem us = new UsersSystem(conn);

			switch (role) {
			case "admin":
				adminMenu(ss, es, ps, cs, sus, us, scanner);
				break;
			case "professor":
				professorMenu(ss, es, ps, cs, sus, scanner);
				break;
			case "student":
				studentMenu(ss, es, ps, cs, sus, scanner);
				break;
			default:
				System.out.println("Invalid role.");
			}

			// Close the database connection
			conn.close();
		} catch (SQLException e) {
			System.out.println("Database connection error: " + e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// Handle connection close error
				}
			}
			scanner.close();
		}
	}

	public static String authenticateUser(Connection conn, String username, String password) {
		// Check if password meets criteria
		if (!isValidPassword(password)) {
			System.out.println(
					"Password must be 8-24 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.");
			return null;
		}

		try {
			String query = "SELECT role FROM users WHERE username = ? AND password = ?";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, username);
			stmt.setString(2, password);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
			    String role = rs.getString("role").toLowerCase(); // Convert role to lowercase
			    if (role.equals("admin") || role.equals("professor") || role.equals("student")) {
			        return role; // Return role in lowercase
			    } else {
			        return null; // Invalid role
			    }
			} else {
			    return null; // Authentication failed
			}

		} catch (SQLException e) {
			System.out.println("Database error: " + e.getMessage());
			return null;
		}
	}

	private static void adminMenu(StudentSystem ss, EnrollmentSystem es, ProfessorSystem ps, CourseSystem cs,
			SubjectSystem sus, UsersSystem us, Scanner scanner) {
		boolean exit = false;
		while (!exit) {
			displayAdminMenu();
			int choice = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character

			switch (choice) {
			case 1:
				adminCRUDMenu(ss, es, ps, cs, sus, us, scanner);
				break;
			case 2:
				// Add professor
				System.out.print("Enter professor name: ");
				String professorName = scanner.nextLine();
				System.out.print("Enter professor number: ");
				String professorNumber = scanner.nextLine();
				ps.createProfessor(professorName, professorNumber);
				break;
			case 3:
				// Add student
				System.out.print("Enter student name: ");
				String studentName = scanner.nextLine();
				System.out.print("Enter student address: ");
				String studentAddress = scanner.nextLine();
				System.out.print("Enter student number: ");
				long studentNumber = scanner.nextLong();
				scanner.nextLine(); // Consume the newline character
				ss.addStudent(studentName, studentAddress, studentNumber);
				break;
			case 4:
				// Add course
				System.out.print("Enter course name: ");
				String courseName = scanner.nextLine();
				cs.createCourse(courseName);
				break;
			case 5:
				// Add user
				boolean validPassword = false;
				String username = "";
				String password = "";
				System.out.print("Enter username: ");
				username = scanner.nextLine();
				do {
					System.out.print("Enter password: ");
					password = scanner.nextLine();
					
					// Validate password format
					validPassword = isValidPassword(password);
					if (!validPassword) {
						System.out.println("Invalid password format. Please try again.");
					}
				} while (!validPassword);
				
				System.out.print("Enter role (Admin/Professor/Student): ");
				String roleStr = scanner.nextLine();
				Users.Role role = Users.Role.valueOf(roleStr);
				us.addUser(username, password, role);
				break;
			case 6:
				exit = true;
				break;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private static void adminCRUDMenu(StudentSystem ss, EnrollmentSystem es, ProfessorSystem ps, CourseSystem cs,
			SubjectSystem sus, UsersSystem us, Scanner scanner) {
		boolean exit = false;
		while (!exit) {
			displayCRUDMenu();
			int choice = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character

			switch (choice) {
			case 1:
				createMenu(ss, es, ps, cs, sus, us, scanner);
				break;
			case 2:
				readMenu(ss, es, ps, cs, sus, us, scanner);
				break;
			case 3:
				updateMenu(ss, es, ps, cs, sus, us, scanner);
				break;
			case 4:
				deleteMenu(ss, es, ps, cs, sus, us, scanner);
				break;
			case 5:
				exit = true;
				break;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private static void professorMenu(StudentSystem ss, EnrollmentSystem es, ProfessorSystem ps, CourseSystem cs,
			SubjectSystem sus, Scanner scanner) {
		boolean exit = false;
		while (!exit) {
			displayProfessorMenu();
			int choice = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character

			switch (choice) {
			case 1:
				professorCRUDMenu(ss, es, ps, cs, sus, scanner);
				break;
			case 2:
				// Add subject
				System.out.print("Enter subject name: ");
				String subjectName = scanner.nextLine();
				System.out.print("Enter professor ID: ");
				int professorId = scanner.nextInt();
				scanner.nextLine(); // Consume the newline character
				sus.createSubject(subjectName, professorId);
				break;
			case 3:
				// Add student
				System.out.print("Enter student name: ");
				String studentName = scanner.nextLine();
				System.out.print("Enter student address: ");
				String studentAddress = scanner.nextLine();
				System.out.print("Enter student number: ");
				long studentNumber = scanner.nextLong();
				scanner.nextLine(); // Consume the newline character
				ss.addStudent(studentName, studentAddress, studentNumber);
				break;
			case 4:
				exit = true;
				break;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private static void studentMenu(StudentSystem ss, EnrollmentSystem es, ProfessorSystem ps, CourseSystem cs,
	        SubjectSystem sus, Scanner scanner) {
	    boolean exit = false;
	    while (!exit) {
	        displayStudentMenu();
	        int choice = scanner.nextInt();
	        scanner.nextLine(); // Consume the newline character

	        switch (choice) {
	            case 1:
	                enrollInSubject(ss, es, scanner); // Call the enrollInSubject method
	                break;
	            case 2:
	                exit = true;
	                break;
	            default:
	                System.out.println("Invalid choice. Please try again.");
	        }
	    }
	}


	private static void professorCRUDMenu(StudentSystem ss, EnrollmentSystem es, ProfessorSystem ps, CourseSystem cs,
			SubjectSystem sus, Scanner scanner) {
		boolean exit = false;
		while (!exit) {
			displayProfessorCRUDMenu();
			int choice = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character

			switch (choice) {
			case 1:
				// Implement CRUD operations for managing subjects
				manageSubjectsMenu(sus, scanner);
				break;
			case 2:
				exit = true;
				break;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private static void manageSubjectsMenu(SubjectSystem sus, Scanner scanner) {
		boolean exit = false;
		while (!exit) {
			displayManageSubjectsMenu();
			int choice = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character

			switch (choice) {
			case 1:
				// Create subject
				System.out.print("Enter subject name: ");
				String subjectName = scanner.nextLine();
				System.out.print("Enter professor ID: ");
				int professorId = scanner.nextInt();
				scanner.nextLine(); // Consume the newline character
				sus.createSubject(subjectName, professorId);
				break;
			case 2:
				// Display subjects
				sus.displaySubjects();
				break;
			case 3:
				// Update subject
				System.out.print("Enter subject ID to update: ");
				int subjectIdToUpdate = scanner.nextInt();
				scanner.nextLine(); // Consume the newline character
				sus.updateSubject(subjectIdToUpdate, scanner);
				break;
			case 4:
				// Delete subject
				System.out.print("Enter subject ID to delete: ");
				int subjectIdToDelete = scanner.nextInt();
				scanner.nextLine(); // Consume the newline character
				sus.deleteSubject(subjectIdToDelete);
				break;
			case 5:
				exit = true;
				break;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private static void createMenu(StudentSystem ss, EnrollmentSystem es, ProfessorSystem ps, CourseSystem cs,
			SubjectSystem sus, UsersSystem us, Scanner scanner) {
		displayCreateMenu();
		int choice = scanner.nextInt();
		scanner.nextLine(); // Consume the newline character

		switch (choice) {
		case 1:
			// Add Student
			System.out.print("Enter student name: ");
			String studentName = scanner.nextLine();
			System.out.print("Enter student address: ");
			String studentAddress = scanner.nextLine();
			System.out.print("Enter student number: ");
			long studentNumber = scanner.nextLong();
			scanner.nextLine(); // Consume the newline character

			// Proceed with user creation
			ss.addStudent(studentName, studentAddress, studentNumber);
			break;
		case 2:
			System.out.print("Enter student number: ");
			int studentNo = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character
			System.out.print("Enter course ID: ");
			int courseId = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character
			System.out.print("Enter subject ID: ");
			int subjectId = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character
			es.createEnrollment(studentNo, courseId, subjectId);
			break;
		case 3:
			System.out.print("Enter professor name: ");
			String professorName = scanner.nextLine();
			System.out.print("Enter professor number: ");
			String professorNumber = scanner.nextLine();
			ps.createProfessor(professorName, professorNumber);
			break;
		case 4:
			System.out.print("Enter course name: ");
			String courseName = scanner.nextLine();
			cs.createCourse(courseName);
			break;
		case 5:
			System.out.print("Enter subject name: ");
			String subjectName = scanner.nextLine();
			System.out.print("Enter professor ID: ");
			int professorId = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character
			sus.createSubject(subjectName, professorId);
			break;
		case 6:
			// Add User
			System.out.print("Enter username: ");
			String username = scanner.nextLine();
			System.out.print("Enter password: ");
			String password = scanner.nextLine();

			// Validate password
			if (!isValidPassword(password)) {
				System.out.println(
						"Password must have 8-24 characters with uppercase, lowercase, and number characters.");
				break;
			}

			// Prompt for role and status
			System.out.print("Enter role (admin, professor, student): ");
			String roleString = scanner.nextLine();
			Users.Role role = Users.Role.valueOf(roleString.toLowerCase());
			System.out.print("Enter status (active, inactive): ");

			// Proceed with user creation
			us.addUser(username, password, role);
			break;
		default:
			System.out.println("Invalid choice. Please try again.");
		}
	}

	private static void readMenu(StudentSystem ss, EnrollmentSystem es, ProfessorSystem ps, CourseSystem cs,
			SubjectSystem sus, UsersSystem us, Scanner scanner) {
		displayReadMenu();
		int choice = scanner.nextInt();
		scanner.nextLine(); // Consume the newline character

		switch (choice) {
		case 1:
			ss.displayStudent();
			break;
		case 2:
			es.displayEnrollments();
			break;
		case 3:
			ps.displayProfessors();
			break;
		case 4:
			cs.displayCourses();
			break;
		case 5:
			sus.displaySubjects();
			break;
		case 6:
			us.displayUsers();
			break;
		default:
			System.out.println("Invalid choice. Please try again.");
		}
	}

	private static void updateMenu(StudentSystem ss, EnrollmentSystem es, ProfessorSystem ps, CourseSystem cs,
			SubjectSystem sus, UsersSystem us, Scanner scanner) {
		displayUpdateMenu();
		int choice = scanner.nextInt();
		scanner.nextLine(); // Consume the newline character

		switch (choice) {
		case 1:
			// Update Student
			System.out.print("Enter student number to update: ");
			int studentNo = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character
			ss.updateStudent(studentNo, null, null);
			break;
		case 2:
			// Update Professor
			System.out.print("Enter professor ID to update: ");
			int professorId = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character
			ps.updateProfessor(professorId, scanner);
			break;
		case 3:
			// Update Course
			System.out.print("Enter course ID to update: ");
			int courseId = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character
			cs.updateCourse(courseId, scanner);
			break;
		case 4:
			// Update Subject
			System.out.print("Enter subject ID to update: ");
			int subjectId = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character
			sus.updateSubject(subjectId, scanner);
			break;
		case 5:
			// Update Enrollment
			System.out.print("Enter enrollment ID to update: ");
			int enrollmentId = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character
			es.updateEnrollment(enrollmentId, scanner);
			break;
		case 6:
			// Update User
			System.out.print("Enter user ID to update: ");
			int userId = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character
			System.out.print("Enter field to update: ");
			String fieldToUpdate = scanner.nextLine();
			System.out.print("Enter new value: ");
			String newValue = scanner.nextLine();
			us.updateUser(userId, fieldToUpdate, newValue);
			break;
		case 7:
			return; // Exit update menu
		default:
			System.out.println("Invalid choice. Please try again.");
		}
	}

	private static void deleteMenu(StudentSystem ss, EnrollmentSystem es, ProfessorSystem ps, CourseSystem cs,
			SubjectSystem sus, UsersSystem us, Scanner scanner) {
		displayDeleteMenu();
		int choice = scanner.nextInt();
		scanner.nextLine(); // Consume the newline character

		switch (choice) {
		case 1:
			// Delete Student
			System.out.print("Enter student number to delete: ");
			int studentNoToDelete = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character
			ss.deleteStudent(studentNoToDelete);
			break;
		case 2:
			// Delete Enrollment
			System.out.print("Enter enrollment ID to delete: ");
			int enrollmentIdToDelete = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character
			es.deleteEnrollment(enrollmentIdToDelete);
			break;
		case 3:
			// Delete Professor
			System.out.print("Enter professor ID to delete: ");
			int professorIdToDelete = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character
			ps.deleteProfessor(professorIdToDelete);
			break;
		case 4:
			// Delete Course
			System.out.print("Enter course ID to delete: ");
			int courseIdToDelete = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character
			cs.deleteCourse(courseIdToDelete);
			break;
		case 5:
			// Delete Subject
			System.out.print("Enter subject ID to delete: ");
			int subjectIdToDelete = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character
			sus.deleteSubject(subjectIdToDelete);
			break;
		case 6:
			// Delete User
			System.out.print("Enter user ID to delete: ");
			int userIdToDelete = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character
			us.deleteUser(userIdToDelete);
			break;
		default:
			System.out.println("Invalid choice. Please try again.");
		}
	}

	private static boolean isValidPassword(String password) {
		// Check if password length is between 8 and 24 characters
		if (password.length() < 8 || password.length() > 24) {
			return false;
		}

		boolean hasUppercase = false;
		boolean hasLowercase = false;
		boolean hasDigit = false;

		// Check each character of the password
		for (char c : password.toCharArray()) {
			if (Character.isUpperCase(c)) {
				hasUppercase = true;
			} else if (Character.isLowerCase(c)) {
				hasLowercase = true;
			} else if (Character.isDigit(c)) {
				hasDigit = true;
			}

			// If all required character types are found, return true
			if (hasUppercase && hasLowercase && hasDigit) {
				return true;
			}
		}

		// If any required character type is missing, return false
		return false;
	}
	
	
	private static void enrollInSubject(StudentSystem ss, EnrollmentSystem es, Scanner scanner) {
	    System.out.print("Enter student number: ");
	    int studentNo = scanner.nextInt();
	    scanner.nextLine(); // Consume the newline character
	    
	    System.out.print("Enter course ID: ");
	    int courseId = scanner.nextInt();
	    scanner.nextLine(); // Consume the newline character
	    
	    System.out.print("Enter subject ID: ");
	    int subjectId = scanner.nextInt();
	    scanner.nextLine(); // Consume the newline character
	    
	    // Check if the student exists
	    if (ss.studentExists(studentNo)) {
	        // Proceed with enrollment
	        es.createEnrollment(studentNo, courseId, subjectId);
	        System.out.println("Enrollment successful!");
	    } else {
	        System.out.println("Student with number " + studentNo + " does not exist.");
	    }
	}




	private static void displayProfessorCRUDMenu() {
		System.out.println("\nProfessor CRUD Menu:");
		System.out.println("1. Manage Subjects");
		System.out.println("2. Exit");
		System.out.print("Enter your choice: ");
	}

	private static void displayAdminMenu() {
	    System.out.println("\nAdmin Menu:");
	    System.out.println("1. CRUD Operations");
	    System.out.println("2. Add Professor");
	    System.out.println("3. Add Student");
	    System.out.println("4. Add Course");
	    System.out.println("5. User Management"); // Add UserSystem option
	    System.out.println("6. Exit");
	    System.out.print("Enter your choice: ");
	}


	private static void displayCRUDMenu() {
		System.out.println("\nCRUD Operations:");
		System.out.println("1. Student");
		System.out.println("2. Professor");
		System.out.println("3. Course");
		System.out.println("4. Subject");
		System.out.println("5. Enrollment");
		System.out.println("6. Back");
		System.out.print("Enter your choice: ");
	}

	private static void displayStudentMenu() {
		System.out.println("\nStudent Menu:");
		System.out.println("1. Enroll in Subject");
		System.out.println("2. Exit");
		System.out.print("Enter your choice: ");
	}

	private static void displayProfessorMenu() {
		System.out.println("\nProfessor Menu:");
		System.out.println("1. CRUD Operations");
		System.out.println("2. Add Subject");
		System.out.println("3. Add Student");
		System.out.println("4. Exit");
		System.out.print("Enter your choice: ");
	}

	private static void displayManageSubjectsMenu() {
		System.out.println("\nManage Subjects Menu:");
		System.out.println("1. Create Subject");
		System.out.println("2. Display Subjects");
		System.out.println("3. Update Subject");
		System.out.println("4. Delete Subject");
		System.out.println("5. Exit");
		System.out.print("Enter your choice: ");
	}

	private static void displayCreateMenu() {
	    System.out.println("===== CREATE MENU =====");
	    System.out.println("1. Add Student");
	    System.out.println("2. Add Professor");
	    System.out.println("3. Add Course");
	    System.out.println("4. Add Subject");
	    System.out.println("5. User Management"); // Add UserSystem option
	    System.out.println("6. Back to Main Menu");
	    System.out.print("Enter your choice: ");
	}

	private static void displayReadMenu() {
	    System.out.println("===== READ MENU =====");
	    System.out.println("1. Display Students");
	    System.out.println("2. Display Professors");
	    System.out.println("3. Display Courses");
	    System.out.println("4. Display Subjects");
	    System.out.println("5. Display Users"); // Add option to display users
	    System.out.println("6. Back to Main Menu");
	    System.out.print("Enter your choice: ");
	}

	private static void displayUpdateMenu() {
	    System.out.println("===== UPDATE MENU =====");
	    System.out.println("1. Update Student");
	    System.out.println("2. Update Professor");
	    System.out.println("3. Update Course");
	    System.out.println("4. Update Subject");
	    System.out.println("5. Update User"); // Add option to update user
	    System.out.println("6. Back to Main Menu");
	    System.out.print("Enter your choice: ");
	}


	private static void displayDeleteMenu() {
	    System.out.println("===== DELETE MENU =====");
	    System.out.println("1. Delete Student");
	    System.out.println("2. Delete Professor");
	    System.out.println("3. Delete Course");
	    System.out.println("4. Delete Subject");
	    System.out.println("5. Delete User"); // Add option to delete user
	    System.out.println("6. Back to Main Menu");
	    System.out.print("Enter your choice: ");
	}


}
