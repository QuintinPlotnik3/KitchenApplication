package edu.metrostate.Controller;

import edu.metrostate.Main;
import edu.metrostate.Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class InventoryController implements Initializable {
    Stage stage;
    Scene scene;
    Parent root;
    public static Ingredient tempIngredient;

    @FXML private TableView<Ingredient> inventoryTable;
    @FXML private TableColumn<Ingredient, Integer> idColumn;
    @FXML private TableColumn<Ingredient, String> name;
    @FXML private TableColumn<Ingredient, Date> expiryDate;
    @FXML private TableColumn<Ingredient, MacroNutrient> primaryMacroNutrient;
    @FXML private TableColumn<Ingredient, Storage> storage;
    @FXML private TableColumn<Ingredient, Integer> quantity;
    @FXML private TableColumn<Ingredient, String> category;
    @FXML private TextField searchBar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialize start - Inventory controller");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ingredientID"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        expiryDate.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        primaryMacroNutrient.setCellValueFactory(new PropertyValueFactory<>("primaryMacroNutrient"));
        storage.setCellValueFactory(new PropertyValueFactory<>("storage"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        category.setCellValueFactory(new PropertyValueFactory<>("category"));
        try {
            ObservableList<Ingredient> items = loadIngredientsFromDB();
            updateTableView(items);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        inventoryTable.refresh();
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> onSearch());
    }

    public void switchToHome(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Main.class.getResource("/Views/HomeScreen.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        System.out.println(stage);
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToAddIngredient(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/Views/AddToInventory.fxml"));
        Parent root = loader.load();
        // Get the controller
        AddIngredientToInventoryController controller = loader.getController();
        // Pass the parent controller to the child controller
        controller.setInventoryController(this);
        controller.setRoot(root);
        // Switch to the new scene
        stage = new Stage();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToEditIngredient(MouseEvent event) throws IOException {
        if (inventoryTable.getSelectionModel().getSelectedItem() != null) {
            tempIngredient = inventoryTable.getSelectionModel().getSelectedItem();
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/Views/ChangeQuantityInventoryModal.fxml"));
            Parent root = loader.load();
            // Get the controller
            EditIngredient controller = loader.getController();
            controller.setInventoryController(this);
            controller.setDetails(tempIngredient);
            // Switch to the new scene
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    public void backToHome(MouseEvent event) throws IOException {
        System.out.println("Going back to home");
        switchToHome(event);
    }

    public void updateTableView(ObservableList<Ingredient> ingredients) throws SQLException {
        System.out.println("updateTableView - inventoryController");
        inventoryTable.setItems(ingredients);
        inventoryTable.refresh();
    }

    public void openIngredientModal(MouseEvent event) throws IOException, SQLException {
        if (event.getClickCount() == 2) {
            Ingredient selectedIngredient = inventoryTable.getSelectionModel().getSelectedItem();
            tempIngredient = inventoryTable.getSelectionModel().getSelectedItem();
            if(selectedIngredient != null){
                System.out.println("Selected Ingredient: " + selectedIngredient.getName());
                tempIngredient = inventoryTable.getSelectionModel().getSelectedItem();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Views/IngredientDetailedModal.fxml"));
                AnchorPane ingredientModal = fxmlLoader.load();
                IngredientPopupController ingredientPopupController = fxmlLoader.getController();
                ingredientPopupController.setInventoryController(this);

                ingredientPopupController.setIngredientModalDetails(selectedIngredient);

                Stage stage = new Stage();
                Scene scene = new Scene(ingredientModal);
                stage.setScene(scene);
                stage.setTitle("Ingredient Popup");
                stage.initModality(Modality.APPLICATION_MODAL);

                stage.show();
                ingredientPopupController.setIngredientPopupStage(stage);
            }
        }
    }

    public InventoryController getController() {
        return this;
    }

    public void setDeleteButton(MouseEvent event) throws SQLException {
        Ingredient selectedIngredient = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedIngredient != null) {
            String deleteQuery = "DELETE FROM IngredientTable WHERE ingredientID = ?";
            try (Connection conn = Database.getConnection()) {
                try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                    stmt.setInt(1, selectedIngredient.getIngredientID());
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0){
                        System.out.println("Ingredient deleted successfully!");
                        inventoryTable.getItems().remove(selectedIngredient);
                    } else {
                        System.out.println("Couldn't delete ingredient :(");
                    }
                }
            } catch (SQLException e) {
                System.out.println("Couldn't get a connection to delete an item :(");
                e.printStackTrace();
            }
        }
    }

    public static ObservableList<Ingredient> loadIngredientsFromDB() throws SQLException {
        String query = "SELECT * FROM IngredientTable";  //SQL query to fetch ingredients
        ObservableList<Ingredient> items = FXCollections.observableArrayList();
        try (Connection conn = Database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();

                // Loop through the result set and create Ingredient objects
                while (rs.next()) {
                    int ingredientID = rs.getInt("ingredientID");
                    String name = rs.getString("name");
                    Date expiryDate = rs.getDate("expiryDate");
                    int quantity = rs.getInt("quantity");
                    int nutritionID = rs.getInt("nutritionID");
                    String description = rs.getString("description");

                    // Handle enum values for primaryMacroNutrient, storage, and category
                    MacroNutrient primaryMacroNutrient = getEnumValue(MacroNutrient.class, rs, "primaryMacroNutrient");
                    Storage storage = getEnumValue(Storage.class, rs, "Storage");
                    Category category = getEnumValue(Category.class, rs, "Category");

                    // Create an Ingredient object with the retrieved data
                    Ingredient ingredient = new Ingredient.Builder()
                            .ingredientID(ingredientID)
                            .nutritionID(nutritionID)
                            .name(name)
                            .expiryDate(expiryDate)
                            .quantity(quantity)
                            .primaryMacroNutrient(primaryMacroNutrient)
                            .storage(storage)
                            .category(category)
                            .description(description)
                            .build();

                    // Add the ingredient to the TableView
                    items.add(ingredient);
                }

            }
        } catch (SQLException e) {
            System.out.println("Error while loading ingredients from the db :(");
            e.printStackTrace();
            throw e;
        }
        return items;
    }

    private static <T extends Enum<T>> T getEnumValue(Class<T> enumClass, ResultSet resultSet, String columnName) throws SQLException {
        String selectedValue = resultSet.getString(columnName);
        if (selectedValue == null || selectedValue.isEmpty()){
            return null;
        }
        try{
            return Enum.valueOf(enumClass, selectedValue.toUpperCase());
        } catch(IllegalArgumentException e){
            return null;
        }
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
                    int nutritionID = rs.getInt("nutritionID");
                    String description = rs.getString("description");

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
                                .nutritionID(nutritionID)
                                .name(name)
                                .expiryDate(expiryDate)
                                .quantity(quantity)
                                .primaryMacroNutrient(primaryMacroNutrient)
                                .storage(storage)
                                .category(category)
                                .description(description)
                                .build();

                        searchResults.add(ingredient);
                    } catch (SQLException e){
                        System.out.println("One of the searched for items has missing information");
                    }
                }
                // Update the table with the search results
                inventoryTable.setItems(searchResults);
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while searching the db :(");
            e.printStackTrace();
        }
    }

    // Called when the user types in the search bar
    @FXML
    public void onSearch() {
        String searchTerm = searchBar.getText().trim();
        performIngredientSearch(searchTerm);
    }
}
