package br.com.shoebiz.shoeconf_2.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;

public class MaskUtils {
    private static final String MASCARA_CNPJ = "##.###.###/####-##";
    private static final String MASCARA_DINHEIRO = "###,###,###,###,###.00";

    public enum MaskType {
        CNPJ
    }

    public static TextWatcher insert(final EditText editText, final MaskType maskType) {
        return new TextWatcher() {
            boolean isUpdating;
            String oldValue = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = MaskUtils.unmask(s.toString());
                String mask = "";

                switch (maskType) {
                    case CNPJ:
                        mask = MASCARA_CNPJ;
                        break;
                }

                String maskAux = "";
                if (isUpdating) {
                    oldValue = value;
                    isUpdating = false;
                    return;
                }

                int i = 0;
                for (char m : mask.toCharArray()) {
                    if ((m != '#' && value.length() > oldValue.length()) || (m != '#' && value.length() < oldValue.length() && value.length() != i)) {
                        maskAux += m;
                        continue;
                    }

                    try {
                        maskAux += value.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }

                isUpdating = true;
                editText.setText(maskAux);
                editText.setSelection(maskAux.length());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    public static String addMaskCnpj(final String textoAFormatar, final MaskType maskType) {
        String formatado = "";
        String mask = "";
        int i = 0;

        switch (maskType) {
            case CNPJ:
                mask = MASCARA_CNPJ;
                break;
        }

        for (char m : mask.toCharArray()) {
            if (m != '#') {
                formatado += m;
                continue;
            }

            try {
                formatado += textoAFormatar.charAt(i);
            } catch (Exception e) {
                break;
            }
            i++;
        }
        return formatado;
    }

    public static String addMaskMoney(double valor) {
        if (valor == 0) {
            return "0,00";
        } else {
            return new DecimalFormat(MASCARA_DINHEIRO).format(valor);
        }
    }

    private static String unmask(String texto) {
        return texto.replaceAll("[^0-9]*", "");
    }
}