package com.personal.firebaseapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class ListaUsuariosActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<UsuarioAdapter.Familiar> lista = new ArrayList<>();
    private UsuarioAdapter adapter;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_usuarios);

        Toolbar tb = findViewById(R.id.toolbar1);
        setSupportActionBar(tb);

        listView = findViewById(R.id.listViewUsuarios);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        adapter = new UsuarioAdapter(this, lista);
        listView.setAdapter(adapter);

        cargarRegistroFamiliar();

        // Al hacer clic en un item, mostrar el diálogo para eliminar ese registro específico
        listView.setOnItemClickListener((parent, view, position, id) -> mostrarDialogoEliminar(position));
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarRegistroFamiliar(); // Recargar cuando se regrese a esta pantalla
    }

    private void cargarRegistroFamiliar() {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (uid == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("Usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    lista.clear();
                    if (doc.exists() && doc.contains("Registro")) {
                        List<String> registros = (List<String>) doc.get("Registro");

                        if (registros != null) {
                            for (String registro : registros) {
                                UsuarioAdapter.Familiar familiar = new UsuarioAdapter.Familiar();
                                String[] partes = registro.split(", ");

                                for (String parte : partes) {
                                    if (parte.startsWith("Cédula: ")) {
                                        familiar.cedula = parte.replace("Cédula: ", "");
                                    } else if (parte.startsWith("Nombre: ")) {
                                        familiar.nombres = parte.replace("Nombre: ", "");
                                    } else if (parte.startsWith("Celular: ")) {
                                        familiar.celular = parte.replace("Celular: ", "");
                                    } else if (parte.startsWith("Dirección: ")) {
                                        familiar.direccion = parte.replace("Dirección: ", "");
                                    }
                                }

                                lista.add(familiar);
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void mostrarDialogoEliminar(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar registro")
                .setMessage("¿Deseas eliminar este familiar?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarRegistro(position))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarRegistro(int position) {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (uid == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        UsuarioAdapter.Familiar familiar = lista.get(position);
        String registro = "Cédula: " + familiar.cedula + ", Nombre: " + familiar.nombres +
                ", Celular: " + familiar.celular + ", Dirección: " + familiar.direccion;

        firestore.collection("Usuarios")
                .document(uid)
                .update("Registro", FieldValue.arrayRemove(registro))
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Familiar eliminado", Toast.LENGTH_SHORT).show();
                    lista.remove(position);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ver_sms) {
            startActivity(new Intent(this, SMSActivity.class));
        } else if (id == R.id.registrar) {
            startActivity(new Intent(this, PerfilActivity.class));
        } else if (id == R.id.cerrar_sesion) {
            auth.signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
