package br.com.shoebiz.shoeconf_2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.model.Cor;
import br.com.shoebiz.shoeconf_2.model.NotaPedido;
import br.com.shoebiz.shoeconf_2.fragment.dialog.InfoGtinDialog;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class DanfePedidoAdapter extends RecyclerView.Adapter<DanfePedidoAdapter.DanfePedidoHolder> {

    private final List<NotaPedido> notaPedidos;
    private final Context context;

    private int posicaoPedido;

    public DanfePedidoAdapter(Context context, List<NotaPedido> notaPedidos) {
        this.context = context;
        this.notaPedidos = notaPedidos;
    }

    @NonNull
    @Override
    public DanfePedidoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_danfe_pedido, viewGroup, false);

        return new DanfePedidoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DanfePedidoHolder holder, int position) {
        NotaPedido danfePedido = notaPedidos.get(position);

        this.posicaoPedido = position;

        holder.tvPedido.setText(context.getString(R.string.frag_vd_pedido, danfePedido.codigo));
        holder.tvCodProduto.setText(danfePedido.codigoPai);
        holder.tvCodResumido.setText(danfePedido.codigoResumido);
        holder.tvDescProduto.setText(danfePedido.descricao);
        holder.rvDanfeCores.setAdapter(new CorAdapter(context, danfePedido.cores, onClick()));

        if (danfePedido.pedidoOk.equals("N")) {
            holder.rlInfoGradePedido.setVisibility(View.GONE);
            holder.tvTextoSemPedido.setVisibility(View.VISIBLE);

            Utils.toast(context, "Pedido: " + danfePedido.codigo.toUpperCase().trim() + " excluído!");
        }
    }

    private CorAdapter.CorAdapterOnClickListener onClick() {
        return (view, idx) -> {
            Cor cor = notaPedidos.get(posicaoPedido).cores.get(idx);

            if (cor != null) {
                FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();

                InfoGtinDialog.show(manager, cor);
            } else {
                Utils.toast(context, "Problema ao carregar a grade da cor!");
            }
        };
    }

    @Override
    public int getItemCount() {
        return this.notaPedidos != null ? this.notaPedidos.size() : 0;
    }

    class DanfePedidoHolder extends RecyclerView.ViewHolder {
        private TextView tvPedido;
        private TextView tvCodProduto;
        private TextView tvCodResumido;
        private TextView tvDescProduto;
        private TextView tvTextoSemPedido;
        private RelativeLayout rlInfoGradePedido;
        private RecyclerView rvDanfeCores;

        DanfePedidoHolder(View view) {
            super(view);

            tvPedido = view.findViewById(R.id.tvPedido);
            tvCodProduto = view.findViewById(R.id.tvCodProduto);
            tvCodResumido = view.findViewById(R.id.tvCodResumido);
            tvDescProduto = view.findViewById(R.id.tvDescProduto);
            tvTextoSemPedido = view.findViewById(R.id.tvTextoSemPedido);

            rlInfoGradePedido = view.findViewById(R.id.rlInfoGradePedido);

            rvDanfeCores = view.findViewById(R.id.rvDanfeCores);
            rvDanfeCores.setHasFixedSize(true);
            rvDanfeCores.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            rvDanfeCores.setItemAnimator(new DefaultItemAnimator());
        }
    }
}