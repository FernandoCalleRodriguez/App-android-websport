package com.example.ferky.tfg;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Integer.parseInt;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetallesReservaFragment extends Fragment {
    private static final String TAG = DetallesReservaFragment.class.getName();

    //definición objeto Databasefirebase
    private DatabaseReference ref;
    int fecha ;
    private String Spista,Sfecha,Sduracion,Sprecio,Shora,$horainicio;

    //Widgets
    private TextView TvPista,día,hora,duracion, precio, usuario1, usuario2;
    private Button btnborrar;

    //Método para crear una instancia del fragment pasandole obligatoriamente una serie de argumentos
    public static DetallesReservaFragment newInstance(Bundle arguments) {
        DetallesReservaFragment fragment = new DetallesReservaFragment();
        if (arguments != null) {
            fragment.setArguments(arguments);
        }
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detalles_reserva, container, false);

        ((MainActivity) getActivity()).hideFloatingActionButton();

        //Recupera los argumentos recibidos en un objeto Bundle
        final Bundle bundle = getArguments();
        final String key = bundle.getString("id");
        final String tipo = bundle.getString("tipo");

        //Inicializar y localizar los componentes del xml
        TvPista = (TextView) view.findViewById(R.id.tvPista);
        día = (TextView) view.findViewById(R.id.tvNombre);
        hora = (TextView) view.findViewById(R.id.tvHora);
        duracion = (TextView) view.findViewById(R.id.tvDuracion);
        precio = (TextView) view.findViewById(R.id.tvPrecio);
        usuario2 = (TextView) view.findViewById(R.id.tvUsuario2);
        usuario1 = (TextView) view.findViewById(R.id.tvUsuario1);
        btnborrar = (Button) view.findViewById(R.id.btn_finalizar);
        usuario2.setText(key);

        if(tipo.equals("gestor")){
            btnborrar.setVisibility(View.GONE);

        }else{
            usuario1.setVisibility(View.GONE);
            usuario2.setVisibility(View.GONE);
        }
        ref = FirebaseDatabase.getInstance().getReference().child("Reservas");
        ref.orderByKey().equalTo(bundle.getString("id")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    ObtenerInfoPista(postSnapshot.child("pista").getValue().toString(),Integer.parseInt(postSnapshot.child("duracion").getValue().toString()));
                    String id = postSnapshot.getKey();
                    Log.d(TAG,"ID: "  +id);

                    día.setText(postSnapshot.child("fecha").getValue().toString().substring(0,4)+"/"+postSnapshot.child("fecha").getValue().toString().substring(4,6)+"/"+postSnapshot.child("fecha").getValue().toString().substring(6,8));
                    hora.setText(postSnapshot.child("horainicio").getValue().toString());
                    precio.setText(postSnapshot.child("precio").getValue().toString()+ " €");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Se incorpora un escuchador para el boton
        btnborrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date fechaInicial= new Date();
                Date fechaFinal = new Date();
                String SfechaFinal = dateFormat.format(fechaFinal);
                try {
                    fechaInicial = dateFormat.parse(día.getText().toString()+" "+ hora.getText().toString()+":00");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    fechaFinal = dateFormat.parse(SfechaFinal);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.d("FECHAS INICIO",día.getText().toString()+" "+ hora.getText().toString());
                Log.d("FECHAS",fechaInicial+" "+fechaFinal);
                long Finicio = fechaInicial.getTime();
                long Ffinal = fechaFinal.getTime();

                long horas= Finicio - Ffinal;
                Log.d("Diferencia", ""+horas);

                if(horas < -1565576296)
                    horas = -1;
                int horas2 = (int)(horas);
                Log.d("Diferencia", ""+horas2);
                horas2= ((horas2/1000)/3600);
                Log.d("Diferencia", ""+horas2);
                if(horas2 >= 24) {
                    //Generar y mostrar un AlertDialog
                    AlertDialog.Builder mybuild = new AlertDialog.Builder(getContext());
                    mybuild.setMessage("¿De verdad quiere cancelar la reserva?");
                    mybuild.setTitle("Cancelación de reserva");
                    //Funcionalidad si se presiona "SI"
                    mybuild.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseDatabase.getInstance().getReference().child("Reservas").child(key).setValue(null);
                            //Se reemplaza el fragmento por el que se genera y se notifica por pantalla
                            Fragment f = new ListadoReservasFragment();
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.escenario, f);
                            transaction.commit();
                            Toast.makeText(getContext(), "Reserva cancelada", Toast.LENGTH_LONG).show();
                            sendEmail();

                        }

                    });
                    //Funcionalidad si se presiona "SI"
                    mybuild.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    mybuild.create().show();
                }else if(horas2 <= 0) {
                    Toast.makeText(getContext(), "Reserva ya finalizada", Toast.LENGTH_LONG).show();

                }else{
                        Toast.makeText(getContext(), "Esta reserva no se puede cancelar por proximidad menor a 24h", Toast.LENGTH_LONG).show();


                }

            }
        });
        return view;
    }


    protected void sendEmail() {
        String[] TO = {"ferky_27@hotmail.com"}; //aquí pon tu correo
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
// Esto podrás modificarlo si quieres, el asunto y el cuerpo del mensaje
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Asunto");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Escribe aquí tu mensaje");

        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar email..."));
            /*finish();*/
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(),
                    "No tienes clientes de email instalados.", Toast.LENGTH_SHORT).show();
        }
    }
    public void ObtenerInfoPista(String pista,final int duracionreserva){

        ref = FirebaseDatabase.getInstance().getReference().child("Pistas");
        ref.orderByKey().equalTo(pista).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    TvPista.setText(postSnapshot.child("nombre").getValue().toString());
                    duracion.setText((Integer.parseInt(postSnapshot.child("duracion").getValue().toString())*duracionreserva)+" min");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
