package br.com.shoebiz.shoeconf_2.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import br.com.shoebiz.shoeconf_2.BuildConfig;
import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.model.APIError;
import retrofit2.Response;

public class Utils {
    public static final int APP_NAME = R.string.app_name;
    public static final String CODIGO = "607149fcba4578ead297f4c23762c0896416444fb0751edbce3707dcb93d3565";

    public static final int TIME_OUT_RETROFIT_READ = 120;
    public static final int TIME_OUT_RETROFIT_CONNECT = 15;

    public static final int SPLASH_DISPLAY_LENGTH = 500;
    public static final int TIME_OUT_WS = 5000;
    public static final int TIMEOUT = 120;

    public static final String URLWSSRVLOJ = BuildConfig.WS_URL_LOJ;
    public static final String DIRWSSRVLOJ = BuildConfig.WS_DIR_LOJ;

    public static final int SHOEBIZ = R.string.SHOEBIZ;
    public static final int SHOEBIZ_COMERCIO = R.string.SHOEBIZ_COMERCIO;

    public static final int OP_TRANSF_OT = R.string.op_transf_ot;
    public static final int OP_TRANSF_NOTA = R.string.op_transf_nota;

    private static final String URLPROTHEUSESC = BuildConfig.URL_PROTHEUS_ESC;

    private static final String URLRESTESC_1 = BuildConfig.REST_URL_ESC_1;
    private static final String URLRESTESC_2 = BuildConfig.REST_URL_ESC_2;
    private static final String URLRESTESC_3 = BuildConfig.REST_URL_ESC_3;

    private static ProgressDialog progress;

    public static String getUrlRestEsc_1() {
        return URLRESTESC_1 + BuildConfig.REST_SECAO_SB;
    }

    public static String getUrlRestEsc_2() {
        return URLRESTESC_2 + BuildConfig.REST_SECAO_SB;
    }

    public static String getUrlRestEsc_3() {
        return URLRESTESC_3 + BuildConfig.REST_SECAO_SB;
    }

    public static String getUrlFotoProduto() {
        return URLPROTHEUSESC + "/ws_shoebiz/fotos/";
    }

    public static String getUrlArquivoProdutos() {
        return URLPROTHEUSESC + "/ws_shoebiz/";
    }

    public static String maskMoney() {
        return "###,###,###,###,###.00";
    }

    public static String formatoData() {
        return "dd/MM/yyyy";
    }

    public static String formatoDataHora() {
        return "dd/MM/yyyy HH:mm:ss";
    }

    /*public static String formatoDataWs(){
        return "yyyyMMdd";
    }*/

    public static String getVersionName(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        String packageName = activity.getPackageName();
        String versionName;

        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "N/A";
        }

        return versionName;
    }

    public static boolean isAppAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list = mgr.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static String formatoDataSql() {
        return "yyyyMMdd";
    }

    /*public static String formatoDataHoraSQL(){
        return "yyyy-MM-dd HH:mm:ss";
    }*/

    private static String formatoHoraSql() {
        return "HHmmss";
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDataAtual() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDataAtualSQL() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDataAtualSQL_2() {
        return new SimpleDateFormat(formatoDataSql()).format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getHoraAtualSQL() {
        return new SimpleDateFormat(formatoHoraSql()).format(new Date());
    }

    @SuppressLint("HardwareIds")
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String descTipoAcesso(Context context, String tpAcesso) {
        if (tpAcesso.equals("E")) {
            return context.getString(R.string.escritorio);
        } else {
            return context.getString(R.string.loja);
        }
    }

    public static boolean getFormaColeta(Context context, String formaColeta) {
        boolean ret = false;

        if (context != null) {
            if (formaColeta.equals(context.getString(R.string.forma_coleta_cod_shoebiz)) || formaColeta.equals(context.getString(R.string.forma_coleta_gtin)) ||
                    formaColeta.equals(context.getString(R.string.forma_coleta_hibrido))) {
                ret = true;
            }
        }

        return ret;
    }

    @SuppressLint("NonConstantResourceId")
    public static String getDescOperacaoColetor(Context context, int status) {
        String descOperacao = "";

        switch (status){
           case R.string.coletor_status_defeito_analise:
               descOperacao = context.getString(R.string.coletor_defeito_analise);
               break;
           case R.string.coletor_status_gato_pequeimado:
               descOperacao = context.getString(R.string.coletor_gato_pequeimado);
               break;
           case R.string.coletor_status_contagem:
               descOperacao = context.getString(R.string.coletor_contagem);
               break;
        }

        return descOperacao;
    }

    @SuppressLint("NonConstantResourceId")
    public static String getStatusNota(int status) {
        String statusRet = "";

        switch (status){
            case R.string.status_nota_finalizado:
                statusRet = "F";
                break;
            case R.string.status_nota_pendente:
                statusRet = "P";
                break;
            case R.string.status_nota_a_receber:
                statusRet = "R";
        }

        return statusRet;
    }

    public static String getTipoOpTransf(int status) {
        String opRet = "";

        if (status == R.string.status_transf_ot_aberto || status == R.string.status_transf_ot_pendente) {
            opRet = String.valueOf(OP_TRANSF_OT);
        } else if (status == R.string.status_transf_pendente || status == R.string.status_transf_recebido) {
            opRet = String.valueOf(OP_TRANSF_NOTA);
        }

        return opRet;
    }

    public static String getTipoOpTransf(String status) {
        String opRet = "";

        if (status.equals(getStatusOt(R.string.status_transf_ot_aberto)) || status.equals(getStatusOt(R.string.status_transf_ot_pendente))) {
            opRet = String.valueOf(OP_TRANSF_OT);
        } else if (status.equals(getStatusTransf(R.string.status_transf_pendente)) || status.equals(getStatusTransf(R.string.status_transf_recebido))) {
            opRet = String.valueOf(OP_TRANSF_NOTA);
        }

        return opRet;
    }

    @SuppressLint("NonConstantResourceId")
    public static String getStatusOt(int status) {
        String statusRet = "";

        switch (status) {
            case R.string.status_transf_ot_aberto:
                statusRet = "A";
                break;
            case R.string.status_transf_ot_pendente:
                statusRet = "O";
                break;
        }

        return statusRet;
    }

    @SuppressLint("NonConstantResourceId")
    public static String getStatusTransf(int status) {
        String statusRet = "";

        switch (status) {
            case R.string.status_transf_pendente:
                statusRet = "P";
                break;
            case R.string.status_transf_recebido:
                statusRet = "R";
                break;
        }

        return statusRet;
    }

    @SuppressLint({"DefaultLocale", "HardwareIds"})
    public static String getNetworkInfo(Context context, String info) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = Objects.requireNonNull(wifiManager).getConnectionInfo();
        String retorno = null;

        if (info.equals("IP")) {
            int ipAddress = wifiInfo.getIpAddress();

            retorno = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
        } else if (info.equals("MAC")) {
            retorno = wifiInfo.getMacAddress();
        }

        return retorno;
    }

    public static String apiErroMessage(Response response) throws IOException {
        String retorno = "";

        try {
            Gson gson = new GsonBuilder().create();
            APIError apiError = gson.fromJson(Objects.requireNonNull(response.errorBody()).string(), APIError.class);

            if (apiError.errorMessage() != null && !apiError.errorMessage().isEmpty()) {
                retorno += apiError.errorMessage();
            } else if (apiError.message() != null && !apiError.message().isEmpty()) {
                retorno += apiError.message();
            }
        } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
            retorno = e.getMessage();
        }

        return retorno;
    }

    public static String diasParado(Date data) {
        Calendar dataEmissaoNota = Calendar.getInstance();
        dataEmissaoNota.setTime(data);

        long msDiff = Calendar.getInstance().getTimeInMillis() - dataEmissaoNota.getTimeInMillis();
        long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);

        return String.valueOf(daysDiff);
    }

    public static void hideKeyboard(boolean val, Activity activity) {
        View view;
        view = activity.getWindow().getCurrentFocus();

        if (val && view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(inputMethodManager).
                    hideSoftInputFromWindow(Objects.requireNonNull(view).getWindowToken(), 0);
        }
    }

    public static void toast(Context context, String text) {
        if (context != null && text != null && !text.trim().isEmpty()) {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        }
    }

    public static void alertDialog(Context context, String titulo, String mensagem) {
        if (context != null) {
            new AlertDialog.Builder(context)
                    .setTitle(titulo)
                    .setMessage(mensagem)
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss()).show();
        }
    }

    public static void notification(Context context, String channelId, String mensagem, PendingIntent pendingIntent, String textoTitulo) {
        Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        Notification notificacao = builder
                .setContentTitle(textoTitulo)
                .setContentText(mensagem)
                .setSmallIcon(R.drawable.logo_notification)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(defaultUri)
                .setVibrate(new long[] { 1000, 1000 })
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify(0, notificacao);
        }
    }

    public static void showProgress(Context context, String titulo, String mensagem) {
        closeProgress();

        if (context != null && !((Activity) context).isFinishing()) {
            progress = new ProgressDialog(context);
            progress.setTitle(titulo);
            progress.setMessage(mensagem);
            progress.show();
        }
    }

    public static void showProgressBar(Context context, String titulo, String mensagem, int quantidade) {
        if (context != null && !((Activity) context).isFinishing() && progress == null ) {
            progress = new ProgressDialog(context);
            progress.setTitle(titulo);
            progress.setMax(quantidade);
            progress.setMessage(mensagem);
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.show();
        }
    }

    public static void updateProgressBar(int idx) {
        if (progress != null && progress.isShowing()) {
            progress.setProgress(idx);
        }
    }

    public static void closeProgress() {
        if (progress != null && progress.isShowing()) {
            progress.cancel();
        }

        progress = null;
    }
}