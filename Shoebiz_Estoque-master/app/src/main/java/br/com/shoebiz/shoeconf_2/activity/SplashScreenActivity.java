package br.com.shoebiz.shoeconf_2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import br.com.shoebiz.shoeconf_2.BuildConfig;
import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.model.Liberacao;
import br.com.shoebiz.shoeconf_2.service.LiberacaoService;
import br.com.shoebiz.shoeconf_2.utils.FCMUtils;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class SplashScreenActivity extends BaseActivity {
    private String empresa;

    private LinearLayout llLoginEmpresa;
    private LinearLayout llLoginFilial;
    private LinearLayout llLiberarAcesso;
    private LinearLayout llSolicitadoAcesso;
    private LinearLayout llProcessando;
    private LinearLayout llAlerta;
    private LinearLayout llSemAcessoAoSrv;

    private ImageView ivLogoSplashScreen;

    private TextView tvDescAguarde;
    private TextView tvSolicitadoAcesso1;
    private TextView tvSolicitadoAcesso2;
    private TextView tvAlerta1;
    private TextView tvAlerta2;

    private Button btAcessarEmpresa;
    private Button btAcessarFilial;
    private Button btLiberarAcesso;
    private Button btVerifAcesso;
    private Button btSemAcessoAoSrv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alteraTemaFullScreen();
        setContentView(R.layout.activity_splash_screen);

        btAcessarEmpresa = findViewById(R.id.btAcessarEmpresa);
        btAcessarFilial = findViewById(R.id.btAcessarFilial);
        btLiberarAcesso = findViewById(R.id.btLiberarAcesso);
        btVerifAcesso = findViewById(R.id.btVerifAcesso);
        btSemAcessoAoSrv = findViewById(R.id.btSemAcessoAoSrv);

        llLoginEmpresa = findViewById(R.id.llLoginEmpresa);
        llLoginFilial = findViewById(R.id.llLoginFilial);
        llLiberarAcesso = findViewById(R.id.llLiberarAcesso);
        llSolicitadoAcesso = findViewById(R.id.llSolicitadoAcesso);
        llProcessando = findViewById(R.id.llProcessando);
        llAlerta = findViewById(R.id.llAlerta);
        llSemAcessoAoSrv = findViewById(R.id.llSemAcessoAoSrv);

        ivLogoSplashScreen = findViewById(R.id.ivLogoSplashScreen);

        tvSolicitadoAcesso1 = findViewById(R.id.tvSolicitadoAcesso1);
        tvSolicitadoAcesso2 = findViewById(R.id.tvSolicitadoAcesso2);
        tvDescAguarde = findViewById(R.id.tvDescAguarde);
        tvAlerta1 = findViewById(R.id.tvAlerta1);
        tvAlerta2 = findViewById(R.id.tvAlerta2);

        PrefsUtils prefsUtils = new PrefsUtils(getContext());

        if (FCMUtils.getFcmToken(getContext()) == null) {
            FCMUtils.register(getContext());
        }

        if ((prefsUtils.isDescEmp().isEmpty() || prefsUtils.isCodLoja().isEmpty() || prefsUtils.isDescFil().isEmpty()) || validaEmpresa(prefsUtils.isDescEmp())) {
            verificaLocal();
        } else {
            Utils.toast(getContext(), "Empresa/Filial invalida");
            finish();
        }
    }

    private void iniciaSplashScreen(final boolean acessoServidor) {
        final PrefsUtils prefsUtils = new PrefsUtils(getContext());

        new Handler().postDelayed(() -> {
            if (acessoServidor) {
                Liberacao liberacao = prefsUtils.getLiteracao();

                btLiberarAcesso.setOnClickListener(v -> {
                    animacaoOutLinLayout(llLiberarAcesso);
                    animacaoInLinLayout(llProcessando);
                    tvDescAguarde.setText(R.string.enviando_solicitacao);

                    solicitaLiberacao();
                });

                btVerifAcesso.setOnClickListener(v -> {
                    animacaoOutLinLayout(llSolicitadoAcesso);
                    animacaoInLinLayout(llProcessando);

                    verificaLiberacao();
                });

                if (liberacao.liberacaoCodigo.isEmpty() || liberacao.solicitacaoToken.isEmpty()) {
                    animacaoLogo();
                    animacaoInLinLayout(llLiberarAcesso);
                } else if (liberacao.liberacaoToken.isEmpty()) {
                    animacaoLogo();

                    animacaoInLinLayout(llProcessando);
                    tvDescAguarde.setText(R.string.verificando_solicitacao);

                    verificaLiberacao();
                } else {
                    String ultimoAcesso = prefsUtils.isUltimoAcesso();
                    String modoOnline = prefsUtils.isModoOnline();

                    if (!ultimoAcesso.isEmpty() && ultimoAcesso.equals(Utils.getDataAtualSQL()) || modoOnline.equals("N")) {
                        iniciaMainActivity();
                    } else {
                        verificaTipoAcesso();
                    }
                }
            } else {
                animacaoLogo();
                animacaoInLinLayout(llSemAcessoAoSrv);

                btSemAcessoAoSrv.setOnClickListener(v -> {
                    if (new PrefsUtils(getContext()).isLibToken().isEmpty()) {
                        Utils.toast(getContext(), "Login inválido!");
                    } else {
                        Utils.toast(getContext(), "Falha ao acessar o servidor!");
                    }
                });
            }
        }, Utils.SPLASH_DISPLAY_LENGTH);
    }

    private void initButtonAcessar() {
        animacaoLogo();

        if (new PrefsUtils(getContext()).isPerfilAcesso().charAt(0) == 'E') {
            animacaoInLinLayout(llLoginEmpresa);
            initSpinners();

            btAcessarEmpresa.setOnClickListener(v -> {
                String codFilial = "";
                String descEmp = "";

                switch (SplashScreenActivity.this.empresa.toUpperCase()) {
                    case "G2":
                        codFilial = "11";
                        descEmp = "SHOEBIZ";
                        break;
                    case "PI":
                        codFilial = "13";
                        descEmp = "SHOEBIZ";
                        break;
                    case "OS1":
                        codFilial = "21";
                        descEmp = "SHOEBIZ";
                        break;
                    case "AM2":
                        codFilial = "23";
                        descEmp = "SHOEBIZ";
                        break;
                    case "PE":
                        codFilial = "35";
                        descEmp = "SHOEBIZ";
                        break;
                    case "SV":
                        codFilial = "39";
                        descEmp = "SHOEBIZ";
                        break;
                    case "ECO":
                        codFilial = "45";
                        descEmp = "SHOEBIZ";
                        break;
                    case "MC":
                        codFilial = "48";
                        descEmp = "SHOEBIZ";
                        break;
                    case "SM":
                        codFilial = "50";
                        descEmp = "SHOEBIZ";
                        break;
                    case "SO":
                        codFilial = "51";
                        descEmp = "SHOEBIZ";
                        break;
                    case "SB2":
                        codFilial = "52";
                        descEmp = "SHOEBIZ";
                        break;
                    case "JAC":
                        codFilial = "53";
                        descEmp = "SHOEBIZ";
                        break;
                    case "SJC":
                        codFilial = "54";
                        descEmp = "SHOEBIZ";
                        break;
                    case "LP2":
                        codFilial = "57";
                        descEmp = "SHOEBIZ";
                        break;
                    case "GJA":
                        codFilial = "58";
                        descEmp = "SHOEBIZ";
                        break;
                    case "CBT":
                        codFilial = "59";
                        descEmp = "SHOEBIZ";
                        break;
                    case "ITA":
                        codFilial = "60";
                        descEmp = "SHOEBIZ";
                        break;
                    case "GRA":
                        codFilial = "61";
                        descEmp = "SHOEBIZ";
                        break;
                    case "LP3":
                        codFilial = "62";
                        descEmp = "SHOEBIZ";
                        break;
                    case "OCI":
                        codFilial = "64";
                        descEmp = "SHOEBIZ";
                        break;
                    case "SH5":
                        codFilial = "65";
                        descEmp = "SHOEBIZ";
                        break;
                    case "SUZ":
                        codFilial = "68";
                        descEmp = "SHOEBIZ";
                        break;
                    case "TB":
                        codFilial = "70";
                        descEmp = "SHOEBIZ";
                        break;
                }

                new PrefsUtils(getContext()).setCodLoja(codFilial);
                new PrefsUtils(getContext()).setDescFil(SplashScreenActivity.this.empresa.toUpperCase());
                new PrefsUtils(getContext()).setDescEmp(descEmp);

                consultaAcesso();
            });
        } else {
            animacaoInLinLayout(llLoginFilial);

            btAcessarFilial.setOnClickListener(v -> consultaAcesso());
        }
    }

    private void initSpinners() {
        Spinner sEmpresa = findViewById(R.id.sEmpresa);
        final Spinner sFiliais = findViewById(R.id.sFiliais);

        ArrayAdapter<CharSequence> adapterSpinnerEmpresa = ArrayAdapter.createFromResource(this, R.array.empresas, R.layout.spinner_item_empresa);
        adapterSpinnerEmpresa.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sEmpresa.setAdapter(adapterSpinnerEmpresa);
        sEmpresa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<String> list = new ArrayList<>();

                list.add("G2");
                list.add("PI");
                list.add("OS1");
                list.add("AM2");
                list.add("PE");
                list.add("SV");
                list.add("ECO");
                list.add("MC");
                list.add("SM");
                list.add("SO");
                list.add("SB2");
                list.add("JAC");
                list.add("SJC");
                list.add("LP2");                
                list.add("GJA");
                list.add("CBT");
                list.add("ITA");
                list.add("GRA");
                list.add("LP3");
                list.add("SH5");
                list.add("SUZ");
                list.add("TB");

                ArrayAdapter<String> adapterSpinnerFiliais = new ArrayAdapter<>(getContext(), R.layout.spinner_item_empresa, list);
                adapterSpinnerFiliais.setDropDownViewResource(R.layout.spinner_dropdown_item);
                sFiliais.setAdapter(adapterSpinnerFiliais);
                sFiliais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        SplashScreenActivity.this.empresa = parent.getItemAtPosition(position).toString().toUpperCase();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void animacaoInLinLayout(LinearLayout layout) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.animacao_splashscreen_1);
        animation.reset();

        layout.setVisibility(LinearLayout.VISIBLE);
        layout.clearAnimation();
        layout.startAnimation(animation);
    }

    private void animacaoOutLinLayout(LinearLayout layout) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.animacao_splashscreen_3);
        animation.reset();

        layout.setVisibility(LinearLayout.GONE);
        layout.clearAnimation();
        layout.startAnimation(animation);
    }

    private void animacaoLogo() {
        int anim = R.anim.animacao_splashscreen_logo_1;

        Animation animLogo = AnimationUtils.loadAnimation(this, R.anim.animacao_splashscreen_1);
        animLogo.reset();

        ivLogoSplashScreen.clearAnimation();
        ivLogoSplashScreen.startAnimation(animLogo);

        Animation animation = AnimationUtils.loadAnimation(getContext(), anim);
        animation.reset();

        ivLogoSplashScreen.clearAnimation();
        ivLogoSplashScreen.startAnimation(animation);
    }

    private void iniciaMainActivity() {
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        SplashScreenActivity.this.finish();
    }

    private void reiniciaSplash() {
        Intent intent = new Intent(new Intent(getContext(), SplashScreenActivity.class));
        startActivity(intent);
        SplashScreenActivity.this.finish();
    }

    public boolean validaEmpresa(String descricaoEmpresa) {
        return descricaoEmpresa.equals(getString(Utils.SHOEBIZ)) || descricaoEmpresa.equals(getString(Utils.SHOEBIZ_COMERCIO));
    }

    private void verificaLocal() {
        if (new PrefsUtils(getContext()).isModoOnline().equals("S")) {
            new LiberacaoService(getContext(), Utils.CODIGO, Utils.getNetworkInfo(getContext(), "IP"), "", "", "", "")
                    .verificaLocal(new LiberacaoService.GenericCallBack() {

                @Override
                public void onSuccess(Liberacao liberacao) {
                    if (liberacao.codigoLoja != null) {
                        PrefsUtils prefsUtils = new PrefsUtils(getContext());

                        if (!prefsUtils.isCodLoja().equals(liberacao.codigoLoja)) {
                            prefsUtils.setCodLoja(liberacao.codigoLoja);
                            prefsUtils.setDescEmp(liberacao.descricaoEmpresa);
                            prefsUtils.setDescFil(liberacao.descricaoFilial);
                            prefsUtils.setUltimoAcesso("");

                            reiniciaSplash();
                        }

                        iniciaSplashScreen(true);
                    } else {
                        Utils.toast(getContext(), "Problema ao acessar servidor!");
                        iniciaSplashScreen(false);
                    }
                }

                @Override
                public void onFailure(String erro) {
                    iniciaSplashScreen(false);
                }
            });
        } else {
            iniciaSplashScreen(true);
        }
    }

    private void solicitaLiberacao() {
        Liberacao liberacao = new Liberacao();
        liberacao.idAndroid = Utils.getAndroidId(getContext());
        liberacao.mac = Utils.getNetworkInfo(getContext(), "MAC");
        liberacao.appNome = getString(Utils.APP_NAME).toUpperCase().trim();

        new LiberacaoService(getContext(), Utils.CODIGO, Utils.getNetworkInfo(getContext(), "IP"), "", "", "", "")
                .solicitaLiberacao(liberacao, new LiberacaoService.SolicitaLiberacaoCallBack() {

            @Override
            public void onSuccess(Liberacao liberacao) {
                PrefsUtils prefsUtils = new PrefsUtils(getContext());
                prefsUtils.setLibCodigo(liberacao.liberacaoCodigo);
                prefsUtils.setSolToken(liberacao.solicitacaoToken);

                tvSolicitadoAcesso1.setText(liberacao.mensagem);
                tvSolicitadoAcesso2.setText(getString(R.string.num_codigo, liberacao.liberacaoCodigo));

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Solicitação");
                bundle.putBoolean(FirebaseAnalytics.Param.SUCCESS, true);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);

                animacaoOutLinLayout(llProcessando);
                animacaoInLinLayout(llSolicitadoAcesso);
            }

            @Override
            public void onUnauthorized(String erro) {
                tvAlerta1.setText(erro);
                tvAlerta2.setText("");

                animacaoOutLinLayout(llProcessando);
                animacaoInLinLayout(llAlerta);
            }

            @Override
            public void onFailure(String erro) {
                Utils.toast(getContext(), erro);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Solicitação");
                bundle.putBoolean(FirebaseAnalytics.Param.SUCCESS, false);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);

                animacaoOutLinLayout(llProcessando);
                animacaoInLinLayout(llLiberarAcesso);
            }
        });
    }

    private void verificaLiberacao() {
        PrefsUtils prefsUtils = new PrefsUtils(getContext());

        new LiberacaoService(getContext(), Utils.CODIGO, Utils.getNetworkInfo(getContext(), "IP"), prefsUtils.isLibCodigo(), Utils.getAndroidId(getContext()), prefsUtils.isSolToken(), "")
                .consultaLiberacao(new LiberacaoService.ConsultaLiberacaoCallBack() {

            @Override
            public void onSuccess(Liberacao liberacao) {
                outProcLayout();

                new PrefsUtils(getContext()).setLibToken(liberacao.liberacaoToken);

                verificaTipoAcesso();
            }

            @Override
            public void onForbidden(String erro) {
                outProcLayout();

                tvSolicitadoAcesso1.setText(erro);
                tvSolicitadoAcesso2.setText("");
                animacaoInLinLayout(llSolicitadoAcesso);
            }

            @Override
            public void onUnauthorized(String erro) {
                outProcLayout();

                tvAlerta1.setText(erro);
                tvAlerta2.setText("");
                animacaoInLinLayout(llAlerta);
            }

            @Override
            public void onFailure(String erro) {
                Utils.toast(getContext(), erro);

                animacaoOutLinLayout(llProcessando);
                animacaoInLinLayout(llLiberarAcesso);
            }

            private void outProcLayout() {
                animacaoOutLinLayout(llProcessando);
            }
        });
    }

    private void verificaTipoAcesso() {
        new LiberacaoService(getContext(), "", "", "", "", "", "").consultaTipoAcesso(new LiberacaoService.GenericCallBack() {

            @Override
            public void onSuccess(Liberacao liberacao) {
                new PrefsUtils(getContext()).setPerfilAcesso(Utils.descTipoAcesso(getContext(), liberacao.perfilAcesso));

                initButtonAcessar();
            }

            @Override
            public void onFailure(String erro) {
                new PrefsUtils(getContext()).setPerfilAcesso(Utils.descTipoAcesso(getContext(), "L"));

                initButtonAcessar();
            }
        });
    }

    private void consultaAcesso() {
        Utils.showProgress(getContext(), "Atenção...", "Validando acesso...");

        new LiberacaoService(getContext(), "", "", "", "", "", new PrefsUtils(getContext()).isFcmToken())
                .consultaAcesso(new LiberacaoService.GenericCallBack() {

            @Override
            public void onSuccess(Liberacao liberacao) {
                Utils.closeProgress();

                new PrefsUtils(getContext()).setUltimoAcesso(Utils.getDataAtualSQL());

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Login");
                bundle.putBoolean(FirebaseAnalytics.Param.SUCCESS, true);
                bundle.putString("Loja", new PrefsUtils(getContext()).isCodLoja());
                bundle.putString("Build", BuildConfig.BUILD_TYPE);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);

                iniciaMainActivity();
            }

            @Override
            public void onFailure(String erro) {
                Utils.closeProgress();

                if (new PrefsUtils(getContext()).isPerfilAcesso().charAt(0) == 'E') {
                    animacaoOutLinLayout(llLoginEmpresa);
                } else {
                    animacaoOutLinLayout(llLoginFilial);
                }

                tvAlerta1.setText(erro);
                tvAlerta2.setText("");

                animacaoInLinLayout(llAlerta);
            }
        });
    }
}