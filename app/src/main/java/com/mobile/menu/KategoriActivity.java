package com.mobile.menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.menu.databinding.ActivityKategoriBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class KategoriActivity extends AppCompatActivity {
    private ActivityKategoriBinding binding;
    List<Item> itemList;
    RecyclerView recycler_grid;
    String id_kategori;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKategoriBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);
        Intent i = new Intent(getIntent());
        id_kategori = String.valueOf(i.getIntExtra("id_kategori", 0));
        Log.d("id_kategori", "code" + id_kategori);
        binding.btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(KategoriActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
        //yg dipakai recyclerView untuk menampilkan list
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ///ArrayList untuk menyimpan objek-objek model Item
        itemList = new ArrayList<>();
        dataperkategori();
    }

    private void dataperkategori() {
        String DATA_PERKATEGORI = "http://192.168.100.142/ServerMenu/detailperkategori.php";
        AndroidNetworking.post(DATA_PERKATEGORI)
                .addBodyParameter("id_kategori", id_kategori)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject item = null;
                                item = response.getJSONObject(i);
                                itemList.add(new Item(
                                        item.getInt("id_item"),
                                        item.getInt("id_kategori"),
                                        item.getString("nama_kategori"), ///kesalahan td PHP tidak terhubung (tidak di joinkan sedangkan kita butuh nama_kategori) dengan tb_kategori field nama_kategori
                                        item.getString("nama_item"),
                                        item.getInt("harga_item"),
                                        item.getString("foto_item")
                                ));
                            }
                            ItemAdapter adapter = new ItemAdapter(KategoriActivity.this, itemList);
                            binding.recyclerView.setAdapter(adapter); //panggil recyclerView guna menampilkan List
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("eror", "code : " + anError);
                        Toast.makeText(KategoriActivity.this, "Gagal get Data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
