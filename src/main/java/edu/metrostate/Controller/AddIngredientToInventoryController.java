package edu.metrostate.Controller;

import edu.metrostate.Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.SQLException;
import javafx.scene.control.Alert.AlertType;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class AddIngredientToInventoryController {
    @FXML private TextField itemNameField;
    @FXML private TextField expiryDateField;
    @FXML private TextField servingSizeField;
    @FXML private TextField quantityField;
    @FXML private TextField caloriesField;
    @FXML private TextField carbsField;
    @FXML private TextField fatField;
    @FXML private TextField proteinField;
    @FXML private TextField sodiumField;
    @FXML private TextField sugarField;
    @FXML private TextField fiberField;
    @FXML private TextField cholesterolField;
    @FXML private TextArea itemDescriptionArea;
    @FXML private ListView<String> macroNutrientListView;
    @FXML private ListView<String> storageListView;
    @FXML private ListView<String> categoryListView;
    @FXML private TextArea fileNameDisplay;

    private File file;
    //IngredientList ingredientList;
    private InventoryController inventoryController;
    private Parent root;
    private Stage stage;

    @FXML
    public void initialize() {
        // Populate the MacroNutrient ListView
        ObservableList<String> macroNutrientOptions = FXCollections.observableArrayList();
        for (MacroNutrient nutrient : MacroNutrient.values()) {
            macroNutrientOptions.add(nutrient.name());
        }
        macroNutrientListView.setItems(macroNutrientOptions);

        // Populate the Storage ListView
        ObservableList<String> storageOptions = FXCollections.observableArrayList();
        for (Storage storage : Storage.values()) {
            storageOptions.add(storage.name());
        }
        storageListView.setItems(storageOptions);

        //Populate the Category ListView
        ObservableList<String> categoryOptions = FXCollections.observableArrayList();
        for (Category category : Category.values()){
            categoryOptions.add(category.name());
        }
        categoryListView.setItems(categoryOptions);

        setInventoryController(inventoryController);
    }

    public void setInventoryController(InventoryController inventoryController) {
        this.inventoryController = inventoryController;  // Properly set the inventory controller
    }

    //What allows the user to save an item
    public void setSaveButton(MouseEvent event) {
        //First we can set some String fields and initialize variables needed in/out of try and catch blocks
        NutritionalChart nutrition = null;
        LocalDate expiryDate = null;
        java.sql.Date expiryDateObj = null;
        int quantity = 0;
        String name = itemNameField.getText();
        String expiryDateString = expiryDateField.getText();
        String quantityString = quantityField.getText();
        String description = itemDescriptionArea.getText();

        //Exception handling
        if (name.isEmpty()){
            displayErrorMessage("Name", "Items name cannot be empty!");
            return;
        }

        try {
            if (quantityString.isEmpty()) {
                displayErrorMessage("Quantity", "Items must have a quantity!");
                return;
            }
            quantity = Integer.parseInt(quantityString);
        }catch (Exception e){
            displayErrorMessage("Quantity", "Please specify quantity in a digit character!!");
            return;
            }

        try{
            if (expiryDateString.isEmpty()) {
                displayErrorMessage("Expiration Date", "Items must have a expiration date!");
                return;
            }
            expiryDate = LocalDate.parse(expiryDateString);
            expiryDateObj = java.sql.Date.valueOf(expiryDate);
        } catch (Exception e){
            displayErrorMessage("Expiration Date", "There was an error saving your date. Please " +
                    "follow the specified format YYYY-MM-DD");
            return;
        }

        try {
            // Gather nutritional information
            Integer servingSize = tryParseInt(servingSizeField.getText());
            Integer calories = tryParseInt(caloriesField.getText());
            Integer totalCarbohydrates = tryParseInt(carbsField.getText());
            Integer totalFat = tryParseInt(fatField.getText());
            Integer totalProtein = tryParseInt(proteinField.getText());
            Integer totalSodium = tryParseInt(sodiumField.getText());
            Integer totalSugars = tryParseInt(sugarField.getText());
            Integer dietaryFiber = tryParseInt(fiberField.getText());
            Integer cholesterol = tryParseInt(cholesterolField.getText());

            MacroNutrient selectedMacroNutrient = getEnumValue(macroNutrientListView, MacroNutrient.class, "MacroNutrient");
            Storage selectedStorage = getEnumValue(storageListView, Storage.class, "Storage");
            Category selectedCategory = getEnumValue(categoryListView, Category.class, "Category");

            //We try to create a connection with the database through the database class
            System.out.println("get connection - set save button: add ingredient to inventory controller");
            try (Connection connection = Database.getConnection()) {
                // Create the nutritional chart object
                nutrition = new NutritionalChart.Builder()
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

                //We use the insert method that inserts the object to the database amd returns a unique id
                int nutritionID = nutrition.insert(connection);
                System.out.println("The id to be set " + nutritionID);
                if (nutritionID == -1) {
                    throw new SQLException("Failed to receive a valid nutritionID.");
                }
                System.out.println("Acquired NutritionalChart: " + nutrition);

                // Create and insert the Ingredient
                Ingredient item = new Ingredient.Builder()
                        .name(name)
                        .expiryDate(expiryDateObj)
                        .nutritionID(nutritionID)
                        .nutrition(nutrition)
                        .primaryMacroNutrient(selectedMacroNutrient)
                        .storage(selectedStorage)
                        .quantity(quantity)
                        .category(selectedCategory)
                        .description(description)
                        .image(file)
                        .build();

                //We use the insert method that inserts the object to the database amd returns a unique id
                int ingredientID = item.insert(connection);
                System.out.println("The id to be set " + ingredientID);
                if (ingredientID == -1) {
                    throw new SQLException("Failed to receive a valid ingredientID.");
                }
                System.out.println("Acquired ingredient: " + item);

                //Ensure that the ID primary/foreign keys are accurately set so they can reference one another
                nutrition.setIngredientID(ingredientID);
                item.setNutritionID(nutritionID);
                nutrition.updateIngredientID(connection, ingredientID);
                item.updateNutritionID(connection, nutritionID);
                //item.setImage(file);

                System.out.println("Item added to the db: " + item);

                //Add the new item to the singleton list to be viewed
                ObservableList<Ingredient> updatedIngredients = InventoryController.loadIngredientsFromDB();
                inventoryController.updateTableView(updatedIngredients);
                ReturnToInventoryHome(event);
                Database.dbDisconnect(connection);
            }
        }catch (Exception e) {
            e.printStackTrace();
            displayErrorMessage("Database Error", "An error occurred while attempting to save your item");
        }
    }

    private <T extends Enum<T>> T getEnumValue(ListView<String> listView, Class<T> enumClass, String typeName) {
        String selectedValue = listView.getSelectionModel().getSelectedItem();
        if (selectedValue == null || selectedValue.isEmpty()) {
            return null; // Return null for unselected enum values
        }
        try {
            return Enum.valueOf(enumClass, selectedValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            displayErrorMessage(typeName, "Invalid selection for " + typeName + ".");
            return null;
        }
    }

    public static Integer tryParseInt(String value) {
        try {
            if (value == null || value.isEmpty()) {
                return null;  // Return null if input is empty
            }
            return Integer.parseInt(value);  // Parse and return the integer value
        } catch (NumberFormatException e) {
            return null;  // Return null if input is not a valid integer
        }
    }


    public void displayErrorMessage(String guiltyField, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText("Invalid Input in " + guiltyField);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setCancelButton(MouseEvent event) throws IOException, SQLException {
        clearFields();
        ReturnToInventoryHome(event);
    }

    private void clearFields() {
        itemNameField.clear();
        expiryDateField.clear();
        quantityField.clear();
        caloriesField.clear();
        carbsField.clear();
        fatField.clear();
        proteinField.clear();
        sodiumField.clear();
        sugarField.clear();
        fiberField.clear();
        cholesterolField.clear();
        itemDescriptionArea.clear();
    }

    //Go back to the referenced Inventory Controller to display changes
    public void ReturnToInventoryHome(MouseEvent event) throws IOException, SQLException {
        //inventoryController.updateTableView();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void setRoot(Parent root) {
        this.root = root;
    }

    @FXML
    public void imageChooser(MouseEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpeg", "*.png"));
        file = fc.showOpenDialog(null);

        if (file != null) {
            fileNameDisplay.setText(file.getPath());
        }
    }
}