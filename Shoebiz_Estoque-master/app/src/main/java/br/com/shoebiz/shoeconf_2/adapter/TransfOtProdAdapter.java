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
import br.com.shoebiz.shoeconf_2.model.OtProduto;

public class TransfOtProdAdapter extends RecyclerView.Adapter<TransfOtProdAdapter.TransfOtProdViewHolder> {
    private final List<OtProduto> otProdutoList;
    private final Context context;
    private final String formaColeta;
    private final boolean isTelaDiv;
    private final TransfOtOnClickListener transfOtOnClickListener;

    public interface TransfOtOnClickListener {
        void onClickProduto(View view, int idx, boolean isTelaDiv);
    }

    public TransfOtProdAdapter(Context context, List<OtProduto> otProdutoList, boolean isTelaDiv, String formaColeta, TransfOtOnClickListener transfOtOnClickListener) {
        this.otProdutoList = otProdutoList;
        this.context = context;
        this.isTelaDiv = isTelaDiv;
        this.formaColeta = formaColeta;
        this.transfOtOnClickListener = transfOtOnClickListener;
    }

    @NonNull
    @Override
    public TransfOtProdViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_transf_ot_prod, viewGroup, false);

        return new TransfOtProdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransfOtProdViewHolder holder, int position) {
        OtProduto otProduto = otProdutoList.get(position);
        String priCodigo;
        String segCodigo;

        holder.tvQuantidadeProduto.setText(context.getString(R.string.rv_nTextoQuant, String.valueOf(otProduto.quantidade)));
        holder.tvDescricao.setText(otProduto.descricao);

        if (formaColeta.equals(context.getString(R.string.forma_coleta_gtin))) {
            priCodigo = otProduto.gtin;
            segCodigo = context.getString(R.string.acti_co_codigo, otProduto.codigo);

            holder.tvCodigo.setText(segCodigo);
        } else {
            priCodigo = otProduto.codigo;

            holder.tvCodigo.setVisibility(View.GONE);
        }

        if (isTelaDiv) {
            if (otProduto.codigoProduto) {
                holder.tvCodigoProduto.setText(context.getString(R.string.acti_co_prod_p, priCodigo));
            } else if (otProduto.codigoCaixa) {
                holder.tvCodigoProduto.setText(context.getString(R.string.acti_co_caixa_p, priCodigo));
            } else {
                holder.tvCodigoProduto.setText(priCodigo);
            }

            holder.tvCodigoProduto.setTextColor(context.getResources().getColor(R.color.red));
            holder.tvQuantidadeProduto.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            holder.tvCodigoProduto.setText(priCodigo);
        }

        if (transfOtOnClickListener != null) {
            holder.itemView.setOnClickListener(v -> transfOtOnClickListener.onClickProduto(holder.view, position, isTelaDiv));
        }
    }

    @Override
    public int getItemCount() {
        return this.otProdutoList != null ? this.otProdutoList.size() : 0;
    }

    static class TransfOtProdViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private final TextView tvCodigoProduto;
        private final TextView tvCodigo;
        private final TextView tvDescricao;
        private final TextView tvQuantidadeProduto;

        TransfOtProdViewHolder(@NonNull View view) {
            super(view);
            this.view = view;

            tvCodigoProduto = view.findViewById(R.id.tvCodigoProduto);
            tvCodigo = view.findViewById(R.id.tvCodigo);
            tvDescricao = view.findViewById(R.id.tvDescricao);
            tvQuantidadeProduto = view.findViewById(R.id.tvQuantidadeProduto);
        }
    }
}