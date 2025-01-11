package edu.metrostate.Controller;

import edu.metrostate.Main;
import edu.metrostate.Model.*;
import edu.metrostate.Util;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import static edu.metrostate.Model.Ingredient.getIngredientByID;

public class AddToRecipeController {

    //Fields not for the nutritional chart
    @FXML private TextField recipeNameField;
    @FXML private TextField cuisineNameField;
    @FXML private TextField cuisineOriginField;
    @FXML private TextField primaryIngredientField;
    @FXML private TextField servingsField;
    @FXML private TextField cookTimeField;
    @FXML private TextField recipeDescriptionArea;
    @FXML private TextArea directionsArea;
    @FXML private TextArea ingredientsField;

    //Fields for a nutritional Chart
    @FXML private TextField servingSizeField;
    @FXML private TextField caloriesField;
    @FXML private TextField carbsField;
    @FXML private TextField fatField;
    @FXML private TextField proteinField;
    @FXML private TextField sodiumField;
    @FXML private TextField sugarField;
    @FXML private TextField fiberField;
    @FXML private TextField cholesterolField;

    //Declare instance of the recipe list to manage the list.
    RecipeListModel recipeList;
    IngredientList ingredientList;
    Ingredient primaryIngredient;
    //Declare an instance of the recipe controller to add items to the list and update view
    private RecipeController recipeController;
    private Parent root;
    private Stage stage;

    //Initialize the recipeList/Controller allows the controller to access and modify the shared recipe list
    public void setRecipeList(RecipeListModel recipeList, RecipeController recipeController){
        this.recipeList = recipeList;
        this.recipeController = recipeController;
    }

    //Clear fields so no data remains
    private void clearFields(){
        recipeNameField.clear();
        cuisineNameField.clear();
        cuisineOriginField.clear();
        primaryIngredientField.clear();
        servingsField.clear();
        cookTimeField.clear();
        directionsArea.clear();
        recipeDescriptionArea.clear();
        ingredientsField.clear();
        servingSizeField.clear();
        caloriesField.clear();
        carbsField.clear();
        fatField.clear();
        proteinField.clear();
        sodiumField.clear();
        sugarField.clear();
        fiberField.clear();
        cholesterolField.clear();
    }

    //Cancel button to stop adding a recipe clear the fields and go back
    public void setCancelButton(MouseEvent event) throws IOException, SQLException {
        clearFields();
        ReturnToRecipeHome(event);
    }

    public void ReturnToRecipeHome(MouseEvent event) throws IOException, SQLException {
        recipeController.updateTableView();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void setRoot(Parent root) {
        this.root = root;
    }


    public void handleSaveButtonAction(MouseEvent event) throws SQLException, IOException {

        try (Connection connection = Database.getConnection()){
            //get all info into local variables.
            Recipe recipe = new Recipe();

            String recipeName = recipeNameField.getText();
            if (recipeName.isEmpty()) {
                Util.displayErrorMessage("Name", "Recipe name cannot be empty!");
                return;
            } else {
                recipe.setName(recipeName);
            }

            String description = recipeDescriptionArea.getText();
            if (description.isEmpty()) {
                Util.displayErrorMessage("Description", "Description cannot be empty!");
                return;
            } else {
                recipe.setDescription(description);
            }

            Integer cookTime = validateAndParseField(cookTimeField, "Cook Time Field");
            if (cookTime == null) return;
            recipe.setCookTime(Integer.parseInt(cookTimeField.getText()));

            Integer servings = validateAndParseField(servingsField, "Servings Field");
            if (servings == null) return;
            recipe.setServings(Integer.parseInt(servingsField.getText()));

            String instructions = directionsArea.getText();
            if (instructions.isEmpty()) {
                displayErrorMessage("Directions", "Directions cannot be empty!");
                return;
            } else {
                recipe.setInstructions(instructions);
            }

            String ingredients = ingredientsField.getText();
            if (ingredients.isEmpty()) {
                displayErrorMessage("Ingredients", "Ingredients cannot be empty!");
                return;
            } else {
                recipe.setIngredients(ingredients);
            }

            recipe.setPrimaryIngredientID(primaryIngredient.getIngredientID());

            try {
            //Getting values for Nutritional chart to insert into recipe.
                Integer servingSize = validateAndParseField(servingSizeField, "Serving size");
                if (servingSize == null) return;
                Integer calories = validateAndParseField(caloriesField, "Calories");
                if (calories == null) return;
                Integer totalCarbohydrates = validateAndParseField(carbsField, "Total carbohydrates");
                if (totalCarbohydrates == null) return;
                Integer totalFat = validateAndParseField(fatField, "Total fat");
                if (totalFat == null) return;
                Integer totalProtein = validateAndParseField(proteinField, "Total protein");
                if (totalProtein == null) return;
                Integer totalSodium = validateAndParseField(sodiumField, "Total sodium");
                if (totalSodium == null) return;
                Integer totalSugars = validateAndParseField(sugarField, "Total sugars");
                if (totalSugars == null) return;
                Integer dietaryFiber = validateAndParseField(fiberField, "Dietary fiber");
                if (dietaryFiber == null) return;
                Integer cholesterol = validateAndParseField(cholesterolField, "Cholesterol");
                if (cholesterol == null) return;

                NutritionalChart nutritionalChart = new NutritionalChart.Builder()
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
                int nutritionID;
                if (nutritionalChart.checkAllValuesExist()) {
                    //Inserting nutritional chart to DB
                    nutritionID = nutritionalChart.insert(connection);
                } else {
                    nutritionID = 0;
                }
                recipe.setNutritionID(nutritionID);
                recipe.setNutrition(nutritionalChart);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception thrown while inserting a nutrition chart from AddToRecipeController");
            }

            try {
                String cuisineName;
                if (cuisineNameField.getText().isEmpty()) {
                    displayErrorMessage("Cuisine Name", "Cuisine name cannot be empty!");
                    return;
                } else {
                    cuisineName = cuisineNameField.getText();
                }
                String cuisineCountry;
                if (cuisineOriginField.getText().isEmpty()) {
                    displayErrorMessage("Cuisine Origin Field", "Cuisine Origin Field cannot be empty!");
                    return;
                } else {
                    cuisineCountry = cuisineOriginField.getText();
                }
                Cuisine cuisine = new Cuisine(cuisineName, cuisineCountry);

                Integer cuisineID;
                if (cuisine.checkAllValuesExist()) {
                    //Inserting cuisine to DB
                    cuisineID = cuisine.insert(connection);
                } else {
                    cuisineID = 0;
                }
                recipe.setCuisineID(cuisineID);
                recipe.setCuisine(cuisine);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Inserting recipe to DB
            recipe.insert(connection);
            if (ingredientList.getIngredients().isEmpty()) {
                displayErrorMessage("Ingredients", "Ingredients cannot be empty!");
            } else {
                IngredientRecipeController.addIngredientRecipeToDB(recipe, ingredientList.getIngredients());
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

        clearFields();
        ReturnToRecipeHome(event);
    }

    public void displayErrorMessage(String guiltyField, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText("Invalid Input in " + guiltyField);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public void addIngredientsToRecipe(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/AddIngredientsToRecipe.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setTitle("Add Ingredients to Recipe");
            newStage.setScene(new Scene(root));
            AddIngredientToRecipeController controller = loader.getController();
            controller.setAddToRecipeController(this);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIngredientList (IngredientList ingredientList, Ingredient primaryIngredient) {
        this.ingredientList = ingredientList;
        this.primaryIngredient = primaryIngredient;
    }

    public void populateIngredients (IngredientList ingredientList, Ingredient primaryIngredient) {
        StringBuilder sb = new StringBuilder();
        for (Ingredient ingredient : ingredientList.getIngredients()) {
            sb.append(ingredient.getName()).append(",").append("\n");
        }
        if (!sb.isEmpty()) {
            ingredientsField.setText(sb.toString());
        }
        if (primaryIngredient != null ) {
            primaryIngredientField.setText(primaryIngredient.getName());
        }
    }

    private Integer validateAndParseField(TextField field, String fieldName) {
        String input = field.getText();

        //Check if the field is empty
        if (input.isEmpty()) {
            displayErrorMessage(fieldName, fieldName + " cannot be empty!");
            System.err.println(fieldName + " is empty.");
            return null;
        }

        //Check if the input contains only numeric characters
        if (!input.matches("\\d+")) {
            displayErrorMessage(fieldName, fieldName + " must contain only numeric characters!");
            System.err.println(fieldName + " contains non-numeric characters.");
            return null;
        }

        //Attempt to parse the input as an integer
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            displayErrorMessage(fieldName, fieldName + " must be a valid number!");
            System.err.println("Error parsing " + fieldName + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
