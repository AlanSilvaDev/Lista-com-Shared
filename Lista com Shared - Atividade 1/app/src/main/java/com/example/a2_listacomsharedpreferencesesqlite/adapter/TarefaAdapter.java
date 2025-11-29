package com.example.a2_listacomsharedpreferencesesqlite.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.a2_listacomsharedpreferencesesqlite.R;
import com.example.a2_listacomsharedpreferencesesqlite.database.TarefaDAO;
import com.example.a2_listacomsharedpreferencesesqlite.model.Tarefa;
import com.example.a2_listacomsharedpreferencesesqlite.utils.Preferencias;

import java.util.List;

/**
 * Adapter responsável por exibir a lista de Tarefas no ListView.
 *
 * Funções principais:
 *  - Inflar o layout de cada item (item_tarefa.xml)
 *  - Preencher os textos (título e data)
 *  - Aplicar cores de acordo com a prioridade
 *  - Aplicar estilo de "concluída" (texto apagado/cinza)
 *  - Atualizar o status da tarefa no banco de dados (TarefaDAO)
 *  - Respeitar a configuração de ocultar tarefas concluídas (Preferencias)
 *  - Disparar callback em cliques longos para a Activity tratar (editar/apagar, etc.)
 */
public class TarefaAdapter extends ArrayAdapter<Tarefa> {

    // ================================
    // Atributos principais
    // ================================
    private LayoutInflater inflater;
    private TarefaDAO dao;
    private Preferencias prefs;

    // ================================
    // Interface de callback para clique longo
    // ================================
    public interface OnTarefaLongClickListener {
        void onTarefaLongClick(Tarefa tarefa);
    }

    private OnTarefaLongClickListener longClickListener;

    /**
     * Permite que a Activity registre um "ouvinte" de clique longo
     * para tratar ações como editar/apagar tarefa.
     */
    public void setOnTarefaLongClickListener(OnTarefaLongClickListener listener) {
        this.longClickListener = listener;
    }

    // ================================
    // Construtor
    // ================================
    public TarefaAdapter(Context context, List<Tarefa> lista) {
        super(context, 0, lista);
        inflater = LayoutInflater.from(context);
        dao = new TarefaDAO(context);
        prefs = new Preferencias(context);
    }

    // ================================
    // ViewHolder: guarda as referências das Views
    // para evitar vários findViewById() (melhora desempenho)
    // ================================
    private static class ViewHolder {
        TextView txtTitulo;
        TextView txtData;
        CheckBox chkConcluida;
    }

    // ================================
    // getView: monta cada linha do ListView
    // ================================
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        // 1) Inflar a View ou reaproveitar (ViewHolder pattern)
        if (convertView == null) {
            // Cria a nova linha a partir do layout XML
            convertView = inflater.inflate(R.layout.item_tarefa, parent, false);

            // Cria o holder e associa componentes da tela
            holder = new ViewHolder();
            holder.txtTitulo = convertView.findViewById(R.id.txtTitulo);
            holder.txtData   = convertView.findViewById(R.id.txtData);
            holder.chkConcluida = convertView.findViewById(R.id.chkConcluida);

            // Guarda o holder dentro da própria View para reutilizar depois
            convertView.setTag(holder);
        } else {
            // Reaproveita o holder já existente
            holder = (ViewHolder) convertView.getTag();
        }

        // 2) Recuperar a tarefa referente a esta posição
        Tarefa tarefa = getItem(position);
        if (tarefa == null) return convertView;

        // 3) Preencher textos básicos (título e data)
        holder.txtTitulo.setText(tarefa.getTitulo());
        holder.txtData.setText(tarefa.getData());

        // 4) Configurar o CheckBox
        //    Primeiro, remover qualquer listener antigo para evitar "loop" ao reciclar a View
        holder.chkConcluida.setOnCheckedChangeListener(null);
        holder.chkConcluida.setChecked(tarefa.isConcluido());

        // 5) Aplicar cores de prioridade / estilo visual
        //    Obs: a prioridade só vale quando a tarefa NÃO está concluída.
        if (!tarefa.isConcluido()) {
            // Tarefa não concluída → aplica cor de prioridade
            aplicarCorPrioridade(holder, tarefa.getPrioridade());

            // Garante opacidade total nos textos
            holder.txtTitulo.setAlpha(1f);
            holder.txtData.setAlpha(1f);
        } else {
            // Tarefa concluída → texto apagado e cinza
            holder.txtTitulo.setAlpha(0.4f);
            holder.txtData.setAlpha(0.4f);
            holder.txtTitulo.setTextColor(Color.GRAY);
            holder.txtData.setTextColor(Color.GRAY);
        }

        // ================================
        // 6) Listener do CheckBox
        //    - Atualiza a tarefa no banco (DAO)
        //    - Aplica o estilo de concluída / não concluída
        //    - Remove da lista se a opção "ocultar concluídas" estiver ativada
        // ================================
        holder.chkConcluida.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Atualizar objeto em memória
            tarefa.setConcluido(isChecked);

            // Salvar alteração no banco de dados
            dao.atualizar(tarefa);

            if (isChecked) {
                // Estilo de tarefa concluída
                holder.txtTitulo.setAlpha(0.4f);
                holder.txtData.setAlpha(0.4f);
                holder.txtTitulo.setTextColor(Color.GRAY);
                holder.txtData.setTextColor(Color.GRAY);

                // Se o usuário optou por ocultar concluídas, remove da lista e atualiza a tela
                if (prefs.getOcultarConcluidas()) {
                    remove(tarefa);
                    notifyDataSetChanged();
                }
            } else {
                // Voltou a ser NÃO concluída
                holder.txtTitulo.setAlpha(1f);
                holder.txtData.setAlpha(1f);

                // Reaplica cor de prioridade
                aplicarCorPrioridade(holder, tarefa.getPrioridade());
            }
        });

        // ================================
        // 7) Clique simples na linha:
        //    alterna o estado do CheckBox (mais prático para o usuário)
        // ================================
        convertView.setOnClickListener(v -> {
            // Inverte o estado atual do CheckBox
            holder.chkConcluida.setChecked(!holder.chkConcluida.isChecked());
        });

        // ================================
        // 8) Clique longo:
        //    dispara callback para Activity (editar/apagar, etc.)
        // ================================
        convertView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onTarefaLongClick(tarefa);
                return true;
            }
            return false;
        });

        return convertView;
    }

    // ================================
    // Método auxiliar para aplicar a cor conforme a prioridade
    // 1 = baixa  (verde)
    // 2 = média  (amarelo)
    // 3 = alta   (vermelho)
    // ================================
    private void aplicarCorPrioridade(ViewHolder holder, int prioridade) {
        switch (prioridade) {
            case 1:
                // Prioridade BAIXA – verde
                holder.txtTitulo.setTextColor(Color.parseColor("#2E7D32"));
                holder.txtData.setTextColor(Color.parseColor("#2E7D32"));
                break;

            case 2:
                // Prioridade MÉDIA – amarelo
                holder.txtTitulo.setTextColor(Color.parseColor("#F9A825"));
                holder.txtData.setTextColor(Color.parseColor("#F9A825"));
                break;

            case 3:
                // Prioridade ALTA – vermelho
                holder.txtTitulo.setTextColor(Color.parseColor("#C62828"));
                holder.txtData.setTextColor(Color.parseColor("#C62828"));
                break;

            default:
                // Caso não informado, pode-se manter a cor padrão do tema
                holder.txtTitulo.setTextColor(Color.BLACK);
                holder.txtData.setTextColor(Color.DKGRAY);
                break;
        }
    }
}