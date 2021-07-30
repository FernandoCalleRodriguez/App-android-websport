package com.example.ferky.tfg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity {

    //Creación de la variable de preferencias compartidas
    SharedPreferences prefs;
    //definición objeto firebase
    private FirebaseAuth firebaseAuth;

    //definición objeto firebase

    private ProgressDialog progressDialog;

    private int SPLASH_TIME = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Cambiar idioma
        SharedPreferences prefsIdi = PreferenceManager.getDefaultSharedPreferences(this);
        String resPrefIdi = prefsIdi.getString("example_list", "");
        if(resPrefIdi.isEmpty()){
            resPrefIdi = "es";
        }
        Locale locale = new Locale(resPrefIdi);
        Locale.setDefault(locale);
        Configuration configu = new Configuration();
        configu.locale = locale;
        getBaseContext().getResources().updateConfiguration(configu,getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_splash_screen);

        //inicializamos el objeto preference
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //inicializamos el objeto firebaseauth
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        //Agregamos un mensaje en el ProgressDialog
        progressDialog.setMessage("Iniciando..");
        //Mostramos el ProgressDialog
        progressDialog.show();



        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                //Recuperar preferencias compartidas
                String resultadoEmail = prefs.getString("email", "");
                String resultadoPass = prefs.getString("pass", "");
                String resultadoTipo = prefs.getString("tipo", "");
                Log.d("Preferencias",resultadoEmail+" "+resultadoPass+" "+resultadoTipo);

                //Comprobamos las preferencias compartidas
                if (TextUtils.isEmpty(resultadoEmail) || TextUtils.isEmpty(resultadoPass) ||TextUtils.isEmpty(resultadoTipo )) {
                    //finaliza el progressDialog
                    progressDialog.dismiss();
                    //Accedemos al activity Login
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    //finaliza el progressDialog
                    //Accedemos al activity MainActivity
                    loguear(resultadoEmail,resultadoPass,resultadoTipo);


                    /*Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);*/

                }
                //finish();

            }
        }, 3000);
    }

    public void updateUI(FirebaseUser currentUser, String resultadoTipo ){
        progressDialog.dismiss();

        if(currentUser != null){
            //Creamos un nuevo intent con destino a la calse MAinActivity
            if (resultadoTipo.equals("3")) {
                Intent itemintent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(itemintent);
                finish();
            }else{
                Intent itemintent = new Intent(SplashScreenActivity.this, GestorActivity.class);
                startActivity(itemintent);
                finish();
            }
        }else{
            Intent itemintent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(itemintent);
            finish();
        }
    }

    public void loguear(String email, String password,final String resultadoTipo){
        //Creación de un nuevo usuario
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        //control de que todo fue correcto
                        if(task.isSuccessful()){
                            Toast.makeText(getApplication(),"Bienvenido",Toast.LENGTH_LONG).show();
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            updateUI(currentUser,resultadoTipo);

                        }else{
                            Toast.makeText(getApplication(),"Email o contraseña no correcta",Toast.LENGTH_LONG).show();
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.remove("email");
                            editor.remove("pass");
                            editor.remove("tipo");
                            editor.apply();

                            updateUI(null,null);

                        }
                    }
                });
    }
}
