package edu.metrostate.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

public class NutritionalChart {

    private Integer nutritionID;
    private Integer ingredientID;
    private Integer servingSize;
    private Integer calories;
    private Integer totalCarbohydrates;
    private Integer totalFat;
    private Integer totalProtein;
    private Integer totalSodium;
    private Integer totalSugars;
    private Integer dietaryFiber;
    private Integer cholesterol;

    private NutritionalChart(Builder builder) {
        this.nutritionID = builder.nutritionID;
        this.ingredientID = builder.ingredientID;
        this.servingSize = builder.servingSize;
        this.calories = builder.calories;
        this.totalCarbohydrates = builder.totalCarbohydrates;
        this.totalFat = builder.totalFat;
        this.totalProtein = builder.totalProtein;
        this.totalSodium = builder.totalSodium;
        this.totalSugars = builder.totalSugars;
        this.dietaryFiber = builder.dietaryFiber;
        this.cholesterol = builder.cholesterol;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID;
    }

    public Integer getIngredientID() {
        return ingredientID;
    }

    public void setNutritionID(int nutritionID) {
        this.nutritionID = nutritionID;
    }

    public Integer getNutritionID() {
        return nutritionID;
    }

    public void setServingSize(int servingSize){
        this.servingSize = servingSize;
    }

    public Integer getServingSize() {
        return servingSize;
    }

    public void setCalories(int calories){
        this.calories = calories;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setTotalCarbohydrates(int totalCarbohydrates){
        this.totalCarbohydrates = totalCarbohydrates;
    }

    public Integer getTotalCarbohydrates() {
        return totalCarbohydrates;
    }

    public void setTotalFat(int totalFat){
        this.totalFat = totalFat;
    }

    public Integer getTotalFat() {
        return totalFat;
    }

    public void setTotalProtein(int totalProtein){
        this.totalProtein = totalProtein;
    }

    public Integer getTotalProtein() {
        return totalProtein;
    }

    public void setTotalSodium(int totalSodium){
        this.totalSodium = totalSodium;
    }

    public Integer getTotalSodium() {
        return totalSodium;
    }

    public void setTotalSugars(int totalSugars){
        this.totalSugars = totalSugars;
    }

    public Integer getTotalSugars() {
        return totalSugars;
    }

    public void setDietaryFiber(int dietaryFiber){
        this.dietaryFiber = dietaryFiber;
    }

    public Integer getDietaryFiber() {
        return dietaryFiber;
    }

    public void setCholesterol(int cholesterol){
        this.cholesterol = cholesterol;
    }

    public Integer getCholesterol() {
        return cholesterol;
    }

    public static void createTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS NutritionalChart(" +
                "nutritionID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ingredientID INTEGER, " +
                "servingSize INTEGER," +
                "calories INTEGER, " +
                "totalCarbohydrates INTEGER, " +
                "totalFat INTEGER, " +
                "totalProtein INTEGER, " +
                "totalSodium INTEGER," +
                "totalSugars INTEGER, " +
                "dietaryFiber INTEGER, " +
                "cholesterol INTEGER, " +
                "FOREIGN KEY (ingredientID) REFERENCES IngredientTable (ingredientID) ON DELETE CASCADE" +
                ");";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
        }
    }

    public int insert(Connection connection) throws SQLException {
        String sql = "INSERT INTO NutritionalChart (" +
                "ingredientID, " +
                "servingSize, " +
                "calories, " +
                "totalCarbohydrates, " +
                "totalFat, " +
                "totalProtein, " +
                "totalSodium, " +
                "totalSugars, " +
                "dietaryFiber, " +
                "cholesterol) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setObject(1, this.ingredientID, Types.INTEGER);
            stmt.setObject(2, this.servingSize, Types.INTEGER);
            stmt.setObject(3, this.calories, Types.INTEGER);
            stmt.setObject(4, this.totalCarbohydrates, Types.INTEGER);
            stmt.setObject(5, this.totalFat, Types.INTEGER);
            stmt.setObject(6, this.totalProtein, Types.INTEGER);
            stmt.setObject(7, this.totalSodium, Types.INTEGER);
            stmt.setObject(8, this.totalSugars, Types.INTEGER);
            stmt.setObject(9, this.dietaryFiber, Types.INTEGER);
            stmt.setObject(10, this.cholesterol, Types.INTEGER);
            stmt.execute();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.nutritionID = generatedKeys.getInt(1);
                    return this.nutritionID;
                }
            }
        }
        return -1;
    }

    public void updateIngredientID(Connection connection, int ingredientID) throws SQLException {
        System.out.println("I've received the ingredientID and it's being set to " + this.nutritionID);
        String sql = "UPDATE NutritionalChart SET ingredientID = ? WHERE nutritionID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ingredientID); // Set the new ingredientID
            stmt.setInt(2, this.nutritionID); // Use the current nutritionID to locate the record
            stmt.executeUpdate(); // Execute the update
        }
    }

    public boolean checkAllValuesExist() {
        boolean isFilled = Stream.of(this.getServingSize(),
                this.getCalories(),
                this.getTotalCarbohydrates(),
                this.getTotalFat(),
                this.getTotalProtein(),
                this.getTotalSodium(),
                this.getTotalSugars(),
                this.getDietaryFiber(),
                this.getCholesterol()).allMatch(Objects::nonNull);
        return isFilled;
    }

    public static NutritionalChart getNutritionalChartByID(int ID) throws SQLException {
        String sql = "SELECT * FROM NutritionalChart WHERE nutritionID = ?";
        System.out.println("In the getNutritionalChartByID for " + ID);
        try (Connection connection = Database.getConnection()) {
            assert connection != null;
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, ID);
                ResultSet rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    int servingSize = rs.getInt("servingSize");
                    System.out.println("The serving size is " + servingSize);
                    int calories = rs.getInt("calories");
                    System.out.println("The calories are " + calories);
                    int totalCarbohydrates = rs.getInt("totalCarbohydrates");
                    int totalFat = rs.getInt("totalFat");
                    int totalProtein = rs.getInt("totalProtein");
                    int totalSodium = rs.getInt("totalSodium");
                    int totalSugars = rs.getInt("totalSugars");
                    int dietaryFiber = rs.getInt("dietaryFiber");
                    int cholesterol = rs.getInt("cholesterol");

                    NutritionalChart nutritionalChart = new Builder()
                            .servingSize(servingSize)
                            .calories(calories)
                            .totalCarbohydrates(totalCarbohydrates)
                            .totalFat(totalFat)
                            .totalProtein(totalProtein)
                            .totalSodium(totalSodium)
                            .totalSugars(totalSugars)
                            .dietaryFiber(dietaryFiber)
                            .cholesterol(cholesterol)
                            .build();
                    return nutritionalChart;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
            throw new SQLException("Error fetching data for ID " + ID, e);
        }
        return null;
    }

    public static ArrayList<Recipe> getNutritionalChartByID(ArrayList<Recipe> recipeArrayList) throws SQLException {
        try (Connection connection = Database.getConnection()) {

            for (Recipe recipe : recipeArrayList) {
                int ID = recipe.getNutritionID();
                String sql = "SELECT * FROM NutritionalChart WHERE nutritionID = ?";

                assert connection != null;
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, ID);
                    ResultSet rs = preparedStatement.executeQuery();
                    while (rs.next()) {

                        int servingSize = rs.getInt("servingSize");
                        int calories = rs.getInt("calories");
                        int totalCarbohydrates = rs.getInt("totalCarbohydrates");
                        int totalFat = rs.getInt("totalFat");
                        int totalProtein = rs.getInt("totalProtein");
                        int totalSodium = rs.getInt("totalSodium");
                        int totalSugars = rs.getInt("totalSugars");
                        int dietaryFiber = rs.getInt("dietaryFiber");
                        int cholesterol = rs.getInt("cholesterol");

                        NutritionalChart nutritionalChart = new Builder()
                                .servingSize(servingSize)
                                .calories(calories)
                                .totalCarbohydrates(totalCarbohydrates)
                                .totalFat(totalFat)
                                .totalProtein(totalProtein)
                                .totalSodium(totalSodium)
                                .totalSugars(totalSugars)
                                .dietaryFiber(dietaryFiber)
                                .cholesterol(cholesterol)
                                .build();
                        recipe.setNutrition(nutritionalChart);
                    }
                }
            }
        }
        return recipeArrayList;
    }


    public static class Builder {
        private Integer nutritionID;
        private Integer ingredientID;
        private Integer servingSize;
        private Integer calories;
        private Integer totalCarbohydrates;
        private Integer totalFat;
        private Integer totalProtein;
        private Integer totalSodium;
        private Integer totalSugars;
        private Integer dietaryFiber;
        private Integer cholesterol;

        public Builder nutritionID(Integer nutritionID){
            this.nutritionID = nutritionID;
            return this;
        }

        public Builder ingredientID(Integer ingredientID){
            this.ingredientID = ingredientID;
            return this;
        }

        public Builder servingSize(Integer servingSize){
            this.servingSize = servingSize;
            return this;
        }

        public Builder calories(Integer calories){
            this.calories = calories;
            return this;
        }

        public Builder totalCarbohydrates(Integer totalCarbohydrates){
            this.totalCarbohydrates = totalCarbohydrates;
            return this;
        }

        public Builder totalFat(Integer totalFat){
            this.totalFat = totalFat;
            return this;
        }

        public Builder totalProtein(Integer totalProtein){
            this.totalProtein = totalProtein;
            return this;
        }

        public Builder totalSodium(Integer totalSodium){
            this.totalSodium = totalSodium;
            return this;
        }

        public Builder totalSugars(Integer totalSugars){
            this.totalSugars = totalSugars;
            return this;
        }

        public Builder dietaryFiber(Integer dietaryFiber){
            this.dietaryFiber = dietaryFiber;
            return this;
        }

        public Builder cholesterol(Integer cholesterol){
            this.cholesterol = cholesterol;
            return this;
        }

        public NutritionalChart build(){
            return new NutritionalChart(this);
        }
    }
}