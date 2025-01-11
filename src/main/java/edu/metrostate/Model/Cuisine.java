package edu.metrostate.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

public class Cuisine {

    private Integer cuisineID;
    private String name;
    private String country;

    public Cuisine(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return name;
    }

    public Integer getCuisineID() {
        return cuisineID;
    }


    public static void createTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS CuisineTable(" +
                "cuisineID INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "name TEXT," +
                "country TEXT" +
                ");";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
            //Database.dbDisconnect(connection);
        }

    }


    public int insert(Connection connection) {
        String insertBlank = "INSERT INTO CuisineTable(" +
                "name," +
                "country)" +
                "VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertBlank, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, this.name);
            preparedStatement.setString(2, this.country);

            preparedStatement.execute();
            //Database.dbDisconnect(connection);
            try (ResultSet generatedKey = preparedStatement.getGeneratedKeys()) {
                if (generatedKey.next()) {
                    this.cuisineID = generatedKey.getInt(1);
                    return cuisineID;
                }
            }
        } catch (SQLException exception) {
        exception.printStackTrace();
        }
        return -1;
    }

    public static Cuisine getCuisineByID(int ID) throws SQLException {
        String sql = "SELECT * FROM CuisineTable WHERE cuisineID = ?";
        try (Connection connection = Database.getConnection()) {
            assert connection != null;
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, ID);
                ResultSet rs = preparedStatement.executeQuery();
                //Database.dbDisconnect(connection);

                while (rs.next()) {
                    String name = rs.getString("name");
                    String country = rs.getString("country");

                    return new Cuisine(name, country);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static ArrayList<Recipe> getCuisineByID(ArrayList<Recipe> recipeArrayList) throws SQLException {
        try (Connection connection = Database.getConnection()) {
        for (Recipe recipe : recipeArrayList) {
            int ID = recipe.getCuisineID();

            String sql = "SELECT * FROM CuisineTable WHERE cuisineID = ?";

            assert connection != null;
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, ID);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {

                    String name = rs.getString("name");
                    String country = rs.getString("country");

                    recipe.setCuisine(new Cuisine(name, country));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        }
        return recipeArrayList;
    }

    public boolean checkAllValuesExist() {
        boolean isFilled = Stream.of(this.getName(),
                this.getCountry()).allMatch(Objects::nonNull);
        return isFilled;
    }

}