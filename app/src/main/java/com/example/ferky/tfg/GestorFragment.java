package com.example.ferky.tfg;


import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class GestorFragment extends Fragment {
    private static final String TAG = GestorFragment.class.getName();

    private static final String CERO = "0";
    private static final String BARRA = "/";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    //Atributo necesario para comunicar el fragment con la Activity
    ArrayList<Reserva> listaReservas;
    ArrayList<String> aPistasNombre = new ArrayList<String>();
    Map<String, String> map = new HashMap<String, String>();

    private OnFragmentInteractionListener mListener;

    private Spinner spPista;
    private EditText etFecha;
    private ImageButton ibObtenerFecha;
    private ListView lista;


    //definición objeto Databasefirebase
    private DatabaseReference ref;

    public GestorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gestor, container, false);

        //Inicializar y localizar los componentes del xml
        lista = (ListView) view.findViewById(R.id.lista);
        // EditText donde se mostrara la fecha obtenida
        etFecha = (EditText) view.findViewById(R.id.et_mostrar_fecha_picker);
        // ImageButton del cual usaremos el evento clic para obtener la fecha
        ibObtenerFecha = (ImageButton) view.findViewById(R.id.ib_obtener_fecha);
        //Spinner donde mostraremos las pista
        spPista = (Spinner) view.findViewById(R.id.sp_fecha_reservas);

        //Realizar la consulta para obtener todas las reservas de tu usuario
        listaReservas = new ArrayList<Reserva>();

        aPistasNombre.clear();

        ref = FirebaseDatabase.getInstance().getReference().child("Pistas");
        Log.d(TAG, ref.getKey());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String id = postSnapshot.getKey();
                    String nombre = postSnapshot.child("nombre").getValue().toString();
                    aPistasNombre.add(nombre);
                    Log.d(TAG, id + " " + nombre);
                    map.put(nombre, id);
                    //tipos[nombre] = postSnapshot.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        aPistasNombre.add("Selecciona un tipo de pista");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, aPistasNombre);
        spPista.setAdapter(adapter);


        //Evento hacer click en calendario
        ibObtenerFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatePickerDialog dpd = new DatePickerDialog(getActivity(), R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                        final int mesActual = month + 1;
                        //Formateo el día obtenido: antepone el 0 si son menores de 10
                        String diaFormateado = (dayOfMonth < 10) ? CERO + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                        //Formateo el mes obtenido: antepone el 0 si son menores de 10
                        String mesFormateado = (mesActual < 10) ? CERO + String.valueOf(mesActual) : String.valueOf(mesActual);
                        //Muestro la fecha con el formato deseado
                        etFecha.setText(year + BARRA + mesFormateado + BARRA + diaFormateado);
                    }
                }, anio, mes, dia);
                dpd.getDatePicker().setMinDate(new Date().getTime());
                //Convertir fecha long a date
                Date hoy = new Date(new Date().getTime());
                //Sumamos 2 meses
                Calendar cal = Calendar.getInstance();
                cal.setTime(hoy);
                cal.add(Calendar.MONTH, 2);
                //Convertimos date a long
                hoy = cal.getTime();
                dpd.getDatePicker().setMaxDate(hoy.getTime());
                dpd.show();
            }
        });
        Date myDate = new Date();

        //Aquí obtienes el formato que deseas
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        etFecha.setText(sdf.format(myDate));

        spPista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                    BuscarReservas(map.get(spPista.getSelectedItem().toString()),Integer.parseInt(etFecha.getText().toString().replace("/", "")));
                    /*Log.d("AAAAAAAAAAAAAAAAAAA","B");
                    Log.d("RESERVA", map.get(spPista.getSelectedItem().toString())+" "+etFecha.getText().toString().replace("/", ""));
                    ref =  FirebaseDatabase.getInstance().getReference().child("Reservas");
                    Log.d(TAG, ref.getKey());
                    ref.child("Reservas").orderByChild("fecha_pista").equalTo(etFecha.getText().toString().replace("/", "")+" "+spPista.getSelectedItem().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        Reserva r = null;

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            listaReservas.clear();

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String id = postSnapshot.getKey();
                                Log.d(TAG, id + " " + "salida");
                                r = new Reserva();
                                r.setHorainicio(postSnapshot.child("horainicio").getValue().toString());
                                r.setFecha(postSnapshot.child("fecha").getValue().toString().substring(0, 4) + "/" + postSnapshot.child("fecha").getValue().toString().substring(4, 6) + "/" + postSnapshot.child("fecha").getValue().toString().substring(6, 8));
                                r.setPrecio(postSnapshot.child("precio").getValue().toString() + " €");
                                r.setId(postSnapshot.getKey());
                                listaReservas.add(r);

                            }
                            Adaptador adaptador = new Adaptador(getContext(), listaReservas);
                            lista.setAdapter(adaptador);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });*/

                }


                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    //Nada seleccionado
                    Log.d(TAG, "Ninguna opción seleccionada");

                }

            });

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
                bundle.putString("tipo", "gestor");
                mListener.onFragmentInteraction(bundle,"Reserva");


            }
        });

        return view;
    }

    public void BuscarReservas(String pista,final Integer fecha){

        ref =  FirebaseDatabase.getInstance().getReference().child("Reservas");
        Log.d(TAG, ref.getKey());
            ref.orderByChild("fecha_pista").equalTo(fecha+" "+pista).addListenerForSingleValueEvent(new ValueEventListener() {
                Reserva r = null;
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listaReservas.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String id = postSnapshot.getKey();
                        Log.d(TAG, id + " " + "salida");
                        r = new Reserva();
                        r.setHorainicio(postSnapshot.child("horainicio").getValue().toString());
                        r.setFecha(postSnapshot.child("fecha").getValue().toString().substring(0, 4) + "/" + postSnapshot.child("fecha").getValue().toString().substring(4, 6) + "/" + postSnapshot.child("fecha").getValue().toString().substring(6, 8));
                        r.setPrecio(postSnapshot.child("precio").getValue().toString() + " €");
                        r.setId(postSnapshot.getKey());
                        listaReservas.add(r);

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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GestorFragment.OnFragmentInteractionListener) {
            mListener = (GestorFragment.OnFragmentInteractionListener) context;
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
