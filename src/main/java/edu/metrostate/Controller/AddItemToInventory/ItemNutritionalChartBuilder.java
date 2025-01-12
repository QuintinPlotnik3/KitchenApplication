package edu.metrostate.Controller.AddItemToInventory;

import edu.metrostate.Model.NutritionalChart;
import javafx.scene.control.TextField;

public class ItemNutritionalChartBuilder {

    //Build a nutritional chart
    public static NutritionalChart build(TextField servingSizeField, TextField caloriesField,
                                             TextField carbsField, TextField fatField, TextField proteinField,
                                             TextField sodiumField, TextField sugarField, TextField fiberField,
                                             TextField cholesterolField){
        return new NutritionalChart.Builder()
                .servingSize(tryParseInt(servingSizeField.getText()))
                .calories(tryParseInt(caloriesField.getText()))
                .totalCarbohydrates(tryParseInt(carbsField.getText()))
                .totalFat(tryParseInt(fatField.getText()))
                .totalProtein(tryParseInt(proteinField.getText()))
                .totalSodium(tryParseInt(sodiumField.getText()))
                .totalSugars(tryParseInt(sugarField.getText()))
                .dietaryFiber(tryParseInt(fiberField.getText()))
                .cholesterol(tryParseInt(cholesterolField.getText()))
                .build();
    }

    //Try to parse integers and return null if it was left empty
    private static Integer tryParseInt(String value){
        try{
            return value != null && !value.trim().isEmpty() ? Integer.parseInt(value.trim()) : null;
        } catch (NumberFormatException e){
            return null;
        }
    }
}
