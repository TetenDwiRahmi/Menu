package com.mobile.menu;

public class Item {
    private int id_item;
    private int id_kategori;
    private String nama_kategori;
    private String nama_item;
    private int harga_item;
    private String foto_item;

    public int getId_item() {
        return id_item;
    }

    public void setId_item(int id_item) {
        this.id_item = id_item;
    }

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

    public String getNama_item() {
        return nama_item;
    }

    public void setNama_item(String nama_item) {
        this.nama_item = nama_item;
    }

    public int getHarga_item() {
        return harga_item;
    }

    public void setHarga_item(int harga_item) {
        this.harga_item = harga_item;
    }

    public String getFoto_item() {
        return foto_item;
    }

    public void setFoto_item(String foto_item) {
        this.foto_item = foto_item;
    }



    public Item(int id_item, int id_kategori, String nama_kategori, String nama_item, int harga_item, String foto_item) {
        this.id_item = id_item;
        this.id_kategori = id_kategori;
        this.nama_kategori = nama_kategori;
        this.nama_item = nama_item;
        this.harga_item = harga_item;
        this.foto_item = foto_item;
    }


}
