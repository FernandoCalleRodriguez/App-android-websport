package com.example.ferky.tfg;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {
    private static final String TAG = PerfilFragment.class.getName();


    public PerfilFragment() {
        // Required empty public constructor
    }
    private EditText nombre,apellido,telefono,email;
    private Button actualizar;
    private AppCompatDelegate mDelegate;

    //definición objeto Databasefirebase
    private DatabaseReference ref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_perfil, container, false);

        //((HideShowIconInterface) getActivity()).showBackIcon();

        ((MainActivity) getActivity()).hideFloatingActionButton();

        nombre = (EditText) view.findViewById(R.id.etNombre);
        apellido = (EditText) view.findViewById(R.id.etApellido);
        telefono = (EditText) view.findViewById(R.id.etTelefono);
        email = (EditText) view.findViewById(R.id.etEmail);
        actualizar = (Button) view.findViewById(R.id.btActualizar);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            String uid = user.getUid();
            ref = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(uid);
            Log.d(TAG,uid);


        }
        email.setText(user.getEmail());
        email.setEnabled(false);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nombreuser = dataSnapshot.child("nombre").getValue().toString();
                nombre.setText(nombreuser);
                String apellidouser = dataSnapshot.child("apellido").getValue().toString();
                apellido.setText(apellidouser);
                String telefonouser = dataSnapshot.child("telefono").getValue().toString();
                telefono.setText(telefonouser);
                Log.d(TAG,nombreuser + apellidouser + telefonouser);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String uid = user.getUid();

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(uid);
                Usuarios userObj = new Usuarios(uid,nombre.getText().toString(),apellido.getText().toString(),telefono.getText().toString(),3);
                ref.setValue(userObj);
                Toast.makeText(getContext(), "Contacto actualizado!!", Toast.LENGTH_LONG).show();

            }
        });
        return view;
    }

}

