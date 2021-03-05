package com.example.servermonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private TextView banner;
    private EditText etNama,etUsia,etEmail,etPassword;
    private Button daftar;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        banner = findViewById(R.id.banner);
        daftar = findViewById(R.id.registButton);
        etNama = findViewById(R.id.fullName);
        etUsia = findViewById(R.id.age);
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);

        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daftar();
            }
        });

        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignIn();
            }
        });

    }
    public void openSignIn(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void daftar(){
        String nama = etNama.getText().toString().trim();
        String usia = etUsia.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (nama.isEmpty()){
            etNama.setError("Nama harus diisi!");
            etNama.requestFocus();
            return;
        }if(usia.isEmpty()){
            etUsia.setError("Usia harus diisi!");
            etUsia.requestFocus();
            return;
        }if(email.isEmpty()){
            etEmail.setError("Email harus diisi!");
            etEmail.requestFocus();
            return;
        }if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError(("Masukkan format email yang benar!"));
            etEmail.requestFocus();
            return;
        }if(password.isEmpty()){
            etPassword.setError("Password harus diisi!");
            etPassword.requestFocus();
            return;
        }if(password.length()<6){
            etPassword.setError("Minimal harus 6 karakter!");
            etPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(nama, usia, email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Berhasil Mendaftar", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);

                                        //Redirected to login activity
                                    }else{
                                        Toast.makeText(RegisterActivity.this, "Gagal Mendaftar! Coba Lagi!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RegisterActivity.this, "Gagal Mendaftar! Coba Lagi!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}