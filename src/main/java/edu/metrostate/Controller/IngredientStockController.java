package edu.metrostate.Controller;

import edu.metrostate.Model.Ingredient;
import edu.metrostate.Model.IngredientStockData;
import edu.metrostate.Model.RecipeListModel;
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
import java.util.ArrayList;
import java.util.ResourceBundle;

public class IngredientStockController implements Initializable {

    @FXML private TableView<IngredientStockData> stockTable;
    @FXML private TableColumn<Ingredient, String> ingredientNameColumn;
    @FXML private TableColumn<Ingredient, Integer> ingredientInStockColumn;
    @FXML private TableColumn<Ingredient, Integer> ingredientRequiredColumn;
    @FXML private TextField titleMessage;

    private ArrayList<Ingredient> ingredientRecipeArrayList;
    private ArrayList<Ingredient> ingredientInStockArrayList = new ArrayList<>();

    public void handleCancel(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void runProcess() {
        ingredientInStockArrayList = getIngredientInStockArrayList(ingredientRecipeArrayList);
        checkStock(ingredientRecipeArrayList, ingredientInStockArrayList);
        populateStockTable();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ingredientNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ingredientInStockColumn.setCellValueFactory(new PropertyValueFactory<>("inStock"));
        ingredientRequiredColumn.setCellValueFactory(new PropertyValueFactory<>("required"));
    }

    public void setIngredientRecipeArrayList(ArrayList<Ingredient> ingredientArrayList) {
        this.ingredientRecipeArrayList = ingredientArrayList;
    }

    public ArrayList<Ingredient> getIngredientInStockArrayList(ArrayList<Ingredient> ingredientRecipeArrayList) {
        for (Ingredient ingredient : ingredientRecipeArrayList) {
            System.out.println(ingredient.getIngredientID());
            int ID = ingredient.getIngredientID();
            Ingredient inStockIngredient = Ingredient.getIngredientByID(ID);
            ingredientInStockArrayList.add(inStockIngredient);
        }
        return ingredientInStockArrayList;
    }

    public void checkStock(ArrayList<Ingredient> ingredientRecipeArrayList, ArrayList<Ingredient> ingredientInStockArrayList) {
        boolean quantityFlag = true;
        for (Ingredient ingredient : ingredientRecipeArrayList) {
            for (Ingredient ingredientInStock : ingredientInStockArrayList) {
                if (ingredient.getIngredientID() == ingredientInStock.getIngredientID()) {
                    if (ingredientInStock.getQuantity() < ingredient.getQuantity()) {
                        quantityFlag = false;
                    }
                }
            }
        }
        if (!quantityFlag) {
            titleMessage.setText("Ingredients are not in stock!");
        } else {
            titleMessage.setText("Ingredients are in stock!");
        }
    }


    public void populateStockTable() {
        ObservableList<IngredientStockData> tableData = FXCollections.observableArrayList();

        for (Ingredient ingredientRecipe : ingredientRecipeArrayList) {
            Ingredient matchingStockIngredient = ingredientInStockArrayList.stream()
                    .filter(stockIngredient -> stockIngredient.getIngredientID() == ingredientRecipe.getIngredientID())
                    .findFirst()
                    .orElse(null);

            if (matchingStockIngredient != null) {
                tableData.add(new IngredientStockData(
                        ingredientRecipe.getName(),
                        matchingStockIngredient.getQuantity(),
                        ingredientRecipe.getQuantity()
                ));
            }
        }

        stockTable.setItems(tableData);
    }

}
