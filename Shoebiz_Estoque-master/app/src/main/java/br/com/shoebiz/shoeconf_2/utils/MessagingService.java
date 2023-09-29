package br.com.shoebiz.shoeconf_2.utils;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.activity.MainActivity;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        FCMUtils.setFcmToken(getApplicationContext(), s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String channelId = getString(R.string.default_notification_channel_id);
        String textoTitulo = "";

        if (remoteMessage.getNotification() != null) {
            RemoteMessage.Notification notification = remoteMessage.getNotification();

            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (remoteMessage.getData().size() > 0) {
                if (remoteMessage.getData().get("data") != null) {
                    textoTitulo = gravaEvento(Objects.requireNonNull(remoteMessage.getData().get("data")));
                }
            }

            Utils.notification(getApplicationContext(), channelId, notification.getBody(), pendingIntent, textoTitulo);
        }
    }

    private String gravaEvento(String data) {
        String textoEvento;

        switch (data) {
            case "TransfMercadoria":
                textoEvento = "Transferencia Mercadoria";
                break;
            case "EntradaMercadoria":
                textoEvento = "Entrada Mercadoria";
                break;
            default:
                textoEvento = null;
                break;
        }

        if (textoEvento != null) {
            Bundle bundle = new Bundle();
            bundle.putString("chave", textoEvento);
            bundle.putString("Servidor", new PrefsUtils(getBaseContext()).isCodLoja());
            FirebaseAnalytics.getInstance(this).logEvent("Troca", bundle);
        }

        return textoEvento;
    }
}