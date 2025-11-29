package com.example.a2_listacomsharedpreferencesesqlite.database;

//DAO - Data Acess Object - Objeto de Acesso a Dados
//Classe é responsável por todas as operações de CRUD no banco SQLite
//Funções:
//  - Inserir nova tarefa
//  - Atualizar tarefa existente
//  - Excluir tarefa
//  - Listar tarefas
//Centralizamos aqui toda a lógica de acesso ao banco de dados.

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.a2_listacomsharedpreferencesesqlite.model.Tarefa;
import com.example.a2_listacomsharedpreferencesesqlite.utils.Preferencias;

import java.util.ArrayList;

public class TarefaDAO {

    private final DBHelper dbHelper;
    private final Context context;

    //Construtor
    public TarefaDAO(Context context) {
        this.context = context;
        this.dbHelper = new DBHelper(context);
    }

    //INSERIR - cria uma nova tarefa no BD
    public long inserir(Tarefa t) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("titulo", t.getTitulo());
        valores.put("descricao", t.getDescricao());
        valores.put("data", t.getData());
        valores.put("prioridade", t.getPrioridade());
        valores.put("concluido", t.isConcluido() ? 1 : 0);
        //? equivale ao if (condição verdade)
        //: equivale ao else (condição falsa)

        long idGerado = db.insert("tarefas",
                null, valores);
        db.close();

        return idGerado;
    }

    //ATUALIZAR - modifica os dados de uma tarefa existente
    public int atualizar(Tarefa t) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("titulo", t.getTitulo());
        valores.put("descricao", t.getDescricao());
        valores.put("data", t.getData());
        valores.put("prioridade", t.getPrioridade());
        valores.put("concluido", t.isConcluido() ? 1 : 0);
        //? equivale ao if (condição verdade)
        //: equivale ao else (condição falsa)

        int linhasAfetadas = db.update(
                "tarefas",
                valores,
                "id = ?",
                new String[]{String.valueOf(t.getId())}
        );
        db.close();

        return linhasAfetadas;
    }

    //DELETAR - remove uma tarefa pelo ID
    public int deletar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int linhasAfetadas = db.delete(
                "tarefas",
                "id = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return linhasAfetadas;
    }

    //LISTAR - retorna todas tarefas do BD
    public ArrayList<Tarefa> listar() {

        ArrayList<Tarefa> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Carregar as preferêcias do usuário
        Preferencias prefs = new Preferencias(context);
        boolean ocultarConcluidas = prefs.getOcultarConcluidas();

        //Base da consulta
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT id, titulo, descricao, data, prioridade, concluido ");
        sql.append("FROM tarefas ");

        //FILTRO: ocultar concluídas
        if (ocultarConcluidas) {
            sql.append("WHERE concluido = 0");
        }

        //Executar consulta
        Cursor cursor = db.rawQuery(sql.toString(), null);

        if (cursor.moveToFirst()) {
            do {
                Tarefa t = new Tarefa();
                t.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                t.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow("titulo")));
                t.setDescricao(cursor.getString(cursor.getColumnIndexOrThrow("descricao")));
                t.setData(cursor.getString(cursor.getColumnIndexOrThrow("data")));
                t.setPrioridade(cursor.getInt(cursor.getColumnIndexOrThrow("prioridade")));
                t.setConcluido(cursor.getInt(cursor.getColumnIndexOrThrow("concluido")) == 1);

                lista.add(t);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;

    }

}
