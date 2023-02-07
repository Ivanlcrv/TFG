package com.example.app;


import android.util.Pair;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;

public class Recipe {

    private String name;
    private String description;
    private List<Pair<String, String>> list;
    private String type;

    public Recipe(){}

    public Recipe(String name, String description, List<Pair<String, String>> list, String type) {
        this.name = name;
        this.description = description;
        this.list = list;
        this.type = type;
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

    public String getType(){ return type; }

    @Override
    public boolean equals(@Nullable Object obj) {
        Recipe r = (Recipe) obj;
        if(r == null) return false;
        if (this.getName()==null) return r.getName()==null;
        return this.getName().equals(r.getName());
    }
}
