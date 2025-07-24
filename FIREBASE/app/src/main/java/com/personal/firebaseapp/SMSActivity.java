package com.personal.firebaseapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class SMSActivity extends AppCompatActivity {

    private TextView smsSender;
    private TextView smsLink;
    private ListView listViewHistorial;
    private Button btnRegresar;

    private ArrayList<String> historialMensajes = new ArrayList<>();
    private ArrayAdapter<String> adaptador;

    private static final String PREFS_NAME = "HistorialPrefs";
    private static final String CLAVE_HISTORIAL = "mensajes_historial";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsactivity);

        // Vincular vistas
        smsSender = findViewById(R.id.sms_sender);
        smsLink = findViewById(R.id.sms_link);
        listViewHistorial = findViewById(R.id.listViewHistorial);
        btnRegresar = findViewById(R.id.btnregresa);

        // Recuperar historial guardado
        cargarHistorial();

        // Adaptador con texto negro directamente
        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, historialMensajes) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.BLACK); // ← letras negras fuertes
                return view;
            }
        };
        listViewHistorial.setAdapter(adaptador);

        // Detectar clic en ítem del historial
        listViewHistorial.setOnItemClickListener((parent, view, position, id) -> {
            String mensajeSeleccionado = historialMensajes.get(position);

            // Crear opciones del diálogo
            ArrayList<String> opciones = new ArrayList<>();
            opciones.add("Eliminar");
            if (mensajeSeleccionado.contains("https://maps.google.com")) {
                opciones.add("Ver ubicación");
            }
            opciones.add("Cancelar");

            // Mostrar diálogo
            new AlertDialog.Builder(SMSActivity.this)
                    .setTitle("Opciones")
                    .setItems(opciones.toArray(new CharSequence[0]), (dialog, which) -> {
                        String seleccion = opciones.get(which);

                        switch (seleccion) {
                            case "Eliminar":
                                historialMensajes.remove(position);
                                guardarHistorial();
                                adaptador.notifyDataSetChanged();
                                break;

                            case "Ver ubicación":
                                String url = extraerUrl(mensajeSeleccionado);
                                if (url != null) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    startActivity(intent);
                                }
                                break;

                            case "Cancelar":
                                dialog.dismiss();
                                break;
                        }
                    })
                    .show();
        });

        // Recibir mensaje
        String mensaje = getIntent().getStringExtra("mensaje_sms");

        if (mensaje != null && !mensaje.isEmpty()) {
            smsSender.setText("Mensaje recibido:");
            smsLink.setText(mensaje);

            // Obtener fecha y hora actual
            String fechaHora = DateFormat.format("dd/MM/yyyy HH:mm:ss", new Date()).toString();
            String mensajeConFechaHora = "📅 " + fechaHora + "\n" + mensaje;

            // Añadir al historial
            historialMensajes.add(mensajeConFechaHora);
            guardarHistorial();
            adaptador.notifyDataSetChanged();
        } else {
            smsSender.setText("No se recibió ningún mensaje.");
            smsLink.setText("");
        }

        // Botón REGRESAR
        btnRegresar.setOnClickListener(view -> {
            Intent intent = new Intent(SMSActivity.this, ListaUsuariosActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void guardarHistorial() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> set = new HashSet<>(historialMensajes);
        editor.putStringSet(CLAVE_HISTORIAL, set);
        editor.apply();
    }

    private void cargarHistorial() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(CLAVE_HISTORIAL, new HashSet<>());
        historialMensajes.clear();
        historialMensajes.addAll(set);
    }

    // Extrae la primera URL de Google Maps encontrada en el mensaje
    private String extraerUrl(String texto) {
        String[] partes = texto.split("\\s+");
        for (String parte : partes) {
            if (parte.startsWith("https://maps.google.com")) {
                return parte;
            }
        }
        return null;
    }
}
