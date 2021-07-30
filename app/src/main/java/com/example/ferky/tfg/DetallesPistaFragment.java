package com.example.ferky.tfg;


import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static java.lang.Integer.parseInt;


public class DetallesPistaFragment extends Fragment {


    MapView mMapView;
    private GoogleMap googleMap;
    private TextView nombre, precio,horarioLV, horarioSD;
    final StyleSpan bss = new StyleSpan(Typeface.BOLD);

    //definición objeto Databasefirebase
    private DatabaseReference ref;

    //Método para crear una instancia del fragment pasandole obligatoriamente una serie de argumentos
    public static DetallesPistaFragment newInstance(Bundle arguments) {
        DetallesPistaFragment fragment = new DetallesPistaFragment();
        if (arguments != null) {
            fragment.setArguments(arguments);
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //infla la vista
        View view = inflater.inflate(R.layout.fragment_detalles_pista, container, false);

        //Recupera los argumentos recibidos en un objeto Bundle
        final Bundle bundle = getArguments();
        final String key = bundle.getString("id");

        mMapView = (MapView) view.findViewById(R.id.mapView);
        nombre = (TextView) view.findViewById(R.id.tvNombrePista);
        precio = (TextView) view.findViewById(R.id.tvPrecioSlot);
        horarioLV = (TextView) view.findViewById(R.id.tvHorarioLV);
        horarioSD = (TextView) view.findViewById(R.id.tvHorarioSD);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ref = FirebaseDatabase.getInstance().getReference().child("Pistas").child(key);

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

                googleMap = mMap;

                // For showing a move to my location button
                //googleMap.setMyLocationEnabled(true);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String nombreuser = dataSnapshot.child("localizacion").getValue().toString();
                        String string = "123-654321";
                        String[] parts = nombreuser.split(",");
                        Double part1 = Double.parseDouble(parts[0]);
                        Double part2 = Double.parseDouble(parts[1]);
                        LatLng segovia = new LatLng(part1, part2);
                        // For dropping a marker at a point on the Map
                        googleMap.getUiSettings().setMapToolbarEnabled(true);
                        googleMap.addMarker(new MarkerOptions().position(segovia).title(dataSnapshot.child("nombre").getValue().toString()));
                        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            public boolean onMarkerClick(Marker marker) {
                                return false;
                            }
                        });
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        googleMap.getUiSettings().setAllGesturesEnabled(false);
                        //Poner negrita
                        String nombrepista = "Nombre de la pista: "+dataSnapshot.child("nombre").getValue().toString();
                        final SpannableStringBuilder sb = new SpannableStringBuilder(nombrepista);
                        sb.setSpan(bss, 0, 20, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // make first 4 characters Bold
                        nombre.setText(sb);

                        final SpannableStringBuilder sp = new SpannableStringBuilder("Precio de la pista: " +dataSnapshot.child("precio").getValue().toString()+"€ por cada "+dataSnapshot.child("duracion").getValue().toString()+" min");
                        sp.setSpan(bss, 0, 20, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // make first 4 characters Bold

                        precio.setText(sp);
                        SacarHorarios(Integer.parseInt(dataSnapshot.child("duracion").getValue().toString()),key);
                        // For zooming automatically to the location of the marker
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(segovia).zoom(16).build();
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
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
    public void SacarHorarios(final Integer duracionslot, String key){
        ref =  FirebaseDatabase.getInstance().getReference().child("Horarios");

        ref.orderByChild("pista").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    if(Integer.parseInt(postSnapshot.child("findesemana").getValue().toString()) == 1 && Integer.parseInt(postSnapshot.child("especial").getValue().toString()) == 0){
                        String inicio = postSnapshot.child("horainicio").getValue().toString();
                        String fin= "";
                        for (int i = 1; i <= Integer.parseInt(postSnapshot.child("slots").getValue().toString()); i++) {
                            fin = SumarMinutos(inicio, duracionslot+"");
                            inicio = fin;
                        }
                        final SpannableStringBuilder sh1 = new SpannableStringBuilder("Horario de Sabados y Domingos: " + postSnapshot.child("horainicio").getValue().toString() +" - "+fin);
                        sh1.setSpan(bss, 0, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // make first 4 characters Bold
                        horarioSD.setText(sh1);
                    }else  if(Integer.parseInt(postSnapshot.child("findesemana").getValue().toString()) == 0 && Integer.parseInt(postSnapshot.child("especial").getValue().toString()) == 0){
                        String inicio = postSnapshot.child("horainicio").getValue().toString();
                        String fin= "";
                        for (int i = 1; i <= Integer.parseInt(postSnapshot.child("slots").getValue().toString()); i++) {
                            fin = SumarMinutos(inicio, duracionslot+"");
                            inicio = fin;
                        }
                        final SpannableStringBuilder sh2 = new SpannableStringBuilder("Horarios de lunes a viernes: "+postSnapshot.child("horainicio").getValue().toString() +" - "+fin);
                        sh2.setSpan(bss, 0, 28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // make first 4 characters Bold
                        horarioLV.setText(sh2);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }

        });
    }
}
