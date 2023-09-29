package br.com.shoebiz.shoeconf_2.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.fragment.TransfMercadoriaFragment;

public class TransfOtTabAdapter extends FragmentPagerAdapter {

    private Context context;

    public TransfOtTabAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence charSequence;

        if (position == 0) {
            charSequence = context.getString(R.string.status_transf_ot_aberto);
        } else {
            charSequence = context.getString(R.string.status_transf_ot_pendente);
        }

        return charSequence;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        if (position == 0) {
            fragment = TransfMercadoriaFragment.newInstance(R.string.status_transf_ot_aberto);
        } else {
            fragment = TransfMercadoriaFragment.newInstance(R.string.status_transf_ot_pendente);
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}