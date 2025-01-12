package edu.metrostate.Controller;

import edu.metrostate.Model.*;
import edu.metrostate.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import static edu.metrostate.Controller.InventoryController.loadIngredientsFromDB;

public class AddIngredientToRecipeController implements Initializable {
    @FXML private TableView<Ingredient> ingredientListTable;
    @FXML private TableColumn<Ingredient, String> ingredientListNameColumn;
    @FXML private TableColumn<Ingredient, String> listPrimaryMacroColumn;
    @FXML private TextField quantityTextField;
    @FXML private TextField primaryIngredientTextField;
    @FXML private TextField searchBar;

    @FXML private TableView<Ingredient> ingredientSelectionTable;
    @FXML private TableColumn<Ingredient, String> ingredientSelectionNameColumn;
    @FXML private TableColumn<Ingredient, String> selectionPrimaryMacroColumn;
    @FXML private TableColumn<Ingredient, Integer> ingredientSelectionQuantityColumn;

    AddToRecipeController addToRecipeController;
    private IngredientList ingredientList = new IngredientList();
    private Ingredient primaryIngredient;

    public void handleCancel(MouseEvent event) {
        // Get the current stage from the event source
        System.out.println(primaryIngredient);
        for (Ingredient ingredient : ingredientList.getIngredients()) {
            System.out.println(ingredient);
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Close the stage
        stage.close();
    }

    public void handleSaveIngredientsToRecipe(MouseEvent event) {
        if (ingredientList.getIngredients().isEmpty()) {
            Util.displayErrorMessage("Ingredient", "You must select at least one ingredient");
            return;
        }
        if (primaryIngredient == null) {
            Util.displayErrorMessage("Primary Ingredient", "You must select a primary ingredient");
            return;
        }
        addToRecipeController.setIngredientList(ingredientList, primaryIngredient);
        addToRecipeController.populateIngredients(ingredientList, primaryIngredient);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void setAddToRecipeController(AddToRecipeController addToRecipeController) {
        this.addToRecipeController = addToRecipeController;
    }

    public void populateListTable() throws SQLException {
        ingredientListTable.getItems().clear();
        ObservableList<Ingredient> items = loadIngredientsFromDB();
        ingredientListTable.setItems(items);
        ingredientListTable.refresh();
    }

    public void populateSelectedTable() {
        ingredientSelectionTable.getItems().clear();
        ObservableList<Ingredient> items = FXCollections.observableArrayList(ingredientList.getIngredients());
        ingredientSelectionTable.setItems(items);
        ingredientSelectionTable.refresh();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ingredientListNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        listPrimaryMacroColumn.setCellValueFactory(new PropertyValueFactory<>("primaryMacroNutrient"));
        ingredientSelectionNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        selectionPrimaryMacroColumn.setCellValueFactory(new PropertyValueFactory<>("primaryMacroNutrient"));
        ingredientSelectionQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        try {
            populateListTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> onSearch());
    }

    public Ingredient addIngredientToRecipeList(MouseEvent event) {
        int selectedIngredientQuantity;
        Ingredient selectedIngredient = ingredientListTable.getSelectionModel().getSelectedItem();
        if (selectedIngredient != null) {
        String quantity = quantityTextField.getText();
        quantityTextField.clear();
        if (!quantity.isEmpty() && Util.isNumeric(quantity)) {
            selectedIngredientQuantity = Integer.parseInt(quantity);
            selectedIngredient.setQuantity(selectedIngredientQuantity);
        } else {
            selectedIngredient.setQuantity(0);
        }
            ingredientList.addIngredient(selectedIngredient);
            populateSelectedTable();
            return selectedIngredient;
        }
        return null;
    }

    public void addPrimaryIngredientToRecipeList(MouseEvent event) {
        primaryIngredient = addIngredientToRecipeList(event);
        primaryIngredientTextField.setText(primaryIngredient.getName());
    }

    @FXML
    public void onSearch() {
        String searchTerm = searchBar.getText().trim();
        performIngredientSearch(searchTerm);
    }

    private void performIngredientSearch(String searchTerm) {
        String query = "SELECT * FROM IngredientTable WHERE name LIKE ?";
        try (Connection conn = Database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                // Set the search term to filter by ingredient name
                stmt.setString(1, "%" + searchTerm + "%");

                // Execute the query and get the results
                ResultSet rs = stmt.executeQuery();

                ObservableList<Ingredient> searchResults = FXCollections.observableArrayList();

                // Process the result set and add matching ingredients to the list
                while (rs.next()) {
                    int ingredientID = rs.getInt("ingredientID");
                    String name = rs.getString("name");
                    Date expiryDate = rs.getDate("expiryDate");
                    int quantity = rs.getInt("quantity");

                    //Ensure that we do not try to get an enum for a null value
                    try {
                        String primaryMacroString = rs.getString("primaryMacroNutrient");
                        MacroNutrient primaryMacroNutrient = null;
                        if (primaryMacroString != null){
                            primaryMacroNutrient = MacroNutrient .valueOf(primaryMacroString.toUpperCase());
                        }

                        String storageString = rs.getString("storage");
                        Storage storage = null;
                        if (storageString != null){
                            storage = Storage.valueOf(storageString.toUpperCase());
                        }

                        String categoryString = rs.getString("category");
                        Category category = null;
                        if (categoryString != null){
                            category = Category.valueOf(categoryString.toUpperCase());
                        }


                        Ingredient ingredient = new Ingredient.Builder()
                                .ingredientID(ingredientID)
                                .name(name)
                                .expiryDate(expiryDate)
                                .quantity(quantity)
                                .primaryMacroNutrient(primaryMacroNutrient)
                                .storage(storage)
                                .category(category)
                                .build();

                        searchResults.add(ingredient);
                    } catch (SQLException e){
                        System.out.println("One of the searched for items has missing information");
                    }
                }
                // Update the table with the search results
                ingredientListTable.setItems(searchResults);
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while searching the db :(");
            e.printStackTrace();
        }
    }
}
