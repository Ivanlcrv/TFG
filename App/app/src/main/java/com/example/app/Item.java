package com.example.app;

public class Item {
    private String name;
    private String amount;

    public Item(){}

    public Item(String name, String amount) {
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
