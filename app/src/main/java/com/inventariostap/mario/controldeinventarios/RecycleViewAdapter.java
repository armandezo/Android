package com.inventariostap.mario.controldeinventarios;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;




public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    Context context;
    ArrayList<Category> values;
    View view;
    ViewHolder viewHolder;

    public RecycleViewAdapter(Context context, ArrayList<Category> values) {//CONSTRUCTOR DE LA CLASE
        this.context = context;
        this.values = values;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {//SUBCLASE PARA LAS VISTAS
        //SE IMPLEMENTAN LOS INTERFACES PARA LEER LOS CLIKS Y LONGCLICKS
        private ItemClickListener itemClickListener;
        private MyLongClickListener myLongClickLitener;
        TextView nombre;
        TextView cantidad;
        ImageView imagen;

        public ViewHolder(View itemView) {//CONSTRUCTOR
            super(itemView);
            nombre = (TextView) itemView.findViewById(R.id.producto);
            cantidad = (TextView) itemView.findViewById(R.id.cantidad);
            imagen = (ImageView) itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        //SE SOBREESCRIBEN LOS METODOS PARA LOS CLICKS
        @Override
        public void onClick(View view) {
            this.itemClickListener.onIntentClick(view, getAdapterPosition());
        }

        public void setItemClickListener(ItemClickListener ic) {
            this.itemClickListener = ic;
        }

        @Override
        public boolean onLongClick(View view) {
            this.myLongClickLitener.onItemLongClick(getAdapterPosition(), view);
            return true;
        }

        public void setOnItemLongClickListener(MyLongClickListener clickListener) {
            this.myLongClickLitener = clickListener;
        }

        //FIN DE LA SOBREESCRITURA DE LOS METODOS

    }

    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {//LO QUE SE REALIZA CON EL LANZAMIENTO DE LA CLASE
        view = LayoutInflater.from(context).inflate(R.layout.item_categary, parent, false);//SE INFLA LA VUSTA CON EL CONTENEDOR PERSONALIZADO
        viewHolder = new ViewHolder(view);//SE LLENA EL VIEW HOLDER CON LA VISTA PREVIAMENTE INFLADA
        return viewHolder;//SE RETORNA EL VIEWHOLDER
    }

    @Override
    public void onBindViewHolder(final RecycleViewAdapter.ViewHolder holder, final int position) {//SE GUARDA CADA UNO DE LOS ELEMENTOS DENTRO DE SU RESPECTIVA VISTA
        holder.nombre.setText(values.get(position).getTitle());
        holder.cantidad.setText("Exist..." + values.get(position).getCantidad());

        if (values.get(position).getImagen().equals("default")) {

        } else {
            Bitmap mBitmap = BitmapFactory.decodeFile(values.get(position).getImagen());//SE OBTIENE LA IMAGEN
            //SE REDIMENCIONA LA IMAGEN
            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();
            int newWidth = 960;
            int newHeigth = 960;
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeigth) / height;
            //SE CREA UNA MATRIZ PARA LA MANIPULACION
            Matrix matrix = new Matrix();

            matrix.postScale(scaleWidth, scaleHeight);
            // SE CREA UN NUEVO BITMAP
            Bitmap image = Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);
            holder.imagen.setImageBitmap(image);//SE INSERTA LA IMAGEN REDIMENCIONADA EN SU RESPECTIVA VISTA
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onIntentClick(View view, int pos) {//AL PRECIONAR SOBRE LA LISTA
                Intent intent = new Intent(context, Edit.class);//SE CREA UNA REDIRECION A LA CALSE DE EDICION
                intent.putExtra("Element", values.get(position).getTitle());//SE GUARDA EL VALOR A MODIFICAR, DENTRO DEL INTENT
                context.startActivity(intent);//SE REDIRECCIONA A LA CLASE DE EDICION

            }
        });

        holder.setOnItemLongClickListener(new RecycleViewAdapter.MyLongClickListener() {//METODO A RELAIZAR CUANDO SE HACE UN LONGCLICK

            @Override
            public boolean onItemLongClick(int position, View v) {
                try {
                    AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());//SE CONSTRUYE UN ALERTDIALOG
                    //SE RELLENA DE VALORES
                    alert.setTitle("¡Atención¡");
                    alert.setMessage("¿Desea eliminar este elemento?");
                    alert.setCancelable(false);
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {//METODO A REALIZAR SI SE PRECIONA EL BOTON ACEPTAR
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//ACCION A REALIZAR CUANDO SE PRECIONA EL BOTON DE ACEPTAR
                            BaseD db = new BaseD(context, null, null, 1);//SE REFERENCIA LA BASE DE DATOS
                            String message = db.deleteelement(holder.nombre.getText().toString());//SE ELIMINA EL ELEMENTO DE LA BSE DE DATPS
                            Toast.makeText(context, holder.nombre.getText().toString(), Toast.LENGTH_SHORT).show();//SE MUETSRA EL MENSAJE OBTENIDO DE LA ELIMINACION
                            Intent i;
                            switch (db.get_tipo(holder.nombre.toString())) {
                                case "Parrilla":
                                    i = new Intent(context, Frutas.class);//SE RETORNA A LA CLASE
                                    context.startActivity(i);
                                    break;
                                case "Cilindro":
                                    i = new Intent(context, Verduras.class);//SE RETORNA A LA CLASE
                                    context.startActivity(i);
                                    break;
                                case "Cocina":
                                    i = new Intent(context, Secos.class);//SE RETORNA A LA CLASE
                                    context.startActivity(i);
                                    break;
                            }


                            //METODO PARA ELIMINAR EL ELEMENTO DE LA BASE DE DATOS
                        }
                    });

                    alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {//SI SE PRECIONA EL BOTON ACEPTAR
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //ACCION A REALIZAR SI SE PRECIONA EL BOTON DE CANCELAR
                        }
                    });

                    Dialog alerta = alert.create();
                    alerta.show();//SE MUESTRA EL MENSAJE

                } catch (Exception e) {
                    Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });


    }

    public interface MyLongClickListener {
        boolean onItemLongClick(int position, View v);
    }

    @Override
    public int getItemCount() {
        return values.size();
    }
}
