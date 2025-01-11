package edu.metrostate.Model;

import java.sql.*;

public class Database {
    private static String databaseName = "test.db";
    private static String connectionString = "jdbc:sqlite:" + databaseName;

    // Method to establish a connection to the database
    public static Connection getConnection() {
        try {
            System.out.println("Connection string: " + connectionString);
            Connection connection = DriverManager.getConnection(connectionString);
            // Create the table if it doesn't exist
            Ingredient.createTable(connection);
            NutritionalChart.createTable(connection);
            Recipe.createTable(connection);
            Cuisine.createTable(connection);
            return connection;
        } catch (SQLException e) {
            System.err.println("Can't get a connection!");
            e.printStackTrace();
            return null;
        }
    }

    public static void dbDisconnect(Connection conn) throws SQLException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}

