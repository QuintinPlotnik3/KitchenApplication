CREATE TABLE IngredientTable(
ingredientID INTEGER PRIMARY KEY AUTOINCREMENT,
name TEXT,
expiryDate DATE,
nutritionID INTEGER,
primaryMacroNutrient TEXT,
storage TEXT,
quantity INTEGER,
category TEXT,
description TEXT,
FOREIGN KEY (nutritionID) REFERENCES NutritionalChart(nutritionID) ON DELETE CASCADE
);