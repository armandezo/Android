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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Frutas extends AppCompatActivity {
    ImageButton Inicio, Buscar;
    RecyclerView lista;
    RecycleViewAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Category> frutas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secos);

        TextView title = (TextView) findViewById(R.id.titulo);
            title.setText("PARRILLA");


        Carga_datos load = new Carga_datos();
        load.execute();
        //METODOS PARA EL LLENADO DE LA LISTVIEW

        lista = (RecyclerView) findViewById(R.id.Contenedor);
        lista.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        //SE REFERENCIAN LOS ELEMENTOS CON LAS VISTAS
        Buscar = (ImageButton) findViewById(R.id.imageButton20);
        Inicio = (ImageButton) findViewById(R.id.imageButton23);


        Inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//AL PRECIONAR EL BOTON INICIO
                Intent inicio = new Intent(Frutas.this, Principal.class);
                startActivity(inicio);//SE RETORNA A LA PAGUINA PRINCIPAL
                finish();
            }
        });

    }


    private class Carga_datos extends AsyncTask<Void, Void, ArrayList<Category>> {//METODO PARA REALIZAR TAREA EN SEGUNDO PLANO
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {//METODO QUE EJECUTA LOS PROCESOS
            pDialog = new ProgressDialog(Frutas.this);//MENSAJE DE PROCESO
            pDialog.setMessage("Cargando datos...");
            pDialog.show();

            long delayInMillis = 2000;//SE LE AIGNA UN TIEMPO DE ESPERA DE 2 SEGUNDOS
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    pDialog.dismiss();
                }
            }, delayInMillis);

        }


        @Override
        protected ArrayList<Category> doInBackground(Void... params) {//MIENTRAS TANTO EN SEGUNDO PLANO SE EJECUTA A LA PAR
            BaseD bd = new BaseD(Frutas.this, null, null, 1);
            frutas = bd.llenado("Parrilla");//SE LLENA EL ARRAY CON LOS DATOS EXTRAIDOR DE LA BASE DE DATOS
            return frutas;
        }

        @Override
        protected void onPostExecute(ArrayList<Category> frutas) {//UNA VEZ TERMINADO EL PROCESO EN SEGUN PLANO SE RELAIZA
            super.onPostExecute(frutas);
            lista.setLayoutManager(layoutManager);
            adapter = new RecycleViewAdapter(Frutas.this, frutas);//SE RELLENA EL ADAPTADOR CON LOS DAOTS EXTRAIDOS
            lista.setAdapter(adapter);//SE POBLA AL RECYCLEVIEW


        }
    }
}
