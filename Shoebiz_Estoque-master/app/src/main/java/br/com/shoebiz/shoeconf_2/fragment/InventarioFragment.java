package br.com.shoebiz.shoeconf_2.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.activity.AreasColetadasActivity;
import br.com.shoebiz.shoeconf_2.dao.InventarioDao;
import br.com.shoebiz.shoeconf_2.dao.InventarioProdutoDao;
import br.com.shoebiz.shoeconf_2.dao.ProdutoDao;
import br.com.shoebiz.shoeconf_2.model.Inventario;
import br.com.shoebiz.shoeconf_2.model.ProdutoFilho;
import br.com.shoebiz.shoeconf_2.service.InventarioService;
import br.com.shoebiz.shoeconf_2.fragment.dialog.QuantidadeDialog;
import br.com.shoebiz.shoeconf_2.fragment.dialog.StatusInventarioDialog;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.Utils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InventarioFragment extends BaseFragment {

    private boolean inventarioAtivo = false;
    private boolean aparelhoLiberado = false;

    private Inventario inventario;

    private EditText etCodigoProduto;
    private EditText etCodigoArea;

    private TextView tvOperador;

    private LinearLayout llCodigoProduto;
    private LinearLayout llCodigoArea;
    private LinearLayout llInfoSemInventario;

    private Switch sModoOnLine;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventario, container, false);

        requireActivity().setTitle(R.string.nav_inventario);
        setHasOptionsMenu(true);

        inventario = new Inventario();
        inventario.produtosFilho = new ArrayList<>();

        llCodigoProduto = view.findViewById(R.id.llCodigoProduto);
        llCodigoArea = view.findViewById(R.id.llCodigoArea);
        llInfoSemInventario = view.findViewById(R.id.llInfoSemInventario);

        TextView tvCodigo = view.findViewById(R.id.tvCodigo);
        tvCodigo.setText(new PrefsUtils(getContext()).isLibCodigo());

        tvOperador =  view.findViewById(R.id.tvOperador);

        etCodigoProduto = view.findViewById(R.id.etCodigoProduto);
        etCodigoProduto.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String codDigitado = etCodigoProduto.getText().toString().trim();

                if (codDigitado.length() == 12 || codDigitado.length() == 13 || codDigitado.length() == 14) {
                    switch (codDigitado.length()) {
                        case 12:
                            codDigitado = "0" + codDigitado;
                            break;
                        case 14:
                            codDigitado = codDigitado.substring(1);
                            break;
                    }

                    if (new ProdutoDao(getContext()).buscaPorCodigo(codDigitado.substring(0,11)) != null) {
                        incluirProduto(codDigitado);
                    } else {
                        alertaCodigoInvalido("Código '"+ codDigitado + "' não cadastrado!");
                    }
                } else {
                    alertaCodigoInvalido("Código '"+ codDigitado + "' inválido!");
                }

                etCodigoProduto.setText("");
                etCodigoProduto.requestFocus();
            }

            return false;
        });

        etCodigoArea = view.findViewById(R.id.etCodigoArea);
        etCodigoArea.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String codArea = etCodigoArea.getText().toString().trim();

                if (codArea.length() == 6) {
                    if (new PrefsUtils(getContext()).isCadProdOk().equals("S")) {
                        if (new InventarioDao(getContext()).buscaPorCodigo(codArea.trim()) == null) {
                            etCodigoArea.setEnabled(false);

                            etCodigoProduto.setEnabled(true);
                            etCodigoProduto.requestFocus();

                            Animation animation_1 = AnimationUtils.loadAnimation(getContext(),
                                    R.anim.animacao_invent_cod_prod_1);
                            animation_1.reset();

                            Animation animation_2 = AnimationUtils.loadAnimation(getContext(),
                                    R.anim.animacao_invent_cod_prod_2);
                            animation_2.reset();

                            llCodigoProduto.setVisibility(View.VISIBLE);
                            llCodigoProduto.clearAnimation();
                            llCodigoProduto.setAnimation(animation_1);

                            llCodigoArea.clearAnimation();
                            llCodigoArea.setAnimation(animation_2);

                            inventario.area = codArea;
                            inventario.integrado = "N";
                        } else {
                            Utils.toast(getContext(), "Código '"+ codArea +"' já coletado!");

                            etCodigoArea.setText("");
                            etCodigoArea.requestFocus();
                        }
                    } else {
                        Utils.toast(getContext(), "Cadastro de produtos está desatualizado. Favor atualizar!");
                    }
                } else {
                    alertaCodigoInvalido("Código de área inválido!");

                    etCodigoArea.setText("");
                    etCodigoArea.requestFocus();
                }
            }
            return false;
        });

        Button bCancelar = view.findViewById(R.id.bCancelar);
        bCancelar.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setTitle("Atenção!")
                .setMessage("Confirma o CANCELAMENTO da área?")
                .setNegativeButton("Não", null)
                .setPositiveButton("Sim", (dialog, which) -> cancelaArea()).show());

        Button bFecharArea = view.findViewById(R.id.bFecharArea);
        bFecharArea.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setTitle("Atenção!")
                .setMessage("Confirma o FECHAMENTO da área?")
                .setNegativeButton("Não", null)
                .setPositiveButton("Sim", (dialog, which) -> {
                    int quant = 0;

                    for (ProdutoFilho produtoFilho : inventario.produtosFilho) {
                        quant += produtoFilho.quantidade;
                    }

                    final int finalQuant = quant;
                    QuantidadeDialog.show(getChildFragmentManager(), 0, quantidade -> {
                        if (finalQuant != quantidade) {
                            Utils.alertDialog(getContext(), "Atenção",
                                    "Quantidades divergentes! Favor refazer a contagem da área!");

                            cancelaArea();
                        } else {
                            new GravaInventarioTask(inventario).execute();
                        }
                    });
                }).show());

        sModoOnLine = view.findViewById(R.id.sModoOnLine);
        sModoOnLine.setChecked(new PrefsUtils(getContext()).isModoOnline().equals("S"));
        sModoOnLine.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                new PrefsUtils(getContext()).setModoOnline("S");

                Utils.toast(getContext(), "Modo OnLine ativo!");

                inventarioAberto();
            } else {
                new PrefsUtils(getContext()).setModoOnline("N");

                Utils.toast(getContext(), "Modo OnLine inativo!");
            }
        });

        if (new PrefsUtils(getContext()).isModoOnline().equals("N")) {
            inventarioAtivo = true;
            aparelhoLiberado = true;
            inventario.codigo = new PrefsUtils(getContext()).isInventCod();

            llInfoSemInventario.setVisibility(View.GONE);
            llCodigoArea.setVisibility(View.VISIBLE);
            etCodigoArea.requestFocus();
            tvOperador.setText(new PrefsUtils(getContext()).isInventOperNome());
            sModoOnLine.setEnabled(true);
        } else {
            inventarioAberto();
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_inventario, menu);
    }

    @Override
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (inventarioAtivo && aparelhoLiberado && new PrefsUtils(getContext()).isModoOnline().equals("S")) {
            switch (item.getItemId()) {
                case R.id.action_areas_coletadas:
                    if (new InventarioDao(getContext()).quantidade() > 0){
                        startActivity(new Intent(getContext(), AreasColetadasActivity.class));
                    } else {
                        Utils.toast(getContext(), "Não há área(s) coletada(s)");
                    }

                    break;
                case R.id.action_carrega_produtos:
                    if (new PrefsUtils(getContext()).isCadProdOk().equals("N")) {
                        new CarregaProdutoTask().execute(Utils.getUrlArquivoProdutos() + "sb1.json");
                    } else {
                        Utils.toast(getContext(), "Cadastro de produtos já está atualizado!");
                    }

                    break;
                case R.id.action_status_inventario:
                    StatusInventarioDialog.show(getChildFragmentManager());

                    break;
            }
        } else {
            if (!inventarioAtivo) {
                Utils.toast(getContext(), "Inventario não iniciado!");
            } else if (!aparelhoLiberado) {
                Utils.toast(getContext(), "Aparelho não liberado!");
            } else if (new PrefsUtils(getContext()).isModoOnline().equals("N")) {
                Utils.toast(getContext(), "Modo OnLine está inativo!");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void cancelaArea() {
        inventario = new Inventario();
        inventario.produtosFilho = new ArrayList<>();

        llCodigoProduto.setVisibility(View.GONE);

        etCodigoArea.setEnabled(true);
        etCodigoArea.setText("");
        etCodigoArea.requestFocus();
    }

    private void alertaCodigoInvalido(String mensagem) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Atenção!")
                .setMessage(mensagem)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.setOnShowListener(dialog1 -> {
            Button button = ((AlertDialog) dialog1).getButton(AlertDialog.BUTTON_NEGATIVE);
            button.setOnClickListener(v -> alerta());
        });

        dialog.show();

        alerta();
    }

    private void alerta() {
        Ringtone ringtone = RingtoneManager.getRingtone(getContext(),
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        ringtone.play();

        Vibrator vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(1000);
        }
    }

    private void incluirProduto(String codProduto) {
        boolean inclui = true;

        for (ProdutoFilho produtoFilhoArr : inventario.produtosFilho) {
            if (produtoFilhoArr.codigo.toUpperCase().trim().equals(codProduto)) {
                produtoFilhoArr.quantidade++;

                inclui = false;
            }
        }

        if (inclui) {
            ProdutoFilho produtoFilhoAux = new ProdutoFilho();
            produtoFilhoAux.id = 0;
            produtoFilhoAux.codigo = codProduto;
            produtoFilhoAux.quantidade = 1;

            inventario.produtosFilho.add(produtoFilhoAux);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void trataDataHoraArqSb1(Inventario inventario) {
        Activity activity = getActivity();
        boolean atualizar = false;

        if (activity != null && isAdded()) {
            Calendar dataUltArq = Calendar.getInstance();
            Calendar dataPrefs = Calendar.getInstance();

            PrefsUtils prefsUtils = new PrefsUtils(getContext());

            if (!(prefsUtils.isCadProdData().isEmpty() && prefsUtils.isCadProdHora().isEmpty())) {
                try {
                    dataUltArq.setTime(Objects.requireNonNull(new SimpleDateFormat(Utils.formatoDataHora()).parse(inventario.dataArqSB1 + " " + inventario.horaArqSB1)));
                    dataPrefs.setTime(Objects.requireNonNull(new SimpleDateFormat(Utils.formatoDataHora()).parse(prefsUtils.isCadProdData() + " " + prefsUtils.isCadProdHora())));

                    if (dataPrefs.getTimeInMillis() < dataUltArq.getTimeInMillis()) {
                        atualizar = true;
                    }
                } catch (ParseException e) {
                    Utils.toast(getContext(), "Erro ao buscar hora/data do último arquivo: " + e.getMessage());
                }
            } else {
                atualizar = true;
            }

            if (atualizar) {
                Utils.alertDialog(getContext(), "Atenção...", "Cadastro de produto não está atualizado! Favor atualizar!");

                new PrefsUtils(getContext()).setCadProdOk("N");
            } else {
                new PrefsUtils(getContext()).setCadProdOk("S");

                sModoOnLine.setEnabled(true);
                sModoOnLine.setChecked(prefsUtils.isModoOnline().equals("S"));
            }
        }
    }

    private void carregaProduto(JSONArray jsonArray, String data, String hora) {
        new GravaProdutosTask(jsonArray, data, hora).execute();
    }

    private void inventarioAberto() {
        new InventarioService(getContext()).consultaInventarioAbertoAPI(inventario -> {
            if (inventario != null) {
                PrefsUtils prefsUtils = new PrefsUtils(getContext());

                if (!inventario.codigo.equals(prefsUtils.isInventCod())){
                    prefsUtils.setInventCod("");
                    prefsUtils.setInventOperCod("");
                    prefsUtils.setInventOperNome("");

                    new InventarioDao(getContext()).deletar();
                    new InventarioProdutoDao(getContext()).deletar();
                }

                prefsUtils.setInventCod(inventario.codigo);

                statusAparelho(inventario);
            } else {
                inventarioAtivo = false;

                new PrefsUtils(getContext()).setModoOnline("S");
                sModoOnLine.setChecked(true);
            }
        });
    }

    private void statusAparelho(Inventario inventario) {
        this.inventarioAtivo = true;
        this.inventario.codigo = inventario.codigo;

        new InventarioService(getContext()).consultaStatusAparelhoAPI(inventario1 -> {
            if (inventario1 != null) {
                PrefsUtils prefsUtils = new PrefsUtils(getContext());
                prefsUtils.setInventOperCod(inventario1.operadorCodigo);
                prefsUtils.setInventOperNome(inventario1.operadorNome);

                llInfoSemInventario.setVisibility(View.GONE);
                llCodigoArea.setVisibility(View.VISIBLE);
                etCodigoArea.requestFocus();
                tvOperador.setText(inventario1.operadorNome);

                consultaArquivoSb1(inventario1);
            } else {
                aparelhoLiberado = false;
            }
        });
    }

    private void consultaArquivoSb1(Inventario inventario) {
        aparelhoLiberado = true;

        this.inventario.operadorCodigo = inventario.operadorCodigo;
        this.inventario.operadorNome = inventario.operadorNome;

        new InventarioService(getContext()).consultaArquivoSB1API(inventario1 -> {
            if (inventario1 != null) {
                trataDataHoraArqSb1(inventario1);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class CarregaProdutoTask extends AsyncTask<String, Void, JSONArray> {
        private String erro;

        private String data;
        private String hora;

        @Override
        protected void onPreExecute() {
            Utils.showProgress(getContext(), "Atenção...", "Carregando os produtos do protheus!");
        }

        @Override
        protected JSONArray doInBackground(String... urls) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(urls[0])
                        .build();

                Response response = okHttpClient.newCall(request).execute();

                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());

                data = jsonObject.getString("data");
                hora = jsonObject.getString("hora");

                return jsonObject.getJSONArray("produtos");
            } catch (Exception e) {
                erro = e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            Utils.closeProgress();

            if (erro != null) {
                Utils.toast(getContext(), erro);
            } else {
                if (jsonArray != null) {
                    carregaProduto(jsonArray, data, hora);
                } else {
                    Utils.toast(getContext(), "Erro ao carregar produtos");
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GravaProdutosTask extends AsyncTask<Void, Void, Void> {
        private JSONArray jsonArray;

        private String data;
        private String hora;

        GravaProdutosTask(JSONArray jsonArray, String data, String hora) {
            this.jsonArray = jsonArray;
            this.data = data;
            this.hora = hora;
        }

        @Override
        protected void onPreExecute() {
            Utils.showProgressBar(getContext(), "Atenção...", "Gravando os produtos!", jsonArray.length());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            new ProdutoDao(getContext()).deletar();

            for (int n=0; n < jsonArray.length(); n++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(n);

                    new ProdutoDao(getContext()).salvar(jsonObject.getString("codigo"));
                } catch (JSONException e) {
                    Utils.toast(getContext(), e.getMessage());
                }

                Utils.updateProgressBar(n);
            }

            new PrefsUtils(getContext()).setCadProdData(data);
            new PrefsUtils(getContext()).setCadProdHora(hora);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Utils.closeProgress();

            Utils.toast(getContext(), "Atualização finalizada!");

            inventarioAberto();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GravaInventarioTask extends AsyncTask<Void, Void, Void> {
        private String erro;
        private Inventario inventario;

        GravaInventarioTask(Inventario inventario) {
            this.inventario = inventario;
        }

        @Override
        protected void onPreExecute() {
            Utils.showProgress(getContext(), "Aguarde..", "Gravando os dados da área!");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Thread.sleep(500);

                new InventarioDao(getContext()).salvar(inventario);

                for (ProdutoFilho produtoFilho : inventario.produtosFilho) {
                    new InventarioProdutoDao(getContext()).salvar(inventario.area, produtoFilho);
                }
            } catch (Exception e) {
                erro = e.getMessage();
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Utils.closeProgress();

            if (erro != null) {
                Utils.toast(getContext(), erro);
            } else {
                Utils.toast(getContext(), "Área gravada!");

                cancelaArea();
            }
        }
    }
}