package com.mobile.menu;

public class Registrasi {
    private int id;
    private String username;
    private String email;
    private String password;
    private String foto_user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFoto_user() {
        return foto_user;
    }

    public void setFoto_user(String foto_user) {
        this.foto_user = foto_user;
    }

    public Registrasi(int id, String username, String email, String password, String foto_user) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.foto_user = foto_user;
    }


}
