package com.mobile.menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.menu.databinding.ActivityUpdateBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UpdateActivity extends Activity {
    private ActivityUpdateBinding binding;
    String id_item;
    List<String> kategoriList;
    ArrayAdapter<String> adapter;
    int idKat = 1;
    int pos = 0;

    Bitmap bitmap, decoded;
    int PICK_IMAGE_REQUEST = 1;
    int bitmap_size = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);

        Intent i = new Intent(getIntent());
        id_item = String.valueOf(i.getIntExtra("id_item", 0));
        Log.d("id_item", "code : " + id_item);

        datapesanan();

        binding.btnSubmitUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatepesanan();
            }
        });

        binding.btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UpdateActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        binding.imageSetUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        kategoriList = new ArrayList<String>();
        dataperkategori();
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
                                String data = kategori.getString("nama_kategori");
                                kategoriList.add(data);
                            }
                            setAdapterSpinner();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    private void setAdapterSpinner() {
        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, kategoriList);
        binding.spinKategori.setAdapter(adapter);
        binding.spinKategori.setSelection(pos);
        Log.e("eror", "data : " + pos);
        binding.spinKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("kategori", "code" + (position+1));
                idKat = position+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void datapesanan() {
        Intent i = new Intent(getIntent());
        pos = i.getIntExtra("id_kat",0)-1;
        Log.d("idkat", "dat : "+pos);
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
//                                pos = data.getInt("id_kategori")-1; //jika data id_kategori mis makanan (berarti data ke-1), jadi untuk mengambil data array dikurangi 1 agar array yg diambil dr 0
                                binding.editNamaItem.setText(data.getString("nama_item"));
                                binding.editHargaItem.setText(data.getString("harga_item"));
                                String foto = data.getString("foto_item");
                                Picasso.get().load("http://192.168.100.142/ServerMenu/upload/" + foto)
                                        .resize(172, 172).into(binding.imageSetUpdate);
                                binding.editDeskripsi.setText(data.getString("deskripsi"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", "code : " + anError);
                        Toast.makeText(UpdateActivity.this, "Gagal get Data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updatepesanan() {
        String UPDATE_PESANAN_URL = "http://192.168.100.142/ServerMenu/update_item.php";
        Log.e("update", UPDATE_PESANAN_URL);
        AndroidNetworking.post(UPDATE_PESANAN_URL)
                .addBodyParameter("id_item", id_item)
                .addBodyParameter("id_kategori", idKat + "")
                .addBodyParameter("nama_item", binding.editNamaItem.getText().toString())
                .addBodyParameter("harga_item", binding.editHargaItem.getText().toString())
                .addBodyParameter("foto_item", getStringImage(decoded))
                .addBodyParameter("deskripsi", binding.editDeskripsi.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.getString("status").equalsIgnoreCase("1")) {
                                Toast.makeText(UpdateActivity.this, "Berhasil Mengubah Pesanan", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UpdateActivity.this, "Gagal Mengubah Pesanan", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(UpdateActivity.this, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);

        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        binding.imageSetUpdate.setImageBitmap(decoded);
    }

    // fungsi resize image
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //mengambil fambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                setToImageView(getResizedBitmap(bitmap, 512));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}