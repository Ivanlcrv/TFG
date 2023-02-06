package com.example.app;


import android.util.Pair;

import java.util.List;
import java.util.Map;

public class Recipe {

    private String name;
    private String description;
    private List<Pair<String, String>> list;

    public Recipe(){}

    public Recipe(String name, String description, List<Pair<String, String>> list) {
        this.name = name;
        this.description = description;
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Pair<String, String>> getList() {
        return list;
    }

}
