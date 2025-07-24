package com.personal.firebaseapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityMap extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String link = getIntent().getStringExtra("sms_link");
        if (link != null && link.startsWith("https://maps.google.com")) {
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(mapIntent);
        }

        finish(); // Cierra la actividad después de redirigir
    }
}
