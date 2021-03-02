package com.mobile.menu;

import androidx.appcompat.app.AppCompatActivity;

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
import com.mobile.menu.databinding.ActivityDetailBinding;
import com.mobile.menu.databinding.ActivityUpdateBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    String id_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        Intent i = new Intent(getIntent());
        id_item = String.valueOf(i.getIntExtra("id_item", 0));
        Log.d("id_item", "code : " + id_item);

        data();

    }

    private void data() {
        String DATA_PESANAN = "http://192.168.100.142/ServerMenu/detailpesanan.php";
        AndroidNetworking.post(DATA_PESANAN)
                .addBodyParameter("id_item", id_item)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("sukses", "code : " + response);
                            if (response.getString("status").equalsIgnoreCase("1")) {
                                JSONObject data = response.getJSONObject("data");
                                binding.txtNamaKategori.setText(data.getString("nama_kategori"));
                                String foto = data.getString("foto_item");
                                Picasso.get().load("http://192.168.100.142/ServerMenu/upload/" + foto)
                                        .into(binding.image);
                                binding.txtNamaItem.setText(data.getString("nama_item"));
                                binding.txtNamaItem2.setText(data.getString("nama_item"));
                                binding.txtHargaItem.setText("Rp. " + data.getString("harga_item"));
                                binding.txtDeskripsi.setText(data.getString("deskripsi"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", "code : " + anError);
                        Toast.makeText(DetailActivity.this, "Gagal get Data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}