package com.mobile.menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Item> itemList;
    List<Kategori> kategoriList;
    RecyclerView recyclerView, recycler_grid;
    PrefManager prefManager;
    String id_item, id_kategori;
    Button btnInsert;
    ImageView foto_user;
    TextView txtUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidNetworking.initialize(this);

        prefManager = new PrefManager(this);

        foto_user = findViewById(R.id.imageProfile);
        txtUser = findViewById(R.id.txtUser);

        Intent i = new Intent(getIntent());
        id_item = String.valueOf(i.getIntExtra("id_item", 0));
        Log.d("id_item", "code : " + id_item);

        id_kategori = String.valueOf(i.getIntExtra("id_kategori", 0));
        Log.d("id_kategori", "code" + id_kategori);

        btnInsert = findViewById(R.id.btnInsert);
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, InsertActivity.class);
                startActivity(i);
            }
        });

        foto_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ProfilActivity.class);
                i.putExtra("id", prefManager.getIdUser());
                startActivity(i);
            }
        });

        recyclerView = findViewById(R.id.recyclerViewMain);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recycler_grid = findViewById(R.id.recycler_grid);
        recycler_grid.setHasFixedSize(true);
        recycler_grid.setLayoutManager(new LinearLayoutManager(this, GridLayoutManager.HORIZONTAL, false));

        itemList = new ArrayList<>();
        kategoriList = new ArrayList<>();

        data();
        dataperkategori();

        if (!prefManager.getIdUser().isEmpty()) {
            getfotoprofile();
        }
        Log.d("id User", " id : " + prefManager.getIdUser());
    }

    //Profil user di Home
    private void getfotoprofile() {
        String DATA_PESANAN = "http://192.168.100.142/ServerMenu/detailprofil.php";
        AndroidNetworking.post(DATA_PESANAN)
                .addBodyParameter("id", prefManager.getIdUser())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d("data ", " code :" + response);
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject data = response.getJSONObject(i);
                                txtUser.setText("Hello, " + data.getString("username") + "!");
                                String foto = data.getString("foto_user");
                                Picasso.get().load("http://192.168.100.142/ServerMenu/upload/" + foto).into(foto_user);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", "code : " + anError);
                        Toast.makeText(MainActivity.this, "Gagal get Data", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        setMode(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    private void setMode(int itemId) {
        switch (itemId) {
            case R.id.logout:
                new AlertDialog.Builder(this)
                        .setTitle("Perhatian")
                        .setMessage("Apakah anda yakin ingin keluar?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Aksi Logout
                                prefManager.setLoginStatus(false);
                                prefManager.setIdUser("");
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton("Tidak", null)
                        .show();
        }
    }


    private void data() {
        String DATA_PESANAN_URL = "http://192.168.100.142/ServerMenu/showpesanan.php";
        AndroidNetworking.get(DATA_PESANAN_URL)
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
                                        item.getString("nama_kategori"),
                                        item.getString("nama_item"),
                                        item.getInt("harga_item"),
                                        item.getString("foto_item")
                                ));
                            }
                            ItemAdapter adapter = new ItemAdapter(MainActivity.this, itemList);
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "onErrorResponse : " + anError);

                    }
                });
    }

    private void dataperkategori() {
        String DATA_PERKATEGORI = "http://192.168.100.142/ServerMenu/namakategori.php";
        AndroidNetworking.post(DATA_PERKATEGORI)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject kategori = null;
                                kategori = response.getJSONObject(i);
                                kategoriList.add(new Kategori(
                                        kategori.getInt("id_kategori"),
                                        kategori.getString("nama_kategori"),
                                        kategori.getString("foto_kategori")
                                ));
                            }
                            KategoriAdapter adapter = new KategoriAdapter(MainActivity.this, kategoriList);
                            recycler_grid.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("eror", "code : " + anError);
                        Toast.makeText(MainActivity.this, "Gagal get Data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}