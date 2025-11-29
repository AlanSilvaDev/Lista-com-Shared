package com.example.a2_listacomsharedpreferencesesqlite.database;

//Responsável por criar e atulizar o banco de dados SQLite
//Funções principais:
//  - Criar banco e a tabela de tarefas na primeira execução
//  - Gerenciar versões do banco (onUpgrade)

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    //Configurações do BD
    private static final String NOME_BANCO = "tarefas.db";
    private static final int VERSAO = 1;

    //Construtor
    public DBHelper(Context context){
        super(context, NOME_BANCO, null, VERSAO);
    }

    //onCreate (ciclo de vida -> primeira coisa executada)
    //Chamado somente na primeira execução do app
    @Override
    public void onCreate(SQLiteDatabase db){

        //Comando SQL para criação da tabela
        String sql = "CREATE TABLE tarefas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "titulo TEXT NOT NULL," +
                "descricao TEXT," +
                "data TEXT," +
                "prioridade INTEGER," +
                "concluido INTEGER DEFAULT 0" +
                ");";
        db.execSQL(sql); //Executa a criação da tabela
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        //Caso a estrutura do BD evolua no futuro
        db.execSQL("DROP TABLE IF EXISTS tarefas");
        //Recria a tabela com a nova estrutura
        onCreate(db);
    }
}
