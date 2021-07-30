package com.example.ferky.tfg;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetallesPagoFragment extends Fragment {


    public DetallesPagoFragment() {
        // Required empty public constructor
    }

    TextView txtId,txtEstatus,txtMonto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_detalles_pago, container, false);

        ((MainActivity) getActivity()).hideFloatingActionButton();

        txtId = (TextView) view.findViewById(R.id.txtId);
        txtEstatus = (TextView) view.findViewById(R.id.txtEstatus);
        txtMonto = (TextView) view.findViewById(R.id.txtMonto);

        Bundle b = this.getArguments();


        try {
            JSONObject jsonObject = new JSONObject((b.getString("PaymentDetails")));
            verDetalles(jsonObject.getJSONObject("response"),b.getString("PaymentAmount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void verDetalles(JSONObject response, String paymentAmount){
        //Seteo los valores de los textview
        try{
            txtId.setText(response.getString("id"));
            txtEstatus.setText(response.getString("state"));
            txtMonto.setText(paymentAmount);
        }catch (JSONException e){

        }
    }
}