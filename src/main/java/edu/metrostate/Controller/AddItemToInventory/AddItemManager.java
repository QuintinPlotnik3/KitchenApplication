package edu.metrostate.Controller.AddItemToInventory;

import edu.metrostate.Controller.InventoryController;
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
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.SQLException;
import javafx.scene.control.Alert.AlertType;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class AddItemManager {
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
    private InventoryController inventoryController;
    private Parent root;
    private Stage stage;

    //Initializes the list view selections for the new view
    @FXML
    public void initialize() {
        System.out.println("You've reached the AddItemToInventoryController!");
        populateListView(MacroNutrient.values(), macroNutrientListView);
        populateListView(Storage.values(), storageListView);
        populateListView(Category.values(), categoryListView);
    }

    //Uses a generic enum value to work with any enum type to get the values and populate the view
    private <T extends Enum<T>> void populateListView(T[] enumValues, ListView<String> listView) {
        ObservableList<String> options = FXCollections.observableArrayList();
        for (T value : enumValues) {
            options.add(value.name());
        }
        listView.setItems(options);
    }

    //Save a new item to the database
    @FXML
    public void setSaveButton(MouseEvent event) throws SQLException {
        try {
            String name = ItemInputValidator.validateTextField(itemNameField, "Name", "Item name cannot be empty!");
            int quantity = ItemInputValidator.validateIntegerField(quantityField, "Quantity", "Items must have a quantity!");
            LocalDate expiryDate = ItemInputValidator.validateDateField(expiryDateField, "Expiration Date", "Items must have an expiration date!");
            java.util.Date expiryDateUtil = java.sql.Date.valueOf(expiryDate);
            String description = itemDescriptionArea.getText();

            //Passes the fields to the nutritional chart builder for items to construct the chart
            NutritionalChart nutrition = ItemNutritionalChartBuilder.build(servingSizeField, caloriesField, carbsField,
                    fatField, proteinField, sodiumField, sugarField, fiberField, cholesterolField);

            //Gathers list views
            MacroNutrient macroNutrient = getEnumValue(macroNutrientListView, MacroNutrient.class, "MacroNutrient");
            Storage storage = getEnumValue(storageListView, Storage.class, "Storage");
            Category category = getEnumValue(categoryListView, Category.class, "Category");


            try (Connection connection = Database.getConnection()) {
                //Use the item Adder class to add nutritional chart to the database and get the ID
                int nutritionID = ItemAdder.saveNutritionalChart(connection, nutrition);
                Ingredient item = new Ingredient.Builder()
                        .name(name)
                        .expiryDate(expiryDateUtil)
                        .nutritionID(nutritionID)
                        .nutrition(nutrition)
                        .primaryMacroNutrient(macroNutrient)
                        .storage(storage)
                        .quantity(quantity)
                        .category(category)
                        .description(description)
                        .image(file)
                        .build();
                //Use the item Adder class to add  to the database and get the ID
                ItemAdder.saveItem(connection, item);
                //Update the table view and close the add item pop up
                ObservableList<Ingredient> updatedIngredient = InventoryController.loadIngredientsFromDB();
                inventoryController.updateTableView(updatedIngredient);
                ReturnToInventoryHome(event);
            }
        } catch (Exception e) {
            displayErrorMessage("Error", e.getMessage());
        }
    }

    //Uses a generic enum value to work with any enum type to get the values and pass them to be added to a new item
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

    //Displays an error message when given bad inputs
    public void displayErrorMessage(String guiltyField, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText("Invalid Input in " + guiltyField);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //Gathers the inventory controller to go back
    public void setInventoryController(InventoryController inventoryController) {
        this.inventoryController = inventoryController;
    }

    //Gathers the root to go back
    public void setRoot(Parent root) {
        this.root = root;
    }

    //Allows the user to cancel adding an item
    public void setCancelButton(MouseEvent event) throws IOException, SQLException {
        clearFields();
        ReturnToInventoryHome(event);
    }

    //Clears all fields to leave
    private void clearFields() {
        System.out.println("Clearing the fields!");
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
        System.out.println("Going back to InventoryHome!");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void imageChooser(MouseEvent event) {
        file = ItemImage.chooseImageFile((Stage) ((Node) event.getSource()).getScene().getWindow());
        if (file != null) {
            fileNameDisplay.setText(file.getPath());
        }
    }
}