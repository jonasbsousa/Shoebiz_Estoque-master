package br.com.shoebiz.shoeconf_2.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class FCMUtils {

    public static String getFcmToken(Context context) {
        String fcmToken = new PrefsUtils(context).isFcmToken();

        if (fcmToken == null || fcmToken.trim().length() == 0) {
            return null;
        }

        Log.d("shoebiz_token", fcmToken);

        return fcmToken;
    }

    public static void register(final Context context) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();

                Log.d("shoebiz_token", token);
                setFcmToken(context, token);
            } else {
                Log.w("shoebiz_token", "Fetching FCM registration token failed", task.getException());
            }
        });
    }

    public static void setFcmToken(Context context, String fcmToken) {
        new PrefsUtils(context).setFcmToken(fcmToken);
    }
}