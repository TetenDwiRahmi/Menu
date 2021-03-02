package com.mobile.menu;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.menu.databinding.ActivityInsertBinding;
import com.mobile.menu.databinding.ActivityLoginBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    PrefManager prefManager;
    String id;
    boolean isPasswordVisible;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefManager = new PrefManager(this);
        AndroidNetworking.initialize(this);

        //Show Hide Password
        binding.editPass.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (binding.editPass.getRight() - binding.editPass.getCompoundDrawables()[RIGHT].getBounds().width())) {
                        int selection = binding.editPass.getSelectionEnd();
                        if (isPasswordVisible) {
                            // set drawable image
                            binding.editPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.closeeye, 0);
                            // hide Password
                            binding.editPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            isPasswordVisible = false;
                        } else {
                            // set drawable image
                            binding.editPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.open_eye, 0);
                            // show Password
                            binding.editPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            isPasswordVisible = true;
                        }
                        binding.editPass.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });


        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegistrasiActivity.class);
                startActivity(i);
            }
        });

        if (prefManager.getLoginStatus()) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }


    private void login() {
        String LOGIN_URL = "http://192.168.100.142/ServerMenu/login.php";
        AndroidNetworking.post(LOGIN_URL)
                .addBodyParameter("email", binding.editEmail.getText().toString())
                .addBodyParameter("password", binding.editPass.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equalsIgnoreCase("1")) {
                                Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();

                                JSONArray data = response.getJSONArray("data");
                                JSONObject user = data.getJSONObject(0);
                                String id = user.getString("id");
                                prefManager.setIdUser(id);
                                prefManager.setLoginStatus(true);
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);

//                                ID di set statis
//                                prefManager.setLoginStatus(true);
//                                prefManager.setIdUser("1");
//                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                                startActivity(i);

                            } else if (response.getString("status").equalsIgnoreCase("2")) {
                                Toast.makeText(LoginActivity.this, "Email atau Password Invalid", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Data Harus Diisi", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(LoginActivity.this, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}