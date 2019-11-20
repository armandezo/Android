package com.inventariostap.mario.controldeinventarios;

import android.view.View;



public interface ItemClickListener {//INTERFAZ LA REALIZAR ACCIONES AL PRECIONAR SOBRE UN ELEMENTOS...UTILIZADO EN EL RECYCLEVIEW

    void onIntentClick(View view, int posicion);

}
