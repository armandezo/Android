package com.inventariostap.mario.controldeinventarios;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;

//IMPORTAMOS MODIFICACIONES EN EL MANIFEST PARA COMPATIBILIDAD DE PERMISOS CON ANDROID 6 O SUPERIOR
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class AgregarArticulo extends AppCompatActivity {

    Button Regresar, Agregar;
    EditText Clave, Minimo, Maximo, Existencia, Codigo;
    Spinner spinner;
    ImageButton Lector;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    //VARIABLES NECESARIAS PARA LAS IMAGENES (ASD*)
    private static String APP_DIRECTORY = "Control de Inventarios/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "Fotos Inventario";

    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;

    private ImageView mSetImage;
    private ImageButton mOptionButton;
    private ConstraintLayout mRlView;
    private String mPath = "default", codigolector = "";
    private String imgNombre = "default.jpg";
    // FIN (ASD*)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_articulo);
        setTitle("Agregar Artículo");

        //IDENTIFICAR LOS BOTONES POR SU ID
        Regresar = (Button) findViewById(R.id.regresar);
        Agregar = (Button) findViewById(R.id.agregar);
        //DECLARACIONDE VARIABLES PARA LA IMAGEN
        mSetImage = (ImageView) findViewById(R.id.set_picture);
        mOptionButton = (ImageButton) findViewById(R.id.btnset_image);
        mRlView = (ConstraintLayout) findViewById(R.id.mRlView);
        //IDENTIFICAR LOS EDITTEXT POR SU ID
        Clave = (EditText) findViewById(R.id.Clave);
        Minimo = (EditText) findViewById(R.id.minimo);
        Maximo = (EditText) findViewById(R.id.maximo);
        Existencia = (EditText) findViewById(R.id.existencia);

        //DECLARACIÓN DEL PARÁMETROS PARA EL LECTOR DE CODIGOS DE BARRA
        Lector = (ImageButton) findViewById(R.id.barcode);
        Codigo = (EditText) findViewById(R.id.ClaveCo);

        //DECLARACION DE LOS TIPOS DE PRODUCTOS
        String[] datos = {"Parrilla", "Cilindro", "Cocina"};
        //IDENTIFICAR EL SPINER POR SU ID
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, datos));
        final String[] tipo = new String[1];//VARIABLE PARA LOS TIPOS DE PRODUCTOS

        //METODO PARA CARGA, GUARDADO, MOSTRAR IMAGENES (XZA**)
        if (mayRequestStoragePermission()) {
            mOptionButton.setEnabled(true);
        } else {
            mOptionButton.setEnabled(false);
        }
        mOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptions();
            }
        });
        //FIN (XZA**)

        //METODO PARA LA ASIGNACION DE UN VALOR A LA VARIABLE "TIPO" SEGUN EL ELEMENTO SELECCIONADO DEL SPINNER
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipo[0] = spinner.getSelectedItem().toString();//OBTENER EL TEXTO DEL ITEM SELECCIONADO
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //FIN DEL METODO DEL SPINNER

        //METODO PARA VOLVER A LA PAGUINA PRINCIPAL CUANDO SE PRECIONE EL BOTÓN "VOLVER"
        Regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inicio = new Intent(AgregarArticulo.this, Principal.class);
                startActivity(inicio);
                finish();
            }
        });

        //METODO PARA AGREGAR UN NUEVO ARTICULO EN LA BASE DE DATOS
        Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Clave.getText().length() == 0 || Minimo.getText().length() == 0 || Maximo.getText().length() == 0 || Existencia.getText().length() == 0 || Codigo.getText().length() == 0) {//SE VALIDA DE QUE TODOS LOS CAMPOS ESTEN LLENOS
                    Snackbar.make(view, "Dejaste algún(os) campo(s) vacio(s),\n Favor de rellenar todos los campos", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                } else {

                    //SE OBTIENEN LOS STOCKS DEL ARTICULO
                    int min, max;
                    min = Integer.parseInt(Minimo.getText().toString());
                    max = Integer.parseInt(Maximo.getText().toString());

                    if (max <= min) {//SE COMPRUEBA LA LOGICA DE MINIMOS Y MAXIMOS
                        Snackbar.make(view, "El Stock Máximo que ingresaste tiene un error \t\t Favor de Ingresar un dato correcto", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else {
                        //SE OBTIENE LA EXISTENCIA DEL ARTICULO
                        int exis;
                        exis = Integer.parseInt(Existencia.getText().toString());

                        if (exis < min || exis > max) {//SE VERIFICA QUE ESTÉ ENTRE LOS RANGOS DEL STOCK
                            Snackbar.make(view, "Se recomienda surtir este producto", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }

                        Intent intent = new Intent(AgregarArticulo.this, Principal.class);//SE CREA UN INTENTO PARA VIAJAR A LA PAGUINA PRINCIPAL

                        //CREAR REGISTRO PARA LA BD
                        BaseD db = new BaseD(AgregarArticulo.this.getApplicationContext(), null, null, 1);
                        //COMPROBAR DE QUE EL ELEMENTO QUE SE DESEA AGREGAR NO EXISTA YA
                        if (db.elemento(Clave.getText().toString())) {
                            Toast.makeText(AgregarArticulo.this.getApplicationContext(), "El producto ya existe, verifique el nombre del producto", Toast.LENGTH_LONG).show();//MOSTRARMENSAJE SEGUN EL CORRECTO GUARDADO EL EL INCORRECTO DEL MISMO
                        } else {
                            //*** IF PARA AVISAR AL USUARIO SI NO HA SELECCIONADO NINGUNA IMAGEN ***
                            if (mPath.equals("default")) {
                                Toast.makeText(getApplication(), "Se tomara la imagen por default para este producto", Toast.LENGTH_SHORT).show();
                            }

                            String mensa = db.guardarfull(Clave.getText().toString(), Existencia.getText().toString(), Minimo.getText().toString(), Maximo.getText().toString(), tipo[0], mPath, Codigo.getText().toString());//GUARDAR DATOS
                            Toast.makeText(AgregarArticulo.this.getApplicationContext(), mensa, Toast.LENGTH_SHORT).show();//MOSTRARMENSAJE SEGUN EL CORRECTO GUARDADO EL EL INCORRECTO DEL MISMO

                            startActivity(intent);//SE REDIRIJE A LA PAGUINA PRINCIPAL
                            finish();
                        }
                        //FIN DEL REGISTRO

                    }
                }
            }
        });

        final Activity activity = this;//VARIABLE QUE FIJA LA ACTIVITY A LA QUE SE ESTÁ MOSTRANDO EN ESE MOMENTO

        Lector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private boolean mayRequestStoragePermission() {//METODO PARA COMPROBAR LOS PERMISOS NECESARIOS PARA ANDROID 6 EN ADELANTE

        //SE VERIFICA LAS VERSIONES DE ANDROID Y SI LOS PERMISOS YA ESTAN ACEPTADOS
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;
        if ((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
            return true;

        // SI SE ABRE LA CAMARA O LA GALERIA Y LOS PERMISOS AUN NO SON ACEPTADOS SE MOSTRARA UN MENSAJE DE ALERTA
        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))) {
            Snackbar.make(mRlView, "Esperando permisos necesarios", Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok,
                    new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View v) {
                            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);//REDIRECCION PARA LOS PERMISOS
                        }
                    }).show();
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }
        return false;
    }

    private void showOptions() {//MUESTRA LAS OPCIONES PARA TOMAR FOTO/ SUBIR IMAGEN/ CANCELAR

        final CharSequence[] option = {"Tomar","Elegir","Cancelar"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(AgregarArticulo.this);//SE MUESTRA EL ALERT DIALOG AL DAR CLICK
        builder.setTitle("Elige una opción");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whict) {
                if (option[whict] == "Tomar") {//SE LLAMA AL METODO O SE CANCELA
                    openCamera();
                } else {
                    dialog.dismiss();
                }


               if(option[whict]=="Elegir"){
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                }else{
                    dialog.dismiss();
                }



            }
        });
        builder.show();//SI  ESTA LINEA ES OMITIDA NO SE MOSTRARA EL ALERT DIALOG
    }

    private void openCamera() {//METODO PARA TOMAR IMAGEN Y CARGARLA AL LIST VIEW
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        //COMPROVAR SI EXISTE EL DIRECTORIO DONDE ALMACENAREMOS LAS IMAGENES, SI ES FALSO, LO CREAMOS
        if (!isDirectoryCreated) {
            isDirectoryCreated = file.mkdirs();
        }

        if (isDirectoryCreated) {
            //EL NUEVO NOMBRE DE LA IMAGEN SERA UN TIMESTAM ASI EL NOMBRE SIMPRE SERA UNICO
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";//LO CONVERTIMOS A ESTRING + EXTENCION DE LA IMAGEN
            //mPath CONTENDRA LA RUTA COMPLETA DONDE SE ENCUENTRA LA IMAGEN
            mPath = Environment.getExternalStorageDirectory() + file.separator + MEDIA_DIRECTORY + file.separator + imageName;
            File newFile = new File(mPath);
            //INTENT PARA GUARDAR LA NUEVA IMAGEN
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imgNombre = imageName;
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
            startActivityForResult(intent, PHOTO_CODE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("file_path", mPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPath = savedInstanceState.getString("file_path");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//METODO QUE GENERA UN RESULTADO EN LA ACTIVIDAD QUE SE MANDA A LLAMAR
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);//CREA VARIABLE TIPO INTENT RESULT QUE INTEGRE LO QUE SE VAYA HACIENDO DURANTE EL PROCESO

        if (result != null) {//CHECA SI ES NULO PARA REALIZAR EL CODIGO SINO, EJECUTA OTRO ALGORITMO
            if (result.getContents() == null) {// CHECA SI EL CONTENIDO DE LA VARIABLE ES NULO
                Log.d("AgregarArticulo", "Cancelled scan");//MUESTRA EN ANDROID MONITOR LA VISUALIZACIÓN DE ESTAS CADENAS.
                Toast.makeText(this, "Escaneo Cancelado", Toast.LENGTH_LONG).show();
            } else {
                Log.d("AgregarArticuo", "Scanned");//MUESTRA EN ANDROID MONITOR LA VISUALIZACIÓN DE ESTAS CADENAS
                Toast.makeText(this, "Formato: " + result.getFormatName(), Toast.LENGTH_LONG).show(); // MUESTRA EL FORMATO DEL CODIGO LEIDO EN UN TOAST
                Codigo.setText(result.getContents().toString());//SE FIJA LO QUE SE LEYÓ EN EL CODIGO EN EL EDITTEXT DE CLAVE
                Codigo.setEnabled(false);//SE DESABILITA LA OPCION DE ESCRITURA DEL EDITTEXT DE CLAVE, PARA EVITAR BORRAR DATOS NECESARIOS
                codigolector = Codigo.getText().toString();
                //VARIABLES CREADAS PARA CHECAR SI LO QUE SE LEYO NO ES UN CODIGO VÁLIDO SINO UNA URL
                String clave = Codigo.getText().subSequence(0, 4).toString();
                String clave2 = Codigo.getText().subSequence(0, 5).toString();
                String claveF = Codigo.getText().subSequence(Codigo.length() - 1, Codigo.length()).toString();
                String clavew = Codigo.getText().subSequence(0, 3).toString();
                String claveF2 = Codigo.getText().subSequence(Codigo.length() - 4, Codigo.length()).toString();

                //CHECA SI LAS VARIABLES ANTERIORES NO CUMPLEN CON REQUISITOS QUE UNA URL TIENE, PARA MOSTRAR UNA SERIE DE INSTRUCCIONES
                if (clave.equalsIgnoreCase("http") || clave2.equalsIgnoreCase("https") || clavew.equalsIgnoreCase("www") || claveF.equalsIgnoreCase("/") || claveF2.equalsIgnoreCase(".com") || claveF2.equalsIgnoreCase(".org")) {
                    Toast.makeText(getApplicationContext(), "Usted escaneo una dirección URL\n No es válida", Toast.LENGTH_LONG).show();

                    try {//MÉTODO QUE ABRE LA URL ESCANEADA
                        Uri uri = Uri.parse(Codigo.getText().toString());//SE FIJA EN UNA VARIABLE TIPO URI LA DIRECCIÓN URL ESCANEADA
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);//SE CREA UNA VARIABLE TIPO INTENT QUE ABRA LA ACCIÓN DE VISUALIZAR LA URL CON UNA APLICACIÓN EXTERNA
                        startActivity(intent);//INICIA EL INTENT
                        //FIJA CARACTERÍSTICAS QUE PERMITEN LA REESCRITURA DE UN NUEVO CODIGO VÁLIDO
                        Codigo.setText("");
                        Codigo.setHint("Clave");
                        Codigo.setEnabled(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case PHOTO_CODE:
                        MediaScannerConnection.scanFile(this, new String[]{mPath}, null, new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("ExternalStarage", "Scanned" + path + ":");
                                Log.i("ExternalStarage", "-> Uri =" + uri);
                            }
                        });
                        //SE REDIMENCIONA LA IMAGEN TOMADA
                        Bitmap mBitmap = BitmapFactory.decodeFile(mPath);
                        //REDIMENCIONANDO
                        int width = mBitmap.getWidth();
                        int height = mBitmap.getHeight();
                        int newWidth = 480;
                        int newHeigth = 480;
                        float scaleWidth = ((float) newWidth) / width;
                        float scaleHeight = ((float) newHeigth) / height;
                        //SE CREA UNA MATRIZ
                        Matrix matrix = new Matrix();
                        //SE REDIMENCIONAN LOS BITS
                        matrix.postScale(scaleWidth, scaleHeight);
                        //SE GENERA UN NUEVO BITMAP REDIMENCIONADO
                        Bitmap image = Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);
                        mSetImage.setImageBitmap(image);//SE INSERTA LA IMAGEN EN EL CAMPO DE IMAGEN

                        break;
                    case SELECT_PICTURE:
                        Uri path = data.getData();
                        mSetImage.setImageURI(path);
                        break;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //VERIFICAMOS SI LOS PERMISOS YA FUERON ACEPTADOS
        if (requestCode == MY_PERMISSIONS) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //MENSAJE DE ACEPTACION DE PERMISOS EN CASO CORRECTO
                Toast.makeText(AgregarArticulo.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                mOptionButton.setEnabled(true);
            }
        } else {
            showExplanation();
        }
    }

    //METODO PARA MOSTRAR EXPLICACION SI ALGO FALLO
    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AgregarArticulo.this);
        //MOSTRAR MENSAJES DE EXPLICACION A LOS FALLOS
        builder.setTitle("Permisos denegados");
        builder.setTitle("Necesitas aceptar los permisos");
        //METODO QUE REDIRIGE A SETINGD PARA QUE LOS PERMISOS ACEPTADOS
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("Package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);//VERIFICAMOS SI NO EXISTIO NINGUN PROBLEMA EN LA OPERACION
            }
        });
        //OPCION DE RECHAZAR LOS PERMISOS
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
    }
}
