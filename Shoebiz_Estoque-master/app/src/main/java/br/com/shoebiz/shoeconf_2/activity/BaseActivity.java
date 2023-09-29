package br.com.shoebiz.shoeconf_2.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.fragment.ColetorFragment;
import br.com.shoebiz.shoeconf_2.model.TransfStatus;
import br.com.shoebiz.shoeconf_2.service.TransfMercadoriaService;
import br.com.shoebiz.shoeconf_2.fragment.AlteracaoPrecoFragment;
import br.com.shoebiz.shoeconf_2.fragment.ColetaGtinFragment;
import br.com.shoebiz.shoeconf_2.fragment.InventarioMarcaFragment;
import br.com.shoebiz.shoeconf_2.fragment.ConsultaProdutoFragment;
import br.com.shoebiz.shoeconf_2.fragment.EntradaMercadoriaTabFragment;
import br.com.shoebiz.shoeconf_2.fragment.InventarioFragment;
import br.com.shoebiz.shoeconf_2.fragment.TransfMercadoriaTabFragment;
import br.com.shoebiz.shoeconf_2.fragment.ValidaDanfeFragment;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.Utils;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    public FirebaseAnalytics mFirebaseAnalytics;

    private TextView countTransf;

    protected DrawerLayout drawerLayout;
    protected Toolbar toolbar;
    protected NavigationView navigationView;
    protected ActionBar actionBar;

    protected Context getContext()   {
        return this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utils.hideKeyboard(true, this);

            if (drawerLayout != null) {
                openDrawer();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        Utils.closeProgress();
        super.onDestroy();
    }

    protected void alteraTemaFullScreen() {
        setTheme(R.style.FullScreenSb);
    }

    protected void alteraTema() {
        setTheme(R.style.AppTheme_Shoebiz_NoActionBar_NavDrawer);
    }

    protected void alteraStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(getContext(),R.color.colorSbPrimaryDark));
    }

    protected void alteraTemaSP() {
        setTheme(R.style.AppTheme_Shoebiz_NoActionBar);
    }

    protected void setUpToolBar() {
        toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    protected void setupNavDrawer() {
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        if (navigationView != null && drawerLayout != null) {
            navigationView.setNavigationItemSelectedListener(menuItem -> {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                onNavDrawerItemSelected(menuItem);
                return true;
            });

            countTransf = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_drawer_trans_mercadoria));
            countTransf.setTextColor(getResources().getColor(R.color.red_i));
            countTransf.setGravity(Gravity.CENTER|Gravity.CENTER_VERTICAL);
            countTransf.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void onNavDrawerItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_drawer_valida_danfe:
                replaceFragment(new ValidaDanfeFragment());
                break;
            case R.id.nav_drawer_entrada_mercadoria:
                replaceFragment(new EntradaMercadoriaTabFragment());
                break;
            case R.id.nav_drawer_alteracao_preco:
                replaceFragment(new AlteracaoPrecoFragment());
                break;
            case R.id.nav_drawer_consult_prod:
                replaceFragment(new ConsultaProdutoFragment());
                break;
            case R.id.nav_drawer_trans_mercadoria:
                replaceFragment(new TransfMercadoriaTabFragment());
                break;
            case R.id.nav_drawer_inventario:
                replaceFragment(new InventarioFragment());
                break;
            case R.id.nav_drawer_inventario_marca:
                replaceFragment(new InventarioMarcaFragment());
                break;
            case R.id.nav_drawer_coletor:
                replaceFragment(new ColetorFragment());
                break;
            case R.id.nav_drawer_coleta_gtin:
                replaceFragment(new ColetaGtinFragment());
                break;
            case R.id.nav_drawer_configuracoes:
                startActivity(new Intent(this, ConfiguracoesActivity.class));
                break;
            case R.id.nav_drawer_sair:
                sairApp();
                break;
        }
    }

    protected void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, "TAG").commit();
    }

    protected void openDrawer() {
        if (countTransf != null) {
            new TransfMercadoriaService(getContext(), "", "").consultaStatus(new TransfMercadoriaService.ConsultaStatusCallback() {

                @SuppressLint("StringFormatMatches")
                @Override
                public void onSuccess(TransfStatus transfStatus) {
                    if (transfStatus != null) {
                        int quantPend = transfStatus.quantOtAberta + transfStatus.quantNotaPendente;

                        if (quantPend > 0) {
                            countTransf.setText(getString(R.string.novas_ot, String.valueOf(quantPend), quantPend > 1 ? "s" : ""));
                            countTransf.setVisibility(View.VISIBLE);
                        } else {
                            countTransf.setVisibility(View.GONE);
                        }
                    } else {
                        countTransf.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(String erro) {
                    countTransf.setText("");
                }
            });
        }

        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    /*protected void closeDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }*/

    public void sairApp() {
        PrefsUtils prefsUtils = new PrefsUtils(getApplicationContext());

        if (prefsUtils.isModoOnline().equals("S")) {
            prefsUtils.setUltimoAcesso("");
        }

        finish();
    }
}