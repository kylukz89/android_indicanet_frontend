package com.rede.indicanetapp.Firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.RemoteMessage;
import com.rede.indicanet.R;
import com.rede.indicanetapp.Toolbox.Ferramentas;
import com.rede.indicanetapp.View.MainActivity;

/**
 * Service called when firebase cloud message received
 *
 * @author Tom Misawa (riversun.org@gmail.com)
 */
public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    Handler h1;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            String channelId = "Default";
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId).setStyle(new NotificationCompat.BigTextStyle()
                    .setBigContentTitle(remoteMessage.getNotification().getTitle())
                    .bigText(remoteMessage.getNotification().getBody()))
                    .setSmallIcon(R.drawable.ic_notificacao_padrao)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody()).setAutoCancel(true).setContentIntent(pendingIntent);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }
            manager.notify((int) Math.random(), builder.build());
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Log.e("FIREBASE", "Message Notification Body: " + Objects.requireNonNull(remoteMessage.getNotification()).getBody());
                sendNotification(remoteMessage);
            }*/
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(RemoteMessage remoteMessage) {
        try {
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            Ferramentas ferramenta = new Ferramentas();
            ferramenta.geraNotificacaoPushPadrao(MyFirebaseMessagingService.this, notification.getTitle(), notification.getBody(), (int) Math.random());
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @Override
    public void onNewToken(String registrationToken) {
        Logg.d("Firebase #onNewToken registrationToken=" + registrationToken);
        startService(new Intent(this, FcmTokenRegistrationService.class));
    }


}
