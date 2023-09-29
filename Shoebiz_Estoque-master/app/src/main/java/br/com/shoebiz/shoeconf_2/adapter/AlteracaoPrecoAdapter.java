package br.com.shoebiz.shoeconf_2.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.fragment.AlteracaoPrecoFragment;
import br.com.shoebiz.shoeconf_2.model.AlteracaoPreco;
import br.com.shoebiz.shoeconf_2.model.Produto;
import br.com.shoebiz.shoeconf_2.utils.Utils;

@SuppressWarnings("unchecked")
public class AlteracaoPrecoAdapter extends RecyclerView.Adapter<AlteracaoPrecoAdapter.AlteracaoPrecoHolder> implements Filterable {
    private final DecimalFormat DECIMALFORMAT = new DecimalFormat(Utils.maskMoney());

    private List<Produto> produtos;
    private final List<Produto> filtroProdutos;
    private final Context context;
    private final AlteracaoOnClickListener alteracaoOnClickListener;
    private final boolean isAlteracaoCor;
    private CustomFilter customFilter;

    public interface AlteracaoOnClickListener {
        void onClickAlteracaoPreco(View view, int idx, boolean isAlteracaoCor);
    }

    public AlteracaoPrecoAdapter(Context context, List<Produto> produtos, AlteracaoOnClickListener alteracaoOnClickListener, boolean isAlteracaoCor) {
        this.context = context;
        this.produtos = produtos;
        this.filtroProdutos = produtos;
        this.alteracaoOnClickListener = alteracaoOnClickListener;
        this.isAlteracaoCor = isAlteracaoCor;
    }

    @Override
    public Filter getFilter() {
        if (customFilter == null) {
            customFilter = new CustomFilter(filtroProdutos, this);
        }

        return customFilter;
    }

    @NonNull
    @Override
    public AlteracaoPrecoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_alteracao_preco, viewGroup, false);

        return new AlteracaoPrecoHolder(view);
    }

    @Override
    @SuppressLint({"RecyclerView", "SimpleDateFormat"})
    public void onBindViewHolder(@NonNull final AlteracaoPrecoHolder holder, final int position) {
        Produto produto = produtos.get(position);

        if (produto.tipoAlteracao.toUpperCase().equals("A")) {
            holder.ivSetaAlteracao.setImageResource(R.drawable.ic_arrow_drop_up);
        } else if (produto.tipoAlteracao.toUpperCase().equals("B")) {
            holder.ivSetaAlteracao.setImageResource(R.drawable.ic_arrow_drop_down);
        }

        try {
            if (this.isAlteracaoCor) {
                holder.tvCodigo.setText(context.getString(R.string.frag_ap_codigo_cod, produto.codigoPai.substring(0, 6), produto.codigoPai.substring(6, 9), produto.codigoPai.substring(9, 11)));
            } else {
                holder.tvCodigo.setText(context.getString(R.string.frag_ap_codigo, produto.codigoPai.substring(0, 6), produto.codigoPai.substring(6, 9)));
            }
        } catch (StringIndexOutOfBoundsException e) {
            holder.tvCodigo.setText("...");
        }

        holder.tvDataAlteracao.setText(new SimpleDateFormat(Utils.formatoData()).format(produto.dataAlteracao));
        holder.tvDescricao.setText(produto.descricao);
        holder.tvQuantidade.setText(String.valueOf(produto.quantidadeEstoque));
        holder.tvPrecoDe.setText(DECIMALFORMAT.format(produto.precoAnterior));
        holder.tvPrecoPor.setText(DECIMALFORMAT.format(produto.precoAtual));

        if (alteracaoOnClickListener != null) {
            holder.itemView.setOnClickListener(v -> alteracaoOnClickListener.onClickAlteracaoPreco(holder.view, position, this.isAlteracaoCor));
        }
    }

    @Override
    public int getItemCount() {
        return this.produtos != null ? this.produtos.size() : 0;
    }

    static class AlteracaoPrecoHolder extends RecyclerView.ViewHolder {
        private final TextView tvCodigo;
        private final TextView tvDataAlteracao;
        private final TextView tvDescricao;
        private final TextView tvQuantidade;
        private final TextView tvPrecoDe;
        private final TextView tvPrecoPor;
        private final ImageView ivSetaAlteracao;
        private final View view;

        AlteracaoPrecoHolder(View view) {
            super(view);
            this.view = view;

            tvCodigo = view.findViewById(R.id.tvCodigo);
            tvDataAlteracao = view.findViewById(R.id.tvDataAlteracao);
            tvDescricao = view.findViewById(R.id.tvDescricao);
            tvQuantidade = view.findViewById(R.id.tvQuantidade);
            tvPrecoDe = view.findViewById(R.id.tvPrecoDe);
            tvPrecoPor = view.findViewById(R.id.tvPrecoPor);
            ivSetaAlteracao = view.findViewById(R.id.ivSetaAlteracao);
        }
    }

    public List<Produto> getListProduto() {
        return this.produtos;
    }

    public class CustomFilter extends Filter {
        AlteracaoPrecoAdapter alteracaoPrecoAdapter;
        List<Produto> produtos;

        CustomFilter(List<Produto> produtos, AlteracaoPrecoAdapter alteracaoPrecoAdapter) {
            this.alteracaoPrecoAdapter = alteracaoPrecoAdapter;
            this.produtos = produtos;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if(constraint != null && constraint.length() > 0) {
                constraint = constraint.toString().toUpperCase();

                if (!constraint.equals("TODOS")) {
                    List<Produto> produtosFiltrados = new ArrayList<>();

                    for (int i=0; i < filtroProdutos.size(); i++) {
                        if (filtroProdutos.get(i).grupo.descricao.toUpperCase().contains(constraint)) {
                            produtosFiltrados.add(filtroProdutos.get(i));
                        }
                    }

                    filterResults.count = produtosFiltrados.size();
                    filterResults.values = produtosFiltrados;
                } else {
                    filterResults.count = produtos.size();
                    filterResults.values = produtos;
                }
            } else {
                filterResults.count = produtos.size();
                filterResults.values = produtos;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            alteracaoPrecoAdapter.produtos = (List<Produto>) results.values;
            alteracaoPrecoAdapter.notifyDataSetChanged();
        }
    }
}