package com.rede.indicanetapp.Firebase;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Service for sending this device's registrationToken to your server to remember it.
 *
 * @author Tom Misawa (riversun.org@gmail.com)
 */
public class FcmTokenRegistrationService extends IntentService {

    public FcmTokenRegistrationService() {
        super("FcmTokenRegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            //////////////////////////////// FIREBASE TOKEN ////////////////////////////////
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    String token = task.getResult();
                    Logg.d("Firebase registrationToken=" + token);
                }
            });
        } catch (Exception e) {

        }
    }


}
