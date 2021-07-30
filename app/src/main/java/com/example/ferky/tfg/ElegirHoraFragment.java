package com.example.ferky.tfg;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.lang.Integer.parseInt;


/**
 * A simple {@link Fragment} subclass.
 */
public class ElegirHoraFragment extends Fragment {

    private static final String TAG = ElegirHoraFragment.class.getName();


    HashMap<String, String> Reserva = new HashMap<String, String>();
    HashMap<String, Integer> numReservas = new HashMap<String, Integer>();
    HashMap<Integer, Integer> Disponibilidad = new HashMap<Integer, Integer>();
    ArrayList<String> aPistas = new ArrayList<String>();
    ArrayList<String> aPistasNombre = new ArrayList<String>();
    ArrayList<String> AllCheckbox = new ArrayList<String>();
    Map<String, String> map = new HashMap<String, String>();


    int fecha ;
    String finalpista;
    LinearLayout seccionIndustria;
    private Spinner spPista;



    //Widgets
    private Button btenviar;


    public ElegirHoraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_elegir_hora, container, false);

        ((MainActivity) getActivity()).hideFloatingActionButton();

        Bundle b = this.getArguments();
        if(b.getSerializable("hashmap") != null){
            Reserva = (HashMap<String, String>)b.getSerializable("hashmap");
        }
        if(b.getSerializable("numReservas") != null){
            numReservas = (HashMap<String, Integer>)b.getSerializable("numReservas");
        }
        if(b.getStringArrayList("pistas") != null){
            aPistas = (ArrayList<String>)b.getStringArrayList("pistas");
        }
        fecha = b.getInt("fecha") ;
        //Button nos llevara a buscar las opciones de reserva
        btenviar = (Button)  view.findViewById(R.id.btn_reservar);
        seccionIndustria = (LinearLayout) view.findViewById(R.id.seccion_horas);
        spPista = (Spinner) view.findViewById(R.id.sp_pista);

        aPistasNombre.add("Selecciona una pista");
        String Pista2 = aPistas.toString();
        Iterator<String> it2 = aPistas.iterator();
        while(it2.hasNext()) {
            String pista =it2.next();
            aPistasNombre.add(Reserva.get(pista));
            map.put(Reserva.get(pista),pista);
        }
        //Se genera el adaptador que se asignará al Spinner

        //Se incorpora un escuchador para ver el item del Spinner seleccionado
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, aPistasNombre);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPista.setAdapter(adapter);

        spPista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                btenviar.setEnabled(false);
                seccionIndustria.removeAllViewsInLayout();
                if(!spPista.getSelectedItem().toString().equals("Selecciona una pista")) {
                    String pista = map.get(spPista.getSelectedItem().toString());
                    finalpista = pista;
                    //Log.d(TAG, "pista: " + pista);
                    if(Reserva.get(pista + "_slots") == null){
                        TextView edit = new TextView(getActivity());
                        edit.setText("Hoy la pista no está dsiponible");
                        /*edit.setLayoutParams(
                                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));*/
                        seccionIndustria.addView(edit);

                    }else{
                        btenviar.setEnabled(true);

                        for (int i = 1; i <= parseInt(Reserva.get(pista + "_slots")); i++) {
                            Disponibilidad.put(i, 1);
                        }
                        Log.d(TAG, "containsKey: " + numReservas.containsKey(pista));
                        Log.d(TAG, "containsKey: " + numReservas.get(pista));

                        if (numReservas.containsKey(pista)) {
                            for (int i = 0; i < numReservas.get(pista); i++) {
                                String string = Reserva.get(pista + "_" + i);
                                String[] parts = string.split("_");
                                Integer part1 = parseInt(parts[0]); // 123
                                Integer part2 = parseInt(parts[1]); // 654321
                                Log.d(TAG, "Reserva anterior: " + string);

                                Disponibilidad.put(part1, 0);
                                int x = 0;
                                while (part2 >= 1) {
                                    Disponibilidad.put(part1 + x, 0);
                                    part2--;
                                    x++;
                                }

                            }
                        }
                        String inicio = Reserva.get(pista + "_inicio");

                        for (int i = 1; i <= parseInt(Reserva.get(pista + "_slots")); i++) {
                            if (Disponibilidad.get(i) == 1) {
                                //try{
                                //Log.d(TAG,"DISPONIBLE : "  + i);
                                //SimpleDateFormat df = new SimpleDateFormat("HH:mm",Locale.ENGLISH);
                                //String target = Reserva.get(pista+"_inicio");
                                //Date inicio =  df.parse(target);
                                Date hora = new Date();//Hora de hoy
                                Date dia = new Date();//Fecha de hoy
                                Date comparar2  = new Date();//Hora reserva

                                DateFormat formatoDestino = new SimpleDateFormat("HH:mm");
                                String fechaFormato = formatoDestino.format(hora);

                                DateFormat formatoDia    = new SimpleDateFormat("yyyyMMdd");
                                String fechaDia = formatoDia.format(dia);

                                try {
                                    hora = formatoDestino.parse(fechaFormato);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    comparar2 = formatoDestino.parse(inicio);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String fin = SumarMinutos(inicio, Reserva.get(pista + "_duracion"));
                                CheckBox opcion = new CheckBox(getActivity());
                                opcion.setText(inicio + " - " + fin);
                                opcion.setLayoutParams(
                                        new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                //Log.d("FECHA",fechaDia+" "+fecha);
                                //Log.d("HORA",comparar2+" "+hora);

                                if (comparar2.before(hora) && fechaDia.equals(fecha+"")){
                                    opcion.setEnabled(false);
                                }
                                seccionIndustria.addView(opcion);
                                inicio = fin;
                        /*}catch (ParseException pe) {
                            pe.printStackTrace();
                        }*/
                            } else {
                                //Log.d(TAG,"NO DISPONIBLE : "  + i);
                                String fin = SumarMinutos(inicio, Reserva.get(pista + "_duracion"));
                                CheckBox opcion = new CheckBox(getActivity());
                                opcion.setText(inicio + " - " + fin);
                                opcion.setLayoutParams(
                                        new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                opcion.setEnabled(false);
                                seccionIndustria.addView(opcion);
                                inicio = fin;

                            }

                        }
                    }
                }
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
                int inicio = -1;
                int duracion = 0;
                Log.d(TAG, "total check: " + seccionIndustria.getChildCount());

                for(int i=0; i<seccionIndustria.getChildCount(); i++) {
                    View nextChild = seccionIndustria.getChildAt(i);

                    if(nextChild instanceof CheckBox){
                        CheckBox check = (CheckBox) nextChild;
                        if (check.isChecked()) {
                            AllCheckbox.add(check.getText().toString());
                            Log.d(TAG, "check: " + i);
                            if(inicio == -1){
                                inicio = i;
                                duracion++;
                            }else if((inicio+duracion) == i){
                                duracion++;
                            }else{
                                inicio = 0;
                                duracion = -1;
                            }

                        }
                    }

                }
                if(duracion == 0 ){
                    Toast.makeText(getContext(), "Debe seleccionar almenos 1 slots", Toast.LENGTH_LONG).show();
                }else if(duracion > 3){
                    Toast.makeText(getContext(), "Debe seleccionar como maximo 3 slots", Toast.LENGTH_LONG).show();
                }else if(duracion == -1) {
                    Toast.makeText(getContext(), "Los slots seleccionados deben ser consecutivos", Toast.LENGTH_LONG).show();
                }else{
                    inicio ++;
                    RealizarReserva(finalpista,inicio,duracion,fecha);
                }
            }
            /*String checks = AllCheckbox.toString();
            Iterator<String> it = AllCheckbox.iterator();
            while(itr.ha()){
                String check = itr.next().toString();
                Log.d(TAG, "check: " + check);
            }*/


        });
        return view;
    }

    public String SumarMinutos(String hora,String minutos){
        String[] parts = hora.split(":");
        Integer part1 = parseInt(parts[0]);
        Integer part2 = parseInt(parts[1]);
        Integer Nuevosminutos = part2 + parseInt(minutos);
        while(Nuevosminutos >= 60){
            Nuevosminutos =  Nuevosminutos -60;
            part1 = part1 + 1;
        }
        if (Nuevosminutos == 0) {
            return part1+":"+Nuevosminutos+"0";

        }else{
            return part1+":"+Nuevosminutos;

        }
    }

    public void RealizarReserva(String pista, Integer inicio, Integer duracion,Integer fecha){
        Log.d(TAG,"Reserva VALIDA");
        Reserva.put(pista+"_reserva",inicio+"_"+duracion);
        FinalizarReservaFragment fr = new FinalizarReservaFragment();
        Bundle b = new Bundle();
        b.putSerializable("hashmap",Reserva);
        b.putStringArrayList("pistas",aPistas);
        b.putInt("fecha",fecha);
        fr.setArguments(b);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.escenario,fr).addToBackStack(null);

        transaction.commit();
    }
}
