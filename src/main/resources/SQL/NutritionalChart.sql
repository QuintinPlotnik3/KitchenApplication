CREATE TABLE NutritionalChart(
nutritionID INTEGER PRIMARY KEY AUTOINCREMENT,
ingredientID INTEGER,
servingSize INTEGER ,
calories INTEGER ,
totalCarbohydrates INTEGER ,
totalFat INTEGER ,
totalProtein INTEGER ,
totalSodium INTEGER ,
totalSugars INTEGER ,
dietaryFiber INTEGER ,
cholesterol INTEGER ,
FOREIGN KEY (ingredientID) REFERENCES IngredientTable(ingredientID) ON DELETE CASCADE
);