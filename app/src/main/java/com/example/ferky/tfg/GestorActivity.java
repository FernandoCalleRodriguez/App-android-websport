package com.example.ferky.tfg;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class GestorActivity extends AppCompatActivity implements GestorFragment.OnFragmentInteractionListener {

    private static final String TAG = GestorActivity.class.getName();

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestor);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (savedInstanceState == null) {
            // Crear un fragment y añadirlo al contenido
            Fragment fragment = new GestorFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.content_gestor, fragment, fragment.getClass().getSimpleName()).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_slide, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            //Generar y mostrar un AlertDialog
            AlertDialog.Builder mybuild = new AlertDialog.Builder(this);
            mybuild.setMessage("Cerrar sesión");
            mybuild.setTitle("Quieres cerrar sesión?");
            //Funcionalidad si se presiona "SI"
            mybuild.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int in) {
                    finish();
                    finish();
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove("email");
                    editor.remove("pass");
                    editor.remove("tipo");
                    editor.commit();
                    FirebaseAuth.getInstance().signOut();
                    Log.d(TAG,"Sesión cerrada correctamente");
                    Intent i = new Intent(GestorActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            });
            //Funcionalidad si se presiona "NO"
            mybuild.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            mybuild.create().show();
            return true;
        }else if(id == R.id.action_delete) {

            AlertDialog.Builder mybuild = new AlertDialog.Builder((this));
            mybuild.setMessage("Borrar usuario");
            mybuild.setTitle("Quieres eliminar tu usuario?");
            //Funcionalidad si se presiona "SI"
            mybuild.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int in) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseDatabase.getInstance().getReference().child("Usuarios").child(user.getUid()).setValue(null);
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG,"Usuario borrado correctamente");
                                Intent i = new Intent(GestorActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    });

                }
            });
            //Funcionalidad si se presiona "NO"
            mybuild.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            mybuild.create().show();

        }else{
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Bundle bundle, String data) {
        if(data == "Reserva"){
            //Generar un DetallesCocheFragment al que se le pasan los argumentos recibidos en Bundle
            DetallesReservaFragment f = DetallesReservaFragment.newInstance(bundle);
            //Realizar el reemplazo de fragments
            getSupportFragmentManager().beginTransaction().replace(R.id.content_gestor, f).addToBackStack(null).commit();

        }
    }
}
