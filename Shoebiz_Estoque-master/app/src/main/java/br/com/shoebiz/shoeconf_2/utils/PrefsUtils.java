package br.com.shoebiz.shoeconf_2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import br.com.shoebiz.shoeconf_2.model.Liberacao;

public class PrefsUtils {
    private SharedPreferences sharedPreferences;

    private static final String PREF_FCM_TOKEN = "PREF_FCM_TOKEN";
    private static final String PREF_CAD_PROD_DATA = "PREF_CAD_PROD_DATA";
    private static final String PREF_CAD_PROD_HORA = "PREF_CAD_PROD_HORA";
    private static final String PREF_CAD_PROD_OK = "PREF_CAD_PROD_OK";

    public static final String PREF_COD_LOJA = "PREF_COD_LOJA";
    public static final String PREF_DESC_FIL = "PREF_DESC_FIL";
    public static final String PREF_DESC_EMP = "PREF_DESC_EMP";
    public static final String PREF_PERFIL_ACESSO = "PREF_PERFIL_ACESSO";
    public static final String PREF_ULTIMO_ACESSO = "PREF_ULTIMO_ACESSO";

    public static final String PREF_INVENTARIO_COD = "PREF_INVENTARIO_COD";
    public static final String PREF_INVENTARIO_OPERADOR_COD = "PREF_INVENTARIO_OPERADOR_COD";
    public static final String PREF_INVENTARIO_OPERADOR_NOME = "PREF_INVENTARIO_OPERADOR_NOME";

    public static final String PREF_LIB_COD = "PREF_LIB_COD";
    public static final String PREF_LIB_TOKEN = "PREF_LIB_TOKEN";
    public static final String PREF_SOL_TOKEN = "PREF_SOL_TOKEN";

    private static final String PREF_MODO_ONLINE = "PREF_MODO_ONLINE";

    public PrefsUtils(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Liberacao getLiteracao() {
        Liberacao liberacao = new Liberacao();
        liberacao.codigoLoja = isCodLoja();
        liberacao.descricaoEmpresa = isDescEmp();
        liberacao.descricaoFilial = isDescFil();
        liberacao.solicitacaoToken = isSolToken();
        liberacao.liberacaoCodigo = isLibCodigo();
        liberacao.liberacaoToken = isLibToken();
        liberacao.ultimoAcesso = isUltimoAcesso();

        return liberacao;
    }

    public String isCodLoja() {
        return sharedPreferences.getString(PREF_COD_LOJA, "");
    }

    public void setCodLoja(String codLoja) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString(PREF_COD_LOJA, codLoja);
        edit.apply();
    }

    public String isDescFil() {
        return sharedPreferences.getString(PREF_DESC_FIL, "");
    }

    public void setDescFil(String descFil) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString(PREF_DESC_FIL, descFil);
        edit.apply();
    }

    public String isDescEmp() {
        return sharedPreferences.getString(PREF_DESC_EMP, "");
    }

    public void setDescEmp(String descEmp) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString(PREF_DESC_EMP, descEmp);
        edit.apply();
    }

    public String isPerfilAcesso() {
        return sharedPreferences.getString(PREF_PERFIL_ACESSO, "");
    }

    public void setPerfilAcesso(String nomeVendedor) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString(PREF_PERFIL_ACESSO, nomeVendedor);
        edit.apply();
    }

    public String isUltimoAcesso() {
        return sharedPreferences.getString(PREF_ULTIMO_ACESSO, "");
    }

    public void setUltimoAcesso(String dataUltAcesso) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString(PREF_ULTIMO_ACESSO, dataUltAcesso);
        edit.apply();
    }

    public String isCadProdData() {
        return sharedPreferences.getString(PREF_CAD_PROD_DATA, "");
    }

    public void setCadProdOk(String cadProdOk) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString(PREF_CAD_PROD_OK, cadProdOk);
        edit.apply();
    }

    public String isCadProdOk() {
        return sharedPreferences.getString(PREF_CAD_PROD_OK, "");
    }

    public void setCadProdData(String cadProdData) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString(PREF_CAD_PROD_DATA, cadProdData);
        edit.apply();
    }


    public String isCadProdHora() {
        return sharedPreferences.getString(PREF_CAD_PROD_HORA, "");
    }

    public void setCadProdHora(String cadProdHora) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString(PREF_CAD_PROD_HORA, cadProdHora);
        edit.apply();
    }

    public String isInventCod() {
        return sharedPreferences.getString(PREF_INVENTARIO_COD, "");
    }

    public void setInventCod(String inventCod) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString(PREF_INVENTARIO_COD, inventCod);
        edit.apply();
    }

    public String isInventOperCod() {
        return sharedPreferences.getString(PREF_INVENTARIO_OPERADOR_COD, "");
    }

    public void setInventOperCod(String inventOperCod) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString(PREF_INVENTARIO_OPERADOR_COD, inventOperCod);
        edit.apply();
    }

    public String isInventOperNome() {
        return sharedPreferences.getString(PREF_INVENTARIO_OPERADOR_NOME, "");
    }

    public void setInventOperNome(String inventOperNome) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString(PREF_INVENTARIO_OPERADOR_NOME, inventOperNome);
        edit.apply();
    }

    public String isLibCodigo() {
        return sharedPreferences.getString(PREF_LIB_COD, "");
    }

    public void setLibCodigo(String codigoLiberacao) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString(PREF_LIB_COD, codigoLiberacao);
        edit.apply();
    }

    public String isLibToken() {
        return sharedPreferences.getString(PREF_LIB_TOKEN, "");
    }

    public void setLibToken(String tokenLiberacao) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString(PREF_LIB_TOKEN, tokenLiberacao);
        edit.apply();
    }

    public String isSolToken() {
        return sharedPreferences.getString(PREF_SOL_TOKEN, "");
    }

    public void setSolToken(String tokenLiberacao) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString(PREF_SOL_TOKEN, tokenLiberacao);
        edit.apply();
    }

    public String isModoOnline() {
        return sharedPreferences.getString(PREF_MODO_ONLINE, "S");
    }

    public void setModoOnline(String modoOnline) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString(PREF_MODO_ONLINE, modoOnline);
        edit.apply();
    }

    public String isFcmToken() {
        return sharedPreferences.getString(PREF_FCM_TOKEN, "");
    }

    public void setFcmToken(String fcmToken) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString(PREF_FCM_TOKEN, fcmToken);
        edit.apply();
    }
}