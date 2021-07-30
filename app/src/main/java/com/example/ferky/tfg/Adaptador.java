package com.example.ferky.tfg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class Adaptador  extends BaseAdapter {
    private static final String TAG = Adaptador.class.getName();

    //Declaracion de variables
    Context contexto;
    List<Reserva> listaObjetos;

    //Constructor
    public Adaptador(Context contexto, List<Reserva> listaObjetos) {
        this.contexto = contexto;
        this.listaObjetos = listaObjetos;
    }

    //Método que se utiliza para obtener el tamaño de la lista
    @Override
    public int getCount() {
        return listaObjetos.size();
    }

    //Método que se utiliza para obtener el objeto almacenado en una posicion determinada
    @Override
    public Object getItem(int posicion) {
        return listaObjetos.get(posicion);
    }

    //Método que se utiliza para obtener el id del objeto almacenado en una posicion determinada
    @Override
    public long getItemId(int posicion) {
        return 56;
    }

    //Método que se encarga de generar la vista de los item
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        //infla la vista
        View vista = view;
        LayoutInflater inflate = LayoutInflater.from(contexto);
        vista = inflate.inflate(R.layout.list_view_reserva, null);

        //Inicializar y localizar los componentes del xml
        TextView dia = (TextView) vista.findViewById(R.id.tvNombre);
        TextView hora = (TextView) vista.findViewById(R.id.tvHora);
        TextView precio = (TextView) vista.findViewById(R.id.tvPrecio);

        //Coloca el contenido en los componentes
        dia.setText(listaObjetos.get(position).getFecha());
        hora.setText(listaObjetos.get(position).getHorainicio());
        precio.setText(listaObjetos.get(position).getPrecio());
        /*pista.setText("a");
        dia.setText("b");
        hora.setText("c");
        duracion.setText("d");*/

        return vista;
    }
}
