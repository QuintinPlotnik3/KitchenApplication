package edu.metrostate.Model;

public class GroceryList {

    private String name;
    private String storeName;
    private int totalItemsInList;
    //private IngredientList list;

    /*
    public GroceryList(String name, String storeName, int totalItemsInList, IngredientList list) {
        this.name = name;
        this.storeName = storeName;
        this.totalItemsInList = totalItemsInList;
        this.list = list;
    }

     */

    public void addItemToCart(Ingredient ingredient) {

    }

    public void removeItemFromCart(Ingredient ingredient) {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getTotalItemsInList() {
        return totalItemsInList;
    }

    public void setTotalItemsInList(int totalItemsInList) {
        this.totalItemsInList = totalItemsInList;
    }
}