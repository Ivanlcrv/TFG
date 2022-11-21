package com.example.app;

public class Food {

    private String name;
    private Float amount;

    public Food(){}

    public Food(String name, Float amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public Float getAmount() {
        return amount;
    }
}
