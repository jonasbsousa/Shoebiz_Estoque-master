package br.com.shoebiz.shoeconf_2.fragment.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class AboutDialog extends DialogFragment {

    public static void showAbout(FragmentManager fm) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog_about");

        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);
        new AboutDialog().show(ft, "dialog_about");
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SpannableStringBuilder aboutBody = new SpannableStringBuilder();
        String versionName = Utils.getVersionName(requireActivity());
        aboutBody.append(Html.fromHtml(getString(R.string.about_dialog_text, versionName)));

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        TextView view = (TextView) inflater.inflate(R.layout.dialog_about, null);
        view.setText(aboutBody);
        view.setMovementMethod(new LinkMovementMethod());

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.about_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.ok,
                        (dialog, whichButton) -> dialog.dismiss()
                ).create();
    }
}