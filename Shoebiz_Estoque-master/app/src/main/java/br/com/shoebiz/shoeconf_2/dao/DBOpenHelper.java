package br.com.shoebiz.shoeconf_2.dao;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import br.com.shoebiz.shoeconf_2.R;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "confmobile.sqlite";
    private static final int VERSAO_BANCO = 6;

    private Context context;

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSAO_BANCO);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        lerEExecutarSQLScript(db, context, R.raw.db_create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        log("****** Dropando tabelas");
        lerEExecutarSQLScript(db, context, R.raw.db_drop);
        onCreate(db);
    }

    private void lerEExecutarSQLScript(SQLiteDatabase db, Context context, Integer sqlScriptResId) {
        Resources res = context.getResources();

        try {
            InputStream is = res.openRawResource(sqlScriptResId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            executarSQLScript(db, reader);
            reader.close();
            is.close();
        } catch (IOException e) {
            throw new RuntimeException("Não foi possivel ler o arquivo SQLite", e);
        }
    }

    private void executarSQLScript(SQLiteDatabase db, BufferedReader reader) throws IOException {
        String line;
        StringBuilder statement = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            statement.append(line);
            statement.append("\n");

            if (line.endsWith(";")) {
                String toExec = statement.toString();
                log("Executing script: " + toExec);
                db.execSQL(toExec);
                statement = new StringBuilder();
            }
        }
    }

    private void log(String msg) {
        Log.d(DBOpenHelper.class.getSimpleName(), msg);
    }
}