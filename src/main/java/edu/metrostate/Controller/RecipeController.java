package edu.metrostate.Controller;

import edu.metrostate.Main;
import edu.metrostate.Model.*;
import javafx.beans.property.SimpleStringProperty;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.*;
import java.util.ResourceBundle;

public class RecipeController implements Initializable {
    Stage stage;
    Scene scene;
    Parent root;
    private static RecipeListModel recipeList = new RecipeListModel();

    //Create fields for the table view and its columns
    @FXML private TableView<Recipe> recipeTable;
    @FXML private TableColumn<Recipe, Integer> idColumn;
    @FXML private TableColumn<Recipe, String> nameColumn;
    @FXML private TableColumn<Recipe, Cuisine> cuisineColumn;
    @FXML private TableColumn<Recipe, Integer> cookTimeColumn;
    @FXML private TableColumn<Recipe, Integer> servingsColumn;
    @FXML private TableColumn<Recipe, String> primaryIngredientColumn;
    @FXML private TextField searchBar;

    //Switches back to the home screen
    public void switchToHome(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Main.class.getResource("/Views/HomeScreen.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToAddRecipe(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/Views/AddToRecipe.fxml"));
        Parent root = loader.load();
        //Retrieve the controller associated with the addToRecipe and store it in controller
        AddToRecipeController controller = loader.getController();
        controller.setRoot(root);
        //Call on the controller and pass the recipe list and pass the Recipe controller allowing the AddToRecipe controller
        //to modify the list of recipes
        controller.setRecipeList(this.recipeList, this);
        stage = new Stage();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("recipeID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        cuisineColumn.setCellValueFactory(new PropertyValueFactory<>("cuisine"));
        cookTimeColumn.setCellValueFactory(new PropertyValueFactory<>("cookTime"));
        servingsColumn.setCellValueFactory(new PropertyValueFactory<>("servings"));
        primaryIngredientColumn.setCellValueFactory(cellData -> {
            Ingredient ingredient = cellData.getValue().getPrimaryIngredient(); // Adjust based on your model
            return new SimpleStringProperty(ingredient != null ? ingredient.getName() : ""); // Use ingredient name or empty string
        });

        try {
            updateTableView();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        recipeTable.refresh();
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> onSearch());
    }

    //Adds a new recipe to the list and updates the view
    public void addRecipe(Recipe recipe){
        recipeList.addRecipe(recipe);
        System.out.println("You have added a recipe!");
    }
    //Removes a recipe from the database and updates the view
    public void setDeleteButton(MouseEvent event) throws SQLException {
        Recipe selectedRecipe = recipeTable.getSelectionModel().getSelectedItem();
        if (selectedRecipe != null) {
            String deleteQuery = "DELETE FROM RecipeTable WHERE recipeID = ?";
            try (Connection conn = Database.getConnection()) {
                try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                    stmt.setInt(1, selectedRecipe.getRecipeID());
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0){
                        System.out.println("Recipe deleted successfully!");
                        recipeTable.getItems().remove(selectedRecipe);
                    } else {
                        System.out.println("Couldn't delete Recipe :(");
                    }
                }
            } catch (SQLException e) {
                System.out.println("Couldn't get a connection to delete an item :(");
                e.printStackTrace();
            }
        }
    }

    //Prints the current recipes in the list
    public void updateTableView() throws SQLException {
        System.out.println("updateTableView - recipeController");
        ObservableList<Recipe> items = FXCollections.observableArrayList(recipeList.getRecipes());
        recipeTable.setItems(items);
        recipeTable.refresh();
    }

    @FXML
    public void openRecipeModal(MouseEvent event) throws IOException, SQLException {

        if (event.getClickCount() == 2) {
            Recipe tempRecipe = recipeTable.getSelectionModel().getSelectedItem();
            System.out.println(tempRecipe);
            if (tempRecipe != null) {
                System.out.println(tempRecipe);

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Views/RecipeDetailedModal.fxml"));
                AnchorPane recipeModal = fxmlLoader.load();
                RecipePopupController recipePopupController = fxmlLoader.getController();
                recipePopupController.setRecipeController(this);
                recipePopupController.setRecipeModalDetails(tempRecipe);

                Stage modalStage = new Stage();
                Scene modalScene = new Scene(recipeModal);
                modalStage.setScene(modalScene);
                modalStage.setTitle("Recipe Popup");
                modalStage.initModality(Modality.APPLICATION_MODAL);
                modalStage.show();
            }
        }
    }

    private void performRecipeSearch(String searchTerm) {
        String query = "SELECT * FROM RecipeTable WHERE name LIKE ?";
        try (Connection connection = Database.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, "%" + searchTerm + "%");
                ResultSet rs = stmt.executeQuery();
                ObservableList<Recipe> searchResults = FXCollections.observableArrayList();
                while (rs.next()) {
                    int recipeID = rs.getInt("recipeID");
                    String name = rs.getString("name");
                    int cuisineID = rs.getInt("cuisineID");
                    int cookTime = rs.getInt("cookTime");
                    int servings = rs.getInt("servings");
                    int primaryIngredientID = rs.getInt("primaryIngredientID");
                    String description = rs.getString("description");
                    int nutritionID = rs.getInt("nutritionID");
                    String instructions = rs.getString("instructions");

                    NutritionalChart nutritionalChart = NutritionalChart.getNutritionalChartByID(nutritionID);

                    String ingredientQuery = "SELECT name FROM IngredientTable where ingredientID = ?";
                    try (PreparedStatement ingredientStmt = connection.prepareStatement(ingredientQuery)){
                        ingredientStmt.setInt(1, primaryIngredientID);
                        ResultSet ingredientRs = ingredientStmt.executeQuery();
                        String ingredientName = null;
                        if (ingredientRs.next()){
                            ingredientName = ingredientRs.getString("name");
                        }
                        Ingredient primaryIngredient = new Ingredient.Builder()
                                .name(ingredientName)
                                .build();
                    }
                    String cuisineQuery = "SELECT * FROM CuisineTable WHERE cuisineID = ?";
                    try (PreparedStatement cuisineStmt = connection.prepareStatement(cuisineQuery)) {
                        cuisineStmt.setInt(1, cuisineID);
                        ResultSet cuisineRs = cuisineStmt.executeQuery();
                        String cuisineName = null;
                        if (cuisineRs.next()) {
                            cuisineName = cuisineRs.getString("name");
                        }
                        Cuisine cuisine = new Cuisine(cuisineName, "");
                        Recipe recipe = new Recipe.RecipeBuilder()
                                .setRecipeID(recipeID)
                                .setName(name)
                                .setCuisine(cuisine)
                                .setCookTime(cookTime)
                                .setServings(servings)
                                .setDescription(description)
                                .setNutritionID(nutritionID)
                                .setNutrition(nutritionalChart)
                                .setInstruction(instructions)
                                .build();
                        searchResults.add(recipe);
                    }
                }
                recipeTable.setItems(searchResults);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void onSearch() {
        String searchTerm = searchBar.getText().trim();
        performRecipeSearch(searchTerm);
    }
}

