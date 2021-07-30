package com.example.ferky.tfg;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static java.lang.Integer.parseInt;


/**
 * A simple {@link Fragment} subclass.
 */
public class FinalizarReservaFragment extends Fragment {

    private static final String TAG = FinalizarReservaFragment.class.getName();
    //definición objeto Databasefirebase
    private DatabaseReference ref;

    HashMap<String, String> Reserva = new HashMap<String, String>();
    ArrayList<String> aPistas = new ArrayList<String>();

    int fecha ;

    private String Spista,Sfecha,Sduracion,Sprecio,Shora,$horainicio;

    private static final int PAYPAL_REQUEST_CODE = 7171;
    //Esto es para utilizar cuenta sandbox para test
    private static PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId(Config.PAYPAL_CLIENT_ID);


    //Widgets
    private TextView TvPista,día,hora,duracion, precio;
    private Button btnfinalizar;
    public FinalizarReservaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_finalizar_reserva, container, false);
        // EditText donde se mostrara la fecha obtenida

        ((MainActivity) getActivity()).hideFloatingActionButton();

        TvPista = (TextView) view.findViewById(R.id.tvPista);
        día = (TextView) view.findViewById(R.id.tvNombre);
        hora = (TextView) view.findViewById(R.id.tvHora);
        duracion = (TextView) view.findViewById(R.id.tvDuracion);
        precio = (TextView) view.findViewById(R.id.tvPrecio);
        btnfinalizar = (Button) view.findViewById(R.id.btn_finalizar);



        Bundle b = this.getArguments();
        if(b.getSerializable("hashmap") != null){
            Reserva = (HashMap<String, String>)b.getSerializable("hashmap");
        }
        if(b.getStringArrayList("pistas") != null){
            aPistas = (ArrayList<String>)b.getStringArrayList("pistas");
        }
        fecha = b.getInt("fecha") ;

        String Pista = aPistas.toString();
        Iterator<String> it = aPistas.iterator();
        while(it.hasNext()) {
            String pista =it.next().toString();

            String reserva = Reserva.get(pista+"_reserva");
            if(reserva != null){
                Log.d(TAG,"RESERVA REALIZADA");
                //Precio
                String[] parts = Reserva.get(pista+"_reserva").split("_");
                Integer part1 = parseInt(parts[0]);
                Integer part2 = parseInt(parts[1]);
                Float iprecio = Float.parseFloat(Reserva.get(pista+"_precio"))*part2;
                Sprecio =iprecio+"";
                precio.setText(iprecio+ " €");
                //Duración
                Integer iduracion = part2 * Integer.parseInt(Reserva.get(pista+"_duracion"));
                Log.d(TAG,"DURACION: "+iduracion);
                Sduracion =part2+"";
                duracion.setText(iduracion +" min");
                //Pista
                Spista = pista;
                TvPista.setText(Reserva.get(pista));
                //Fecha
                Sfecha = fecha+"";
                día.setText(Sfecha.substring(0,4)+"/"+Sfecha.substring(4,6)+"/"+Sfecha.substring(6,8));
                //Hora de inicio
                Shora = part1+"";
                String inicio =Reserva.get(pista+"_inicio");
                while(part1 > 1) {
                    inicio = SumarMinutos(inicio, Reserva.get(pista + "_duracion"));
                    part1--;
                }
                hora.setText(inicio);
                $horainicio = inicio;

            }
        }

        btnfinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref = FirebaseDatabase.getInstance().getReference().child("Reservas").push();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid ="";
                if(user!=null){
                    uid = user.getUid();
                }
                ProcesarPago(uid);


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

    private void ProcesarPago(String uid){
        PayPalPayment PayPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(Sprecio)),"MXN","Pagado por "+uid, com.paypal.android.sdk.payments.PayPalPayment.PAYMENT_INTENT_SALE);


        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,PayPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PAYPAL_REQUEST_CODE){
            if(resultCode == RESULT_OK){

                PaymentConfirmation confirmation = data.getParcelableExtra((PaymentActivity.EXTRA_RESULT_CONFIRMATION));

                if(confirmation != null){

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String uid ="";
                        if(user!=null){
                            uid = user.getUid();
                        }
                        Reserva userObj = new Reserva(Sduracion,Sfecha,Sfecha+" "+Spista,Spista,Shora,uid,Sprecio,$horainicio);
                        ref.setValue(userObj);
                        InicioFragment fr = new InicioFragment();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.escenario,fr).addToBackStack(null);
                        transaction.commit();
                        Toast.makeText(getActivity(),"Reserva realizada con exito",Toast.LENGTH_LONG).show();
                        /*String paymentDetails = confirmation.toJSONObject().toString();
                        DetallesPagoFragment fr = new DetallesPagoFragment();
                        Bundle b = new Bundle();
                        b.putString("PaymentAmount",Sprecio);
                        b.putString("PaymentDetails",paymentDetails);
                        fr.setArguments(b);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.escenario,fr).addToBackStack(null);
                        transaction.commit();*/

                }
            }else if( resultCode == RESULT_CANCELED) {

                Toast.makeText(getActivity(),"Invalida",Toast.LENGTH_SHORT).show();
            }

        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
