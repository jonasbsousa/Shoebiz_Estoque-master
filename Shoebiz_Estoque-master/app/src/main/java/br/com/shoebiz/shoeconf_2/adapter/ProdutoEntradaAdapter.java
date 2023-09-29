package br.com.shoebiz.shoeconf_2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.model.ProdutoEntrada;

public class ProdutoEntradaAdapter extends RecyclerView.Adapter<ProdutoEntradaAdapter.ProdutoEntradaViewHolder> {
    private Context context;
    private List<ProdutoEntrada> produtosEntrada;
    private ProdutoEntradaOnClickListener notaEntradaOnClickListener;

    public interface ProdutoEntradaOnClickListener {
        void onClickNota(View view, int idx);
    }

    public ProdutoEntradaAdapter(Context context, List<ProdutoEntrada> produtosEntrada, ProdutoEntradaOnClickListener notaEntradaOnClickListener) {
        this.context = context;
        this.produtosEntrada = produtosEntrada;
        this.notaEntradaOnClickListener = notaEntradaOnClickListener;
    }

    @NonNull
    @Override
    public ProdutoEntradaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_produto_entrada, viewGroup, false);

        return new ProdutoEntradaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProdutoEntradaViewHolder holder, final int position) {
        ProdutoEntrada notaEntrada = this.produtosEntrada.get(position);

        holder.tvCodigo.setText(notaEntrada.codigo);
        holder.tvQuantidade.setText(context.getString(R.string.rv_nTextoQuant, String.valueOf(notaEntrada.quantidade)));

        if (!notaEntrada.status.equals("V")) {
            holder.pbProcessando.setVisibility(View.GONE);
            holder.tvDescricao.setText(notaEntrada.descricao);

            if (notaEntrada.status.equals("S")) {
                holder.rlBoxImagem.setVisibility(View.GONE);
                holder.rlBoxInfoProd.setVisibility(View.VISIBLE);

                holder.tvCor.setText(context.getString(R.string.rv_nTextoCor, String.valueOf(notaEntrada.cor)));
                holder.tvTamanho.setText(context.getString(R.string.rv_nTextoTam, String.valueOf(notaEntrada.tamanho)));
            } else {
                holder.tvDescricao.setTextColor(context.getResources().getColor(R.color.red));
                holder.ivErro.setVisibility(View.VISIBLE);
            }
        }

        if (this.notaEntradaOnClickListener != null) {
            holder.itemView.setOnClickListener(view -> notaEntradaOnClickListener.onClickNota(holder.view, position));
        }
    }

    @Override
    public int getItemCount() {
        return this.produtosEntrada != null ? this.produtosEntrada.size() : 0;
    }

    class ProdutoEntradaViewHolder extends RecyclerView.ViewHolder {
        private View view;

        private TextView tvCodigo;
        private TextView tvQuantidade;
        private TextView tvDescricao;
        private TextView tvCor;
        private TextView tvTamanho;
        private RelativeLayout rlBoxImagem;
        private RelativeLayout rlBoxInfoProd;
        private ProgressBar pbProcessando;
        private ImageView ivErro;

        ProdutoEntradaViewHolder(@NonNull View view) {
            super(view);
            this.view = view;

            tvCodigo = view.findViewById(R.id.tvCodigo);
            tvQuantidade = view.findViewById(R.id.tvQuantidade);
            tvDescricao = view.findViewById(R.id.tvDescricao);
            tvCor = view.findViewById(R.id.tvCor);
            tvTamanho = view.findViewById(R.id.tvTamanho);
            rlBoxImagem = view.findViewById(R.id.rlBoxImagem);
            rlBoxInfoProd = view.findViewById(R.id.rlBoxInfoProd);
            pbProcessando = view.findViewById(R.id.pbProcessando);
            ivErro = view.findViewById(R.id.ivErro);
        }
    }
}