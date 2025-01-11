package edu.metrostate.Model;

import java.util.Queue;

public class MealPlanner {
    private int id;
    private String name;
    private Queue<DailyMealPlanner> dailyMeals;
	
    public MealPlanner(int id, String name) {
        this.id = id;
        this.name = name;
    }
	
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Queue<DailyMealPlanner> getDailyMeals() {
        return dailyMeals;
    }

    public void setDailyMeals(Queue<DailyMealPlanner> dailyMeals) {
        this.dailyMeals = dailyMeals;
    }
}
