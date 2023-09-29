package br.com.shoebiz.shoeconf_2.activity;

import android.os.Bundle;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.fragment.ConsultaProdutoFragment;

public class AlteracaoPrecoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        alteraTema();
        alteraStatusBarColor();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alteracao_preco);
        setUpToolBar();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.titulo_alteracao_preco);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            ConsultaProdutoFragment frag = new ConsultaProdutoFragment();
            frag.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction().add(R.id.flConsultaProduto, frag).commit();
        }
    }
}