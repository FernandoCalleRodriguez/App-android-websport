package com.example.ferky.tfg;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalService;

import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ListadoReservasFragment.OnFragmentInteractionListener , InicioFragment.OnFragmentInteractionListener,  HideShowIconInterface {

    private static final String TAG = MainActivity.class.getName();
    private static PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId(Config.PAYPAL_CLIENT_ID);

    SharedPreferences prefs;
    TextView unHeader;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    //definición objeto firebase
    private FirebaseAuth firebaseAuth;

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

        setContentView(R.layout.activity_main);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //inicializamos el objeto firebaseauth
        firebaseAuth = FirebaseAuth.getInstance();

        //Iniciar los servicios de PayPal
        Intent intent = new Intent(this,PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        //Inicializamos el toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.escenario,new ReservaFragment()).addToBackStack(null).commit();

            }
        });

        //Inicializamos y sincronizamos el toolbar con el Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //Inicializamos la vista del Navigation Drawer
         navigationView = (NavigationView) findViewById(R.id.nav_view);
        //Se incorpora un escuchador para ver el item del Navigation Drawer seleccionado
        navigationView.setNavigationItemSelectedListener(this);

        //Modifica el textView del header
        View headerView = navigationView.getHeaderView(0);
        unHeader = (TextView) headerView.findViewById(R.id.username_header);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String resPref = prefs.getString("email", "");
        unHeader.setText(resPref);

        //Se incorpora un escuchador para ver el item del Navigation Drawer seleccionado
        navigationView.setNavigationItemSelectedListener(this);
        // Comprobad si la actividad ya ha sido creada con anterioridad
        if (savedInstanceState == null) {
            // Crear un fragment y añadirlo al contenido
            Fragment fragment = new InicioFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.escenario, fragment, fragment.getClass().getSimpleName()).commit();
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_slide, menu);
        return true;
    }*/

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
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
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
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

            AlertDialog.Builder mybuild = new AlertDialog.Builder(getApplicationContext());
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
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //Manejador de eventos del Navigation Drawer
        //Recoge el id del item escogido y realiza la funcionalidad asociada al item.
        int id = item.getItemId();

        FragmentManager fm = getSupportFragmentManager();

        if (id == R.id.nav_inicio) {
            // Handle the camera action
            fm.beginTransaction().replace(R.id.escenario,new InicioFragment()).addToBackStack(null).commit();


        } else if (id == R.id.nav_perfil) {
            fm.beginTransaction().replace(R.id.escenario,new PerfilFragment()).addToBackStack(null).commit();

        } else if (id == R.id.nav_settings) {
            //fm.beginTransaction().replace(R.id.escenario,new OpcionesFragment()).commit();
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            /*getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new OpcionesFragment())
                    .commit();*/

        } else if (id == R.id.cerrar_sesion) {
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
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
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
            //return true;

        }else if(id == R.id.nav_reserva){
            fm.beginTransaction().replace(R.id.escenario,new ReservaFragment()).addToBackStack(null).commit();

        }else if(id == R.id.nav_reservas){
            fm.beginTransaction().replace(R.id.escenario,new ListadoReservasFragment()).addToBackStack(null).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Método de la interfaz OnFragmentInteractionListener de los fragmentos para posibilitar el intercambio de
    //datos entre el fragment y el activity
    public void onFragmentInteraction(Uri uri) {
    }

    //Método de la interfaz OnFragmentInteractionListener de los fragmentos para posibilitar el intercambio de
    //datos entre ListaCochesFragment-MainActivity-DetallesCocheFragment
    @Override
    public void onFragmentInteraction(Bundle bundle,String data) {
        //Realizar el reemplazo de fragments
        if (data == "Pista") {
            DetallesPistaFragment f = DetallesPistaFragment.newInstance(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.escenario, f).addToBackStack(null).commit();

        }else if(data == "Reserva"){
            //Generar un DetallesCocheFragment al que se le pasan los argumentos recibidos en Bundle
            DetallesReservaFragment f = DetallesReservaFragment.newInstance(bundle);
            //Realizar el reemplazo de fragments
            getSupportFragmentManager().beginTransaction().replace(R.id.escenario, f).addToBackStack(null).commit();

        }
    }

    @Override
    public void showHamburgerIcon() {
        toggle.setDrawerIndicatorEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(false);
            //actionBar.setHomeButtonEnabled(true);

        }
    }
    @Override
    public void showBackIcon() {
        toggle.setDrawerIndicatorEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        /*if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

        }*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void hideFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
    };
}

 interface HideShowIconInterface{
    void showHamburgerIcon();
    void showBackIcon();
    void hideFloatingActionButton();
}