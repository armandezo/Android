package com.inventariostap.mario.controldeinventarios;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Enviar extends AppCompatActivity {
    private static final String Reporte_PDF_APP = "Control de Inventarios";//NOMBRE DE LA CARPETA PRINCIPAL DONDE SE GUARDARÁN LOS REPORTES
    private static final String GENERADOS = "Mis Reportes";//NOMBRE DE LA CARPETA SECUNDARIA DONDE SE GUARDARÁN LOS DATOS DE LA APLICACIÓN

    Button Regresar, Enviar, Continuar;
    TextView Etiqueta;
    EditText Text, Mensaje;
    RadioButton SMS, Email;
    String correo, contraseña, nombre_completo;
    Session session; //VARIABLE QUE SIRVE PARA EL INCIO DE SESIÓN DEL CORREO

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;//VARIABLE QUE CONTIENE SI LOS PERMISOS LOS TIENE  CONCEDIDOS O NO LO TIENE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar);

        Regresar = (Button) findViewById(R.id.regresar);
        Enviar = (Button) findViewById(R.id.enviar);
        Etiqueta = (TextView) findViewById(R.id.textView31);
        Text = (EditText) findViewById(R.id.correo);
        Mensaje = (EditText) findViewById(R.id.mensaje);
        Continuar = (Button) findViewById(R.id.cambio);

        SMS = (RadioButton) findViewById(R.id.sms);
        Email = (RadioButton) findViewById(R.id.email);

        Regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Enviar.this, Principal.class);
                startActivity(intent);
                finish();
            }
        });

        Continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Continuar.getText().equals("Continuar")) {
                    Etiqueta.setVisibility(View.VISIBLE);
                    Continuar.setText("Reelegir Opción");
                    Continuar.setBackgroundColor(Color.rgb(0, 204, 204));
                    Continuar.setTextColor(Color.rgb(255, 255, 255));
                    SMS.setEnabled(false);
                    Email.setEnabled(false);

                    if (SMS.isChecked()) {
                        Etiqueta.setText("Ingresa el Número telefónico:");
                        Mensaje.setHint("Teléfono");
                        Mensaje.setVisibility(View.VISIBLE);
                        Enviar.setVisibility(View.VISIBLE);
                        Snackbar.make(v, "No se puede mandar PDF por SMS\nÚnicamente por Correo", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    } else if (Email.isChecked()) {
                        Etiqueta.setText("Ingresa el Correo");
                        Text.setVisibility(View.VISIBLE);
                        Text.setHint("Correo");
                        Enviar.setVisibility(View.VISIBLE);
                    }

                } else if (Continuar.getText().equals("Reelegir Opción")) {
                    Etiqueta.setVisibility(View.INVISIBLE);
                    Text.setVisibility(View.INVISIBLE);
                    Continuar.setText("Continuar");
                    Continuar.setBackgroundColor(Color.rgb(224, 224, 224));
                    Continuar.setTextColor(Color.rgb(0, 0, 0));
                    Enviar.setVisibility(View.INVISIBLE);
                    Mensaje.setVisibility(View.INVISIBLE);
                    SMS.setEnabled(true);
                    Email.setEnabled(true);
                }
            }
        });

        Enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Carga_datos load = new Carga_datos();
                load.execute();

                if (SMS.isChecked()) {
                    EnviarSMS(); //MANDA LLAMAR AL MÉTODO DE ENVIO DE SMS

                } else if (Email.isChecked()) {
                    GenerarPDF();// MANDA LLAMAR A GENERAR PDF
                    EnviarCorreo();// MANDA LLAMAR EL MÉTODO DE ENVIAR CORREO
                }

                //MANDA A LLAMAR A LA VENTANA PRINCIPAL CON UN INTENT
                Intent intent = new Intent(Enviar.this, Principal.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void EnviarCorreo() { //METODO PARA EL ENVIO DE CORREOS AUTOMATIZADOS
        correo = "controlinventariosandstudio@gmail.com"; //CORREO DEL QUE SE DESEA ENVIAR
        contraseña = "controlAndroid";//CONTRASEÑA DEL REPECTIVO CORREO

        //AÑADE LAS POLITICAS NECESARIAS PARA EL ENVIO DE CORREOS
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Properties properties = new Properties(); // CREAR UNA VARIABLE DE TIPO PROPIEDADES DEL CORREO
        properties.put("mail.smtp.host", "smtp.googlemail.com"); //DOMINO DEL CORREO EMISOR
        properties.put("mail.smtp.socketFactory.port", "465");//AÑADES EL PUERTO DEL DOMINIO
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");//CLASE DEL CORREO
        properties.put("mail.smtp.auth", "true");//AUTENTICACIÓN DE CORREO
        properties.put("mail.smtp.port", "465");//PUERTO DEL DOMINIO GENERAL

        try { // ALGORITMO PARA INICIO DE SESIÓN EN EL CORREO
            session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(correo, contraseña);
                }
            });

            if (session != null) { //SI LA SESION SE INICIA DE FORMA CORRECTA SE EJECUTARÁ EL SIGUIENTE CÓDIGO
                Message message = new MimeMessage(session); // ABRE UN CORREO NUEVO DESDE EL CORREO
                message.setFrom(new InternetAddress(correo));// AÑADE EMISOR DEL CORREO A LA CREACIÓN DEL CORREO
                message.setSubject("URGENTE: Productos bajos en Inventario");//AÑADE ASUNTO AL CORREO
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(Text.getText().toString()));//AÑADE RECEPTOR

                BodyPart messageBodyPart = new MimeBodyPart();//CREA UNA VARIABLE QUE AYUDARA A LA CREACIÓN DEL CUERPO Y ENVIO DE ARCHIVOS ADJUNTOS EN EL CORREO
                //CREA EL TEXTO QUE SE ESCRIBE EN EL CUERPO DEL CORREO
                messageBodyPart.setText("Aquí se envia un Reporte General de los productos bajos en su inventario\n Este correo fue enviado por la Aplicación CONTROL DE INVENTARIOS\n Hecha por estudiantes del Intituto Tecnológico de Colima\n Carrera de Ingeniería en Sistemas Computacionales");

                BodyPart adjunto = new MimeBodyPart();//CREAMOS UNA VARIABLE PARA EL ARCHIVO ADJUNTO
                adjunto.setDataHandler(new DataHandler(new FileDataSource(nombre_completo)));//SE OBTIENE E INSERTA EL ARCHIVO ADJUNTO DENTRO DEL CORREO
                adjunto.setFileName("Reporte de Productos Bajos.pdf");//SE LE PONE EL NOMBRE QUE VA A TENER EL ARCHIVO EN EL CORREO

                MimeMultipart multipart = new MimeMultipart();//CREAMOS VARIABLE MULTIPARTE PARA CONCATENAR EL TEXTO CON EL ARCHIVO ADJUNTO
                //AÑADEN LAS PARTES CREADAS ANTERIORMENTE
                multipart.addBodyPart(messageBodyPart);
                multipart.addBodyPart(adjunto);

                message.setContent(multipart);//AÑADE CONTENIDO AL MENSAJE QUE SE CREO AL INICIO

                Transport.send(message);//METODO QUE ENVIA EL CORREO
                muestraPDF(nombre_completo, this);//SE MANDA A LLAMAR AL MÉTODO PARA MOSTRAR EL ARCHIVO EN UNA APLICACION DEL CELULAR
                Toast.makeText(getApplicationContext(), "Correo Enviado", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void EnviarSMS() { //MÉTODO QUE ENVIA SMS
        //REVISA SI TIENE LOS DERECHOS CONCEDIDOS, SI NO LOS PIDE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            //PIDE LOS DERECHOS AL USUARIO PARA QUE SE LE CONCEDAN
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            } else {//TOMA LOS PERMISOS CONCEDIDOS ANTERIORMENTE
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            try {
                //ENVIO DE SMS AUTÓMATIZADO
                BaseD db = new BaseD(this, null, null, 1);
                String mensaje = db.SMS();
                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                SmsManager smsManager = SmsManager.getDefault();//CREA VARIABLE TIPO SMSMANAGER PARA EL ENVIO DE SMS
                smsManager.sendTextMessage(Text.getText().toString(), null, mensaje, null, null);//FIJA EL MENSAJE QUE SE ENVIARÁ ASÍ COMO EL NÚMERO AL QUE SE ENVIARÁ
                Toast.makeText(getApplicationContext(), "Mensaje Enviado", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Mensaje no Enviado.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {//MÉTODO ENCARGADO DE PERMITIR PERMISOS ADICIONALES
        //ESTE SWITCH TOMA EL CASO QUE EL USUARIO HALLA ELEGIDO EN EL MENSAJE DE ACEPTAR LOS PERMISOS
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {//EN CASO DE QUE EL USUARIO HALLA DADO QUE NO
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //PRIMERO SE LE VUELVE A PEDIR PERMISOS PARA REALIZAR EL SIGUIENTE CODIGO, SINO, GENERA UN ERROR
                    BaseD db = new BaseD(this, null, null, 1);
                    String mensaje = db.SMS();
                    Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                    SmsManager smsManager = SmsManager.getDefault();//CREA VARIABLE TIPO SMSMANSAGER PARA EL ENVIO DE SMS
                    smsManager.sendTextMessage(Text.getText().toString(), null, mensaje, null, null);//FIJA EL MENSAJE QUE SE ENVIARÁ ASÍ COMO EL NÚMERO AL QUE SE ENVIARÁ
                    Toast.makeText(getApplicationContext(), "Mensaje Enviado", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Mensaje no Enviado.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    public void GenerarPDF() {
        String NOMBRE_ARCHIVO = "Mi Reporte.pdf"; //Nombre del PDF
        String tarjetaSD = Environment.getExternalStorageDirectory().toString(); //Obtiene la ruta de la memoria SD

        Document document = new Document(PageSize.LETTER); //Configuras el PDF para el tamaño de la página
        File pdfDir = new File(tarjetaSD + File.separator + Reporte_PDF_APP); //Crea la carpeta en la cual se va a guardar el archivo

        if (!pdfDir.exists()) { //Checa si existe la carpeta
            pdfDir.mkdir(); //Obtiene la ruta del proceso
        }

        File pdfSubDir = new File(pdfDir.getPath() + File.separator + GENERADOS); //Genera la ruta de la carpeta final donde se guardará el archivo

        if (!pdfSubDir.exists()) { // Checa si existe la carpeta
            pdfSubDir.mkdir(); // Obtiene la ruta de la carpeta
        }

        nombre_completo = Environment.getExternalStorageDirectory() + File.separator + Reporte_PDF_APP + File.separator + GENERADOS + File.separator + NOMBRE_ARCHIVO; //Ruta completa del archivo generado en PDF

        File outpufile = new File(nombre_completo); //Igualas la ruta a una variable

        if (outpufile.exists()) { //Checas si existe el archivo que se acaba de generar
            outpufile.delete(); // Si existe se borra el que ya esta ahí para que quede el más reciente
        }

        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(nombre_completo)); //Abre  la instancia para generar el PDF
            //Crear el documento para escribirlo

            document.open(); //Se abre el proceso para la creación del documento
            document.addAuthor("Mario Josue del Toro Morales"); //Se añade el nombre del autor
            document.addCreator("Chomy");//Se añade el creador del documento
            document.addSubject("Reporte de Artículos Bajos en Inventario"); //Añade asunto
            document.addCreationDate();//Añade la fecha de creación del documento
            document.addTitle("Reporte de Articulos Bajos en Inventario"); // Añade título PERO NO ES EL NOMBRE DEL ARCHIVO

            //METODO PARA INGRESAR UNA IMAGEN AL DOCUMENTO
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher_iconop);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image imagen = Image.getInstance(stream.toByteArray());
            imagen.scalePercent(13);
            imagen.setAbsolutePosition(480, 655);
            document.add(imagen);

            //AGREAGA LAS FUENTES QUE SERÁN NECESARIAS EN ESTE PUNTO PARA LA CREACIÓN DEL DOCUMENTO
            Font fontT = FontFactory.getFont(FontFactory.HELVETICA, 32, Font.BOLD);
            fontT.setColor(0, 0, 0);
            document.add(new Paragraph("        Control de Inventarios", fontT));

            Font Sub = FontFactory.getFont(FontFactory.TIMES, 24, Font.BOLD);
            Sub.setColor(153, 0, 0);
            document.add(new Paragraph("    Reporte de Productos Bajos en Inventario", Sub));

            Font Reporte = FontFactory.getFont(FontFactory.COURIER, 16, Font.BOLDITALIC);
            Reporte.setColor(153, 76, 0);

            //CODIGO PARA INGRESAR FECHA Y HORA AL DOCUMENTO
            Calendar cal = new GregorianCalendar();
            Date date = cal.getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat hr = new SimpleDateFormat("HH:mm:ss");
            String fecha = df.format(date);
            String hora = hr.format(date);
            document.add(new Paragraph("        Fecha: " + fecha + "    Hora: " + hora, Sub));

            document.add(new Paragraph("--------------------------------------------------------", Reporte));

            //SE CREA INSTANCIA DEL TIPO DE LA CLASE BASE DE DATOS
            BaseD db = new BaseD(this, null, null, 1);
            Paragraph bajos;
            bajos = db.Reporte();
            document.add(bajos);
            document.add(new Paragraph("--------------------------------------------------------", Reporte));

            document.close();//CERRAMOS LA CREACIÓN DEL DOCUMENTO
            Toast.makeText(this, "El PDF se generó exitosamente", Toast.LENGTH_LONG).show(); // MUESTRA UN MENSAJE DE QUE SE GENERÓ EXITOSAMENTE EL PDF
        } catch (FileNotFoundException e) {
            e.printStackTrace();//MUESTRA MENSAJE DE ERROR EN ANDROID MONITOR
        } catch (DocumentException e) {
            e.printStackTrace();//MUESTRA MENSAJE DE ERROR EN ANDROID MONITOR
        } catch (IOException e) {
            e.printStackTrace();//MUESTRA MENSAJE DE ERROR EN ANDROID MONITOR
        } catch (Exception e) {
            e.printStackTrace();//MUESTRA MENSAJE DE ERROR EN ANDROID MONITOR
        }
    }

    public void muestraPDF(String archivo, Context context) { //MÉTODO PARA VISUALIZAR EL PDF GENERADO
        Toast.makeText(context, "Leyendo el archivo", Toast.LENGTH_LONG).show(); //MUESTRA UN MENSAJE DE QUE SE ESTÁ LEYENDO EL ARCHIVO GENERADO

        File file = new File(archivo);//SE OBTIENE LA RUTA COMPLETA DEL ARCHIVO
        Intent intent = new Intent(Intent.ACTION_VIEW);//CREAMOS EL INTENT Y EL TIPO QUE SERÁ, EL CUÁL ABRIRÁ UNA APLICACIÓN EXTERNA EN ESTE CASO
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");//OBTIENE LA RUTA DERL ARCHIVO Y EL TIPO DE EXTENSIÓN QUE ESTE TIENE, PARA SABER QUE TIPO DE APLICACIÓNES DEBE ABRIR
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//LIMPIA LA CACHÉ DEL PROCESO
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//DA PRIORIDAD A LA ACTIVIDAD QUE SE VA A EJECUTAR

        try {
            context.startActivity(intent);//INICIA EL INTENT
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No tiene ninguna aplicación para visualizar PDF", Toast.LENGTH_LONG).show();//MUESTRA MENSAJE DE QUE NO TIENE EL USUSARIO NINGUNA APLICACIÓN PARA VISUALIZAR ESTE TIPO DE ARCHIVOS
        }
    }

    private class Carga_datos extends AsyncTask<Void, Void, Void> {
        ProgressDialog pDialog;

        protected void onPreExecute() {
            pDialog = new ProgressDialog(Enviar.this);
            pDialog.setMessage("Enviando Reporte...");
            pDialog.show();
            long delayInMillis = 4000;
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    pDialog.dismiss();
                }
            }, delayInMillis);
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        public void onPostExecute() {
            pDialog.dismiss();
        }
    }
}

