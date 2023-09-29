package br.com.shoebiz.shoeconf_2.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.shoebiz.shoeconf_2.model.ProdutoFilho;

public class InventarioProdutoDao {
    private static final String TABELA_INVENTARIO_PRODUTO = "inventario_produtos";

    private static final String COLUNA_ID = "id";
    private static final String COLUNA_CODIGO = "codigo";
    private static final String COLUNA_PRODUTO = "produto";
    private static final String COLUNA_QUANTIDADE = "quantidade";

    private DBOpenHelper dbOpenHelper;

    public InventarioProdutoDao(Context context) {
        dbOpenHelper = new DBOpenHelper(context);
    }

    public long salvar(String codigo, ProdutoFilho produtoFilho) {
        long id = produtoFilho.id;

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(COLUNA_CODIGO, codigo);
            values.put(COLUNA_PRODUTO, produtoFilho.codigo);
            values.put(COLUNA_QUANTIDADE, produtoFilho.quantidade);

            if (id != 0) {
                return db.update(TABELA_INVENTARIO_PRODUTO, values, COLUNA_ID + "=?", new String[]{String.valueOf(id)});
            } else {
                return db.insert(TABELA_INVENTARIO_PRODUTO, "", values);
            }
        } finally {
            db.close();
        }
    }

    public List<ProdutoFilho> buscaPorCodigo(String codigo) {
        List<ProdutoFilho> produtosFilho = new ArrayList<>();
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABELA_INVENTARIO_PRODUTO, null, COLUNA_CODIGO + " = ? ", new String[]{codigo}, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    ProdutoFilho produtoFilho = new ProdutoFilho();
                    produtoFilho.id = cursor.getLong(cursor.getColumnIndex(COLUNA_ID));
                    produtoFilho.codigo = cursor.getString(cursor.getColumnIndex(COLUNA_PRODUTO));
                    produtoFilho.quantidade = cursor.getInt(cursor.getColumnIndex(COLUNA_QUANTIDADE));

                    produtosFilho.add(produtoFilho);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            db.close();
        }

        return produtosFilho;
    }

    public void deletar() {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        try {
            db.delete(TABELA_INVENTARIO_PRODUTO, null, null);
        } finally {
            db.close();
        }
    }
}