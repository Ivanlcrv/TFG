package com.example.app.ui.account;

public class User {

    private String username;
    private String email;
    private String genre;
    private String date;
    private Boolean admin;

    public User() {
    }

    public User(String username, String email, String genre, String date) {
        this.email = email;
        this.username = username;
        this.genre = genre;
        this.date = date;
        this.admin = false;
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

    public Boolean getAdmin() {
        return admin;
    }

    @Override
    public boolean equals(Object o) {
        User u = (User) o;
        if (o == null) return false;
        if (this.getEmail() == null) return u.getEmail() == null;
        return this.getEmail().equals(u.getEmail());
    }
}