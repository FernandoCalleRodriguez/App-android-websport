package com.example.ferky.tfg;


import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import static java.lang.Integer.parseInt;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReservaFragment extends Fragment {
    private static final String TAG = ReservaFragment.class.getName();
    //Atributo necesario para comunicar el fragment con la Activity


    private static final String CERO = "0";
    private static final String BARRA = "/";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);
    //Widgets
    private EditText etFecha;
    private ImageButton ibObtenerFecha;
    private Spinner spTipoPista;
    private Button btenviar;

    //definición objeto Databasefirebase
    private DatabaseReference ref;

    ArrayList<String> addArray = new ArrayList<String>();
    ArrayList<String> aPistas = new ArrayList<String>();
    Map<String, String> map = new HashMap<String, String>();
    HashMap<String, Integer> NumReservas = new HashMap<String, Integer>();
    HashMap<String, String> Reserva = new HashMap<String, String>();


    public ReservaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reserva, container, false);

        ((MainActivity) getActivity()).hideFloatingActionButton();

        // EditText donde se mostrara la fecha obtenida
        etFecha = (EditText) view.findViewById(R.id.et_mostrar_fecha_picker);
        // ImageButton del cual usaremos el evento clic para obtener la fecha
        ibObtenerFecha = (ImageButton) view.findViewById(R.id.ib_obtener_fecha);
        //Spinner donde mostraremos los tipos de pista
        spTipoPista = (Spinner)  view.findViewById(R.id.sp_tipo_pista);
        //Button nos llevara a buscar las opciones de reserva
        btenviar = (Button)  view.findViewById(R.id.bt_enviar);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.getUid();
        //if(user!=null){

        ref = FirebaseDatabase.getInstance().getReference().child("Tipos");
        Log.d(TAG,ref.getKey());
        //}
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String id = postSnapshot.getKey();
                    String nombre = postSnapshot.child("nombre").getValue().toString();
                    addArray.add(nombre);
                    Log.d(TAG,id + " " + nombre);
                    map.put(nombre,id);
                    //tipos[nombre] = postSnapshot.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Se genera el adaptador que se asignará al Spinner
        //Se incorpora un escuchador para ver el item del Spinner seleccionado
        addArray.add("Selecciona un tipo de pista");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, addArray);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoPista.setAdapter(adapter);

        //Evento hacer click en calendario
        ibObtenerFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatePickerDialog dpd = new DatePickerDialog(getActivity(), R.style.DatePickerDialogTheme,new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                        final int mesActual = month + 1;
                        //Formateo el día obtenido: antepone el 0 si son menores de 10
                        String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                        //Formateo el mes obtenido: antepone el 0 si son menores de 10
                        String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                        //Muestro la fecha con el formato deseado
                        etFecha.setText( year + BARRA + mesFormateado +BARRA + diaFormateado);
                    }
                },anio,mes,dia);
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



        spTipoPista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {

                //Toast.makeText(getBaseContext(), parentView.getItemAtPosition(position).toString() , Toast.LENGTH_LONG).show();
                aPistas.clear();
                FirebaseDatabase.getInstance().getReference().child("Pistas").orderByChild("tipo").equalTo((map.get(spTipoPista.getSelectedItem().toString()))).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Reserva.clear();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                            //Log.d(TAG,"Pistas posibles: "  + postSnapshot.getKey().toString());
                            aPistas.add(postSnapshot.getKey().toString());
                            Reserva.put(postSnapshot.getKey().toString()+"_duracion",postSnapshot.child("duracion").getValue().toString());
                            Reserva.put(postSnapshot.getKey().toString()+"_precio",postSnapshot.child("precio").getValue().toString());
                            Reserva.put(postSnapshot.getKey().toString(),postSnapshot.child("nombre").getValue().toString());
                            //Reserva.put(postSnapshot.getKey().toString()+"_inicio",postSnapshot.child("inicio").getValue().toString());
                            //Reserva.put(postSnapshot.getKey().toString()+"_slots",postSnapshot.child("slots").getValue().toString());
                        }

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

        btenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Opciones elegidas: " + map.get(spTipoPista.getSelectedItem().toString()) + " AND " + etFecha.getText().toString());
                if(!aPistas.isEmpty()) {
                    //BuscarReservas(etFecha.getText().toString());
                    BuscarHorario(Integer.parseInt(etFecha.getText().toString().replace("/","")));
                }
            }


        });

        return view;

    }

    /*public void BuscarPistasPosibles( String TipoPista,String fecha){
        final boolean entrar = false;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //if(user!=null) {
            FirebaseDatabase.getInstance().getReference().child("Pistas").orderByChild("tipo").equalTo(TipoPista).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        Log.d(TAG,"Pistas posibles: "  + postSnapshot.getKey().toString());
                        aPistas.add(postSnapshot.getKey().toString());
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        //}
        if(!aPistas.isEmpty()){
            BuscarReservas("",fecha);

        }

    }*/
    public void BuscarReservas(final Integer fecha){

        String Pista = aPistas.toString();
        Log.d(TAG,"Pistas : "  + Pista);
        Log.d(TAG,"Fecha : "  + fecha);
        Iterator<String> it = aPistas.iterator();
        while(it.hasNext()){
           //Log.d(TAG,"Pista : "  + it.next().toString());
            final String pista = it.next().toString();
            ref =  FirebaseDatabase.getInstance().getReference().child("Reservas");
            ref.orderByChild("fecha_pista").equalTo(fecha+" "+pista).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int i = 0;
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        Log.d(TAG,"Reservas: "  + postSnapshot.getKey().toString());
                        String reserva = postSnapshot.child("inicio").getValue().toString()+"_"+postSnapshot.child("duracion").getValue().toString();
                        Reserva.put(postSnapshot.child("pista").getValue().toString()+"_"+i,reserva);

                        //aPistas .add(postSnapshot.getKey().toString());
                        i++;
                    }
                    NumReservas.put(pista,i);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        BuscarDisponibilidad(fecha);


    }

    public void BuscarHorario(final Integer fecha){

        String Pista = aPistas.toString();
        Log.d(TAG,"Pistas : "  + Pista);
        Log.d(TAG,"Fecha : "  + fecha);
        Iterator<String> it = aPistas.iterator();
        while(it.hasNext()){
            final Integer posicion = 0;
           //Log.d(TAG,"Pista : "  + it.next().toString());
            ref =  FirebaseDatabase.getInstance().getReference().child("Horarios");
            final String pista = it.next();
            Log.d(TAG,"PISTAAAAAA: "  + pista);
            ref.orderByChild("pista").equalTo(pista).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean HorarioValido = false;
                    boolean HorarioEspecial = false;
                    String inicio = "";
                    String slots  = "";

                    if(!dataSnapshot.exists()){
                        Toast.makeText(getContext(), "Este tipo de pista no tiene disponibilidad para esa fecha", Toast.LENGTH_LONG).show();
                    }
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        Log.d(TAG,"Horario: "  + postSnapshot.getKey().toString());
                        if(Integer.parseInt(postSnapshot.child("fechafin").getValue().toString()) >= fecha &&  fecha >= Integer.parseInt(postSnapshot.child("fechainicio").getValue().toString())){
                            Log.d(TAG,"Horario valido: "  + postSnapshot.getKey().toString());

                            if(Integer.parseInt(postSnapshot.child("findesemana").getValue().toString()) == EsFinDeSemana(fecha)){
                                if(Integer.parseInt(postSnapshot.child("especial").getValue().toString()) == 1){
                                    Reserva.put(postSnapshot.getKey().toString()+"_inicio",postSnapshot.child("horainicio").getValue().toString());
                                    Reserva.put(postSnapshot.getKey().toString()+"_slots",postSnapshot.child("slots").getValue().toString());
                                    BuscarReservas(fecha);
                                    HorarioEspecial = true;
                                }else{
                                   HorarioValido = true;
                                   inicio = postSnapshot.child("horainicio").getValue().toString();
                                   slots = postSnapshot.child("slots").getValue().toString();
                                }
                            }
                        }
                    }

                    if(HorarioValido && !HorarioEspecial ){
                        Reserva.put(pista+"_inicio",inicio);
                        Reserva.put(pista+"_slots",slots);
                        BuscarReservas(fecha);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    }

            });
        }


    }
    public int EsFinDeSemana(Integer fecha) {
        String trDate=fecha+"";
        Date tradeDate = new Date();
        try{
        tradeDate = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).parse(trDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] dias={"Domingo","Lunes","Martes", "Miércoles","Jueves","Viernes","Sábado"};
        Calendar cal= Calendar.getInstance();
        cal.setTime(tradeDate);
        int numeroDia=cal.get(Calendar.DAY_OF_WEEK);
        System.out.println("hoy es "+ dias[numeroDia - 1]);
        if(numeroDia - 1 == 0 || numeroDia - 1 == 6){
            return 1;
        }else{
            return 0;
        }
    }

    public void BuscarDisponibilidad(int fecha){
        String Pista = aPistas.toString();
        Log.d(TAG,"Pistas : "  + Pista);
        Iterator<String> it = aPistas.iterator();
        while(it.hasNext()) {
            String pista =it.next();
        Log.d(TAG,"Duración: "+Reserva.get(pista+"_duracion"));
        Log.d(TAG,"Inicio: "+Reserva.get(pista+"_inicio"));
        Log.d(TAG,"Slots: "+Reserva.get(pista+"_slots"));
        /*for(int i = 0;i < NumReservas.get(pista);i++)
            Log.d(TAG,"Inicio_Duracion "+Reserva.get(pista+"_"+i));*/
        }
        ElegirHoraFragment fr = new ElegirHoraFragment();
        Bundle b = new Bundle();
        b.putSerializable("hashmap",Reserva);
        b.putStringArrayList("pistas",aPistas);
        b.putSerializable("numReservas",NumReservas);
        b.putInt("fecha",fecha);
        fr.setArguments(b);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        //transaction.remove(getFragmentManager().findFragmentById(R.id.reserva_fragment)).commit();
        /*
         * When this container fragment is created, we fill it with our first
         * "real" fragment
         */
        transaction.replace(R.id.escenario,fr).addToBackStack(null);
        transaction.commit();

    }


}

