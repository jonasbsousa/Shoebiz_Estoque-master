package br.com.shoebiz.shoeconf_2.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.fragment.EntradaMercadoriaFragment;

public class EntradaMercadoriaTabsAdapter extends FragmentPagerAdapter {
    private Context context;

    public EntradaMercadoriaTabsAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence charSequence;

        if (position == 0) {
            charSequence = context.getString(R.string.status_nota_finalizado);
        } else if (position == 1) {
            charSequence = context.getString(R.string.status_nota_pendente);
        } else {
            charSequence = context.getString(R.string.status_nota_a_receber);
        }

        return charSequence;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        if (position == 0) {
            fragment = EntradaMercadoriaFragment.newInstance(R.string.status_nota_finalizado);
        } else if (position == 1) {
            fragment = EntradaMercadoriaFragment.newInstance(R.string.status_nota_pendente);
        } else {
            fragment = EntradaMercadoriaFragment.newInstance(R.string.status_nota_a_receber);
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}