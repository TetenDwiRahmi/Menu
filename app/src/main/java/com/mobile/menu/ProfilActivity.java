package com.mobile.menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.menu.databinding.ActivityKategoriBinding;
import com.mobile.menu.databinding.ActivityProfilBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfilActivity extends AppCompatActivity {
    private ActivityProfilBinding binding;
    ImageView imageProfil;
    PrefManager prefManager;
    Bitmap bitmap, decoded;
    int PICK_IMAGE_REQUEST = 1;
    int bitmap_size = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);

        imageProfil = findViewById(R.id.imageProfil);

        prefManager = new PrefManager(this);

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfilActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        binding.imageProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        binding.btnUpdateProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateprofil();
            }
        });

        Log.d("id User", " id : "+ prefManager.getIdUser());
        dataprofil();
    }

    private void dataprofil() {
        String DATA_PESANAN = "http://192.168.100.142/ServerMenu/detailprofil.php";
        AndroidNetworking.post(DATA_PESANAN)
                .addBodyParameter("id", prefManager.getIdUser())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d("data ", " code :" +response);
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject data = response.getJSONObject(i);
                                binding.editUsername.setText(data.getString("username"));
                                String foto = data.getString("foto_user");
                                Picasso.get().load("http://192.168.100.142/ServerMenu/upload/" + foto).into(binding.imageProfil);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", "code : " + anError);
                        Toast.makeText(ProfilActivity.this, "Gagal get Data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateprofil() {
        String UPDATE_PESANAN_URL = "http://192.168.100.142/ServerMenu/update_profile.php";
        //Log.e("update", UPDATE_PESANAN_URL);
        AndroidNetworking.post(UPDATE_PESANAN_URL)
                .addBodyParameter("id", prefManager.getIdUser())
                .addBodyParameter("username", binding.editUsername.getText().toString())
                .addBodyParameter("password", binding.editPassword.getText().toString())
                .addBodyParameter("foto_user", getStringImage(decoded))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.getString("status").equalsIgnoreCase("1")) {
                                Toast.makeText(ProfilActivity.this, "Berhasil Mengubah Profil", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfilActivity.this, "Gagal Mengubah Profil", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(ProfilActivity.this, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
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
        binding.imageProfil.setImageBitmap(decoded);
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
                //mengambil gambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                setToImageView(getResizedBitmap(bitmap, 512));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}