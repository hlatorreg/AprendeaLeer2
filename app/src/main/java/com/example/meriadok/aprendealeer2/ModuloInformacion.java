package com.example.meriadok.aprendealeer2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;


public class ModuloInformacion extends Activity {

    private VideoView video;
    private DataBase miDB;
    private ListView listaAlumnos;
    private String resultado;
    private int posicion;
    private Context context;
    private static final String TAG = ModuloInformacion.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        /* //TODO Codigo para mostrar video, copiar a los finales de las actividades.
        setContentView(R.layout.activity_modulo_informacion);
        video = (VideoView) findViewById(R.id.pruebaVideo);
        String urlpath = "android.resource://" + getPackageName() + "/" + R.raw.estrellas;
        video.setVideoURI(Uri.parse(urlpath));
        video.start();

        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });
        */
        preparaPantalla();

    }

    public void preparaPantalla() {
        setContentView(R.layout.activity_modulo_informacion);
        miDB = new DataBase(this);
        final Button agregarAlumno = (Button) findViewById(R.id.opcionesAgregarAlumno);
        final Button cambiarAlumno = (Button) findViewById(R.id.opcionesCambiarAlumno);
        final Button cambiarMail = (Button) findViewById(R.id.opcionesCambiarMail);
        final Button atras = (Button) findViewById(R.id.botonAtras);

        agregarAlumno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarAlumno();
            }
        });

        cambiarAlumno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarDeAlumno();
            }
        });

        cambiarMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEmailTutor();
            }
        });

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void cambiarDeAlumno() {
        setContentView(R.layout.modulo_cambio_alumno);
        llenarLista();
        Button cancelar = (Button) findViewById(R.id.botonCancelar);
        Button cambiar = (Button) findViewById(R.id.botonAceptarCambiar);

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preparaPantalla();
            }
        });

        cambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultado = listaAlumnos.getItemAtPosition(posicion).toString();
                Log.d(TAG, resultado);
                Toast.makeText(ModuloInformacion.this, "Alumno cambiado", Toast.LENGTH_LONG).show();
                cambiarDatos(resultado);
            }
        });
    }

    public void cambiarDatos(String resultado) {
        String[] partes = resultado.split("/");
        String nombre = partes[0].trim();
        String rut = partes[1].trim();

        Alumno alumno = new Alumno();
        alumno.setRut(rut);
        alumno.setNombre(nombre);

        SesionManager sesionManager = new SesionManager(context, miDB);
        sesionManager.setDatosAlumno(alumno);
        Alumno al = miDB.getDatosAlumno(alumno);
        sesionManager.asignarAsharedPref(al);
        preparaPantalla();
    }

    public void llenarLista() {
        List<Alumno> alumnos;
        alumnos = miDB.listaAlumnos();

        String[] myArray = new String[alumnos.size()];

        for (int i = 0; i < alumnos.size(); i++) {
            myArray[i] = alumnos.get(i).getNombre() + " / " + alumnos.get(i).getRut();
        }

        ArrayAdapter<String> miAdaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, myArray);
        listaAlumnos = (ListView) findViewById(R.id.listaAlumnos);
        listaAlumnos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listaAlumnos.setAdapter(miAdaptador);
        listaAlumnos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posicion = position;
            }
        });
    }

    public void agregarAlumno() {
        setContentView(R.layout.activity_main_alt);
        final ViewFlipper flipper = (ViewFlipper) findViewById(R.id.flippOpciones);
        flipper.setDisplayedChild(1);
        final EditText rutAlumno = (EditText) findViewById(R.id.rutAlumno);
        final EditText nombreAlumno = (EditText) findViewById(R.id.nombreAlumno);
        final Button agregarAlumnoBoton = (Button) findViewById(R.id.botonAceptarAlumno);
        final Button cancelarAlumnoBoton = (Button) findViewById(R.id.botonCancelarAlumno);

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
                        Toast.makeText(ModuloInformacion.this, "Alumno insertado correctamente", Toast.LENGTH_LONG).show();
                        setContentView(R.layout.activity_main);
                        preparaPantalla();
                    } else {
                        Toast.makeText(ModuloInformacion.this, "El usuario ya existe", Toast.LENGTH_LONG).show();
                        rutAlumno.setText("");
                        nombreAlumno.setText("");
                    }
                } else {
                    Toast.makeText(ModuloInformacion.this, "El rut debe ser valido", Toast.LENGTH_LONG).show();
                    rutAlumno.setText("");
                    nombreAlumno.setText("");
                }

            }
        });

        cancelarAlumnoBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preparaPantalla();
            }
        });

    }

    public void cambiarEmailTutor(){
        setContentView(R.layout.modulo_cambio_email);
        final EditText textoEmail = (EditText) findViewById(R.id.cambioCorreoTexto);
        final Button botonAceptarMail = (Button) findViewById(R.id.botonAceptarCambiarEmail);
        final Button botonCancelarMail = (Button) findViewById(R.id.botonCancelarEmail);

        botonAceptarMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = textoEmail.getText().toString();
                if(!email.equals("")){
                    miDB.cambiarEmail(email);
                    Toast.makeText(ModuloInformacion.this, "Correo cambiado con exito", Toast.LENGTH_LONG).show();
                    setContentView(R.layout.activity_main);
                    preparaPantalla();
                } else {
                    Toast.makeText(ModuloInformacion.this, "Ingrese el nuevo correo", Toast.LENGTH_LONG).show();
                    textoEmail.setText("");
                }
            }
        });

        botonCancelarMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preparaPantalla();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modulo_informacion, menu);
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
