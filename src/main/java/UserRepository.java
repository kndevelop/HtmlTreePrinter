package HtmlTreePrinter;

import java.sql.*;
import java.util.*;
import HtmlTreePrinter.User;

public class UserRepository {
    private static final String URL = "jdbc:postgresql://localhost:5432/scraper_db";
    private static final String USER = "scraper";
    private static final String PASSWORD = "password";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT user_id, user_name, password FROM users";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                User user = new User(
                    rs.getInt("user_id"),
                    rs.getString("user_name"),
                    rs.getString("password")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("DB接続エラー: " + e.getMessage());
        }
        return users;
    }

    public static User getUserById(int userId) {
        String query = "SELECT user_id, user_name, password FROM users WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("DB接続エラー: " + e.getMessage());
        }
        return null;
    }
}
