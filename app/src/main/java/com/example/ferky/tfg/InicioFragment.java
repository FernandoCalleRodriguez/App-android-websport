package com.example.ferky.tfg;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class InicioFragment extends Fragment {
    private static final String TAG = InicioFragment.class.getName();
    //Atributo necesario para comunicar el fragment con la Activity
    ArrayList<Pista> listaPistas;
    private OnFragmentInteractionListener mListener;


    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    //definición objeto Databasefirebase
    private DatabaseReference ref;

    public InicioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        // Obtener el Recycler
        recycler = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(lManager);

        listaPistas = new ArrayList<Pista>();

        //Realizar la consulta para obtener todas las pistas
        ref = FirebaseDatabase.getInstance().getReference().child("Pistas");

        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid;
        if(user!=null){
            uid = user.getUid();
        }*/

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            Pista r = null;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String id = postSnapshot.getKey();
                    Log.d(TAG,"ID: "  +id);

                    //r = new Reserva(postSnapshot.child("duracion").getValue().toString(),postSnapshot.child("fecha").getValue().toString(),postSnapshot.child("fecha_pista").getValue().toString(),postSnapshot.child("pista").getValue().toString(),postSnapshot.child("inicio").getValue().toString(),postSnapshot.child("usuario").getValue().toString());
                    r = new Pista();
                    r.setTipo(postSnapshot.child("tipo").getValue().toString());
                    r.setNombre(postSnapshot.child("nombre").getValue().toString());
                    r.setDuracion(postSnapshot.child("duracion").getValue().toString());
                    r.setPrecio(postSnapshot.child("precio").getValue().toString()+" €");
                    r.setId(postSnapshot.getKey());
                    listaPistas.add(r);
                }

                // Crear un nuevo adaptador
                AdaptadorPista adapter = new AdaptadorPista(getContext(),listaPistas);
                adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick( View view) {
                        //Se recupera la reserva seleccionado
                        Pista objeto = (Pista) listaPistas.get(recycler.getChildAdapterPosition(view));

                        //Toda la informacion se almacena en un Bundle para posteriormente
                        //enviar esta informacion al Activity contenedor (MainActivity)
                        Bundle bundle = new Bundle();
                        bundle.putString("id",  objeto.getId());
                        mListener.onFragmentInteraction(bundle,"Pista");


                    }
                });
                recycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Reserva r = new Reserva("a","a","a","a","a","a");
        //listaReservas.add(r);

        //Se genera el adaptador que se asignará al ListView

        //Se incorpora un escuchador para cuando se pulsa sobre un item del ListView

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ListadoReservasFragment.OnFragmentInteractionListener) {
            mListener = (InicioFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        //TODO: Update argument type and name
        void onFragmentInteraction(Bundle bundle,String data);
    }
}


