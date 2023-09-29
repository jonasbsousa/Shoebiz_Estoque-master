package br.com.shoebiz.shoeconf_2.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import br.com.shoebiz.shoeconf_2.BuildConfig;
import br.com.shoebiz.shoeconf_2.model.ColetorProduto;
import br.com.shoebiz.shoeconf_2.model.ProdutoEntrada;
import br.com.shoebiz.shoeconf_2.model.ProdutoFilho;
import br.com.shoebiz.shoeconf_2.model.TransfProduto;

public class FTPUtils {
    private static final String FTP_URL = BuildConfig.FTP_URL;
    private static final String FTP_USER = BuildConfig.FTP_USER;
    private static final String FTP_PASSWORD = BuildConfig.FTP_PASSWORD;

    private Context context;

    public interface RetornoCallback {
        void onSuccess();
        void onFailure(String mensagem);
    }

    public FTPUtils(Context context) {
        this.context = context;
    }

    public void enviaArquivoEntMercadoria(List<ProdutoEntrada> produtosEntrada, String nomeArquivo, String msgProgress, final RetornoCallback callback) {
        StringBuilder jsonString = new StringBuilder();
        int count = 0;

        jsonString.append("[\n");

        for (ProdutoEntrada produtoEntrada : produtosEntrada) {
            jsonString.append("    {\"CODIGO\":\"").append(produtoEntrada.codigo).append("\", \"QUANTIDADE\":").append(produtoEntrada.quantidade).append("}");

            count++;

            if (count < produtosEntrada.size()) {
                jsonString.append(",");
            }

            jsonString.append("\n");
        }

        jsonString.append("]");

        new FTPJsonTask(nomeArquivo, jsonString.toString(), "edi/nota_entrada/", msgProgress, callback).execute();
    }

    public void enviaArquivoTransferencia(List<TransfProduto> transfProdutos, String nomeArquivo, String msgProgress, RetornoCallback callback) {
        StringBuilder jsonString = new StringBuilder();
        int count = 0;

        jsonString.append("[\n");

        for (TransfProduto transfProduto : transfProdutos) {
            jsonString.append("    {\"CODIGO\":\"").append(transfProduto.coletado).append("\", \"QUANTIDADE\":").append(transfProduto.quantidade).append("}");

            count++;

            if (count < transfProdutos.size()) {
                jsonString.append(",");
            }

            jsonString.append("\n");
        }

        jsonString.append("]");

        new FTPJsonTask(nomeArquivo, jsonString.toString(), "edi/transferencia/", msgProgress, callback).execute();
    }

    public void enviaArquivoInventarioMarca(List<ProdutoFilho> produtosFilho, String nomeArquivo, String msgProgress, RetornoCallback callback) {
        StringBuilder string = new StringBuilder();

        for (ProdutoFilho produtoFilho : produtosFilho) {
            string.append(produtoFilho.codigo).append("|").append(produtoFilho.quantidade).append("\n");
        }

        new FTPJsonTask(nomeArquivo, string.toString(), "edi/inventario_por_marca/", msgProgress, callback).execute();
    }

    public void enviaArquivoColetor(List<ColetorProduto> coletorProdutos, String nomeArquivo, String diretorioFtp, String msgProgress, RetornoCallback callback) {
        StringBuilder jsonString = new StringBuilder();
        int count = 0;

        jsonString.append("[\n");

        for (ColetorProduto coletorProduto : coletorProdutos) {
            jsonString.append("    {\"CODIGO\":\"").append(coletorProduto.codigo).append("\", \"QUANTIDADE\":").append(coletorProduto.quantidade).append("}");

            count++;

            if (count < coletorProdutos.size()) {
                jsonString.append(",");
            }

            jsonString.append("\n");
        }

        jsonString.append("]");

        new FTPJsonTask(nomeArquivo + ".txt", jsonString.toString(), "edi/" + diretorioFtp, msgProgress, callback).execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class FTPJsonTask extends AsyncTask<Void, Void, Void> {
        private RetornoCallback callback;

        private String msgProgress;
        private String nomeArquivo;
        private String jsonString;
        private String diretorioFtp;
        private String erro;


        FTPJsonTask(String nomeArquivo, String jsonString, String diretorioFtp, String msgProgress, RetornoCallback callback) {
            this.callback = callback;
            this.nomeArquivo = nomeArquivo;
            this.jsonString = jsonString;
            this.diretorioFtp = diretorioFtp;
            this.msgProgress = msgProgress;
        }

        @Override
        protected void onPreExecute() {
            Utils.showProgress(context, "Aguarde...", this.msgProgress);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                File file = context.getFileStreamPath(nomeArquivo);

                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(jsonString.getBytes());
                outputStream.close();

                FTPClient ftpClient = new FTPClient();
                ftpClient.connect(FTP_URL);

                if (ftpClient.login(FTP_USER, FTP_PASSWORD)) {
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));

                    ftpClient.changeWorkingDirectory(diretorioFtp);
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                    ftpClient.enterLocalPassiveMode();

                    Thread.sleep(1000);

                    if (!ftpClient.storeFile(nomeArquivo, bufferedInputStream)) {
                        erro = "Não foi possível enviar o arquivo no servidor FTP!";
                    }

                    bufferedInputStream.close();
                    ftpClient.logout();
                    ftpClient.disconnect();

                    Thread.sleep(500);
                } else {
                    erro = "Não foi possível conectar no servidor FTP!";
                }
            } catch (Exception e) {
                erro = e.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Utils.closeProgress();

            if (erro != null) {
                Utils.toast(context, erro);

                if (callback != null) {
                    callback.onFailure(erro);
                }
            } else {
                if (callback != null) {
                    callback.onSuccess();
                }
            }
        }
    }
}