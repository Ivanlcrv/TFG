package com.example.app;

public class User  {

    private String username;
    private String email;
    private String genre;
    private String date;

    public User(){}

    public User(String email, String username, String genre, String date) {
        this.email = email;
        this.username = username;
        this.genre = genre;
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = username;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
