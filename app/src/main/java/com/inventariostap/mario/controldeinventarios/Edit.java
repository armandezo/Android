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
import android.os.Bundle;
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.inventariostap.mario.controldeinventarios.R.id.Clave;

public class Edit extends AppCompatActivity {

    Button Agregar, regresar;
    TextView tittle;
    EditText nombre, cantidad, min, max, Codigo;
    Spinner spinner;
    ImageView image;
    ImageButton Lector;
    String tipo_spinner;
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
    private String mPath = "default";
    private String imgNombre = "default.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_articulo);
        setTitle("Modificar Artículo");

        Bundle extras = getIntent().getExtras();
        final String title_txt = extras.getString("Element", "");
        final int from = extras.getInt("Class", 0);

        //SE REFENCIACIA CADA ELEMENTO A UTILIZAR
        Agregar = (Button) findViewById(R.id.agregar);
        regresar = (Button) findViewById(R.id.regresar);
        tittle = (TextView) findViewById(R.id.agregartxt);
        nombre = (EditText) findViewById(Clave);
        cantidad = (EditText) findViewById(R.id.existencia);
        min = (EditText) findViewById(R.id.minimo);

        max = (EditText) findViewById(R.id.maximo);
        spinner = (Spinner) findViewById(R.id.spinner);
        image = (ImageView) findViewById(R.id.set_picture);
        mSetImage = (ImageView) findViewById(R.id.set_picture);


        //DECLARACIÓN DEL PARÁMETROS PARA EL LECTOR DE CODIGOS DE BARRA
        Lector = (ImageButton) findViewById(R.id.barcode);
        Codigo = (EditText) findViewById(R.id.ClaveCo);

        String[] datos = {"Frutas", "Verduras", "Secos"};//VECTOR PARA LOS TIPOS DE ARTICULOS
        spinner.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, datos));//SE RELLENA EL SPPINER


        //SE MODIFICAN LOS TITULOS DE LA VENTANA
        Agregar.setText("Modificar");
        tittle.setText("Modificar elemento");

        BaseD db = new BaseD(this, null, null, 1);//SE INSTANCIA LA BASE DE DATOS
        Category element = db.objeto(title_txt);//SE OBTIENE EL ELEMTNEO A MODIFICAR, DE LA BASE DE DATOS
        //SE LLENAN LOS VALORES DEL ELEMENTO A MODIFICA EN SU CORRESPONDIENTE CAMPO DENTRO DE LA VISTA

        tipo_spinner = element.getTipo();//SE OBTIENE EL TIPO DE PRODUCTO DEL QUE SE TRATA

        if (tipo_spinner.equalsIgnoreCase("Frutas")) {//SE PONE UNA POSICION DEL SPINNER SEGUN EL TIPO DE DATO DEL QUE SE TRATE
            spinner.setSelection(0);
        } else {
            if (tipo_spinner.equalsIgnoreCase("Verduras")) {
                spinner.setSelection(1);
            } else {
                spinner.setSelection(2);
            }
        }

        mPath = element.getImagen();
        Codigo.setText("" + element.getCodigo());
        spinner.setEnabled(false);
        nombre.setText(title_txt);
        nombre.setEnabled(false);
        cantidad.setText(element.getCantidad());
        min.setText("" + element.getSmin());
        max.setText("" + element.getSmax());

        Bitmap imagen = BitmapFactory.decodeFile(element.getImagen());//SE CREA LA IMAGEN A PARTIR DE LA DIRECCION DE LA IMAGEN, OBTENIDA PREVIAMENTE
        image.setImageBitmap(imagen);//SE INSER LA IMAGEN EN LA VISTA
        //METODO PARA CARGA, GUARDADO, MOSTRAR IMAGENES (XZA**)
        mOptionButton = (ImageButton) findViewById(R.id.btnset_image);

        //SE VERIIFICA LA AUTORIZACION DE LOS PERMISOS PARA LA CAMARA
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
        });//AL PRECIONAR EL BOTON CAMARA

        final String[] selected = new String[1];

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//METODO PARA OBTENER EL TIPO DE DATO SEGUN EL LO SELECCIONADO EN EL SPINNER
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected[0] = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//ACCION A PRODUCIR AL PRECIONAR EL BOTON DE VOLVER
                Intent i = new Intent(Edit.this, Principal.class);
                switch (tipo_spinner) {//SEGUN DE DONDE SE HALLA LLAMADO A ESTA CLASE, SE RETORNARÁ
                    case "Frutas":
                        i = new Intent(Edit.this, Frutas.class);//RETORNO HACIA LA CLASE FRUTAS
                        break;
                    case "Verduras":
                        i = new Intent(Edit.this, Verduras.class);//RETORNO HACIA LA CLASE VERDURAS
                        break;
                    case "Secos":
                        i = new Intent(Edit.this, Secos.class);//RETORNO HACIA LA CLASE SECOS
                        break;
                }
                startActivity(i);//SE LANZA LA PANTALL
                finish();//SE FINALIZA LA ACTUAL PANTALLA
            }
        });

        Agregar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {//ACCION A PRODUCIR AL PRECIONAR EL BOTON DE AGREGAR
                if (nombre.getText().length() == 0 || cantidad.getText().length() == 0 || min.getText().length() == 0 || max.getText().length() == 0 || Codigo.getText().length() == 0) {//SE VERIFICA QUE TODOS LO CAMPOS ESTEN LLENOS
                    Toast.makeText(Edit.this, "Verifica que todos los campos esten llenos", Toast.LENGTH_LONG).show();
                } else {//SI TODOS ESTAN LLENOS SE PROCIGUE

                    //SE OBTIENEN LOS TOCKS
                    int mini, maxi;
                    mini = Integer.parseInt(min.getText().toString());
                    maxi = Integer.parseInt(max.getText().toString());

                    if (maxi < mini) {//SE VERIFICA QUE CUMPLAN LA LOGICA
                        Snackbar.make(view, "El stock Máximo que ingresaste es menor al Stock Mínimo \t\t Favor de Ingresar un dato correcto", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else {
                        //SE OBTIENE LA EXISTENCIA
                        int exis;
                        exis = Integer.parseInt(cantidad.getText().toString());

                        if (exis < mini || exis > maxi) {//SE VERIFICA QUE ESTPE EN EL RANGO DE LOS STOCKS
                            Snackbar.make(view, "Tu existencia esta fuera de los rangos de stock establecidos", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }

                        Intent intent = new Intent(Edit.this, Principal.class);
                        //CREAR REGISTRO PARA LA B

                        //*** IF PARA AVISAR AL USUARIO SI NO HA SELECCIONADO NINGUNA IMAGEN ***
                        if (mPath.equals("default")) {
                            Toast.makeText(getApplication(), "Se tomara la imagen por default para este producto", Toast.LENGTH_SHORT).show();
                        }

                        BaseD db = new BaseD(Edit.this, null, null, 1);//SE INSTANCIA LA BASE DE DATOS

                        //SE REALIZA LA ACTUALIZACION DE LOS DATOS
                        String message = db.Update(nombre.getText().toString(), cantidad.getText().toString(), min.getText().toString(), max.getText().toString(), selected[0], title_txt, mPath, Codigo.getText().toString());
                        Toast.makeText(Edit.this, message, Toast.LENGTH_SHORT).show();//SE MUESTRA EL MENSAJE OBTENIDO

                        startActivity(intent);//SE RETORNA A LA PAGUINA PRINCIPAL
                        finish();//SE FINALIZA LA ACTIVIDAD

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

    private boolean mayRequestStoragePermission() {//METODO PARA LA SOLICITUD DE PERMISOS

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;
        if ((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
            return true;

        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))) {
            Snackbar.make(mRlView, "Esperando permisos necesarios", Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok,
                    new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View v) {
                            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
                        }
                    }).show();
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }
        return false;
    }

    private void showOptions() {
        //final CharSequence[] option = {"Tomar","Elegir","Cancelar"};
        final CharSequence[] option = {"Tomar", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(Edit.this);
        builder.setTitle("Elige una opción");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whict) {
                if (option[whict] == "Tomar") {
                    openCamera();
                } else {
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }

    private void openCamera() {//METODO PARA EJECUTAR LA CAMARA
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if (!isDirectoryCreated) {
            isDirectoryCreated = file.mkdirs();
        }
        if (isDirectoryCreated) {
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";
            mPath = Environment.getExternalStorageDirectory() + file.separator + MEDIA_DIRECTORY + file.separator + imageName;
            File newFile = new File(mPath);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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


                        Bitmap mBitmap = BitmapFactory.decodeFile(mPath);//SE CREA UNA IMAGEN A PARTIR DE SU RUTA
                        //SE REDIMENCIONA LA IMAGEN PARA OBTIMIZAR
                        int width = mBitmap.getWidth();
                        int height = mBitmap.getHeight();
                        int newWidth = 480;
                        int newHeigth = 480;
                        float scaleWidth = ((float) newWidth) / width;
                        float scaleHeight = ((float) newHeigth) / height;
                        //SE CREA UNA MATRIZ PARA LA MANIPULACION
                        Matrix matrix = new Matrix();
                        matrix.postScale(scaleWidth, scaleHeight);
                        // recreate the new Bitmap
                        Bitmap image = Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);//SE CREA UNA IMGANE REDIMECIONADA
                        mSetImage.setImageBitmap(image);//SE INSERTA LA IMAGEN

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

        if (requestCode == MY_PERMISSIONS) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Edit.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                mOptionButton.setEnabled(true);
            }
        } else {
            showExplanation();
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Edit.this);
        builder.setTitle("Permisos denegados");
        builder.setTitle("Necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("Package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
    }


}
