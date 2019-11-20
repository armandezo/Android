package com.inventariostap.mario.controldeinventarios;

import android.os.Parcel;
import android.os.Parcelable;



public class Category {

    private String title, tipo, codigo;
    private int cantidad, smin, smax;
    private String imagen;

    //CLASE PARA REFENCIAR LOS ARTICULOS A REGISTRAR
    public Category(String title, int cantidad, int smin, int smax, String tipo, String image, String codigo) {//CONSTRUCTOR PARA UN ELEMENTO COMPLETO
        super();
        //SE DECLARA CADA UNO DE LOS ELEMENTOS
        this.title = title;
        this.cantidad = cantidad;
        this.smin = smin;
        this.smax = smax;
        this.tipo = tipo;
        this.imagen = image;
        this.codigo = codigo;
    }

    public Category(String nombre, String cantidad, String imagen) {//CONSTRUCTOR PARA LOS ELEMENTOS QUE SE MOSTRARÁN EN LA LISTA
        this.title = nombre;
        this.cantidad = Integer.valueOf(cantidad);
        this.imagen = imagen;
    }

    //METODOS SET Y GET
    public String getCantidad() {
        return "" + cantidad;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getSmin() {
        return smin;
    }

    public void setSmin(int smin) {
        this.smin = smin;
    }

    public int getSmax() {
        return smax;
    }

    public void setSmax(int smax) {
        this.smax = smax;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    //FIN DE LOS METODOS SET Y GET

}
