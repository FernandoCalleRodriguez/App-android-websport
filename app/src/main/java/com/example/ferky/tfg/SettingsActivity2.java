package com.example.ferky.tfg;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.preference.Preference;
import android.preference.PreferenceActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity2 extends AppCompatPreferenceActivity {

    private static final String TAG = SettingsActivity2.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pref_general);
        setupActionBar();

        Preference pref = findPreference("sample_key");
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                //Generar y mostrar un AlertDialog
                AlertDialog.Builder mybuild = new AlertDialog.Builder(preference.getContext());
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
                                    Intent i = new Intent(SettingsActivity2.this, LoginActivity.class);
                                    startActivity(i);
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
                return true;
            }
        });

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setHomeButtonEnabled(true);

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }



}
