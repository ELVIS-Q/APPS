package com.personal.firebaseapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnIngresar, btnRegistrar;
    private FirebaseAuth firebaseAuth;
    private ImageView imageView;

    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.et1);
        etPassword = findViewById(R.id.et2);
        btnIngresar = findViewById(R.id.btningresa);
        btnRegistrar = findViewById(R.id.btnregistraruser);
        imageView = findViewById(R.id.imageView2);
        imageView.setImageResource(R.drawable.fer); // tu imagen

        btnIngresar.setOnClickListener(v -> ingresar());
        btnRegistrar.setOnClickListener(v -> irARegistro());

        solicitarPermisos();
    }

    private void solicitarPermisos() {
        List<String> permisos = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
            permisos.add(Manifest.permission.RECEIVE_SMS);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
            permisos.add(Manifest.permission.READ_SMS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            permisos.add(Manifest.permission.POST_NOTIFICATIONS);

        if (!permisos.isEmpty()) {
            ActivityCompat.requestPermissions(this, permisos.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (results[i] != PackageManager.PERMISSION_GRANTED)
                    Log.e("PERMISOS", "Denegado: " + permissions[i]);
            }
        }
    }

    private void ingresar() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        abrirListaUsuarios();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                        Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void abrirListaUsuarios() {
        Intent intent = new Intent(MainActivity.this, ListaUsuariosActivity.class);
        startActivity(intent);
        finish();
    }

    private void irARegistro() {
        Intent intent = new Intent(MainActivity.this, RegistrarUsuarioActivity.class);
        startActivity(intent);
    }
}
