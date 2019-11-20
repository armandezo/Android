package com.inventariostap.mario.controldeinventarios;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ReporteEspecial extends AppCompatActivity {

    ImageButton Inicio, Chequeo, Reporte, Generar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_especial);

        Inicio = (ImageButton) findViewById(R.id.imageButton11);
        Chequeo = (ImageButton) findViewById(R.id.imageButton10);
        Reporte = (ImageButton) findViewById(R.id.imageButton9);
        Generar = (ImageButton) findViewById(R.id.imageButton12);

        Inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inicio = new Intent(ReporteEspecial.this, Principal.class);
                startActivity(inicio);
                finish();
            }
        });

        Chequeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inicio = new Intent(ReporteEspecial.this, ReporteEspecial.class);
                startActivity(inicio);
                finish();
            }
        });

        Reporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inicio = new Intent(ReporteEspecial.this, ReporteGeneral.class);
                startActivity(inicio);
                finish();
            }
        });

        Generar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
