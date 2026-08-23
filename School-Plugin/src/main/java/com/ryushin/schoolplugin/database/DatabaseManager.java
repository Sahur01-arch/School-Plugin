package com.ryushin.schoolplugin.database;

import com.ryushin.ryulib.RyuPlugin;

import java.io.File;
import java.sql.*;
import java.util.*;

public class DatabaseManager {

    private final RyuPlugin plugin;
    private Connection connection;

    public DatabaseManager(RyuPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            File dbFile = new File(dataFolder, "school_database.db");
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);

            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                        "uuid TEXT PRIMARY KEY, " +
                        "name TEXT, " +
                        "class_name TEXT)");

                stmt.execute("CREATE TABLE IF NOT EXISTS grades (" +
                        "uuid TEXT, " +
                        "subject TEXT, " +
                        "grade TEXT, " +
                        "PRIMARY KEY (uuid, subject))");

                stmt.execute("CREATE TABLE IF NOT EXISTS koperasi (" +
                        "uuid TEXT PRIMARY KEY, " +
                        "balance INTEGER)");

                stmt.execute("CREATE TABLE IF NOT EXISTS organisations (" +
                        "name TEXT PRIMARY KEY, " +
                        "description TEXT)");

                stmt.execute("CREATE TABLE IF NOT EXISTS org_members (" +
                        "org_name TEXT, " +
                        "uuid TEXT, " +
                        "name TEXT, " +
                        "role TEXT, " +
                        "PRIMARY KEY (org_name, uuid))");
            }

            plugin.info("[Database] SQLite database initialized successfully.");
        } catch (Exception e) {
            plugin.error("[Database] Failed to initialize SQLite database: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                File dbFile = new File(plugin.getDataFolder(), "school_database.db");
                String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
                connection = DriverManager.getConnection(url);
            }
        } catch (SQLException e) {
            plugin.error("[Database] Failed to get connection: " + e.getMessage());
        }
        return connection;
    }

    // Koperasi Balance Operations
    public int getBalance(String uuid) {
        String sql = "SELECT balance FROM koperasi WHERE uuid = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("balance");
            }
        } catch (SQLException e) {
            plugin.error("Error getting balance: " + e.getMessage());
        }
        return 0;
    }

    public void setBalance(String uuid, int balance) {
        String sql = "INSERT OR REPLACE INTO koperasi (uuid, balance) VALUES (?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            pstmt.setInt(2, balance);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.error("Error setting balance: " + e.getMessage());
        }
    }

    // Grades Operations
    public void setGrade(String uuid, String subject, String grade) {
        String sql = "INSERT OR REPLACE INTO grades (uuid, subject, grade) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            pstmt.setString(2, subject);
            pstmt.setString(3, grade);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.error("Error setting grade: " + e.getMessage());
        }
    }

    public Map<String, String> getGrades(String uuid) {
        Map<String, String> grades = new HashMap<>();
        String sql = "SELECT subject, grade FROM grades WHERE uuid = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                grades.put(rs.getString("subject"), rs.getString("grade"));
            }
        } catch (SQLException e) {
            plugin.error("Error getting grades: " + e.getMessage());
        }
        return grades;
    }

    // Organisation Operations
    public void saveOrganisation(String name, String description) {
        String sql = "INSERT OR REPLACE INTO organisations (name, description) VALUES (?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.error("Error saving organisation: " + e.getMessage());
        }
    }

    public void deleteOrganisation(String name) {
        String sql = "DELETE FROM organisations WHERE name = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.error("Error deleting organisation: " + e.getMessage());
        }
    }

    public void addOrgMember(String orgName, String uuid, String name, String role) {
        String sql = "INSERT OR REPLACE INTO org_members (org_name, uuid, name, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, orgName);
            pstmt.setString(2, uuid);
            pstmt.setString(3, name);
            pstmt.setString(4, role);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.error("Error adding org member: " + e.getMessage());
        }
    }

    public void removeOrgMember(String orgName, String uuid) {
        String sql = "DELETE FROM org_members WHERE org_name = ? AND uuid = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, orgName);
            pstmt.setString(2, uuid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.error("Error removing org member: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) {}
    }
}
