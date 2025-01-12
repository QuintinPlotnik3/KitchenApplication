package edu.metrostate.Controller.AddItemToInventory;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ItemImage {

    public static File chooseImageFile(Stage stage){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files",
                "*.jpeg", "*.png"));
        return fileChooser.showOpenDialog(stage);
    }

    /*
    @FXML
    public void imageChooser(MouseEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpeg", "*.png"));
        file = fc.showOpenDialog(null);

        if (file != null) {
            fileNameDisplay.setText(file.getPath());
        }
    }
     */
}
