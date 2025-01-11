package edu.metrostate.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.stream.Stream;

public class Recipe {

    private int recipeID;
    private String name;
    private String instructions;
    private String description;
    private int cookTime;
    private int servings;
    private NutritionalChart nutrition;
    private int nutritionID;
    private Cuisine cuisine;
    private int cuisineID;
    private Ingredient primaryIngredient;
    private int primaryIngredientID;

    //LEAVING AS STRING FOR NOW. CHANGE TO LIST LATER AND INCORPORATE ADDING INGREDIENT TO RECIPE LIST.
    private String ingredients;

    //default constructor
    public Recipe() {
    }

    private Recipe(RecipeBuilder builder) {
        this.recipeID = builder.recipeID;
        this.name = builder.name;
        this.cuisine = builder.cuisine;
        this.description = builder.description;
        this.cookTime = builder.cookTime;
        this.servings = builder.servings;
        this.primaryIngredientID = builder.primaryIngredientID;
        this.nutrition = builder.nutrition;
        this.ingredients = builder.ingredients;
        this.instructions = builder.instruction;
        this.nutritionID = builder.nutritionID;
        this.cuisineID = builder.cuisineID;
    }

    public boolean queryIngredientsToGroceryList() {
        return false;
    }

    public int getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(int recipeID) {
        this.recipeID = recipeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Cuisine getCuisine() {
        return cuisine;
    }

    public void setCuisine(Cuisine cuisine) {
        this.cuisine = cuisine;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public int getPrimaryIngredientID() {
        return primaryIngredientID;
    }

    public void setPrimaryIngredientID(int primaryIngredientID) {
        this.primaryIngredientID = primaryIngredientID;
    }

    public String getIngredientList() {
        return ingredients;
    }

    public void setIngredientList(String ingredients) {
        this.ingredients = ingredients;
    }

    public NutritionalChart getNutrition() {
        return nutrition;
    }

    public void setNutrition(NutritionalChart nutrition) {
        this.nutrition = nutrition;
    }

    public Ingredient getPrimaryIngredient() {
        return primaryIngredient;
    }

    public void setPrimaryIngredient(Ingredient primaryIngredient) {
        this.primaryIngredient = primaryIngredient;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "recipeID=" + recipeID +
                ", name='" + name + '\'' +
                ", instructions='" + instructions + '\'' +
                ", description='" + description + '\'' +
                ", cookTime=" + cookTime +
                ", servings=" + servings +
                ", nutrition=" + nutrition +
                ", nutritionID=" + nutritionID +
                ", cuisine=" + cuisine +
                ", cuisineID=" + cuisineID +
                ", primaryIngredient=" + primaryIngredient +
                ", primaryIngredientID=" + primaryIngredientID +
                ", ingredients='" + ingredients + '\'' +
                '}';
    }

    public String getInstructions() {
        return instructions;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getNutritionID() {
        return nutritionID;
    }

    public void setNutritionID(int nutritionID) {
        this.nutritionID = nutritionID;
    }

    public int getCuisineID() {
        return cuisineID;
    }

    public void setCuisineID(int cuisineID) {
        this.cuisineID = cuisineID;
    }

    public static void createTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS RecipeTable (" +
                "recipeID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "cuisineID INTEGER, " +
                "description TEXT," +
                "cookTime INTEGER, " +
                "servings INTEGER, " +
                "primaryIngredientID INTEGER, " +
                "nutritionID INTEGER, " +
                "ingredients TEXT, " +
                "instructions TEXT, " +
                "FOREIGN KEY (nutritionID) REFERENCES NutritionalChart(nutritionID) ON DELETE CASCADE," +
                "FOREIGN KEY (cuisineID) REFERENCES CuisineTable(cuisineID) ," +
                "FOREIGN KEY (primaryIngredientID) REFERENCES IngredientTable(ingredientID)" +
                ");";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        }
    }

    public int insert(Connection connection) throws SQLException {

        String sql = "INSERT INTO RecipeTable (" +
                "name, " +
                "cuisineID, " +
                "description, " +
                "cookTime, " +
                "servings, " +
                "primaryIngredientID, " +
                "nutritionID, " +
                "ingredients, " +
                "instructions)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, this.name);
            preparedStatement.setInt(2, this.cuisineID);
            preparedStatement.setString(3, this.description);
            preparedStatement.setInt(4, this.cookTime);
            preparedStatement.setInt(5, this.servings);
            preparedStatement.setInt(6, this.primaryIngredientID);
            preparedStatement.setInt(7, this.nutritionID);
            preparedStatement.setString(8, this.ingredients);
            preparedStatement.setString(9, this.instructions);

            preparedStatement.execute();

            try (ResultSet generatedKey = preparedStatement.getGeneratedKeys()) {
                if (generatedKey.next()) {
                    this.recipeID = generatedKey.getInt(1);
                    return this.recipeID;
                }
            }
        }
        return -1;
    }

    public boolean checkAllValuesExist() {
        boolean isFilled = Stream.of(this.name,
                this.cuisineID,
                this.description,
                this.cookTime,
                this.primaryIngredient,
                this.ingredients,
                this.instructions).allMatch(Objects::nonNull);
        return isFilled;
    }


    public static class RecipeBuilder {
        private int recipeID;
        private String name;
        private Cuisine cuisine;
        private String description;
        private int cookTime;
        private int servings;
        private int primaryIngredientID;
        private Ingredient primaryIngredient;
        private NutritionalChart nutrition;
        private String ingredients;
        private String instruction;
        private int nutritionID;
        private int cuisineID;

        public RecipeBuilder setRecipeID(int recipeID) {
            this.recipeID = recipeID;
            return this;
        }

        public RecipeBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public RecipeBuilder setCuisine(Cuisine cuisine) {
            this.cuisine = cuisine;
            return this;
        }

        public RecipeBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public RecipeBuilder setCookTime(int cookTime) {
            this.cookTime = cookTime;
            return this;
        }

        public RecipeBuilder setServings(int servings) {
            this.servings = servings;
            return this;
        }

        public RecipeBuilder setPrimaryIngredientID(int primaryIngredientID) {
            this.primaryIngredientID = primaryIngredientID;
            return this;
        }

        public RecipeBuilder setPrimaryIngredient(Ingredient primaryIngredient) {
            this.primaryIngredient = primaryIngredient;
            return this;
        }

        public RecipeBuilder setNutrition(NutritionalChart nutrition) {
            this.nutrition = nutrition;
            return this;
        }

        public RecipeBuilder setIngredients(String ingredients) {
            this.ingredients = ingredients;
            return this;
        }

        public RecipeBuilder setInstruction(String instruction) {
            this.instruction = instruction;
            return this;
        }

        public RecipeBuilder setNutritionID(int nutritionID) {
            this.nutritionID = nutritionID;
            return this;
        }

        public RecipeBuilder setCuisineID(int cuisineID) {
            this.cuisineID = cuisineID;
            return this;
        }

        public Recipe build() {
            return new Recipe(this);
        }
    }
}




