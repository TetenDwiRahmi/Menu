package com.mobile.menu;

public class Kategori {
    private int id_kategori;
    private String nama_kategori;
    private String foto_kategori;

    public int getId_kategori() {
        return id_kategori;
    }

    public void setId_kategori(int id_kategori) {
        this.id_kategori = id_kategori;
    }

    public String getNama_kategori() {
        return nama_kategori;
    }

    public void setNama_kategori(String nama_kategori) {
        this.nama_kategori = nama_kategori;
    }

    public String getFoto_kategori() {
        return foto_kategori;
    }

    public void setFoto_kategori(String foto_kategori) {
        this.foto_kategori = foto_kategori;
    }

    public Kategori(int id_kategori, String nama_kategori, String foto_kategori) {
        this.id_kategori = id_kategori;
        this.nama_kategori = nama_kategori;
        this.foto_kategori = foto_kategori;
    }


}
