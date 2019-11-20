package com.inventariostap.mario.controldeinventarios;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ReporteGeneral extends AppCompatActivity {

    ImageButton Inicio, Chequeo, Reporte;
    Button Enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_general);

        Enviar = (Button) findViewById(R.id.button);
        Inicio = (ImageButton) findViewById(R.id.imageButton15);
        Chequeo = (ImageButton) findViewById(R.id.imageButton14);
        Reporte = (ImageButton) findViewById(R.id.imageButton13);

        Inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inicio = new Intent(ReporteGeneral.this, Principal.class);
                startActivity(inicio);
                finish();
            }
        });

        Chequeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chequeo = new Intent(ReporteGeneral.this, ReporteEspecial.class);
                startActivity(chequeo);
                finish();
            }
        });

        Reporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reporte = new Intent(ReporteGeneral.this, ReporteGeneral.class);
                startActivity(reporte);
                finish();
            }
        });

        Enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
