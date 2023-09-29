package br.com.shoebiz.shoeconf_2.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.shoebiz.shoeconf_2.model.Inventario;

public class InventarioDao {
    private static final String TABELA_INVENTARIO = "inventario";

    private static final String COLUNA_ID = "id";
    private static final String COLUNA_CODIGO = "codigo";
    private static final String COLUNA_INTEGRADO = "integrado";

    private DBOpenHelper dbOpenHelper;

    public InventarioDao(Context context) {
        this.dbOpenHelper = new DBOpenHelper(context);
    }

    public long salvar(Inventario inventario) {
        long id = inventario.id;

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(COLUNA_CODIGO, inventario.area);
            values.put(COLUNA_INTEGRADO, inventario.integrado);

            if (id != 0) {
                return db.update(TABELA_INVENTARIO, values, COLUNA_ID + "=?", new String[]{String.valueOf(id)});
            } else {
                return db.insert(TABELA_INVENTARIO, "", values);
            }
        } finally {
            db.close();
        }
    }

    public int quantidade() {
        int quantidade;
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        Cursor cursor = null;

        try {
            cursor = db.query(TABELA_INVENTARIO, null, null, null, null, null, null);

            quantidade = cursor.getCount();
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            db.close();
        }

        return quantidade;
    }

    public List<Inventario> buscaTodos() {
        List<Inventario> inventarios = new ArrayList<>();
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABELA_INVENTARIO, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    Inventario inventario = new Inventario();
                    inventario.id = cursor.getLong(cursor.getColumnIndex(COLUNA_ID));
                    inventario.area = cursor.getString(cursor.getColumnIndex(COLUNA_CODIGO));
                    inventario.integrado = cursor.getString(cursor.getColumnIndex(COLUNA_INTEGRADO));

                    inventarios.add(inventario);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            db.close();
        }

        return inventarios;
    }

    public Inventario buscaPorCodigo(String codigo)  {
        Inventario inventario = null;
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        Cursor cursor = null;

        try {
            cursor = db.query(TABELA_INVENTARIO, null, COLUNA_CODIGO + " = ? ", new String[]{codigo}, null, null, null);

            if (cursor.moveToFirst()) {
                inventario = new Inventario();
                inventario.area = cursor.getString(cursor.getColumnIndex(COLUNA_CODIGO));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            db.close();
        }

        return inventario;
    }

    public void deletar() {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        try {
            db.delete(TABELA_INVENTARIO, null, null);
        } finally {
            db.close();
        }
    }
}