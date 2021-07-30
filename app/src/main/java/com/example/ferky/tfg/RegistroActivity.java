package com.example.ferky.tfg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class RegistroActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = RegistroActivity.class.getName();

    //difinicion objetos xml
    private TextView lblGotoLogin;
    private Button btnRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputName,inputNumber,inputApell;
    private ProgressDialog progressDialog;

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
        setContentView(R.layout.activity_registro);

        //inicializamos el objeto firebaseauth
        firebaseAuth = FirebaseAuth.getInstance();

        //Referenciamos los views
        inputEmail = (EditText) findViewById(R.id.txtEmail);
        inputPassword = (EditText) findViewById(R.id.txtPass);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        lblGotoLogin = (TextView) findViewById(R.id.link_to_login);
        inputName = (EditText) findViewById(R.id.nombrereg);
        inputApell = (EditText) findViewById(R.id.apellidoreg);
        inputNumber = (EditText) findViewById(R.id.telefonoreg);
        progressDialog = new ProgressDialog(this);

        btnRegister.setOnClickListener(this);
        lblGotoLogin.setOnClickListener(this);
    }
    @Override
    public void onClick (View view){
        switch (view.getId()){
            case R.id.link_to_login:
                updateUI();
                break;
            case R.id.btnRegister:
                registrarUsuario();
                break;
        }
    }

    private void registrarUsuario(){
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

        progressDialog.setMessage("Realizando registro en línea...");
        progressDialog.show();

        //Creación de un nuevo usuario
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        //control de que todo fue correcto
                        if(task.isSuccessful()){
                            Toast.makeText(RegistroActivity.this,"Se ha registrado el usuario correctamente",Toast.LENGTH_LONG).show();
                            RegistrarRestoUsuario();
                            updateUI();
                        }else{

                            if(task.getException() instanceof FirebaseAuthUserCollisionException){//Si el usuario ya existe
                                Toast.makeText(RegistroActivity.this,"El usuario ya existe",Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(RegistroActivity.this,"No se pudo registrar el usuario",Toast.LENGTH_LONG).show();

                        }
                        progressDialog.dismiss();
                    }
                });

    }
    public  void RegistrarRestoUsuario(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            String uid = user.getUid();
            ref = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(uid);
            Log.d(TAG,uid);
            Usuarios userObj = new Usuarios(uid,inputName.getText().toString(),inputApell.getText().toString(),inputNumber.getText().toString(),3);
            ref.setValue(userObj);

        }
    }
    public void updateUI(){
            //Creamos un nuevo intent con destino a la calse MAinActivity
            Intent itemintent = new Intent(getApplication(), LoginActivity.class);
            startActivity(itemintent);
            finish();
    }


}
