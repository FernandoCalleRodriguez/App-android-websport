package com.example.ferky.tfg;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListadoReservasFragment extends Fragment {
    private static final String TAG = ListadoReservasFragment.class.getName();
    //Atributo necesario para comunicar el fragment con la Activity
    ArrayList<Reserva> listaReservas;
    private OnFragmentInteractionListener mListener;
    ArrayList<String> aPistasNombre = new ArrayList<String>();
    private Spinner spPista;


    //definición objeto Databasefirebase
    private DatabaseReference ref;

    public ListadoReservasFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listado_reservas, container, false);

        //Inicializar y localizar los componentes del xml
        final ListView lista = (ListView) view.findViewById(R.id.lista);
        spPista = (Spinner) view.findViewById(R.id.sp_pista_reserva);

        ((MainActivity) getActivity()).hideFloatingActionButton();

        //Realizar la consulta para obtener todas las reservas de tu usuario
        listaReservas = new ArrayList<Reserva>();

        aPistasNombre.clear();
        aPistasNombre.add("Reservas activas");
        aPistasNombre.add("Historial de reservas");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, aPistasNombre);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPista.setAdapter(adapter);

        ref = FirebaseDatabase.getInstance().getReference().child("Reservas");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
         String id = "";
        if(user!=null){
            id = user.getUid();
        }
        final String uid = id;
        //final String uid = "HXUZv3MCIah7GsiiJOqItanz9RF2";

        spPista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //lista.removeAllViewsInLayout();

                ref.orderByChild("usuario").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    Reserva r = null;

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listaReservas.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            String id = postSnapshot.getKey();
                            Log.d(TAG, "ID: " + id);

                            //r = new Reserva(postSnapshot.child("duracion").getValue().toString(),postSnapshot.child("fecha").getValue().toString(),postSnapshot.child("fecha_pista").getValue().toString(),postSnapshot.child("pista").getValue().toString(),postSnapshot.child("inicio").getValue().toString(),postSnapshot.child("usuario").getValue().toString());
                            r = new Reserva();
                            r.setHorainicio(postSnapshot.child("horainicio").getValue().toString());
                            r.setFecha(postSnapshot.child("fecha").getValue().toString().substring(0, 4) + "/" + postSnapshot.child("fecha").getValue().toString().substring(4, 6) + "/" + postSnapshot.child("fecha").getValue().toString().substring(6, 8));

                            r.setPrecio(postSnapshot.child("precio").getValue().toString() + " €");
                            r.setId(postSnapshot.getKey());
                            if (spPista.getSelectedItem().toString().equals("Reservas activas")) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Date convertedDate = new Date();
                                try {
                                    convertedDate = dateFormat.parse(r.getFecha()+" "+r.getHorainicio()+":00");
                                    Log.d(TAG,"Proximas "+convertedDate+" "+new Date());

                                    if (/*convertedDate.after(new Date()) ||*/ convertedDate.compareTo(new Date()) > 0){
                                        listaReservas.add(r);
                                        Log.d(TAG,"Proximas");

                                    }
                                } catch (ParseException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    Log.d(TAG,"Error");

                                }

                            } else {
                                listaReservas.add(r);
                                Log.d(TAG,"Historial");


                            }
                        }
                        Adaptador adaptador = new Adaptador(getContext(), listaReservas);
                        lista.setAdapter(adaptador);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                //Nada seleccionado
                Log.d(TAG,"Ninguna opción seleccionada");

            }
        });
        //Reserva r = new Reserva("a","a","a","a","a","a");
        //listaReservas.add(r);

        //Se genera el adaptador que se asignará al ListView



        //Se incorpora un escuchador para cuando se pulsa sobre un item del ListView
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                //Se recupera la reserva seleccionado
                Reserva objeto = (Reserva) adapterView.getItemAtPosition(pos);

                //Toda la informacion se almacena en un Bundle para posteriormente
                //enviar esta informacion al Activity contenedor (MainActivity)
                Bundle bundle = new Bundle();
                bundle.putString("id", objeto.getId());
                bundle.putString("tipo", "usuario");
                mListener.onFragmentInteraction(bundle,"Reserva");


            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
