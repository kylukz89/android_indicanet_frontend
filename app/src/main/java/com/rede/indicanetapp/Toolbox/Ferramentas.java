package com.rede.indicanetapp.Toolbox;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.rede.indicanetapp.View.MainActivity;
import com.rede.indicanet.R;
public class Ferramentas {

    public void geraNotificacaoPushPadrao(Context ctx, String titulo, String msg, int idNotification) {
        try {
            Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(700);
        } catch (Exception e) {
            System.err.println(e);
        }

        ////////////////////// VIBRAR ///////////////////////////
        Notification.Builder mBuilder = null;
        mBuilder = new Notification.Builder(ctx)
                .setSmallIcon(R.drawable.ic_notificacao_padrao)
                .setContentTitle(titulo).setStyle(new Notification.BigTextStyle())
                .setContentText(msg);
        Intent resultIntent = new Intent(ctx, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(10, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(idNotification, mBuilder.build());
        //////////////////////////////////////////////////////////
    }

    /**
     * Faz o celular vibrar com base nos milisegundos
     *
     * @author Igor Maximo
     * @date 23/06/2021
     */
    private static void setVibrar(Context context) {
        try {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(VariaveisGlobais.VIBRAR_TOQUE_MILI, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(VariaveisGlobais.VIBRAR_TOQUE_MILI);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}