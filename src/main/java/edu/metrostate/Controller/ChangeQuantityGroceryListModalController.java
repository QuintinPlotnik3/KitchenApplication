package edu.metrostate.Controller;

import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ChangeQuantityGroceryListModalController implements Initializable {

    private Stage changeQuantityGroceryListModalstage;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Grocery list quantity modal is initialized");
    }

    public void handleBackButton(MouseEvent event) {
        System.out.println("Closing Grocery list Quantity modal");
        changeQuantityGroceryListModalstage.close();
    }

    public void setChangeQuantityGroceryListModalStage(Stage stage) {
        this.changeQuantityGroceryListModalstage = stage;
    }
}
