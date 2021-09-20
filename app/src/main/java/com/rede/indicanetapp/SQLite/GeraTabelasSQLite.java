package com.rede.indicanetapp.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rede.indicanetapp.Toolbox.VariaveisGlobais;

/**
 * DDL SQLite para gerar cadeia de tabelas no banco embarcado
 * RETORNO:
 * 0                      - Tabela existente
 * 1                      - Tabela criada com sucesso
 *
 * @author Igor Maximo
 * @criado 19/02/2019
 * @editado 02/03/2019
 */
public class GeraTabelasSQLite extends SQLiteOpenHelper {

    private static final String ID = "_id";

    private static final String tabelaGerenciamento = "gerenciamento";

    public final String sqlCriaTabelaGerenciamento = "CREATE TABLE " + tabelaGerenciamento + " ("
            + ID + " integer, "
            + "usuario" + " text,"
            + "senha" + " text"
            + ")";

    private static GeraTabelasSQLite instance;

    public static synchronized GeraTabelasSQLite getHelper(Context context) {
        if (instance == null)
            instance = new GeraTabelasSQLite(context);
        return instance;
    }

    public GeraTabelasSQLite(Context context) {
        super(context, VariaveisGlobais.NOME_BANCO, null, VariaveisGlobais.VERSAO_DB);
    }

    // Metodo que abre conexao com banco sqlite
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
    // Método nativo que starta a criação da tabela sqlite
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(sqlCriaTabelaGerenciamento);
            System.err.println("Tabela Gerenciamento criada com sucesso! SQLiteGeraTabelaGerenciamento " + sqlCriaTabelaGerenciamento);
        } catch (Exception e) {
            System.err.println("Erro ao criar tabelas  - SQLiteGeraTabela !");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + tabelaGerenciamento);
            try {
                onCreate(db);
                System.err.println("Tabela Dropada! " + tabelaGerenciamento);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }
}