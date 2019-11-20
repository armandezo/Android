package com.inventariostap.mario.controldeinventarios;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterCategory extends ArrayAdapter<Category> {//ADAPTADRO QUE SIRVE COMO INTERMEDIARIO ENTRE EL RECYVLEVIEW Y EL LAYOUT

    private final ArrayList<Category> list;//ARRAY QUE CONTENDRA LOS DATOS
    private final Context context;//CONTENDRÁ EL CONTEXTO DE DONDE ES LLAMADO

    public AdapterCategory(Context context, ArrayList<Category> list) {//CONSTRUCTOR
        super(context, R.layout.item_categary, list);
        this.context = context;//SE ALMACENA EL CONTEXT
        this.list = list;//SE GUARDAN LOS DATOS RECIBIDOS

    }


    static class ViewHolder {//CLASE QUE SIRVE COMO CONTENEDOR. SE UTILIZA PARA ASIGNAR CADA DATO CON SU RESPECTIVA VISTA
        protected TextView nombre;//SE CREAN LAS VARIABLES A MANEJAR
        protected TextView cantidad;
        protected ImageView imagen;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {//AL OBTENER LAS VISTAS
        View view = null;

        if (convertView == null) {//SI LA VISTA ES NULA, SE INFLAN LOS LAYOUT

            LayoutInflater inflate = LayoutInflater.from(context);
            view = inflate.inflate(R.layout.item_categary, null);//SE INFLA CON EL CONTENEDOR PERSONALIZADO
            ViewHolder viewHolder = new ViewHolder();//SE INICIALIZA EL VIEWHOLDER

            viewHolder.cantidad = (TextView) view.findViewById(R.id.cantidad);//SE REFENCIA CADA DATO CON SU ELEMENTO EN EL LAYOUT
            viewHolder.imagen = (ImageView) view.findViewById(R.id.imageView);
            viewHolder.nombre = (TextView) view.findViewById(R.id.producto);

            view.setTag(viewHolder);//SE ALMACENA EL HOLDER EN LA VISTA CORRESPONDIENTE

        } else {
            view = convertView;//SI LA VISTA YA ESTÁ INFLADA, SOLO SEIGUALA A LA VISTA OBTENIDA
        }


        ViewHolder holder = (ViewHolder) view.getTag();//SE OBTIENEN LOS ELEMENTOS YA INFLADOS

        holder.nombre.setText(list.get(position).getTitle());//SE INSERTA EL NOMBRE DEL OBJETO A SI RESPECTIVO TEXTVIEW
        holder.cantidad.setText(list.get(position).getCantidad());//
        if (list.get(position).getImagen().equals("default")) {//SI EL ARCHIVO DE LA IMAGEN TIEN POR DEFECTO UN "DEFAULT", SIGNIFICA QUE NO SE HA TOMADO FOTO DEL ARTICULO
//NO SE AGREGA IMAGENES AL ELEMENTO DE LA LISTA
        } else {//DE OTRO MODO, EL ELEMENTO TIENE EL NOMBRE Y LA RUTA DE LA IMAGEN


            Bitmap mBitmap = BitmapFactory.decodeFile(list.get(position).getImagen());//SE HACE UNA MAGEN A PARTIR DE LA RUTA
            //SE REDIMENCIONA A LA IMAGEN ACTUAL PARA OPTIMIZAR LA LISTA
            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();
            int newWidth = 960;
            int newHeigth = 960;
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeigth) / height;
            // create a matrix for the manipulation
            Matrix matrix = new Matrix();
            // resize the bit map
            matrix.postScale(scaleWidth, scaleHeight);
            // recreate the new Bitmap
            Bitmap image = Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);//SE CREA LA NUEVA IMAGEN REDIMENCIONADA
            holder.imagen.setImageBitmap(image);//SE INSERTA LA IMAGEN EN EL ELEMENTO DE LA LISTA
        }

        return view;

    }


}
