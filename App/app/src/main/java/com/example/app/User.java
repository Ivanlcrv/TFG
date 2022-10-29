package com.example.app;

public class User  {

    private String username;
    private String email;
    private String genre;
    private String date;

    public User(){}

    public User(String username, String email, String genre, String date) {
        this.email = email;
        this.username = username;
        this.genre = genre;
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getGenre() {
        return genre;
    }

    public String getDate() {
        return date;
    }
}
