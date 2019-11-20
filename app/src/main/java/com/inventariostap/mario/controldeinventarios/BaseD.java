package com.inventariostap.mario.controldeinventarios;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.util.ArrayList;



public class BaseD extends SQLiteOpenHelper {

    public BaseD(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "Pruebas", factory, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {//SE EJEECUTA EL METODO AL INICIALIZAR LA BASE DE DATOS POR PRIMERA VEZ

        //SE CREAN LAS TABLAS DE LOS ELEMENTOS COMO DE FECHAS
        db.execSQL("CREATE TABLE datos(nombre text, cantidad text, minimo text, maximo text, tipo text, imagen text, codigo text)");
        db.execSQL("CREATE TABLE date(fecha text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //METODOS PARA LAS FECHAS

    public boolean fecha_exist() {//SE COMPRUEBA SI EXISTE ALGUN ELEMENTO DENTRO DE LA TABLA DE FECHAS
        boolean flag = false;
        SQLiteDatabase db = this.getReadableDatabase();//SE INSTANCIA LA BASE DE DATOS
        String q = "SELECT * FROM date";
        Cursor cursos = db.rawQuery(q, null);
        try {
            if (cursos.moveToFirst()) {
                flag = true;
            }
        } catch (SQLException e) {

        }

        return flag;
    }

    public String set_fecha(String date) {//METODO PARA AGREGAR UNA FECHA COMO DATO A LA BASE DE DATOS
        String fecha;
        SQLiteDatabase db = this.getWritableDatabase();//SE INSTANCIA LA BASE DE DATOS
        ContentValues container = new ContentValues();
        container.put("fecha", date);
        try {
            db.insert("date", null, container);
            fecha = "Fecha de movimiento guardada";
        } catch (SQLException e) {
            fecha = "Error guardando la fecha";
        }
        return fecha;
    }


    public String get_fecha() {//METODO PARA OBTENER LA FECHA ALMACENADA EN LA BASE DE DATOS
        String fecha = "";
        SQLiteDatabase db = this.getReadableDatabase();//SE INSTANCIA LA BASE DE DATOS
        String q = "SELECT * FROM date";
        Cursor cursos = db.rawQuery(q, null);
        try {
            if (cursos.moveToFirst()) {
                fecha = cursos.getString(0);
            }
        } catch (SQLException e) {
            fecha = "No se pudo obtener la fecha.";
        }

        return fecha;
    }

    public String update_fecha(String date) {//METODO PARA ACTUALIZAR LA FECHA EN LA BASE DE DATOS
        String message = "";
        SQLiteDatabase db = this.getWritableDatabase();//SE INSTANCIA LA BASE DA DATOS
        String q = "SELECT * FROM date";
        Cursor cursos = db.rawQuery(q, null);
        String name = cursos.getString(0);
        ContentValues container = new ContentValues();
        container.put("date", date);
        try {
            db.update("date", container, "fecha='" + name + "'", null);
            message = "Fecha actualizada";
        } catch (SQLException e) {
            message = "Error al guardar la fecha";
        }


        return message;
    }

    //FIN DE LOS METODOS PARA LA FECHA

//METODOS PARA LOS ARTICULOS A GUARDAR

    public String deleteelement(String element) {//METODO PARA ELIMINAR UN ELEMENTO
        String message = "";
        String q = "DELETE FROM datos WHERE nombre='" + element + "'";//SE CREA LA CONSULTA
        try {
            SQLiteDatabase db = this.getWritableDatabase();//SE INSTANCIA LA BASE DE DATOS
            db.execSQL(q);//SE EJECUTA LA CONSULTA
            message = "Eliminado correctamente";//SE INSERTA UN MENSAJE QUE SE RETORNARÁ
        } catch (SQLException e) {
            message = "Hubo un error al eliminar el elemento.";//EN CASO DE ERRORES, SE INSERTA UN MENSAJE DISTINTO
        }
        return message;//SE RETORNA EL MENSAJE
    }

    String guardarfull(String producto, String cantidad, String min, String max, String tipo, String imagen, String codigo) {//METODO PARA GUARDAR REGISTROS EN BASE DE DATOS
        String message = "";
        SQLiteDatabase db = this.getWritableDatabase();//SE INSTANCIA LA BASE DE DATOS
        ContentValues container = new ContentValues();//SE DECLARA UN CONTENEDOR
        container.put("nombre", producto);//SE INSERTA CADA UNOS DE LOS ELEMENTOS QUE CONFORMAN AL OBJETO
        container.put("cantidad", cantidad);
        container.put("minimo", min);
        container.put("maximo", max);
        container.put("tipo", tipo);
        container.put("imagen", imagen);
        container.put("codigo", codigo);
        try {
            db.insertOrThrow("datos", null, container);//SE REALIZA EL GUARDADO EN LA BASE DE DATOS
            message = "Ingresado correctamente";//SE INSERTA UN MENSAJE DE CONFIRMACION

        } catch (SQLException e) {
            message = "Error " + e.getMessage();//EN CASO DE ERROR SE INSERTA EL MENSAJE DEL ERRO
        }

        return message;//SE RETORNA EL MENSAJE
    }

    String[] buscar(String producto) {//METODO PARA BUSCAR LOS REGISTROS EN LA BASE DE DATOS
        String[] data = new String[3];//VECTOR QUE CONTENDRÁ LOS DATOS A OBTENER
        SQLiteDatabase db = this.getReadableDatabase();//SE INSTANCIA LA BASE DE DATOS
        String q = "SELECT * FROM datos WHERE tipo='" + producto + "'";//SE DECLARA LA CONSULTA
        Cursor registros = db.rawQuery(q, null);//SE REALIZA LA CONSULTA
        try {
            if (registros.moveToFirst()) {//SI EXISTEN REGISTROS, SE HA ENCONTRADO AL ELEMENTO
                do {
                    for (int i = 0; i < 2; i++) {
                        data[i] = registros.getString(i);//SE GUARDAN LOS DATOS EN UN VECTOR
                    }
                    data[2] = "Encontrado";//SE AGREGA UN MENSAJE
                } while (registros.moveToNext());
            } else {
                data[2] = "No se encontró a " + producto;//SI NO SE ENCUENTRA EL ELEMENTO, SE DA A SABER MEDIANTE UN MENSAJE
            }
        } catch (SQLException e) {
            data[2] = "Error " + e.getMessage();//EN CASO DE ERRORES, SE GUARDA EL ERROR COMO MENSAJE
        }

        return data;//SE RETORNA EL VECTOR QUE CONTIENE LOS DATOS

    }

    String Update(String producto, String cantidad, String min, String max, String tipo, String tittle, String image, String codigo) {//METODO PARA ACTUALIZAR REGISTROS EN BASE DE DATOS
        String message = "";
        SQLiteDatabase db = this.getWritableDatabase();//SE INSTANCIA LA BASE DE DATOS
        ContentValues container = new ContentValues();//SE DECLARA UN CONTENEDOR;
        container.put("nombre", producto);//SE AGREGA CADA UNO DE LOS ELEMENTOS A MODIFICAR
        container.put("cantidad", cantidad);
        container.put("minimo", min);
        container.put("maximo", max);
        container.put("tipo", tipo);
        container.put("imagen", image);
        container.put("codigo", codigo);
        try {
            db.update("datos", container, "nombre='" + tittle + "'", null);//SE REALIZA LA ACTUALIZACION DE VALORES
            message = "Ingresado correctamente";//SE AGREGA UN  MENSAJE

        } catch (SQLException e) {
            message = "Error " + e.getMessage();//EN CASO DE ERRORES SE AGREGA EL ERROR COMO MENSAJE
        }

        return message;//SE RETORNA EL MENSAJE
    }

    ArrayList<Category> llenado(String tipo) {//METODO PARA OBTENER TODOS LOS ELEMENTOS DE LA TABLA DATOS
        ArrayList<Category> datos = new ArrayList<>();//SE DECLARA UN ARRAYLIST QUE OCNTENDRÁ LOS ELEMENTOS
        SQLiteDatabase data = this.getWritableDatabase();//SE INSTANCIA LA BASE DE DATOS
        String q = "SELECT nombre FROM datos WHERE tipo='" + tipo + "'";//SE DECLARAN LAS CONSULTAS PARA CADA DATO A OBTENER
        String q2 = "SELECT cantidad FROM datos WHERE tipo='" + tipo + "'";
        String q3 = "SELECT imagen FROM datos WHERE tipo='" + tipo + "'";
        Cursor registro2 = data.rawQuery(q2, null);//SE LLEVAN A CABO LAS CONSULTAS
        Cursor registros = data.rawQuery(q, null);
        Cursor registro3 = data.rawQuery(q3, null);
        try {
            if (registros.moveToFirst() && registro2.moveToFirst() && registro3.moveToFirst()) {//SI EXISTENREGISTROS SE PROCEDE CON EL GUARDADO
                do {
                    datos.add(new Category(registros.getString(0), registro2.getString(0), registro3.getString(0)));//CADA ELEMENTO DE LA CONSULTA SE AñADE COMO UN NUEVO OBJETO DENTRO DEL ARRAYLIST
                }
                while (registros.moveToNext() && registro2.moveToNext() && registro3.moveToNext());//SE MUEVE MIENTRAS EXISTAN REGISTROS
            }
        } catch (SQLException e) {

        }
        return datos;//SE RETORNA EL ARRAYLIST CON LOS DATOS
    }


    boolean elemento(String name) {//METODO PARA COMPROBAR LA EXISTENCIA DE UN ELEMENTO PREVIAMENTE ALMACENADO
        boolean flag = false;
        SQLiteDatabase data = this.getWritableDatabase();//SE INSTANCIA LA BASE DE DATOS
        String q = "SELECT * FROM datos WHERE nombre='" + name + "'";//SE DECLARA LA CONSULTA
        Cursor registros = data.rawQuery(q, null);//SE REALIZA LA CONSULTA
        if (registros.moveToFirst()) {//SI SE PUDE MOVER AL PRIMER REGITRO, ENTONCES HAY REGISTROS
            flag = true;//SE MODIFICA EL VALOR DE LA VARIABLE
        }
        return flag;//SE RETORNA LA VARIABLE
    }

    Category objeto(String tittle) {//METODO PARA OBTENER UN ELEMENTO PREVIMANETE AMACENADO
        Category object = null;//SE DECLARA UN OBJETO
        SQLiteDatabase db = this.getWritableDatabase();//SE INSTANCIA LA BASE DE DATOS
        String q = "SELECT * FROM datos WHERE nombre='" + tittle + "'";//SE DECLARA LA CONSULTA
        Cursor cursor = db.rawQuery(q, null);//SE EJECUTA LA CONSULTA
        if (cursor.moveToFirst()) {//SI EXISTEN REGISTRO, NOS MOVEMOS AL PRIMERO DE ELLOS
            object = new Category(tittle, Integer.valueOf(cursor.getString(1)), Integer.valueOf(cursor.getString(2)), Integer.valueOf(cursor.getString(3)), cursor.getString(4), cursor.getString(5), cursor.getString(6));//SE CRE UN OBJETO CON EL REGISTRO OBTENIDO

        }
        return object;

    }

    public String get_tipo(String name) {//METODO PARA COMPROBAR LA EXISTENCIA DE UN ELEMENTO PREVIAMENTE ALMACENADO
        String t = "";
        SQLiteDatabase data = this.getWritableDatabase();//SE INSTANCIA LA BASE DE DATOS
        String q = "SELECT tipo FROM datos WHERE nombre='" + name + "'";//SE DECLARA LA CONSULTA
        Cursor registros = data.rawQuery(q, null);//SE REALIZA LA CONSULTA
        if (registros.moveToFirst()) {//SI SE PUDE MOVER AL PRIMER REGITRO, ENTONCES HAY REGISTROS
            t = registros.getString(0);
        }
        return t;//SE RETORNA LA VARIABLE
    }

    public Category search_code(String code) {
        Category element;
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT * FROM datos WHERE codigo='" + code + "'";
        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()) {
            element = new Category(cursor.getString(0), Integer.valueOf(cursor.getString(1)), Integer.valueOf(cursor.getString(2)), Integer.valueOf(cursor.getString(3)), cursor.getString(4), cursor.getString(5), cursor.getString(6));
        } else {
            element = new Category("", "", "");
        }

        return element;
    }

    Paragraph Reporte() {
        Paragraph Repo = new Paragraph(); //= new Chunk("", FontFactory.getFont(FontFactory.HELVETICA, 18, Font.NORMAL));

        //SE CREAN INSTANCIAS TIPO TABLA QUE SERAN INGRESADAS AL DOCUMENTO
        PdfPTable table = new PdfPTable(4);
        PdfPTable titulo = new PdfPTable(4);

        //SE CREAN LAS FUENTES NECESARIAS PARA LOS TEXTOS DEL DOCUMENTO
        Font font = new Font(FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL));
        Font Reporte = FontFactory.getFont(FontFactory.COURIER, 14, Font.BOLDITALIC);
        Reporte.setColor(153, 76, 0);

        //SE CREA LA INSTANCIA CELDA PARA MODIFICAR CARACTERISTICAS DE CADA CELDA DE LA TABLA
        PdfPCell cell;

        //SE SACANA LAS TABLAS Y REGISTROS DE LA BASE DE DATOS ORDENADOS ALFABETICAMENTE POR EL NOMBRE
        SQLiteDatabase data = this.getWritableDatabase();
        String q = "SELECT nombre FROM datos WHERE tipo='Frutas' ORDER BY nombre";
        String q2 = "SELECT cantidad FROM datos WHERE tipo='Frutas' ORDER BY nombre";
        String q3 = "SELECT maximo FROM datos WHERE tipo='Frutas' ORDER BY nombre";
        String q4 = "SELECT minimo FROM datos WHERE tipo='Frutas' ORDER BY nombre";
        Cursor registro2 = data.rawQuery(q2, null);
        Cursor registros = data.rawQuery(q, null);
        Cursor registro3 = data.rawQuery(q3, null);
        Cursor registro4 = data.rawQuery(q4, null);

        boolean existe = false;
        boolean tabla = false;
        Integer pedido;

        try {
            Repo.add(new Paragraph("*********************************FRUTAS*********************************\n\n", FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, Font.BOLD, BaseColor.BLUE)));
            //CHECA SI EL PRIMER ESPACIO NO ES NULO Y SE MUEVE AL PRIMER REGISTRO
            if (registros != null && registro2 != null && registro3 != null && registro4 != null) {
                if (registros.moveToFirst() && registro2.moveToFirst() && registro3.moveToFirst() && registro4.moveToFirst()) {
                    do {
                        if (Integer.parseInt(registro2.getString(0)) <= Integer.parseInt(registro4.getString(0))) {//COMPARA QUE LA EXISTENCIA NO SEA MENOR AL MINIMO
                            existe = true;
                            tabla = true;

                            //SE AÑADEN LOS CAMPOS NECESARIOS A UNA CELDA Y SE LE COLOCA LA FUENTE DESEADA, ASÍ COMO ALINEACIÓN Y EL INGRESO A LA TABLA
                            cell = new PdfPCell(new Phrase(registros.getString(0), font));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table.addCell(cell);

                            cell = new PdfPCell(new Phrase(registro2.getString(0), font));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table.addCell(cell);

                            cell = new PdfPCell(new Phrase(registro3.getString(0), font));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table.addCell(cell);


                            pedido = Integer.parseInt(registro3.getString(0)) - Integer.parseInt(registro2.getString(0));
                            cell = new PdfPCell(new Phrase(pedido.toString(), font));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table.addCell(cell);
                        }
                    }
                    while (registros.moveToNext() && registro2.moveToNext() && registro3.moveToNext() && registro4.moveToNext());//SE MUEVE AL SIGUIENTE REGISTRO
                } else {//SI NO HAY ARTICULOS DADOS DE ALTA EN EL INVENTARIO
                    existe = true;
                    // SE INGRESA ESTA CADENA AL DOCUMENTO CON UN FORMATO ESPECÍFICO
                    Repo.add(new Paragraph("-----------------NO HAY ARTICULOS EN INVENTARIO------------------", FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, Font.NORMAL, BaseColor.RED)));
                }

                if (!existe) {//SI EXISTEN ARTICULOS DADOS DE ALTA EN INVENTARIO PERO NINGUNO ESTA BAJO EN EXISTENCIA SE ENVIA LA CADENA QUE SE MUESTRA A CONTINUACION
                    Repo.add(new Paragraph("------------NO HAY ARTICULOS BAJOS EN INVENTARIO------------", FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, Font.NORMAL, BaseColor.RED)));
                }

                if (tabla) {//SI LOGRO HACER UN REGISTRO, INGRESARÁ A ESTE PUNTO
                    //COLOCACION DE LOS ENCABEZADOS DE LAS TABLAS ASI COMO LA FUENTE, LA ALINEACIÓN DESEADA, EL COLOR DE LA CELDA Y FINALMENTE EL INGRESO A LAS TABLAS DE LAS CELDAS
                    cell = new PdfPCell(new Phrase("DESCRIPCIÓN", Reporte));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(GrayColor.CYAN);
                    titulo.addCell(cell);

                    cell = new PdfPCell(new Phrase("EXISTENCIA", Reporte));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(GrayColor.CYAN);
                    titulo.addCell(cell);

                    cell = new PdfPCell(new Phrase("STOCK MÁXIMO", Reporte));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(GrayColor.CYAN);
                    titulo.addCell(cell);

                    cell = new PdfPCell(new Phrase("CANTIDAD A PEDIR", Reporte));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(GrayColor.CYAN);
                    titulo.addCell(cell);

                    //AÑADE LAS TABLAS AL DOCUMENTO EN ORDEN PRIMERO LOS ENCABEZADOS Y FINALMENTE LOS ARTICULOS BAJOS
                    Repo.add(titulo);
                    Repo.add(table);
                }
            }

            //SE SACANA LAS TABLAS Y REGISTROS DE LA BASE DE DATOS ORDENADOS ALFABETICAMENTE POR EL NOMBRE
            q = "SELECT nombre FROM datos WHERE tipo='Verduras' ORDER BY nombre";
            q2 = "SELECT cantidad FROM datos WHERE tipo='Verduras' ORDER BY nombre";
            q3 = "SELECT maximo FROM datos WHERE tipo='Verduras' ORDER BY nombre";
            q4 = "SELECT minimo FROM datos WHERE tipo='Verduras' ORDER BY nombre";
            registro2 = data.rawQuery(q2, null);
            registros = data.rawQuery(q, null);
            registro3 = data.rawQuery(q3, null);
            registro4 = data.rawQuery(q4, null);

            existe = false;
            tabla = false;

            Repo.add(new Paragraph("\n*******************************VERDURAS*******************************\n\n", FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, Font.BOLD, BaseColor.BLUE)));

            //CHECA SI EL PRIMER ESPACIO NO ES NULO Y SE MUEVE AL PRIMER REGISTRO
            if (registros != null && registro2 != null && registro3 != null && registro4 != null) {
                if (registros.moveToFirst() && registro2.moveToFirst() && registro3.moveToFirst() && registro4.moveToFirst()) {
                    do {
                        if (Integer.parseInt(registro2.getString(0)) <= Integer.parseInt(registro4.getString(0))) {//COMPARA QUE LA EXISTENCIA NO SEA MENOR AL MINIMO
                            existe = true;
                            tabla = true;

                            //SE AÑADEN LOS CAMPOS NECESARIOS A UNA CELDA Y SE LE COLOCA LA FUENTE DESEADA, ASÍ COMO ALINEACIÓN Y EL INGRESO A LA TABLA
                            cell = new PdfPCell(new Phrase(registros.getString(0), font));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table.addCell(cell);

                            cell = new PdfPCell(new Phrase(registro2.getString(0), font));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table.addCell(cell);

                            cell = new PdfPCell(new Phrase(registro3.getString(0), font));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table.addCell(cell);


                            pedido = Integer.parseInt(registro3.getString(0)) - Integer.parseInt(registro2.getString(0));
                            cell = new PdfPCell(new Phrase(pedido.toString(), font));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table.addCell(cell);
                        }
                    }
                    while (registros.moveToNext() && registro2.moveToNext() && registro3.moveToNext() && registro4.moveToNext());//SE MUEVE AL SIGUIENTE REGISTRO
                } else {//SI NO HAY ARTICULOS DADOS DE ALTA EN EL INVENTARIO
                    existe = true;
                    // SE INGRESA ESTA CADENA AL DOCUMENTO CON UN FORMATO ESPECÍFICO
                    Repo.add(new Paragraph("-----------------NO HAY ARTICULOS EN INVENTARIO------------------", FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, Font.NORMAL, BaseColor.RED)));
                }

                if (!existe) {//SI EXISTEN ARTICULOS DADOS DE ALTA EN INVENTARIO PERO NINGUNO ESTA BAJO EN EXISTENCIA SE ENVIA LA CADENA QUE SE MUESTRA A CONTINUACION
                    Repo.add(new Paragraph("------------NO HAY ARTICULOS BAJOS EN INVENTARIO------------", FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, Font.NORMAL, BaseColor.RED)));
                }

                if (tabla) {//SI LOGRO HACER UN REGISTRO, INGRESARÁ A ESTE PUNTO
                    //COLOCACION DE LOS ENCABEZADOS DE LAS TABLAS ASI COMO LA FUENTE, LA ALINEACIÓN DESEADA, EL COLOR DE LA CELDA Y FINALMENTE EL INGRESO A LAS TABLAS DE LAS CELDAS
                    cell = new PdfPCell(new Phrase("DESCRIPCIÓN", Reporte));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(GrayColor.CYAN);
                    titulo.addCell(cell);

                    cell = new PdfPCell(new Phrase("EXISTENCIA", Reporte));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(GrayColor.CYAN);
                    titulo.addCell(cell);

                    cell = new PdfPCell(new Phrase("STOCK MÁXIMO", Reporte));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(GrayColor.CYAN);
                    titulo.addCell(cell);

                    cell = new PdfPCell(new Phrase("CANTIDAD A PEDIR", Reporte));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(GrayColor.CYAN);
                    titulo.addCell(cell);

                    //AÑADE LAS TABLAS AL DOCUMENTO EN ORDEN PRIMERO LOS ENCABEZADOS Y FINALMENTE LOS ARTICULOS BAJOS
                    Repo.add(titulo);
                    Repo.add(table);
                }
            }

            //SE SACANA LAS TABLAS Y REGISTROS DE LA BASE DE DATOS ORDENADOS ALFABETICAMENTE POR EL NOMBRE
            q = "SELECT nombre FROM datos WHERE tipo='Secos' ORDER BY nombre";
            q2 = "SELECT cantidad FROM datos WHERE tipo='Secos' ORDER BY nombre";
            q3 = "SELECT maximo FROM datos WHERE tipo='Secos' ORDER BY nombre";
            q4 = "SELECT minimo FROM datos WHERE tipo='Secos' ORDER BY nombre";

            registro2 = data.rawQuery(q2, null);
            registros = data.rawQuery(q, null);
            registro3 = data.rawQuery(q3, null);
            registro4 = data.rawQuery(q4, null);

            existe = false;
            tabla = false;

            Repo.add(new Paragraph("\n*********************************SECOS**********************************\n\n", FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, Font.BOLD, BaseColor.BLUE)));

            //CHECA SI EL PRIMER ESPACIO NO ES NULO Y SE MUEVE AL PRIMER REGISTRO
            if (registros != null && registro2 != null && registro3 != null && registro4 != null) {
                if (registros.moveToFirst() && registro2.moveToFirst() && registro3.moveToFirst() && registro4.moveToFirst()) {
                    do {
                        if (Integer.parseInt(registro2.getString(0)) <= Integer.parseInt(registro4.getString(0))) {//COMPARA QUE LA EXISTENCIA NO SEA MENOR AL MINIMO
                            existe = true;
                            tabla = true;

                            //SE AÑADEN LOS CAMPOS NECESARIOS A UNA CELDA Y SE LE COLOCA LA FUENTE DESEADA, ASÍ COMO ALINEACIÓN Y EL INGRESO A LA TABLA
                            cell = new PdfPCell(new Phrase(registros.getString(0), font));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table.addCell(cell);

                            cell = new PdfPCell(new Phrase(registro2.getString(0), font));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table.addCell(cell);

                            cell = new PdfPCell(new Phrase(registro3.getString(0), font));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table.addCell(cell);


                            pedido = Integer.parseInt(registro3.getString(0)) - Integer.parseInt(registro2.getString(0));
                            cell = new PdfPCell(new Phrase(pedido.toString(), font));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table.addCell(cell);
                        }
                    }
                    while (registros.moveToNext() && registro2.moveToNext() && registro3.moveToNext() && registro4.moveToNext());//SE MUEVE AL SIGUIENTE REGISTRO
                } else {//SI NO HAY ARTICULOS DADOS DE ALTA EN EL INVENTARIO
                    existe = true;
                    // SE INGRESA ESTA CADENA AL DOCUMENTO CON UN FORMATO ESPECÍFICO
                    Repo.add(new Paragraph("-----------------NO HAY ARTICULOS EN INVENTARIO------------------", FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, Font.NORMAL, BaseColor.RED)));
                }

                if (!existe) {//SI EXISTEN ARTICULOS DADOS DE ALTA EN INVENTARIO PERO NINGUNO ESTA BAJO EN EXISTENCIA SE ENVIA LA CADENA QUE SE MUESTRA A CONTINUACION
                    Repo.add(new Paragraph("------------NO HAY ARTICULOS BAJOS EN INVENTARIO------------", FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, Font.NORMAL, BaseColor.RED)));
                }

                if (tabla) {//SI LOGRO HACER UN REGISTRO, INGRESARÁ A ESTE PUNTO
                    //COLOCACION DE LOS ENCABEZADOS DE LAS TABLAS ASI COMO LA FUENTE, LA ALINEACIÓN DESEADA, EL COLOR DE LA CELDA Y FINALMENTE EL INGRESO A LAS TABLAS DE LAS CELDAS
                    cell = new PdfPCell(new Phrase("DESCRIPCIÓN", Reporte));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(GrayColor.CYAN);
                    titulo.addCell(cell);

                    cell = new PdfPCell(new Phrase("EXISTENCIA", Reporte));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(GrayColor.CYAN);
                    titulo.addCell(cell);

                    cell = new PdfPCell(new Phrase("STOCK MÁXIMO", Reporte));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(GrayColor.CYAN);
                    titulo.addCell(cell);

                    cell = new PdfPCell(new Phrase("CANTIDAD A PEDIR", Reporte));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(GrayColor.CYAN);
                    titulo.addCell(cell);

                    //AÑADE LAS TABLAS AL DOCUMENTO EN ORDEN PRIMERO LOS ENCABEZADOS Y FINALMENTE LOS ARTICULOS BAJOS
                    Repo.add(titulo);
                    Repo.add(table);
                }
            }

        } catch (SQLException e) {//CAPTURA DE ERROR

            e.printStackTrace();
        }

        return Repo;
    }

    String SMS() { //METODO QUE REGRESA LA CADENA DE PRODUCTOS BAJOS PARA SER ENVIADOS POR SMS
        String mensaje = "Prod. Bajos: "; //SE PONE EL INICIO DE LA CADENA

        //SE SACANA LAS TABLAS Y REGISTROS DE LA BASE DE DATOS ORDENADOS ALFABETICAMENTE POR EL NOMBRE
        SQLiteDatabase data = this.getWritableDatabase();
        String q = "SELECT nombre FROM datos ORDER BY nombre";
        String q2 = "SELECT cantidad FROM datos ORDER BY nombre";
        String q4 = "SELECT minimo FROM datos ORDER BY nombre";
        Cursor registro2 = data.rawQuery(q2, null);
        Cursor registros = data.rawQuery(q, null);
        Cursor registro4 = data.rawQuery(q4, null);

        boolean existe = false;

        try {
            //CHECA SI EL PRIMER ESPACIO NO ES NULO Y SE MUEVE AL PRIMER REGISTRO
            if (registros != null && registro2 != null && registro4 != null) {
                if (registros.moveToFirst() && registro2.moveToFirst() && registro4.moveToFirst()) {
                    do {
                        if (Integer.parseInt(registro2.getString(0)) <= Integer.parseInt(registro4.getString(0))) {//COMPARA QUE LA EXISTENCIA NO SEA MENOR AL MINIMO
                            existe = true;
                            mensaje += registros.getString(0) + ", ";//SE AGREGA EL NOMBRE DEL PRODUCTO BAJO A LA CADENA QUE SERA ENVIADA POR SMS
                        }
                    }
                    while (registros.moveToNext() && registro2.moveToNext() && registro4.moveToNext());//SE MUEVE AL SIGUIENTE REGISTRO

                } else {//SI NO HAY ARTICULOS DADOS DE ALTA EN EL INVENTARIO
                    existe = true;
                    mensaje = "NO HAY ARTICULOS EN INVENTARIO"; // SE ENVIA ESTA CADENA POR SMS
                }

                if (!existe) { //SI EXISTEN ARTICULOS DADOS DE ALTA EN INVENTARIO PERO NINGUNO ESTA BAJO EN EXISTENCIA SE ENVIA LA CADENA QUE SE MUESTRA A CONTINUACION
                    mensaje = "NO HAY ARTICULOS BAJOS EN INVENTARIO";
                }
            }

        } catch (SQLException e) { //CAPTURA DE ERROR

            e.printStackTrace();
        }

        return mensaje;
    }

    //FIN DE LOS METODOS PARA LOS ELEMENTOS

}
