package enrollment;

import java.sql.*;

class Conn {
  private static Connection con = null;
  static final String url = "jdbc:mysql://localhost:3307/students";
  static final String user = "host";
  static final String password = "password123";

  Conn() {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      con = DriverManager.getConnection(url, user, password);
    } catch (ClassNotFoundException | SQLException e) {
      System.out.println(e);
    }
  }

  public static Connection getConnection() {
    // System.out.println("Successfully connected to database");
    return con;
  }

  public static void closeConnection() {
    try {
      con.close();
    } catch (SQLException e) {
      System.out.println(e);
    }
  }
}

