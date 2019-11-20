package com.inventariostap.mario.controldeinventarios;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Verduras extends AppCompatActivity {

    ImageButton Inicio, Chequeo, Reporte, Buscar;
    RecyclerView lista;
    RecycleViewAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Category> verduras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secos);

        TextView title = (TextView) findViewById(R.id.titulo);
        title.setText("CILINDRO");

        lista = (RecyclerView) findViewById(R.id.Contenedor);
        try {
            lista.setHasFixedSize(true);
        } catch (Exception e) {
            Toast.makeText(Verduras.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        layoutManager = new LinearLayoutManager(this);


        Carga_datos load = new Carga_datos();
        load.execute();

        Buscar = (ImageButton) findViewById(R.id.imageButton20);
        Inicio = (ImageButton) findViewById(R.id.imageButton23);

        Buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inicio = new Intent(Verduras.this, Principal.class);
                startActivity(inicio);
                finish();
            }
        });

    }


    private class Carga_datos extends AsyncTask<Void, Void, ArrayList<Category>> {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(Verduras.this);
            pDialog.setMessage("Cargando datos...");
            pDialog.show();
            long delayInMillis = 3000;
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    pDialog.dismiss();
                }
            }, delayInMillis);

        }


        @Override
        protected ArrayList<Category> doInBackground(Void... params) {

            BaseD bd = new BaseD(Verduras.this, null, null, 1);
            verduras = bd.llenado("Cilindro");
            return verduras;
        }

        @Override
        protected void onPostExecute(ArrayList<Category> verduras) {
            super.onPostExecute(verduras);

            try {
                lista.setLayoutManager(layoutManager);
                adapter = new RecycleViewAdapter(Verduras.this, verduras);
                lista.setAdapter(adapter);
            } catch (Exception e) {
                Toast.makeText(Verduras.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }


        }
    }

}


