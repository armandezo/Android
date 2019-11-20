package com.inventariostap.mario.controldeinventarios;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class Principal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ImageButton Calendario, Inicio, Chequeo, Reporte, search;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search = (ImageButton) findViewById(R.id.search);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);//SE REFENCIA EL BOTON FLOTANTE
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Estas a punto de crear un nuevo Artículo en la Base de Datos", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent inicio = new Intent(Principal.this, AgregarArticulo.class);// SE ENCIA A LA VENTANNA DE CREAR UN ARTICULO
                startActivity(inicio);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);//SE REFENCIA EL PANEL LATERAL
        navigationView.setNavigationItemSelectedListener(this);

        TextView fecha = (TextView) findViewById(R.id.textView10);
        BaseD db = new BaseD(this, null, null, 1);
        //OBTENER LA FECHA DE ACTUALIZACION DE INVENTARIO
        if (db.fecha_exist()) {
            String date = db.get_fecha();
            fecha.setText("DatWIF: " + date);
        } else {
            fecha.setText("No hay actualizaciones en la fecha");
        }

        final Activity activity = this;


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseD db = new BaseD(Principal.this, null, null, 1);
                IntentIntegrator integrator = new IntentIntegrator(activity);//SE CREA VARIABLE DE TIPO INTENTINTEGRATOR, PARA QUE SE INTEGRE TODO DENTRO DE LA MIASMA ACTIVITY
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);//SE FIJAN LOS TIPOS DE CÓDIGO QUE SE DESEAN LEER
                integrator.setPrompt("Scan");//SE PONE LA ACCIÓN QUE EL MÓDULO DEBE DE HACER
                integrator.setCameraId(0);//FIJA UN ID DE CAMARA NECESARIO PARA SU USO EN EL LECTOR DE CÓDIGOS
                integrator.setBeepEnabled(false);//SE PONE QUE NO SE DESEA HACER ACCIÓN EXTRA TRAS LA CAPTURA DEL CÓDIGO
                integrator.setBarcodeImageEnabled(false);//SE COLOCA QUE NO SE QUEDE FIJA LA IMAGEN, PARA QUE TERMINE LA VISTA TRAS LA LECTURA DEL CÓDIGO
                integrator.initiateScan();//METODO PARA INCIAR EL ESCANER
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);//CREA VARIABLE TIPO INTENT RESULT QUE INTEGRE LO QUE SE VAYA HACIENDO DURANTE EL PROCESO

        if (result.getContents() == null) {// CHECA SI EL CONTENIDO DE LA VARIABLE ES NULO
            Log.d("AgregarArticulo", "Cancelled scan");//MUESTRA EN ANDROID MONITOR LA VISUALIZACIÓN DE ESTAS CADENAS.
            Toast.makeText(this, "Escaneo Cancelado", Toast.LENGTH_LONG).show();
        } else {
            Log.d("AgregarArticuo", "Scanned");//MUESTRA EN ANDROID MONITOR LA VISUALIZACIÓN DE ESTAS CADENAS
            Toast.makeText(this, "Formato: " + result.getFormatName(), Toast.LENGTH_LONG).show(); // MUESTRA EL FORMATO DEL CODIGO LEIDO EN UN TOAST

            BaseD db = new BaseD(Principal.this, null, null, 1);
            Category busqueda = db.search_code(result.getContents().toString());

            AlertDialog.Builder buil = new AlertDialog.Builder(Principal.this);
            buil.setTitle(busqueda.getTitle());
            buil.setMessage(busqueda.getCantidad());
            buil.setCancelable(false);
            buil.setPositiveButton("Ok", null);
            buil.show();

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.principal) {
            Toast.makeText(this, "Página Principal", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), Principal.class));
            finish();

        } else if (id == R.id.nuevo) {
            Toast.makeText(this, "Nuevo Producto", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), AgregarArticulo.class);
            startActivity(i);
            finish();

        } else if (id == R.id.frutas) {
            Toast.makeText(this, "Parrilla", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), Frutas.class);
            startActivity(i);
            finish();

        } else if (id == R.id.verduras) {
            Toast.makeText(this, "Cilindro", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), Verduras.class));
            finish();

        } else if (id == R.id.secos) {
            Toast.makeText(this, "Cocina", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), Secos.class));
            finish();

        } else if (id == R.id.repo) {
            Toast.makeText(this, "Reporte General", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), ReporteGeneral.class));
            finish();

        } else if (id == R.id.repospecial) {
            Toast.makeText(this, "Reporte Especial", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), ReporteEspecial.class));
            finish();

        }else if (id == R.id.EnviaReporte) {
            Toast.makeText(this, "Enviar Reporte Bajos", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), Enviar.class));

        } else if (id == R.id.Acercade) {
            Toast.makeText(this, "Acerca de", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), Acercade.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
