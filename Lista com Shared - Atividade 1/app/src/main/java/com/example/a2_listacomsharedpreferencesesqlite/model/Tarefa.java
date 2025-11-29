package com.example.a2_listacomsharedpreferencesesqlite.model;

//Modelo de dados da Tarefa que será gravada e lida
public class Tarefa implements java.io.Serializable {

    //Campos armazenados
    //id        : identifacor único no BD (primary key)
    //titulo    : texto principal da tarefa
    //descrição : detalhes adicionais
    //data
    //prioridade: baixa, média, alta
    //concluido : true = concluída, false = pendente

    //Atributos
    private int id;
    private String titulo, descricao, data;
    private int prioridade;
    private boolean concluido;

    //Construtor padrão (necessário para Cursor (e firebase, caso implementado))
    public Tarefa(){

    }

    //Construtor completo
    public Tarefa(int id, String titulo, String descricao, String data, int prioridade, boolean concluido) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.data = data;
        this.prioridade = prioridade;
        this.concluido = concluido;
    }

    //Getters e Setters

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public boolean isConcluido() {
        return concluido;
    }

    public void setConcluido(boolean concluido) {
        this.concluido = concluido;
    }

}
