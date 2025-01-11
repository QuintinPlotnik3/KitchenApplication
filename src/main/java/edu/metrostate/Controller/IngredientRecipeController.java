package edu.metrostate.Controller;

import edu.metrostate.Model.Database;
import edu.metrostate.Model.Ingredient;
import edu.metrostate.Model.Recipe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class IngredientRecipeController {

    public static void addIngredientRecipeToDB(Recipe recipe, ArrayList<Ingredient> ingredientsList) throws SQLException {
        try (Connection connection = Database.getConnection()) {
            int recipeID = recipe.getRecipeID();
            String sql = "INSERT INTO IngredientRecipe (ingredientID, recipeID, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                for (Ingredient ingredient : ingredientsList) {
                    preparedStatement.setInt(1, ingredient.getIngredientID());
                    preparedStatement.setInt(2, recipeID);
                    preparedStatement.setInt(3, ingredient.getQuantity());
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static ArrayList<Ingredient> retrieveIngredientsByRecipeFromDB(Recipe recipe) throws SQLException {

        ArrayList<Ingredient> ingredients = new ArrayList<>();
        try (Connection connection = Database.getConnection()) {
            int recipeID = recipe.getRecipeID();
        //SQL query to join Ingredient and IngredientRecipe tables and retrieve the ingredients with their quantities
        String sql = "SELECT i.ingredientID, i.name, ir.quantity " +
                "FROM IngredientTable i " +
                "JOIN IngredientRecipe ir ON i.ingredientID = ir.ingredientID " +
                "WHERE ir.recipeID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, recipeID);  // Set the recipe_id parameter
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    //Retrieve the ingredient details from the result set
                    int ingredientId = rs.getInt("ingredientID");
                    String name = rs.getString("name");
                    int quantity = rs.getInt("quantity");  // Retrieve the quantity from IngredientRecipe table
                    //Create the Ingredient object and set its properties
                    Ingredient ingredient = new Ingredient.Builder()
                            .ingredientID(ingredientId)
                            .name(name)
                            .quantity(quantity).build();

                    //Add the ingredient to the list
                    ingredients.add(ingredient);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error retrieving ingredients for recipe", e);
        }
    }
        return ingredients;
    }

    public static ArrayList<Recipe> retrieveRecipeByIngredientFromDB(Ingredient ingredient) throws SQLException {
        int ingredientId = ingredient.getIngredientID();
        if (isIngredientInIngredientRecipe(ingredientId) <= 0) {
            return null;
        }
        ArrayList<Recipe> recipes = new ArrayList<>();
        try (Connection connection = Database.getConnection()) {

        //SQL query to join Recipe and IngredientRecipe tables to get recipes by ingredient_id
        String sql = "SELECT r.recipeID, r.name " +
                "FROM RecipeTable r " +
                "JOIN IngredientRecipe ir ON r.recipeID = ir.recipeID " +
                "WHERE ir.ingredientID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, ingredientId);  // Set the ingredient_id parameter

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    //Retrieve the recipe details from the result set
                    int recipeId = rs.getInt("recipeID");
                    String recipeName = rs.getString("name");

                    //Create the Recipe object and set its properties
                    Recipe recipe = new Recipe.RecipeBuilder()
                            .setName(recipeName)
                            .setRecipeID(recipeId)
                            .build();


                    //Add the recipe to the list
                    recipes.add(recipe);
                }
            }
        } catch (SQLException e) {
            System.out.println("error is coming from here!!!");
            e.printStackTrace();
            throw new SQLException("Error retrieving recipes for ingredient", e);
        }
    }
        return recipes;
    }

    public static int isIngredientInIngredientRecipe(int ingredientId) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM IngredientRecipe WHERE ingredientID = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, ingredientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            System.out.println("failing isIngredientInIngredientRecipe");
            e.printStackTrace();
        }
        return 0;
    }

}
