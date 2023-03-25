package com.example.app;

public class Item {
    private String name;
    private String amount;
    private boolean check;

    public Item() {
    }

    public Item(String name, String amount) {
        this.name = name;
        this.amount = amount;
        this.check = false;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public boolean getCheck() {
        return check;
    }

    public void setCheck(boolean checked) {
        check = checked;
    }
}
