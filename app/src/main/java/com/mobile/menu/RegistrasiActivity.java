package com.mobile.menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.menu.databinding.ActivityLoginBinding;
import com.mobile.menu.databinding.ActivityRegistrasiBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RegistrasiActivity extends AppCompatActivity {
    private ActivityRegistrasiBinding binding;
    Bitmap bitmap, decoded;
    int PICK_IMAGE_REQUEST = 1;
    int bitmap_size = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrasiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrasi();
            }
        });
        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegistrasiActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
        binding.imageRegistrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
    }

    private void registrasi() {
        String INSERT_REGIS_URL = "http://192.168.100.142/ServerMenu/register.php";
        AndroidNetworking.post(INSERT_REGIS_URL)
                .addBodyParameter("username", binding.editUsername.getText().toString())
                .addBodyParameter("email", binding.editEmail.getText().toString())
                .addBodyParameter("password", binding.editPassword.getText().toString())
                .addBodyParameter("foto_user", getStringImage(decoded))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (binding.editPassword.getText().toString().equals(binding.editKonfPassword.getText().toString())) {
                                if (response.getString("status").equalsIgnoreCase("2")) {
                                    Toast.makeText(RegistrasiActivity.this, "Berhasil Registrasi", Toast.LENGTH_SHORT).show();
                                } else if (response.getString("status").equalsIgnoreCase("1")) {
                                    Toast.makeText(RegistrasiActivity.this, "Email Sudah Terdaftar", Toast.LENGTH_SHORT).show();
                                } else if (response.getString("status").equalsIgnoreCase("3")) {
                                    Toast.makeText(RegistrasiActivity.this, "Registrasi Gagal", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegistrasiActivity.this, "Data Harus Diisi", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(RegistrasiActivity.this, "Password Tidak Sama", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(RegistrasiActivity.this, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
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
        binding.imageRegistrasi.setImageBitmap(decoded);
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