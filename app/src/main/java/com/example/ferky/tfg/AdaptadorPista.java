package com.example.ferky.tfg;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AdaptadorPista  extends RecyclerView.Adapter<AdaptadorPista.PistaViewHolder> implements View.OnClickListener{
    private static final String TAG = Adaptador.class.getName();

    private View.OnClickListener listener;
    //Declaracion de variables
    Context contexto;
    List<Pista> listaObjetos;

    // Este es nuestro constructor (puede variar según lo que queremos mostrar)
    public AdaptadorPista(Context contexto, List<Pista> listaObjetos) {
        this.contexto = contexto;
        this.listaObjetos = listaObjetos;
    }

    // El layout manager invoca este método
    // para renderizar cada elemento del RecyclerView
    @Override
    public PistaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Creamos una nueva vista
        View  v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pista_cardview, parent, false);

        v.setOnClickListener(this);
        // create ViewHolder
        PistaViewHolder vh = new PistaViewHolder(v);

        // Aquí podemos definir tamaños, márgenes, paddings
        // ...
        return vh;
    }

    // Este método reemplaza el contenido de cada view,
    // para cada elemento de la lista (nótese el argumento position)
    @Override
    public void onBindViewHolder(PistaViewHolder holder, int position) {
        // - obtenemos un elemento del dataset según su posición
        // - reemplazamos el contenido de los views según tales datos

        //holder.imagen.setImageResource(listaObjetos.get(position).getImagen());
        if(listaObjetos.get(position).getTipo().equals("-LUGbpOsRZbLQ8RyANhX")){
            holder.imagen.setImageResource(R.drawable.futbol);
        }else if(listaObjetos.get(position).getTipo().equals("-LTCSZEmM6Hy9ceN_8Qs")){
            holder.imagen.setImageResource(R.drawable.padel);
        }else{
            Log.d(TAG,listaObjetos.get(position).getTipo());
            holder.imagen.setImageResource(R.drawable.otros);
        }
        holder.nombre.setText(listaObjetos.get(position).getNombre());
        holder.precio.setText(listaObjetos.get(position).getPrecio());

    }

    // Obtener referencias de los componentes visuales para cada elemento
    // Es decir, referencias de los EditText, TextViews, Buttons
    public  class PistaViewHolder extends RecyclerView.ViewHolder{
        // en este ejemplo cada elemento consta solo de un título
        public ImageView imagen;
        public TextView nombre;
        public TextView precio;
        public ConstraintLayout constr;

        public PistaViewHolder(View v) {
            super(v);
            imagen = (ImageView) v.findViewById(R.id.imagen);
            nombre = (TextView) v.findViewById(R.id.tvInformId);
            precio = (TextView) v.findViewById(R.id.textView2);
            constr = (ConstraintLayout) v.findViewById(R.id.listado_pistas_fragment);
        }


    }


    public void setOnClickListener(View.OnClickListener listener) {
        this.listener =  listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null){
            listener.onClick(view);
        }

        /*System.out.println("onClick");
        TextView tv = (TextView) view.findViewById(R.id.tvInformId);
        String id = tv.getText().toString();
        mItemClickListener.onItemClick(view, getAdapterPosition(), id); //OnItemClickListener mItemClickListener;*/

    }

    @Override
    public int getItemCount() {
        return listaObjetos.size();
    }

    // Método que define la cantidad de elementos del RecyclerView
    // Puede ser más complejo (por ejemplo si implementamos filtros o búsquedas)
    /*@Override
    public int getItemCount() {
        return mDataSet.length;
    }

    //Método que se utiliza para obtener el id del objeto almacenado en una posicion determinada
    @Override
    public long getItemId(int posicion) {
        return 56;
    }

    //Método que se encarga de generar la vista de los item
    //@Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        //infla la vista
        View vista = view;
        LayoutInflater inflate = LayoutInflater.from(contexto);
        vista = inflate.inflate(R.layout.list_view_pista, null);

        //Inicializar y localizar los componentes del xml
        TextView nombre = (TextView) vista.findViewById(R.id.tvNombre);
        TextView hora = (TextView) vista.findViewById(R.id.tvDuracion);
        TextView precio = (TextView) vista.findViewById(R.id.tvPrecio);

        //Coloca el contenido en los componentes
        nombre.setText(listaObjetos.get(position).getNombre());
        hora.setText(listaObjetos.get(position).getDuracion());
        precio.setText(listaObjetos.get(position).getPrecio());
        /*pista.setText("a");
        dia.setText("b");
        hora.setText("c");
        duracion.setText("d");

        return vista;
    }*/
}
