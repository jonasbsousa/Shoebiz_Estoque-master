package br.com.shoebiz.shoeconf_2.fragment;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.analytics.FirebaseAnalytics;

import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class BaseFragment extends Fragment {
    public FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @Override
    public void onPause() {
        super.onPause();

        Utils.closeProgress();
    }

    public boolean activityIsAlive() {
        Activity activity = getActivity();
        return activity != null && isAdded() && !activity.isFinishing();
    }

    public Context getContext() {
        return getActivity();
    }

    void bloqueiaApp() {
        PrefsUtils prefsUtils = new PrefsUtils(getContext());
        prefsUtils.setUltimoAcesso("");

        requireActivity().finish();
    }
}