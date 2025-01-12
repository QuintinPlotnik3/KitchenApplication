package edu.metrostate.Controller.AddItemToInventory;

import java.sql.Connection;

import edu.metrostate.Model.Ingredient;
import edu.metrostate.Model.NutritionalChart;
import java.sql.SQLException;

public class ItemAdder {

    //Inserts a nutritional chart and gets the ID number
    public static int saveNutritionalChart(Connection connection, NutritionalChart nutrition) throws SQLException {
        if (nutrition == null) {
            return -1;
        }
        int nutritionID = nutrition.insert(connection);
        if (nutritionID == -1){
            throw new SQLException("Failed to receive a valid nutritionID.");
        }
        return nutritionID;
    }

    //Inserts a item and gets the ID number
    public static int saveItem(Connection connection, Ingredient item) throws SQLException{
        int itemID = item.insert(connection);
        if (itemID == -1){
            throw new SQLException("Failed to receive a valid itmeID");
        }
        return itemID;
    }
}
