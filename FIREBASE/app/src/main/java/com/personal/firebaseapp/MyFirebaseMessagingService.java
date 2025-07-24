package com.personal.firebaseapp;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "emergency_channel";

    // Constructor vacío requerido por el sistema
    public MyFirebaseMessagingService() {
        super();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Verificar si el mensaje contiene datos
        if (remoteMessage.getData().size() > 0) {
            String coordinates = remoteMessage.getData().get("coordinates");
            String sender = remoteMessage.getData().get("sender");

            Log.d("FCM", "Coordenadas: " + coordinates + " Remitente: " + sender);

            // Enviar notificación para abrir SMSActivity
            enviarNotificacion(coordinates, sender);
        }
    }

    private void enviarNotificacion(String coordinates, String sender) {
        // Crear canal de notificación (Android Oreo en adelante)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Emergency Alerts";
            String description = "Notificaciones de emergencia";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Intent para abrir SMSActivity con las coordenadas
        Intent intent = new Intent(this, SMSActivity.class);
        intent.putExtra("sms_coordinates", coordinates);
        intent.putExtra("sms_sender", sender);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Pendiente para la notificación
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Crear notificación
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)  // Asegúrate de tener un ícono adecuado
                .setContentTitle("¡Nuevo mensaje de emergencia!")
                .setContentText("Toca para ver el mensaje recibido.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Mostrar la notificación
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
