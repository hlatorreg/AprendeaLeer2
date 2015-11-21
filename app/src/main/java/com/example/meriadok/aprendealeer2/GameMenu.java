package com.example.meriadok.aprendealeer2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class GameMenu extends AppCompatActivity {

    private Button botonPadres;
    private Button botonCuerpo;
    private Button botonCosas;
    private Button botonAcciones;
    private Button botonLibro;
    private SesionManager sesionManager;
    private DataBase miDB;
    private static final String TAG = GameMenu.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);
        Log.d(TAG, "onCreate()");
        prepararPantalla();
    }

    void prepararPantalla() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("logros", Context.MODE_PRIVATE); //sp para guardar y ver logros del usuario.
        miDB = new DataBase(this);
        SesionManager sesionManager = new SesionManager(this, miDB);
        //Creamos un alumno, lo llenamos con los datos de la tabla_alumnos en la BD y con esos datos establecemos las sharedpreferences para los logros (funcion asignarSharedPref)
        Alumno alumno = new Alumno();
        alumno.setRut(sesionManager.getRut());
        Alumno al = miDB.getDatosAlumno(alumno);
        Log.d(TAG, al.getNombre() + " " + al.getRut() + " " + al.getD_cuerpo() + " " + al.getD_cosas() + " " + al.getD_acciones());
        sesionManager.asignarAsharedPref(al);

        boolean descloqueoCuerpo = sharedPreferences.getBoolean("desbloquearCuerpo", false);
        boolean desbloqueoCosas = sharedPreferences.getBoolean("desbloquearCosas", false);
        boolean desbloqueoAcciones = sharedPreferences.getBoolean("desbloquearAcciones", false);


        botonPadres = (Button) findViewById(R.id.botonPadres);
        botonCuerpo = (Button) findViewById(R.id.botonCuerpo);
        if (!descloqueoCuerpo) {
            botonCuerpo.setEnabled(false);
            botonCuerpo.setTextColor(Color.DKGRAY);
        }
        botonCosas = (Button) findViewById(R.id.botonCosas);
        if (!desbloqueoCosas) {
            botonCosas.setEnabled(false);
            botonCosas.setTextColor(Color.DKGRAY);
        }
        botonAcciones = (Button) findViewById(R.id.botonAcciones);
        if (!desbloqueoAcciones) {
            botonAcciones.setEnabled(false);
            botonAcciones.setTextColor(Color.DKGRAY);
        }
        botonLibro = (Button) findViewById(R.id.botonLibro);
        botonLibro.setEnabled(false);
        botonLibro.setTextColor(Color.TRANSPARENT);
        botonPadres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameMenu.this, ModuloPadres.class));
            }
        });

        botonCuerpo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameMenu.this, ModuloCuerpo.class));
            }
        });

        botonCosas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameMenu.this, ModuloCosas.class));
            }
        });

        botonAcciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameMenu.this, ModuloAcciones.class));
            }
        });

        botonLibro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameMenu.this, ModuloLibro.class));
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        prepararPantalla();
        sesionManager = new SesionManager(this, miDB);
        if (sesionManager.completaSesiones()) {
            Log.d(TAG, "Sesiones completadas, finish()");
            this.finish();
        }
    }

    @Override
    protected void onRestart() {
        setContentView(R.layout.activity_game_menu);
        prepararPantalla();
        super.onRestart();

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "Restoreinstancestate");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
