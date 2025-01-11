package edu.metrostate.Model;

import java.sql.Date;

public class DailyMealPlanner {

	private String day;
	private Date date;
	private Recipe breakfast;
	private Recipe lunch;
	private Recipe dinner;
	private Recipe snack;
	private Recipe drink;

	public DailyMealPlanner(String day, Date date, Recipe breakfast, Recipe lunch, Recipe dinner, Recipe snack,
							Recipe drink) {
		this.day = day;
		this.date = date;
		this.breakfast = breakfast;
		this.lunch = lunch;
		this.dinner = dinner;
		this.snack = snack;
		this.drink = drink;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Recipe getBreakfast() {
		return breakfast;
	}

	public void setBreakfast(Recipe breakfast) {
		this.breakfast = breakfast;
	}

	public Recipe getLunch() {
		return lunch;
	}

	public void setLunch(Recipe lunch) {
		this.lunch = lunch;
	}

	public Recipe getDinner() {
		return dinner;
	}

	public void setDinner(Recipe dinner) {
		this.dinner = dinner;
	}

	public Recipe getSnack() {
		return snack;
	}

	public void setSnack(Recipe snack) {
		this.snack = snack;
	}

	public Recipe getDrink() {
		return drink;
	}

	public void setDrink(Recipe drink) {
		this.drink = drink;
	}
}
