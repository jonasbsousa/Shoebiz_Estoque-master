package br.com.shoebiz.shoeconf_2.activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Objects;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;

public class ConfiguracoesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alteraTemaSP();
        setContentView(R.layout.activity_configuracoes);
        setUpToolBar();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.flConfiguracoes, new PrefsFragment());
            fragmentTransaction.commit();
        }
    }

    public static class PrefsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            PrefsUtils prefsUtils = new PrefsUtils(getContext());

            EditTextPreference etpCodLoja = findPreference(PrefsUtils.PREF_COD_LOJA);
            Objects.requireNonNull(etpCodLoja).setSummary(prefsUtils.isCodLoja());
            etpCodLoja.setEnabled(false);

            EditTextPreference etpDescFil = findPreference(PrefsUtils.PREF_DESC_FIL);
            Objects.requireNonNull(etpDescFil).setSummary(prefsUtils.isDescFil());
            etpDescFil.setEnabled(false);

            EditTextPreference etpDescEmp = findPreference(PrefsUtils.PREF_DESC_EMP);
            Objects.requireNonNull(etpDescEmp).setSummary(prefsUtils.isDescEmp());
            etpDescEmp.setEnabled(false);

            EditTextPreference etpPerfilAcesso = findPreference(PrefsUtils.PREF_PERFIL_ACESSO);
            Objects.requireNonNull(etpPerfilAcesso).setSummary(prefsUtils.isPerfilAcesso());
            etpPerfilAcesso.setEnabled(false);

            EditTextPreference etpUltimoAcesso = findPreference(PrefsUtils.PREF_ULTIMO_ACESSO);
            Objects.requireNonNull(etpUltimoAcesso).setSummary(prefsUtils.isUltimoAcesso());
            etpUltimoAcesso.setEnabled(false);

            EditTextPreference etpInventCod = findPreference(PrefsUtils.PREF_INVENTARIO_COD);
            Objects.requireNonNull(etpInventCod).setSummary(prefsUtils.isInventCod());
            etpInventCod.setEnabled(false);

            EditTextPreference etpInventOperCod = findPreference(PrefsUtils.PREF_INVENTARIO_OPERADOR_COD);
            Objects.requireNonNull(etpInventOperCod).setSummary(prefsUtils.isInventOperCod());
            etpInventOperCod.setEnabled(false);

            EditTextPreference etpInventOperNome = findPreference(PrefsUtils.PREF_INVENTARIO_OPERADOR_NOME);
            Objects.requireNonNull(etpInventOperNome).setSummary(prefsUtils.isInventOperNome());
            etpInventOperNome.setEnabled(false);

            EditTextPreference etpLibCodigo = findPreference(PrefsUtils.PREF_LIB_COD);
            Objects.requireNonNull(etpLibCodigo).setSummary(prefsUtils.isLibCodigo());
            etpLibCodigo.setEnabled(false);

            EditTextPreference etpLibToken = findPreference(PrefsUtils.PREF_LIB_TOKEN);
            Objects.requireNonNull(etpLibToken).setSummary(prefsUtils.isLibToken().length() > 0 ? prefsUtils.isLibToken().substring(0,5) + "*****" : "");
            etpLibToken.setEnabled(false);

            EditTextPreference etpSolToken = findPreference(PrefsUtils.PREF_SOL_TOKEN);
            Objects.requireNonNull(etpSolToken).setSummary(prefsUtils.isSolToken().length() > 0 ? prefsUtils.isSolToken().substring(0,5) + "*****" : "");
            etpSolToken.setEnabled(false);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference preference = findPreference(key);

            if (preference instanceof EditTextPreference) {
                EditTextPreference etp = (EditTextPreference) preference;
                preference.setSummary(etp.getText());
            }
        }
    }
}