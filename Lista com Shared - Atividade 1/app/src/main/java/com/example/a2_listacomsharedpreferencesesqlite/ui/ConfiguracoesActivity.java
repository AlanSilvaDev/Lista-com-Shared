package com.example.a2_listacomsharedpreferencesesqlite.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a2_listacomsharedpreferencesesqlite.R;
import com.example.a2_listacomsharedpreferencesesqlite.utils.Preferencias;

/**
 * Activity de configurações gerais do aplicativo.
 *
 * Funções implementadas:
 *   - Carregar preferências salvas (SharedPreferences)
 *   - Alterar a opção "Ocultar tarefas concluídas"
 *   - Salvar as configurações ao clicar no botão
 *
 * Observações:
 *   - O recurso de ordenação foi removido conforme solicitado.
 *   - A Activity retorna para a MainActivity, que faz o recarregamento
 *     da lista automaticamente no onResume().
 */
public class ConfiguracoesActivity extends AppCompatActivity {

    // ================================
    // Componentes da interface
    // ================================
    private CheckBox chkOcultarConcluidas;
    private Button btnSalvarConfig;

    // Classe de preferências (SharedPreferences)
    private Preferencias prefs;

    // ================================
    // Ciclo de vida — onCreate
    // ================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        // Inicializar controlador de preferências
        prefs = new Preferencias(this);

        // Recuperar componentes da interface
        chkOcultarConcluidas = findViewById(R.id.chkOcultarConcluidas);
        btnSalvarConfig = findViewById(R.id.btnSalvarConfig);

        // Carregar estado salvo anteriormente
        carregarPreferencias();

        // Botão de salvar
        btnSalvarConfig.setOnClickListener(v -> salvar());
    }

    // ================================
    // Carregar os valores salvos no SharedPreferences
    // ================================
    private void carregarPreferencias() {
        // Define se tarefas concluídas serão ocultadas
        chkOcultarConcluidas.setChecked(prefs.getOcultarConcluidas());
    }

    // ================================
    // Salvar alterações feitas pelo usuário
    // ================================
    private void salvar() {

        // Salva valor da checkbox
        prefs.setOcultarConcluidas(chkOcultarConcluidas.isChecked());

        // Feedback rápido ao usuário
        Toast.makeText(this, "Configurações salvas!", Toast.LENGTH_SHORT).show();

        // Finaliza esta tela e retorna para a MainActivity
        finish();
    }
}