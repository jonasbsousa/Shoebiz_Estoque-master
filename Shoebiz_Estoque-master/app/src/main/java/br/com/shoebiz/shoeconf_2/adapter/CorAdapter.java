package br.com.shoebiz.shoeconf_2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.shoebiz.shoeconf_2.model.ProdutoFilho;
import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.model.Cor;

public class CorAdapter extends RecyclerView.Adapter<CorAdapter.CorViewHolder> {

    private final List<Cor> cores;
    private final Context context;
    private final CorAdapterOnClickListener corAdapterOnClickListener;

    public interface CorAdapterOnClickListener {
        void onClickCor(View view, int idx);
    }

    public CorAdapter(Context context, List<Cor> cores, CorAdapterOnClickListener corAdapterOnClickListener) {
        this.context = context;
        this.cores = cores;
        this.corAdapterOnClickListener = corAdapterOnClickListener;
    }

    @NonNull
    @Override
    public CorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_cor, viewGroup, false);

        return new CorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CorViewHolder holder, final int position) {
        Cor cor = cores.get(position);
        int qntPares = 0;

        for(ProdutoFilho produtoFilho : cor.produtoFilho) {
            qntPares += produtoFilho.quantidade;
        }

        holder.tvDescCorErp.setText(context.getString(R.string.frag_cp_descCor, cor.codigo, cor.descricaoErp));
        holder.tvDescCorForn.setText(cor.descricaoFornecedor);
        holder.tvQuantTotal.setText(String.valueOf(qntPares));

        holder.rvGrade.setAdapter(new GradeAdapter(context, cor.produtoFilho));

        if (corAdapterOnClickListener != null) {
            holder.itemView.setOnClickListener(view -> corAdapterOnClickListener.onClickCor(holder.view, position));
        }
    }

    @Override
    public int getItemCount() {
        return this.cores != null ? this.cores.size() : 0;
    }

    class CorViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDescCorErp;
        private TextView tvDescCorForn;
        private TextView tvQuantTotal;
        private RecyclerView rvGrade;
        private View view;

        CorViewHolder(View view) {
            super(view);
            this.view = view;

            tvDescCorErp = view.findViewById(R.id.tvDescCorErp);
            tvDescCorForn = view.findViewById(R.id.tvDescCorForn);
            tvQuantTotal = view.findViewById(R.id.tvQuantTotal);
            rvGrade = view.findViewById(R.id.rvGrade);
            rvGrade.setHasFixedSize(true);
            rvGrade.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            rvGrade.setItemAnimator(new DefaultItemAnimator());
        }
    }
}