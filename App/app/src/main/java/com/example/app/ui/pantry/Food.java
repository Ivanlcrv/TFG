package com.example.app.ui.pantry;

public class Food {

    private String name;
    private String amount;

    public Food() {
    }

    public Food(String name, String amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }
}
