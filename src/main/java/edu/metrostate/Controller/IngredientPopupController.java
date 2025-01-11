package edu.metrostate.Controller;

import edu.metrostate.Main;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.PrimitiveIterator;
import java.util.ResourceBundle;

public class IngredientPopupController implements Initializable {

    private InventoryController inventoryController;
    private Stage ingredientPopupStage;
    private Stage inventoryControllerStage;
    private Ingredient ingredient;

    @FXML private Text ingredientTitle;
    @FXML private Text ingredientCategory;
    @FXML private Text ingredientDescription;
    @FXML private TextField ingredientStock;

    @FXML private Text caloriesColumn;
    @FXML private Text carbohydratesColumn;
    @FXML private Text fatColumn;
    @FXML private Text proteinColumn;
    @FXML private Text sodiumColumn;
    @FXML private Text sugarsColumn;
    @FXML private Text dietaryFiberColumn;
    @FXML private Text cholesterolColumn;
    @FXML private Text servingSizeColumn;
    @FXML private ImageView imageView;

    @FXML private TableView<Recipe> recipeTable;
    @FXML private TableColumn<Recipe, String> recipeColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initializing Ingredient pop up screen");
        recipeColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        SetStockDetails();
    }

    @FXML
    public void handleBackButtonClick(MouseEvent event) throws SQLException {
        System.out.println("Closing Ingredient Popup");
        Stage modalStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        modalStage.close();
    }

    public void setInventoryController(InventoryController inventoryController) {
        this.inventoryController = inventoryController;
    }

    public void setStage(Stage stage) {
        this.inventoryControllerStage = stage;
    }

    public void setIngredientModalDetails(Ingredient ingredient) throws SQLException {
        NutritionalChart tempNutritional = null;
        //saving ingredient as a local variable
        setIngredient(ingredient);
        NutritionalChart nutritionalChart = NutritionalChart.getNutritionalChartByID(ingredient.getNutritionID());
        ingredient.setNutrition(nutritionalChart);
        ingredient.getNutrition().toString();
        System.out.println(ingredient.getName() + " in ingredient popup controller");
        System.out.println(ingredient.getName() + " ingredientID is " + ingredient.getIngredientID());
        System.out.println(ingredient.getName() + " nutritionID is " + ingredient.getNutritionID());


        //setting details of all text fields
        this.ingredientTitle.setText(ingredient.getName());
        this.ingredientCategory.setText(ingredient.getCategory() != null ? ingredient.getCategory().name() : "No Category");
        System.out.println(ingredient.getDescription());
        this.ingredientDescription.setText(ingredient.getDescription() == null || ingredient.getDescription().isEmpty() ? "No Description" : ingredient.getDescription());
        try {
            if (ingredient.getNutritionID() > 0) {
                if (ingredient.getNutrition() != null) {
                   // tempNutritional = new NutritionalChart.Builder()
                      //      .build();
                    System.out.println("Nutritional Chart fetched successfully!");
                    System.out.println("Nutritional data is available for " + ingredient.getName() + " with id " + ingredient.getIngredientID());
                    this.caloriesColumn.setText(String.valueOf(ingredient.getNutrition().getCalories()));
                    this.servingSizeColumn.setText(String.valueOf(ingredient.getNutrition().getServingSize()));
                    this.carbohydratesColumn.setText(String.valueOf(ingredient.getNutrition().getTotalCarbohydrates()));
                    this.fatColumn.setText(String.valueOf(ingredient.getNutrition().getTotalFat()));
                    this.proteinColumn.setText(String.valueOf(ingredient.getNutrition().getTotalProtein()));
                    this.sugarsColumn.setText(String.valueOf(ingredient.getNutrition().getTotalSugars()));
                    this.sodiumColumn.setText(String.valueOf(ingredient.getNutrition().getTotalSodium()));
                    this.dietaryFiberColumn.setText(String.valueOf(ingredient.getNutrition().getDietaryFiber()));
                    this.cholesterolColumn.setText(String.valueOf(ingredient.getNutrition().getCholesterol()));
                } else {
                    System.out.println("Nutritional data is missing for " + ingredient.getName());
                    setNutritionalDataUnavailable();
                }
            }else {
                System.out.println("Nutritional chart not found");
                setNutritionalDataUnavailable();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Invalid nutrition ID for" + ingredient.getName());
        }
        ArrayList<Recipe> recipeArrayList = IngredientRecipeController.retrieveRecipeByIngredientFromDB(ingredient);
        if (recipeArrayList != null && !recipeArrayList.isEmpty()) {
            ObservableList<Recipe> recipeObservableList = FXCollections.observableArrayList(recipeArrayList);
            recipeTable.setItems(recipeObservableList);
        }
    }

    private void setNutritionalDataUnavailable() {
        this.caloriesColumn.setText("N/A");
        this.servingSizeColumn.setText("N/A");
        this.carbohydratesColumn.setText("N/A");
        this.fatColumn.setText("N/A");
        this.proteinColumn.setText("N/A");
        this.sugarsColumn.setText("N/A");
        this.sodiumColumn.setText("N/A");
        this.dietaryFiberColumn.setText("N/A");
        this.cholesterolColumn.setText("N/A");
    }

    public void handleAddButtonClick(MouseEvent event) throws IOException {
        System.out.println("Opening add quantity modal");

        Ingredient tempIngredient = InventoryController.tempIngredient;
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/Views/ChangeQuantityInventoryModal.fxml"));
        Parent root = loader.load();
        // Get the controller
        EditIngredient controller = loader.getController();
        controller.setInventoryController(inventoryController);
        controller.setDetails(tempIngredient);
        // Switch to the new scene
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void setIngredientPopupStage(Stage stage) {
        this.ingredientPopupStage = stage;
    }

    public void displayImage(File file) throws IOException {
        if (file != null) {
            try {
                // Read BufferedImage
                BufferedImage bufferedImage = ImageIO.read(file);

                // Convert BufferedImage to byte array
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", outputStream);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

                // Create JavaFX Image from byte array
                Image image = new Image(inputStream);
                imageView.setImage(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public void refreshIngredientStock() {
        this.ingredientStock.setText(String.valueOf(ingredient.getQuantity()));
    }

    public void SetStockDetails(){

        //Sets the Stock for the Add area
        Ingredient ingredient = InventoryController.tempIngredient;
        int quantity = ingredient.getQuantity();
        this.ingredientStock.setText(String.valueOf(quantity));

    }
}
