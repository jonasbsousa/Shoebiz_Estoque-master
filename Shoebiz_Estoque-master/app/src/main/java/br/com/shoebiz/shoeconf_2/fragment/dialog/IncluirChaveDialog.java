package br.com.shoebiz.shoeconf_2.fragment.dialog;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class IncluirChaveDialog extends DialogFragment {
    private Callback callback;

    private EditText etChaveNota;

    public interface Callback {
        void onIncluiChave(String chave);
    }

    @SuppressLint("CommitTransaction")
    public static void show(FragmentManager fragmentManager, Callback callback) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag("inclui_chave");

        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }

        fragmentTransaction.addToBackStack(null);
        IncluirChaveDialog frag = new IncluirChaveDialog();
        frag.callback = callback;
        frag.show(fragmentManager, "inclui_chave");
    }

    @Override
    public void onResume() {
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        Point size = new Point();
        Display display;

        if (window != null) {
            display = window.getWindowManager().getDefaultDisplay();
            display.getSize(size);

            window.setLayout(size.x, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        }

        super.onResume();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_inclui_chave, container, false);
        view.findViewById(R.id.btFechar).setOnClickListener(onClickFecharDialog());

        etChaveNota = view.findViewById(R.id.etChaveNota);
        etChaveNota.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String chaveNota = etChaveNota.getText().toString().trim();

                if (chaveNota.length() == 44) {
                    if (callback != null) {
                        callback.onIncluiChave(chaveNota);
                    }

                    dismiss();
                } else {
                    Utils.toast(getContext(), "Chave incompleta!");

                    etChaveNota.setText("");
                    etChaveNota.requestFocus();
                }
            }

            return false;
        });

        return view;
    }

    private View.OnClickListener onClickFecharDialog() {
        return v -> dismiss();
    }
}