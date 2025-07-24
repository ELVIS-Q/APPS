package com.personal.firebaseapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class UsuarioAdapter extends ArrayAdapter<UsuarioAdapter.Familiar> {

    private final Context context;
    private final List<Familiar> Usuarios;

    public UsuarioAdapter(Context context, List<Familiar> familiares) {
        super(context, 0, familiares);
        this.context = context;
        this.Usuarios  = familiares;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = view.findViewById(android.R.id.text1);
        Familiar f = Usuarios.get(position);

        String texto = "Cédula: " + f.cedula + "\n" +
                "Nombre: " + f.nombres + "\n" +
                "Celular: " + f.celular + "\n" +
                "Dirección: " + f.direccion;

        textView.setText(texto);
        textView.setTextSize(16f);
        textView.setTextColor(0xFF000000); // negro

        return view;
    }

    // Clase Familiar interna para que funcione todo en este mismo archivo
    public static class Familiar {
        public String cedula;
        public String nombres;
        public String celular;
        public String direccion;

        public Familiar() {
            // Constructor requerido por Firestore
        }

        public Familiar(String cedula, String nombres, String celular, String direccion) {
            this.cedula = cedula;
            this.nombres = nombres;
            this.celular = celular;
            this.direccion = direccion;
        }
    }
}
