package com.example.a2_listacomsharedpreferencesesqlite;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a2_listacomsharedpreferencesesqlite.adapter.TarefaAdapter;
import com.example.a2_listacomsharedpreferencesesqlite.database.TarefaDAO;
import com.example.a2_listacomsharedpreferencesesqlite.model.Tarefa;
import com.example.a2_listacomsharedpreferencesesqlite.ui.ConfiguracoesActivity;
import com.example.a2_listacomsharedpreferencesesqlite.ui.FormTarefaActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * Activity principal do aplicativo.
 * <p>
 * Responsabilidades:
 * - Exibir a lista de tarefas em um ListView
 * - Abrir o formulário para criar/editar tarefa
 * - Excluir tarefas (com confirmação)
 * - Abrir a tela de configurações (menu)
 * - Recarregar a lista ao retornar para a tela
 */
public class MainActivity extends AppCompatActivity {

    // ================================
    // Componentes de interface
    // ================================
    private ListView listViewTarefas;
    private FloatingActionButton fabAdicionar;

    // ================================
    // Objetos de dados / adapter
    // ================================
    private ArrayList<Tarefa> listaTarefas;
    private TarefaAdapter adapter;
    private TarefaDAO dao;

    // ================================
    // Ciclo de vida — onCreate
    // ================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ativa layout em tela cheia com tratamento das bordas (status bar / navigation bar)
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ajusta padding da view raiz para não ficar "por baixo" da barra de status
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Toolbar personalizada (MaterialToolbar)
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicializa DAO de tarefas
        dao = new TarefaDAO(this);

        // Referências dos componentes visuais
        listViewTarefas = findViewById(R.id.listViewTarefas);
        fabAdicionar = findViewById(R.id.fabAdicionar);

        // Botão flutuante: abrir tela de formulário para NOVA tarefa
        fabAdicionar.setOnClickListener(v -> abrirFormulario(null));
    }

    // ================================
    // Opções da tarefa (clique longo)
    // Chamado a partir do callback do Adapter
    // ================================
    private void mostrarOpcoesTarefa(Tarefa tarefa) {
        // Opções disponíveis ao usuário
        CharSequence[] opcoes = {"Editar", "Excluir", "Cancelar"};

        new AlertDialog.Builder(this)
                .setTitle(tarefa.getTitulo())
                .setItems(opcoes, (dialog, which) -> {
                    if (which == 0) {
                        // Editar
                        abrirFormulario(tarefa);
                    } else if (which == 1) {
                        // Excluir: chama método que confirma antes de deletar
                        confirmarExclusao(tarefa);
                    }
                    // which == 2 → "Cancelar" → não faz nada
                })
                .show();
    }

    // ================================
    // Menu de opções (Toolbar)
    // ================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla o menu_main.xml na Toolbar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Trata cliques nos itens do menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_configuracoes) {
            // Abre ConfiguraçõesActivity
            startActivity(new Intent(this, ConfiguracoesActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ================================
    // onResume: chamado sempre que a Activity volta para a tela
    // (após criar/editar tarefa ou mudar configurações)
    // ================================
    @Override
    protected void onResume() {
        super.onResume();
        carregarLista();
    }

    // ================================
    // Carregar lista de tarefas do banco e configurar Adapter
    // ================================
    private void carregarLista() {
        // Busca tarefas no banco
        listaTarefas = dao.listar();

        // Cria novo Adapter com a lista obtida
        adapter = new TarefaAdapter(this, listaTarefas);

        // Configura callback de clique longo para que o Adapter
        // notifique a MainActivity quando o usuário segurar uma tarefa
        adapter.setOnTarefaLongClickListener(this::mostrarOpcoesTarefa);

        // Liga o Adapter ao ListView
        listViewTarefas.setAdapter(adapter);
    }

    // ================================
    // Abrir formulário de tarefa
    //  - Se tarefa == null → criar nova
    //  - Se tarefa != null → editar existente
    // ================================
    private void abrirFormulario(Tarefa tarefa) {
        Intent intent = new Intent(this, FormTarefaActivity.class);

        if (tarefa != null) {
            // Envia a tarefa selecionada para edição
            intent.putExtra("tarefa", tarefa);
        }

        startActivity(intent);
    }

    // ================================
    // Confirmar exclusão de uma tarefa
    // ================================
    private void confirmarExclusao(Tarefa tarefa) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir tarefa")
                .setMessage("Deseja realmente excluir esta tarefa?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    // Remove do banco
                    dao.deletar(tarefa.getId());
                    // Recarrega a lista para atualizar a interface
                    carregarLista();
                    Toast.makeText(this, "Tarefa excluída!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Não", null)
                .show();
    }
}