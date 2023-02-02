package com.example.app;

import android.graphics.Bitmap;
import android.util.Pair;

import java.util.List;

public class Recipe {

    private String name;
    private String description;
    private String type;
    private Bitmap selectedImageBitmap;
    private List<Pair<String, String>> list;

    public Recipe(){}

    public Recipe(String name, String description, String type, Bitmap selectedImageBitmap, List<Pair<String, String>> list) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.selectedImageBitmap = selectedImageBitmap;
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

    public Bitmap getBitmap() {
        return selectedImageBitmap;
    }

    public List<Pair<String, String>> getList() {
        return list;
    }

}
