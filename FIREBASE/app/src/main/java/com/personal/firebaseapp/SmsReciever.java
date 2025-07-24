package com.personal.firebaseapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SmsReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                StringBuilder mensaje = new StringBuilder();
                for (Object pdu : pdus) {
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                    mensaje.append(sms.getMessageBody());
                }

                String mensajeTexto = mensaje.toString();

                // 🔥 GUARDAR EN FIREBASE
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> datos = new HashMap<>();
                datos.put("mensaje", mensajeTexto);
                datos.put("fecha", System.currentTimeMillis());
                datos.put("tipo", "RECIBIDO");

                // Extraer lat/lon si hay link de Maps
                try {
                    if (mensajeTexto.contains("maps.google.com/?q=")) {
                        String[] partes = mensajeTexto.split("q=")[1].split(",");
                        datos.put("latitud", Double.parseDouble(partes[0]));
                        datos.put("longitud", Double.parseDouble(partes[1].split("\\s")[0]));
                    }
                } catch (Exception e) {
                    datos.put("latitud", 0);
                    datos.put("longitud", 0);
                }

                db.collection("ubicacion").add(datos);

                // Mostrar mensaje en SMSActivity
                Intent i = new Intent(context, SMSActivity.class);
                i.putExtra("mensaje_sms", mensajeTexto);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
    }
}
