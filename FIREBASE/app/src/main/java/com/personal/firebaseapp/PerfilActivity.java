package com.personal.firebaseapp;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    private EditText ced, nom, cel, dir;
    private Button btnReg, btnBack;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        ced = findViewById(R.id.cedula);
        nom = findViewById(R.id.nombres);
        cel = findViewById(R.id.celular);
        dir = findViewById(R.id.direccion);
        btnReg = findViewById(R.id.btnregistrar);
        btnBack = findViewById(R.id.btnRegresar);

        btnReg.setOnClickListener(v -> registrarFamiliar());
        btnBack.setOnClickListener(v -> finish());
    }

    private void registrarFamiliar() {
        String c = ced.getText().toString().trim();
        String n = nom.getText().toString().trim();
        String ce = cel.getText().toString().trim();
        String d = dir.getText().toString().trim();

        if (c.isEmpty() || n.isEmpty() || ce.isEmpty() || d.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        String correo = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : null;

        if (uid == null || correo == null) {
            Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String registro = "Cédula: " + c + ", Nombre: " + n + ", Celular: " + ce + ", Dirección: " + d;

        Map<String, Object> data = new HashMap<>();
        data.put("Correo", correo);
        data.put("Ubicación", "");
        data.put("Registro", FieldValue.arrayUnion(registro)); // GUARDAR EN "Registro" COMO ARRAY

        firestore.collection("Usuarios")
                .document(uid)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Familiar registrado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
