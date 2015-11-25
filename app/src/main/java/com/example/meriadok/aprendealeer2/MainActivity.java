package com.example.meriadok.aprendealeer2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


public class MainActivity extends AppCompatActivity {

    DataBase miDB;
    private ImageView playButton;
    private ImageView configButton;
    private EditText editMail;
    private TextView consejo;
    private Button aceptarMail;
    private TextView sesionAlumno;
    //Atributos pantalla agregar alumno
    private ViewFlipper flipper;
    private EditText rutAlumno;
    private EditText nombreAlumno;
    //private Button agregarAlumnoBoton;
    private static final String TAG = MainActivity.class.getSimpleName();
    private Context context;
    boolean esPrimera; //valor booleano donde guardamos la sharedpref

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        //setupPantalla();
    }

    public void setupPantalla() {
        SharedPreferences sp = this.getSharedPreferences("esPrimera", Context.MODE_PRIVATE); //sharedpref para asegurar que es la primera vez que se corre el juego
        esPrimera = sp.getBoolean("estado", true);

        //boolean estaBloqueado = sp.getBoolean("bloqueado", false);

        //Reseteamos el valor de la sp para la cantidad de sesiones con el fin de facilitar debug
        SesionManager sesionManager = new SesionManager(this, miDB);
        //TODO Cambiar valor a 0 para presentacion
        //sesionManager.setValorSesiones(4);


        if (esPrimera) {  //si es la primera vez se entra a la vista alternativa de la actividad inicial
            Log.d(TAG, "********** Primera vez que se abre la aplicacion ****************");
            setContentView(R.layout.activity_main_alt); //seteamos el contenido a la vista alternativa
            pantallaPrimeraVez();
        } else {  //en caso de que el juego ya haya sido ejecutado 1 vez
            Log.d(TAG, "********** No es la primera vez que se abre la aplicacion ****************");
            setContentView(R.layout.activity_main);
            pantallaRegular();
        }
    }

    /**
     * Metodo para establecer los elementos de la pantalla
     * que se utiliza la primera vez que se abre la aplicacion
     */

    public void pantallaPrimeraVez() {
        miDB = new DataBase(this); //creamos la base de datos que usaremos para guardar alumnos y tutores
        SharedPreferences sp = this.getSharedPreferences("esPrimera", Context.MODE_PRIVATE); //sharedpref para asegurar que es la primera vez que se corre el juego
        esPrimera = sp.getBoolean("estado", true);
        final SharedPreferences.Editor editor = sp.edit();

        Log.d(TAG, "********** BASE DE DATOS CREADA ************");
        editMail = (EditText) findViewById(R.id.editMail);
        aceptarMail = (Button) findViewById(R.id.aceptarMail);
        flipper = (ViewFlipper) findViewById(R.id.flippOpciones);
        aceptarMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editMail.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "No a ingresado nada", Toast.LENGTH_LONG).show();
                } else {
                    if (miDB.insertMail(editMail.getText().toString())) {
                        Log.d(TAG, "********* MAIL INGRESADO *********");
                        editor.putBoolean("estado", false); //cambiamos el valor de la sp a falso ya que corrimos el juego 1 vez
                        editor.apply();
                        Toast.makeText(MainActivity.this, "Mail Ingresado", Toast.LENGTH_LONG).show();
                        flipper.setDisplayedChild(1);
                        agregarAlumno();
                    } else {
                        Log.d(TAG, "********* FALLO AL INGRESAR MAIL *********");
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }

    public void agregarAlumno() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("datosAlumno", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        rutAlumno = (EditText) findViewById(R.id.rutAlumno);
        nombreAlumno = (EditText) findViewById(R.id.nombreAlumno);
        Button agregarAlumnoBoton = (Button) findViewById(R.id.botonAceptarAlumno);

        agregarAlumnoBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rut = rutAlumno.getText().toString();
                String nombre = nombreAlumno.getText().toString();
                if (Utilidades.validarRut(rut) && !nombre.equals("")) {
                    Alumno alumno = new Alumno();
                    alumno.setRut(rut);
                    alumno.setNombre(nombre);
                    boolean exito = miDB.insertarAlumno(alumno);
                    if (exito) {
                        editor.putString("rut", alumno.getRut());
                        editor.putString("nombre", alumno.getNombre());
                        editor.apply();
                        SesionManager sesionManager = new SesionManager(context, miDB);
                        sesionManager.asignarAsharedPref(alumno);
                        Toast.makeText(MainActivity.this, "Alumno insertado correctamente", Toast.LENGTH_LONG).show();
                        setContentView(R.layout.activity_main);
                        pantallaRegular();
                    } else {
                        Toast.makeText(MainActivity.this, "El usuario ya existe", Toast.LENGTH_LONG).show();
                        rutAlumno.setText("");
                        nombreAlumno.setText("");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "El rut debe ser valido", Toast.LENGTH_LONG).show();
                    rutAlumno.setText("");
                    nombreAlumno.setText("");
                }

            }
        });

    }

    /**
     * Metodo para establecer los elementos de la pantalla
     * que se usa una vez el usuario ya a abierto la aplicacion
     * con anterioridad.
     */

    public void pantallaRegular() {
        miDB = new DataBase(this);
        playButton = (ImageView) findViewById(R.id.playButton);
        configButton = (ImageView) findViewById(R.id.configButton);
        sesionAlumno = (TextView) findViewById(R.id.sesionAlumno);
        consejo = (TextView) findViewById(R.id.advertencia);
        consejo.setVisibility(View.INVISIBLE);
        SesionManager sesionManager = new SesionManager(this, miDB);
        Log.d(TAG, "Numero de sesiones = " + sesionManager.valorSesiones());

        sesionAlumno.setText("Alumno: " + sesionManager.getNombre() + " " + sesionManager.getRut());
        Alumno alumno = new Alumno();
        alumno.setRut(sesionManager.getRut());
        alumno.setNombre(sesionManager.getNombre());

        if (!sesionManager.mismoDia(alumno)) {
            sesionManager.resetValorSesiones();
            Log.d(TAG, "Cambio de dia, se resetearon las sesiones");
            Log.d(TAG, "Numero de sesiones = " + sesionManager.valorSesiones());
            Log.d(TAG, "Numero de sesiones = " + sesionManager.valorSesiones());
        }

        if (sesionManager.completaSesiones()) {
            consejo.setVisibility(View.VISIBLE);
            playButton.setEnabled(false);
        } else {
            consejo.setVisibility(View.INVISIBLE);
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GameMenu.class));

            }
        });

        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ModuloInformacion.class));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    @Override
    public void onResume() {
        super.onResume();
        setupPantalla();
    }


    @Override
    public void onBackPressed() {
        kill_activity();
    }

    private void kill_activity() {
        finish();
    }
}
