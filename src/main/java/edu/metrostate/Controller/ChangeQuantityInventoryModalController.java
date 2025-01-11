package edu.metrostate.Controller;

import edu.metrostate.Main;
import edu.metrostate.Model.Database;
import edu.metrostate.Model.Ingredient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;

public class ChangeQuantityInventoryModalController implements Initializable {

    private Stage changeQuantityInventoryModalstage;
    private Ingredient ingredient;

    @FXML private TextField newExpiry;
    @FXML private TextField inStock;
    @FXML private TextField addQuantity;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Inventory Quantity modal is initialized");

    }

    public void handleSaveButton(MouseEvent event) {
        int tempaddition = Integer.parseInt(addQuantity.getText());
        String sDate = newExpiry.getText();
        LocalDate expiryDate = LocalDate.parse(sDate);
        Date expiryDateObj = java.sql.Date.valueOf(expiryDate);

        if (expiryDateObj != null) {
            ingredient.setExpiryDate(expiryDateObj);
        }
        //increment the quantity if user entered positive number
        if (tempaddition > 0) {
            ingredient.setQuantity(ingredient.getQuantity() + tempaddition);
        }
        handleBackButton(event);
    }

    public void handleBackButton(MouseEvent event) {
        System.out.println("Closing Ingredient Quantity modal");
        changeQuantityInventoryModalstage.close();
    }

    public void setChangeQuantityInventoryModalstage(Stage stage) {
        this.changeQuantityInventoryModalstage = stage;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        inStock.setText(String.valueOf(ingredient.getQuantity()));
        System.out.println(ingredient + "in setIngredient in change quantity inventory modal controller");
    }

}
