package com.example.ferky.tfg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener  {

    private static final String TAG = LoginActivity.class.getName();

    //Creación de los elementos del xml
        private TextView lblGotoRegister;
        private Button btnLogin;
        private EditText inputEmail;
        private EditText inputPassword;
        private ProgressDialog progressDialog;

        //Creación de la variable de preferencias compartidas
        SharedPreferences prefs;


        //definición objeto firebase
        private FirebaseAuth firebaseAuth;
    //definición objeto Databasefirebase
    private DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Cambiar idioma
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String resPrefIdi = prefs.getString("example_list", "");
        if(resPrefIdi.isEmpty()){
            resPrefIdi = "es";
        }
        Locale locale = new Locale(resPrefIdi);
        Locale.setDefault(locale);
        Configuration configu = new Configuration();
        configu.locale = locale;
        getBaseContext().getResources().updateConfiguration(configu,getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_login);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        //inicializamos el objeto firebaseauth
        firebaseAuth = FirebaseAuth.getInstance();

        //Referenciamos los views
        inputEmail = (EditText) findViewById(R.id.email2);
        inputPassword = (EditText) findViewById(R.id.contrasena);
        btnLogin = (Button) findViewById(R.id.inicio);
        lblGotoRegister = (TextView) findViewById(R.id.link_to_registro);
        progressDialog = new ProgressDialog(this);

        //Recuperar preferencias compartidas
        String resultadoEmail = prefs.getString("email", "");
        String resultadoPass = prefs.getString("pass", "");

        //Comprobamos las preferencias compartidas
        if (TextUtils.isEmpty(resultadoEmail) || TextUtils.isEmpty(resultadoPass)) {
            //finaliza el progressDialog
            progressDialog.dismiss();

        } else {/*
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            //Agregamos un mensaje en el ProgressDialog
            progressDialog.setMessage("iniciando sesión");
            //Mostramos el ProgressDialog
            progressDialog.show();
            //finaliza el progressDialog
            progressDialog.dismiss();
            //Accedemos al activity MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);*/

        }


        btnLogin.setOnClickListener(this);
        lblGotoRegister.setOnClickListener(this);
    }

    @Override
    public void onClick (View view){

        //Log.d(TAG,"onClicked");// Logs para monitorización
        switch (view.getId()){
            case R.id.link_to_registro:
                Intent itemintent = new Intent(getApplication(), RegistroActivity.class);
                startActivity(itemintent);
                finish();
                break;
            case R.id.inicio:
                loguearUsuario();
                break;
        }

    }

    private void loguearUsuario(){
        //Inicalizo las preferencias compartidas
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //obtenemos el email y la pass de las cajas de texto
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        //Comprobación de cajas de texto
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Se debe ingresar un email", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Se debe ingresar una password", Toast.LENGTH_LONG).show();
            return;
        }

        //Guarda las preferencias introducidas en el login
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email", email);
        editor.putString("pass", password);
        editor.apply();



        progressDialog.setMessage("Realizando atutenticación en línea...");
        progressDialog.show();

        //Creación de un nuevo usuario
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        //control de que todo fue correcto
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,"Bienvenido",Toast.LENGTH_LONG).show();
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            updateUI(currentUser);

                        }else{
                            Toast.makeText(LoginActivity.this,"Email o contraseña no correcta",Toast.LENGTH_LONG).show();
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.remove("email");
                            editor.remove("pass");
                            editor.apply();
                            updateUI(null);

                        }
                        progressDialog.dismiss();
                    }
                });

    }
    public void updateUI(FirebaseUser currentUser){

        if(currentUser!=null) {
            String uid = currentUser.getUid();
            ref = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(uid);
            Log.d("id","fevdc "+uid);

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("tipo", dataSnapshot.child("tipo").getValue().toString());
                editor.apply();
                if(dataSnapshot.child("tipo").getValue().toString().equals("3")){
                    //Creamos un nuevo intent con destino a la calse MAinActivity
                    Intent itemintent = new Intent(getApplication(), MainActivity.class);
                    startActivity(itemintent);
                    finish();
                }else{
                    Intent itemintent = new Intent(getApplication(), GestorActivity.class);
                    startActivity(itemintent);
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        }
    }


}

