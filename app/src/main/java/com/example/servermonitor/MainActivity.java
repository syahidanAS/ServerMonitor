package com.example.servermonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {
    private TextView daftar;
    private EditText etEmail, etPassword;
    private Button btSignIn;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        daftar = findViewById(R.id.register);
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        btSignIn = findViewById(R.id.signIn);
        mAuth = FirebaseAuth.getInstance();

        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                masuk();
            }
        });
        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegis();
            }
        });
    }
    public void openRegis(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void masuk(){
       String email = etEmail.getText().toString().trim();
       String password = etPassword.getText().toString().trim();

       if (email.isEmpty()){
           etEmail.setError("Email harus diisi");
           etEmail.requestFocus();
           return;
       }
        if (password.isEmpty()){
            etPassword.setError("Password harus diisi");
            etPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this, Home.class));
                    progressBar.setVisibility(View.GONE);
                }else{
                    Toast.makeText(MainActivity.this, "Gagal Login, pastikan anda sudah terdaftar!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}