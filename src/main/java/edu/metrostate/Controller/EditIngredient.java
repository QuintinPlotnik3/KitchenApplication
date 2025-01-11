package edu.metrostate.Controller;

import edu.metrostate.Main;
import edu.metrostate.Model.Database;
import edu.metrostate.Model.Ingredient;
import edu.metrostate.Model.IngredientList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

import static edu.metrostate.Controller.InventoryController.tempIngredient;


public class EditIngredient {
    @FXML private TextField addQuantity;
    @FXML private TextField inStock;
    @FXML private TextField newExpiry;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private InventoryController inventoryController;

    public void IngredientBackButton(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("/Views/Inventory-Home.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.close();
    }

    public void saveButton(MouseEvent event) throws IOException, SQLException {

        Ingredient tempIngredient = InventoryController.tempIngredient;
        String quantityString = addQuantity.getText();

        String dateString = newExpiry.getText();

        if (dateString.isEmpty()) {
            int quantity = Integer.parseInt(quantityString);
            UpdateData(tempIngredient.getIngredientID(), quantity);
        } else if (quantityString.isEmpty()){
            LocalDate TempDate = LocalDate.parse(dateString);
            Date updateDate = Date.valueOf(TempDate);
            UpdateData(tempIngredient.getIngredientID(), updateDate);
        } else {
            int quantity = Integer.parseInt(quantityString);
            LocalDate TempDate = LocalDate.parse(dateString);
            Date updateDate = Date.valueOf(TempDate);
            UpdateData(tempIngredient.getIngredientID(), quantity, updateDate);
        }

        root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("/Views/Inventory-Home.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        ObservableList<Ingredient> updatedIngredients = InventoryController.loadIngredientsFromDB();
        inventoryController.updateTableView(updatedIngredients);
        scene = new Scene(root);
        stage.setScene(scene);
        stage.close();
    }

    public static void UpdateData(int updateID, int updateQuantity, Date newExpiry){
        String query ="UPDATE IngredientTable SET expiryDate = ?, quantity = ? WHERE ingredientID = ?";
        try (Connection conn= Database.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setDate(1, newExpiry);
            preparedStatement.setInt(2, updateQuantity);
            preparedStatement.setInt(3, updateID);

            int rowsAffected = preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void UpdateData(int updateID, int updateQuantity){
        System.out.println("update data without expiry");
        String query ="UPDATE IngredientTable SET quantity = ? WHERE ingredientID = ?";
        try (Connection conn= Database.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setInt(1, updateQuantity);
            preparedStatement.setInt(2, updateID);

            int rowsAffected = preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void UpdateData(int updateID, Date newExpiry){
        String query ="UPDATE IngredientTable SET expiryDate = ? WHERE ingredientID = ?";
        try (Connection conn= Database.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setDate(1, newExpiry);
            preparedStatement.setInt(2, updateID);

            int rowsAffected = preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void setInventoryController(InventoryController inventoryController){
        this.inventoryController = inventoryController;
    }

    public void setDetails(Ingredient ingredient){
        int quantity = ingredient.getQuantity();
        this.inStock.setText(String.valueOf(quantity));
    }

}