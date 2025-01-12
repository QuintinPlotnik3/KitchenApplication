package edu.metrostate.Controller.AddItemToInventory;

import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ItemInputValidator {

    //Throws a exception if the value is invalid
    public static String validateTextField(TextField field, String fieldName, String errorMessage) {
        String value = field.getText();
        if (value == null || value.trim().isEmpty()){
            throw new IllegalArgumentException(fieldName + ": " + errorMessage);
        }
        return value.trim();
    }

    //Throws a exception if the value is invalid
    public static int validateIntegerField(TextField field, String fieldName, String errorMessage) {
        String value = validateTextField(field, fieldName, errorMessage);
        try{
            return Integer.parseInt(value);
        } catch (NumberFormatException e){
            throw new IllegalArgumentException(fieldName + ": Please specify a valid number for " + fieldName + "!");
        }
    }

    //Throws a exception if the value is invalid
    public static LocalDate validateDateField(TextField field, String fieldName, String errorMessage) {
        String value = validateTextField(field, fieldName, errorMessage);
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e){
            throw new IllegalArgumentException(fieldName + ": Please use the format YYYY-MMM-DD for" + fieldName + "!");
        }
    }
}
