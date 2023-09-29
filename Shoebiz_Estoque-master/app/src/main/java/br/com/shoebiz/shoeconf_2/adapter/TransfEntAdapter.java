package br.com.shoebiz.shoeconf_2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.model.TransfProduto;

public class TransfEntAdapter extends RecyclerView.Adapter<TransfEntAdapter.TransfEntViewHolder> {
    private final List<TransfProduto> transfProdutos;
    private final Context context;
    private final String formaColeta;
    private final boolean isTelaDiv;
    private final TransfEntOnClickListener transfEntOnClickListener;

    public interface TransfEntOnClickListener {
        void onClickProduto(View view, int idx);
    }

    public TransfEntAdapter(Context context, List<TransfProduto> transfProdutos, boolean isTelaDiv, String formaColeta, TransfEntOnClickListener transfEntOnClickListener) {
        this.transfProdutos = transfProdutos;
        this.context = context;
        this.isTelaDiv = isTelaDiv;
        this.formaColeta = formaColeta;
        this.transfEntOnClickListener = transfEntOnClickListener;
    }

    @NonNull
    @Override
    public TransfEntAdapter.TransfEntViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_transf_ent, viewGroup, false);

        return new TransfEntViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransfEntViewHolder holder, int position) {
        TransfProduto transfProduto = this.transfProdutos.get(position);
        StringBuilder textoGtin = new StringBuilder();

        if (isTelaDiv) {
            if (transfProduto.descricao != null) {
                if (formaColeta.equals(context.getString(R.string.forma_coleta_gtin))) {
                    for (int n = 0; n < transfProduto.transfGtins.size(); n++) {
                        textoGtin.append(transfProduto.transfGtins.get(n).codigo).append(n < (transfProduto.transfGtins.size() - 1) ? ", " : "");
                    }

                    holder.tvCodigoProduto.setText(context.getString(R.string.acti_co_gtin, textoGtin));
                } else {
                    holder.tvCodigoProduto.setText(context.getString(R.string.acti_co_codigo, transfProduto.codigoProduto));
                }
            } else {
                holder.tvCodigoProduto.setText(context.getString(R.string.acti_co_codigo, transfProduto.coletado));
            }

            holder.tvQuantidadeProduto.setText(String.valueOf(transfProduto.divergencia));

            holder.tvCodigoProduto.setTextColor(context.getResources().getColor(transfProduto.divergencia == 0 ? R.color.green : R.color.red));
            holder.tvQuantidadeProduto.setTextColor(context.getResources().getColor(transfProduto.divergencia == 0 ? R.color.green : R.color.red));
        } else {
            holder.tvCodigoProduto.setText(context.getString(R.string.acti_co_codigo, transfProduto.coletado));
            holder.tvQuantidadeProduto.setText(context.getString(R.string.rv_nTextoQuant, String.valueOf(transfProduto.quantidade)));
        }

        if (transfProduto.descricao != null) {
            holder.tvDescricao.setText(transfProduto.descricao);
        } else {
            holder.tvDescricao.setText(context.getString(R.string.erro_produto_transf));
            holder.tvDescricao.setTextColor(context.getResources().getColor(R.color.red));
        }

        if (this.transfEntOnClickListener != null) {
            holder.itemView.setOnClickListener(view -> transfEntOnClickListener.onClickProduto(holder.view, position));
        }
    }

    @Override
    public int getItemCount() {
        return this.transfProdutos != null ? this.transfProdutos.size() : 0;
    }

    static class TransfEntViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private final TextView tvCodigoProduto;
        private final TextView tvQuantidadeProduto;
        private final TextView tvDescricao;

        TransfEntViewHolder(@NonNull View view) {
            super(view);
            this.view = view;

            tvCodigoProduto = view.findViewById(R.id.tvCodigoProduto);
            tvQuantidadeProduto = view.findViewById(R.id.tvQuantidadeProduto);
            tvDescricao = view.findViewById(R.id.tvDescricao);
        }
    }
}