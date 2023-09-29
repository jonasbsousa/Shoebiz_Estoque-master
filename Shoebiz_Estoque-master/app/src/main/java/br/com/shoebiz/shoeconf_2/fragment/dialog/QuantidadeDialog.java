package br.com.shoebiz.shoeconf_2.fragment.dialog;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
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

public class QuantidadeDialog extends DialogFragment {
    private CallBack callback;

    public interface CallBack {
        void onQuantidade(int quantidade);
    }

    public static void show(FragmentManager fragmentManager, int quant, QuantidadeDialog.CallBack callback) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag("quantidade");

        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }

        Bundle args = new Bundle();
        args.putInt("quantidade", quant);

        fragmentTransaction.addToBackStack(null);
        QuantidadeDialog frag = new QuantidadeDialog();
        frag.callback = callback;
        frag.setArguments(args);
        frag.show(fragmentManager, "quantidade");
    }

    @Override
    public void onResume() {
        Window window = Objects.requireNonNull(getDialog()).getWindow();

        if (window != null) {
            Point size = new Point();

            Display display = window.getWindowManager().getDefaultDisplay();
            display.getSize(size);

            window.setLayout(size.x, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        }

        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adapter_quantidade, container, false);

        final EditText etQuantidade = view.findViewById(R.id.etQuantidade);
        etQuantidade.setText(String.valueOf(requireArguments().getInt("quantidade")));

        view.findViewById(R.id.ibMais).setOnClickListener(v -> {
            int quantidade = 0;

            if (!String.valueOf(etQuantidade.getText()).trim().isEmpty()) {
                try {
                    quantidade = Integer.parseInt(String.valueOf(etQuantidade.getText()));
                } catch (NumberFormatException e) {
                    quantidade = 0;
                }
            }

            quantidade++;

            etQuantidade.setText(String.valueOf(quantidade));
        });

        view.findViewById(R.id.ibMenos).setOnClickListener(v -> {
            int quantidade = 0;

            if (!String.valueOf(etQuantidade.getText()).trim().isEmpty()) {
                try {
                    quantidade = Integer.parseInt(String.valueOf(etQuantidade.getText()));
                } catch (NumberFormatException e) {
                    quantidade = 0;
                }
            }

            quantidade = quantidade <= 1 ? 1 : --quantidade;

            etQuantidade.setText(String.valueOf(quantidade));
        });

        view.findViewById(R.id.bConfirma).setOnClickListener(v -> {
            int quantidade = 0;

            if (callback != null) {
                if (!String.valueOf(etQuantidade.getText()).trim().isEmpty()) {
                    try {
                        quantidade = Integer.parseInt(String.valueOf(etQuantidade.getText()));
                    } catch (NumberFormatException e) {
                        quantidade = 0;
                        etQuantidade.setText(String.valueOf(quantidade));
                    }
                }

                if (quantidade > 0) {
                    callback.onQuantidade(quantidade);

                    dismiss();
                } else {
                    Utils.toast(getContext(), "Quantidade tem que ser maior que 0!");
                }
            }
        });

        view.findViewById(R.id.bCancela).setOnClickListener(v -> {
            if (callback != null) {
                dismiss();
            }
        });

        return view;
    }
}