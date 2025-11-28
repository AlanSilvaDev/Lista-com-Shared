package com.example.a01primeiro;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView txtContador;
    private Button btnMenos, btnReset, BtnMais;
    private int contador = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Liga os Componentes do XML ao Java
        txtContador = findViewById(R.id.txtContador);
        btnMenos = findViewById(R.id.btnMenos);
        btnReset = findViewById(R.id.btnReset);
        BtnMais = findViewById(R.id.btnMais);

        atualizarTexto();

        btnMenos.setOnClickListener(v -> {
            contador--;
            atualizarTexto();
        });

        BtnMais.setOnClickListener(v -> {
            contador++;
            atualizarTexto();
        });

        btnReset.setOnClickListener(v -> {
            contador = 0;
            atualizarTexto();
        });
    }

    private void atualizarTexto() {
        txtContador.setText(String.valueOf(contador));
    }
}