package br.com.shoebiz.shoeconf_2.activity;

import android.os.Bundle;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.fragment.ValidaDanfeFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        alteraTema();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolBar();
        setupNavDrawer();

        if (savedInstanceState == null) {
            replaceFragment(new ValidaDanfeFragment());
        }
    }
}