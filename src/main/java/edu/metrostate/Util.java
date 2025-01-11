package edu.metrostate;

import javafx.scene.control.Alert;

import java.io.Closeable;

public class Util {

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception ex) {
            // close quietly
        }
    }

    public static void closeQuietly(AutoCloseable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception ex) {
            // close quietly
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public static void displayErrorMessage(String guiltyField, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText("Invalid Input in " + guiltyField);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}

