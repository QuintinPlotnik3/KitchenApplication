package edu.metrostate.Controller;

import edu.metrostate.Model.Ingredient;
import edu.metrostate.Model.NutritionalChart;
import edu.metrostate.Model.Recipe;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RecipePopupController implements Initializable {

    private RecipeController recipeController;
    private Stage RecipeControllerStage;

    @FXML private Text recipeTitle;
    @FXML private Text recipeCuisine;
    @FXML private Text recipeDescription;
    @FXML private TextArea recipeInstructions;

    @FXML private Text caloriesColumn;
    @FXML private Text carbohydratesColumn;
    @FXML private Text fatColumn;
    @FXML private Text proteinColumn;
    @FXML private Text sodiumColumn;
    @FXML private Text sugarsColumn;
    @FXML private Text dietaryFiberColumn;
    @FXML private Text cholesterolColumn;
    @FXML private Text servingSizeColumn;

    @FXML private TableView<Ingredient> ingredientTable;
    @FXML private TableColumn<Ingredient, Integer> ingredientQuantitycolumn;
    @FXML private TableColumn<Ingredient, String> ingredientColumn;

    ArrayList<Ingredient> ingredientArrayList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initializing RecipePopupController");
        ingredientQuantitycolumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        ingredientColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    @FXML
    public void handleBackButtonClick(MouseEvent event) {
        Stage modalStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        modalStage.close();
    }

    public void setRecipeController(RecipeController recipeController) {
        this.recipeController = recipeController;
    }

    public void setStage(Stage stage) {
        this.RecipeControllerStage = stage;
    }

    public void setRecipeModalDetails(Recipe recipe) throws SQLException {
        this.recipeTitle.setText(recipe.getName());
        this.recipeCuisine.setText(recipe.getCuisine().getName());
        this.recipeDescription.setText(recipe.getDescription());
        this.recipeInstructions.setText(recipe.getInstructions());

        NutritionalChart tempNutritionalChart = recipe.getNutrition();
        if (tempNutritionalChart == null) {
            this.caloriesColumn.setText("0");
            this.servingSizeColumn.setText("0");
            this.carbohydratesColumn.setText("0");
            this.fatColumn.setText("0");
            this.proteinColumn.setText("0");
            this.sugarsColumn.setText("0");;
            this.sodiumColumn.setText("0");
            this.dietaryFiberColumn.setText("0");
            this.cholesterolColumn.setText("0");
        } else {
            this.caloriesColumn.setText(String.valueOf(tempNutritionalChart.getCalories()));
            this.servingSizeColumn.setText(String.valueOf(tempNutritionalChart.getServingSize()));
            this.carbohydratesColumn.setText(String.valueOf(tempNutritionalChart.getTotalCarbohydrates()));
            this.fatColumn.setText(String.valueOf(tempNutritionalChart.getTotalFat()));
            this.proteinColumn.setText(String.valueOf(tempNutritionalChart.getTotalProtein()));
            this.sugarsColumn.setText(String.valueOf(tempNutritionalChart.getTotalSugars()));
            this.sodiumColumn.setText(String.valueOf(tempNutritionalChart.getTotalSodium()));
            this.dietaryFiberColumn.setText(String.valueOf(tempNutritionalChart.getDietaryFiber()));
            this.cholesterolColumn.setText(String.valueOf(tempNutritionalChart.getCholesterol()));
        }

        ingredientArrayList = IngredientRecipeController.retrieveIngredientsByRecipeFromDB(recipe);
        ObservableList<Ingredient> ingredientObservableList = FXCollections.observableArrayList(ingredientArrayList);
        ingredientTable.setItems(ingredientObservableList);
    }

    public void displayIngredientStock(MouseEvent event) {
        try {
            URL fxmlLocation = getClass().getResource("/Views/IngredientStockModal.fxml");
            System.out.println(fxmlLocation); // Should not be null
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/IngredientStockModal.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setTitle("Ingredients in Stock");
            newStage.setScene(new Scene(root));
            IngredientStockController controller = loader.getController();
            controller.setIngredientRecipeArrayList(ingredientArrayList);
            controller.runProcess();
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
