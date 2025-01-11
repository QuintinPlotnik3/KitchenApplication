package edu.metrostate.Model;

import java.sql.ResultSet;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Ingredient {

    private int ingredientID;
    private String name;
    private Date expiryDate;
    private int nutritionID;
    private NutritionalChart nutrition;
    private MacroNutrient primaryMacroNutrient;
    private Storage storage;
    private int quantity;
    private Category category;
    private String description;
    private File image;

    private Ingredient(Builder builder) {
        this.ingredientID = builder.ingredientID;
        this.name = builder.name;
        this.expiryDate = builder.expiryDate;
        this.nutritionID = builder.nutritionID;
        this.nutrition = builder.nutrition;
        this.primaryMacroNutrient = builder.primaryMacroNutrient;
        this.storage = builder.storage;
        this.quantity = builder.quantity;
        this.category = builder.category;
        this.description = builder.description;
        this.image = builder.image;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getIngredientID() {
        return ingredientID;
    }

    public void setIngredientID(int ingredientID){
        this.ingredientID = ingredientID;
    }

    public int getNutritionID() {
        return nutritionID;
    }

    public void setNutritionID(int nutritionID){
        this.nutritionID = nutritionID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate){
        this.expiryDate = expiryDate;
    }

    public MacroNutrient getPrimaryMacroNutrient() {
        return primaryMacroNutrient;
    }

    public void setPrimaryMacroNutrient(MacroNutrient primaryMacroNutrient){
        this.primaryMacroNutrient = primaryMacroNutrient;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage){
        this.storage = storage;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category){
        this.category = category;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public NutritionalChart getNutrition() {
        return nutrition;
    }

    public void setNutrition(NutritionalChart nutrition){
        this.nutrition = nutrition;
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image){
        this.image = image;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "ingredientID=" + ingredientID +
                ", name='" + name + '\'' +
                ", expiryDate=" + expiryDate +
                ", nutritionID=" + nutritionID +
                ", nutrition=" + nutrition +
                ", primaryMacroNutrient=" + primaryMacroNutrient +
                ", storage=" + storage +
                ", quantity=" + quantity +
                ", category=" + category +
                ", description='" + description + '\'' +
                ", image=" + image +
                '}';
    }

    //This will be the method to update quantity as more ingredients are bought
    public boolean UpdateIngredient(int ingredientID, int NewQuantity, Date NewDate){
        this.ingredientID = ingredientID;
        this.quantity = NewQuantity;
        this.expiryDate = NewDate;
        return false;
    }

    public static void createTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS IngredientTable(" +
                "ingredientID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT, " +
                "expiryDate DATE," +
                "nutritionID INTEGER," +
                "primaryMacroNutrient TEXT, " +
                "storage TEXT, " +
                "quantity INTEGER, " +
                "category TEXT, " +
                "description TEXT," +
                "FOREIGN KEY (nutritionID) REFERENCES NutritionalChart(nutritionID) ON DELETE CASCADE" +
                ");";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
        }
    }

    //inserting an item into the db
    public int insert(Connection connection) throws SQLException {
        String sql = "INSERT INTO IngredientTable (" +
                "name, " +
                "expiryDate, " +
                "nutritionID, " +
                "primaryMacroNutrient, " +
                "storage, " +
                "quantity, " +
                "category, " +
                "description)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, this.name);
            stmt.setDate(2, new java.sql.Date(this.expiryDate.getTime()));
            stmt.setInt(3, this.nutritionID);
            stmt.setString(4, this.primaryMacroNutrient != null ? this.primaryMacroNutrient.name() : null);
            stmt.setString(5, this.storage != null ? this.storage.name() : null);
            stmt.setInt(6, this.quantity);
            stmt.setString(7, this.category != null ? this.category.name() : null);
            stmt.setString(8, this.description);

            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.ingredientID = generatedKeys.getInt(1);
                    return this.ingredientID;
                }
            }
        }
        return -1;
    }

    public static Ingredient getIngredientByName(String name) {
        try (Connection connection = Database.getConnection()){
            String sql = "SELECT * FROM IngredientTable WHERE name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
                preparedStatement.setString(1, name);
                try (ResultSet resultSet = preparedStatement.executeQuery()){
                    if(resultSet.next()){
                        Ingredient item = new Builder()
                                .name(name)
                                .build();
                        return item;
                    }
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static Ingredient getIngredientByID(int ID){
        String sql = "SELECT * FROM IngredientTable WHERE ingredientID = ? ";
        try (Connection connection = Database.getConnection()) {
            assert connection != null;
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, ID);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int ingredientID = rs.getInt("ingredientID");
                    String name = rs.getString("name");
                    Date expiryDate = rs.getDate("expiryDate");
                    int quantity = rs.getInt("quantity");
                    int nutritionID = rs.getInt("nutritionID");

                    System.out.println("The queried ingredient ID is " + ingredientID);
                    System.out.println("The queried nutrition ID is " + nutritionID);

                    NutritionalChart nutrition = NutritionalChart.getNutritionalChartByID(nutritionID);

                    String primaryMacroNutrientString = rs.getString("primaryMacroNutrient");
                    MacroNutrient primaryMacroNutrient = null;
                    if (primaryMacroNutrientString != null) {
                        try {
                            primaryMacroNutrient = MacroNutrient.valueOf(primaryMacroNutrientString.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                            System.out.println("Invalid primaryMacroNutrient value: " + primaryMacroNutrientString);
                        }
                    }

                    String storageString = rs.getString("storage");
                    Storage storage = null;
                    if (storageString != null){
                        try{
                            storage = Storage.valueOf(storageString.toUpperCase());
                        } catch (IllegalArgumentException e){
                            System.out.println("invalid storage value: " + storageString);
                        }
                    }

                    String categoryString = rs.getString("category");
                    Category category = null;
                    if (categoryString != null){
                        try{
                            category = Category.valueOf(categoryString.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid category value: " + categoryString);
                        }
                    }

                    //Creates new objects of items through a simpler constructor for user viewing
                    Ingredient ingredient = new Builder()
                            .ingredientID(ingredientID)
                            .nutritionID(nutritionID)
                            .nutrition(nutrition)
                            .name(name)
                            .expiryDate(expiryDate)
                            .quantity(quantity)
                            .primaryMacroNutrient(primaryMacroNutrient)
                            .storage(storage)
                            .category(category)
                            .build();
                    return ingredient;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    return null;
    }


    public static ArrayList<Recipe> getIngredientByID(ArrayList<Recipe> recipeArrayList) {
        try (Connection connection = Database.getConnection()) {
            assert connection != null;

            for (Recipe recipe : recipeArrayList) {
                int ID = recipe.getPrimaryIngredientID();

                String sql = "SELECT * FROM IngredientTable " +
                        "WHERE ingredientID = ? ";

                try (PreparedStatement stmt = connection.prepareStatement(sql)
                ) {
                    stmt.setInt(1, ID);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        int ingredientID = rs.getInt("ingredientID");
                        String name = rs.getString("name");
                        Date expiryDate = rs.getDate("expiryDate");
                        int quantity = rs.getInt("quantity");

                        String primaryMacroNutrientString = rs.getString("primaryMacroNutrient");
                        MacroNutrient primaryMacroNutrient = null;
                        if (primaryMacroNutrientString != null) {
                            try {
                                primaryMacroNutrient = MacroNutrient.valueOf(primaryMacroNutrientString.toUpperCase());
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                                System.out.println("Invalid primaryMacroNutrient value: " + primaryMacroNutrientString);
                            }
                        }

                        String storageString = rs.getString("storage");
                        Storage storage = null;
                        if (storageString != null) {
                            try {
                                storage = Storage.valueOf(storageString.toUpperCase());
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                                System.out.println("invalid storage value: " + storageString);
                            }
                        }

                        String categoryString = rs.getString("category");
                        Category category = null;
                        if (categoryString != null) {
                            try {
                                category = Category.valueOf(categoryString.toUpperCase());
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                                System.out.println("Invalid category value: " + categoryString);
                            }
                        }

                        //Creates new objects of items through a simpler constructor for user viewing
                        Ingredient ingredient = new Builder()
                                .ingredientID(ingredientID)
                                .name(name)
                                .expiryDate(expiryDate)
                                .quantity(quantity)
                                .primaryMacroNutrient(primaryMacroNutrient)
                                .storage(storage)
                                .category(category)
                                .build();
                        recipe.setPrimaryIngredient(ingredient);
                    }
                }
            }
            } catch(SQLException e){
                e.printStackTrace(); // Handle exceptions appropriately
            }

        return recipeArrayList;
    }

//    public static void insertIntoIngredientRecipe(ArrayList<Ingredient> ingredientArrayList) {
//        try (Connection connection = Database.getConnection()) {
//            for (Ingredient ingredient : ingredientArrayList) {
//                String sql = "INSERT INTO IngredientRecipeTable (" +
//                        "name, " +
//                        "expiryDate, " +
//                        "nutritionID, " +
//                        "primaryMacroNutrient, " +
//                        "storage, " +
//                        "quantity, " +
//                        "category, " +
//                        "description)" +
//                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
//                try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
//                    stmt.setString(1, ingredient.name);
//                    stmt.setDate(2, new java.sql.Date(ingredient.expiryDate.getTime()));
//                    stmt.setInt(3, ingredient.nutritionID);
//                    stmt.setString(4, ingredient.primaryMacroNutrient != null ? ingredient.primaryMacroNutrient.name() : null);
//                    stmt.setString(5, ingredient.storage != null ? ingredient.storage.name() : null);
//                    stmt.setInt(6, ingredient.quantity);
//                    stmt.setString(7, ingredient.category != null ? ingredient.category.name() : null);
//                    stmt.setString(8, ingredient.description);
//
//                    stmt.executeUpdate();
//                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
//                        if (generatedKeys.next()) {
//                            this.ingredientID = generatedKeys.getInt(1);
//
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    //Updating the keys after insertion
    public void updateNutritionID(Connection connection, int nutritionID) throws SQLException {
        System.out.println("I've received the nutritionID and it's being set to " + nutritionID);
        String sql = "UPDATE IngredientTable SET nutritionID = ? WHERE ingredientID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, nutritionID); // Set the nutritionID
            stmt.setInt(2, this.ingredientID); // Set the ingredientID to locate the record
            stmt.executeUpdate(); // Execute the update
        }
    }

    public static class Builder {
        private int ingredientID;
        private String name;
        private Date expiryDate;
        private int nutritionID;
        private NutritionalChart nutrition;
        private MacroNutrient primaryMacroNutrient;
        private Storage storage;
        private int quantity;
        private Category category;
        private String description;
        private File image;

        public Builder ingredientID(int ingredientID) {
            this.ingredientID = ingredientID;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder expiryDate(Date expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder nutritionID(int nutritionID) {
            this.nutritionID = nutritionID;
            return this;
        }

        public Builder nutrition(NutritionalChart nutrition) {
            this.nutrition = nutrition;
            return this;
        }

        public Builder primaryMacroNutrient(MacroNutrient primaryMacroNutrient) {
            this.primaryMacroNutrient = primaryMacroNutrient;
            return this;
        }

        public Builder storage(Storage storage) {
            this.storage = storage;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder image(File image) {
            this.image = image;
            return this;
        }

        public Ingredient build(){
            return new Ingredient(this);
        }
    }
}