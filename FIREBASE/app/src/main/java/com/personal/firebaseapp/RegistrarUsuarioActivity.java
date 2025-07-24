package com.personal.firebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrarUsuarioActivity extends AppCompatActivity {

    private EditText etEmail, etPass;
    private Button btnReg;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_registrar_usuario);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.email);
        etPass = findViewById(R.id.password);
        btnReg = findViewById(R.id.btnRegistrar);

        btnReg.setOnClickListener(v -> {
            String e = etEmail.getText().toString().trim();
            String p = etPass.getText().toString().trim();

            if (e.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(e, p)
                    .addOnSuccessListener(res -> {
                        String uid = res.getUser().getUid();

                        // Crear mapa de datos para Firestore
                        Map<String, Object> usuario = new HashMap<>();
                        usuario.put("userId", uid);
                        usuario.put("email", e);

                        // Guardar en Firestore en la colección "usuarios"
                        firestore.collection("Usuarios")
                                .document(uid)
                                .set(usuario)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, MainActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(error -> {
                                    Toast.makeText(this, "Error al guardar en Firestore: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    })
                    .addOnFailureListener(err -> {
                        if (err instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, err.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}