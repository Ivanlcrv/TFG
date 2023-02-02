package com.example.app;


import android.util.Pair;

import java.util.List;
import java.util.Map;

public class Recipe {

    private String name;
    private String description;
    private String type;
    private Map<String, String> list;

    public Recipe(){}

    public Recipe(String name, String description, String type, Map<String, String> list) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }


    public Map<String, String> getList() {
        return list;
    }

}
