package br.com.shoebiz.shoeconf_2.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.com.shoebiz.shoeconf_2.model.Produto;

public class ProdutoDao {
    private static final String TABELA_PRODUTO = "produto";

    private static final String COLUNA_PRODUTO = "codigo";

    private DBOpenHelper dbOpenHelper;

    public ProdutoDao(Context context) {
        this.dbOpenHelper = new DBOpenHelper(context);
    }

    public long salvar(String codigo) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(COLUNA_PRODUTO, codigo);

            return db.insert(TABELA_PRODUTO, "", values);
        } finally {
            db.close();
        }
    }

    public void deletar() {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        try {
            db.delete(TABELA_PRODUTO, null, null);
        } finally {
            db.close();
        }
    }

    public Produto buscaPorCodigo(String codigo)  {
        Produto produto = null;
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        Cursor cursor = null;

        try {
            cursor = db.query(TABELA_PRODUTO, null, COLUNA_PRODUTO + " = ? ", new String[]{codigo}, null, null, null);

            if (cursor.moveToFirst()) {
                produto = new Produto();
                produto.codigoPai = cursor.getString(cursor.getColumnIndex(COLUNA_PRODUTO));
            }
        } finally {
             if (cursor != null) {
                 cursor.close();
             }

             db.close();
        }

        return produto;
    }
}