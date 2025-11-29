package com.example.a2_listacomsharedpreferencesesqlite.utils;

import android.content.Context;
import android.content.SharedPreferences;

//SharedPreferences
//  - Armazena dados simples (int, boolean, string) no formato chave-valor
//  - É usado para configurações, preferências de usuário
//  - Não é um bd; não serve para listas grandes nem dados complexos

public class Preferencias {

    private static final String NOME_ARQUIVO = "configuracoes_app";
    private SharedPreferences prefs;

    public Preferencias(Context context){
        prefs = context.getSharedPreferences(NOME_ARQUIVO,
                Context.MODE_PRIVATE);
    }

    //Ocultar tarefas concluídas
    public void setOcultarConcluidas(boolean valor){
        prefs.edit().putBoolean("ocultarConcluidas", valor).apply();
    }

    public boolean getOcultarConcluidas(){
        return prefs.getBoolean("ocultarConcluidas", false);
    }
}
