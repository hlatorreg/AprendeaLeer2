package com.example.meriadok.aprendealeer2;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewFlipper;

import com.koushikdutta.ion.Ion;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Vector;

public class ModuloCosasGame extends AppCompatActivity {
    //Objeto que se comunica con la base de datos
    private DataBase miDB;
    //Recursos de pantalla
    ViewFlipper flipJuego;
    ImageView imagenCosa, simboloResultado1, simboloResultado2, simboloResultado3, estrella, animacionEstrellas;
    TextView opcion1, opcion2, opcion3, puntajeP, textoFelicitaciones;
    private Drawable[] imagenesCosas;
    int sonidoId;
    int sonidoEstrellaId;
    SoundPool sonidoEstrella;
    private static final String TAG = ModuloCosasGame.class.getSimpleName();

    /***
     * Variables para mantener puntaje, el indice de la respuesta correcta
     * y los indices de las otras dos opciones.
     */
    int puntaje = 0;
    int correcto;
    int op2, op3;
    //LIMITE cambia el numero de respuestas correctas necesarias
    int LIMITE = 2;
    int INTENTOS = 0;
    //Arreglo con las palabras
    String[] palabrasCosas;

    /***
     * Handler para presentacion de opciones, asigna puntaje si la respuesta correcta es escojida
     * finalmente llama el proximo handler
     */
    private Handler hOpciones = new Handler();

    //Handler para el manejo del evento desbloqueo nivel
    //actualiza el texto con el fin de darle la propiedad onClick
    private Handler hDesbloqueo = new Handler();

    //handler para reproducir el video
    private Handler hVideo = new Handler();

    //Handler a cargo de setear las opciones de la proxima pantalla, elije palabras al azar
    //y elije la respuesta correcta al azar, cambia las TextView de acuerdo con estas opciones
    //finalmente cambia al proximo flip (pantalla imagen) y llama al handler
    private Handler hImagen = new Handler();
    //Referencia al contexto para uso en runnable
    private Context context;
    //maneja el agendamiento de norificactiones, llama al cliente que llama al servicio de notificaciones.
    private ScheduleClient scheduleClient1;
    private ScheduleClient scheduleClient2;
    private ScheduleClient scheduleClient3;
    private ScheduleClient scheduleClient4;

    private Alumno alumno;

    private VideoView video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_modulo_cosas_game);
        context = this;
        //creamos el cliente y linkeamos la actividad a este.
        scheduleClient1 = new ScheduleClient(context);
        scheduleClient2 = new ScheduleClient(context);
        scheduleClient3 = new ScheduleClient(context);
        scheduleClient4 = new ScheduleClient(context);
        scheduleClient1.doBindService();
        scheduleClient2.doBindService();
        scheduleClient3.doBindService();
        scheduleClient4.doBindService();
        //metodo que prepara la pantalla agregando todos los recursos.
        PreparaPantalla();
    }


    /**
     * Runnable aOpciones, objetivo es mostrar opciones e indicar si la escojida por el usuario es la correcta antes
     * de pasar a la proxima imagen.
     */
    private Runnable aOpciones = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnable aOpciones");
            flipJuego.showNext();
            INTENTOS++;
            opcion1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    opcion1.setClickable(false);
                    opcion2.setClickable(false);
                    opcion3.setClickable(false);
                    if (opcion1.getText() == palabrasCosas[correcto]) {                     //Si esta opcion es correcta
                        Log.d(TAG, "Correcto opcion 1");
                        simboloResultado1.setImageResource(R.drawable.m2_success);          //Se indica (a travez de un simbolo sobre el texto) que la opcion es la correcta
                        puntaje++;                                                          //Sumamos puntaje
                        puntajeP.setText(" =       " + puntaje);                           //Cambiamos el texto del puntaje
                        hImagen.postDelayed(aImagen, 1500);                                 //Llamamos al runnable aImagen con un retrado de 2 segundos a travez del handler hImagen
                    } else {
                        Log.d(TAG, "Incorrecto opcion 1");
                        simboloResultado1.setImageResource(R.drawable.m2_fail);             //Simbolo para opcion incorrecta
                        hImagen.postDelayed(aImagen, 1500);                                 //Llamamos al runnable aImagen con un retrado de 2 segundos a travez del handler hImagen
                    }
                }
            });

            opcion2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    opcion1.setClickable(false);
                    opcion2.setClickable(false);
                    opcion3.setClickable(false);
                    if (opcion2.getText() == palabrasCosas[correcto]) {
                        Log.d(TAG, "Correcto opcion 2");
                        simboloResultado2.setImageResource(R.drawable.m2_success);
                        puntaje++;
                        puntajeP.setText(" =       " + puntaje);
                        hImagen.postDelayed(aImagen, 1500);
                    } else {
                        Log.d(TAG, "Incorrecto opcion 2");
                        simboloResultado2.setImageResource(R.drawable.m2_fail);
                        hImagen.postDelayed(aImagen, 1500);
                    }
                }
            });

            opcion3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    opcion1.setClickable(false);
                    opcion2.setClickable(false);
                    opcion3.setClickable(false);
                    if (opcion3.getText() == palabrasCosas[correcto]) {
                        Log.d(TAG, "Correcto opcion 3");
                        simboloResultado3.setImageResource(R.drawable.m2_success);
                        puntaje++;
                        puntajeP.setText(" =       " + puntaje);
                        hImagen.postDelayed(aImagen, 1500);
                    } else {
                        Log.d(TAG, "Incorrecto opcion 3");
                        simboloResultado3.setImageResource(R.drawable.m2_fail);
                        hImagen.postDelayed(aImagen, 1500);
                    }
                }
            });
        }
    };


    private Runnable aImagen = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnable aImagen");
            if (puntaje == LIMITE) {
                Log.d(TAG, "puntaje == LIMITE, " + puntaje + " == " + LIMITE);
                //Proximas 5 lineas cambian los valores de los desbloqueos en la BD
                llenarDatosAlumno();
                miDB.modificarDesbloqueo(alumno, "acciones");
                Toast.makeText(context, "Desbloqueado Modulo Acciones", Toast.LENGTH_LONG).show();
                hDesbloqueo.postDelayed(aDesbloqueo, 0);

            } else if (INTENTOS == LIMITE) {
                Log.d(TAG, "INTENTOS == LIMITE, " + INTENTOS + " == " + LIMITE);
                llenarDatosAlumno();
                finActividad();
            } else {
                //Volvemos a crear 3 numeros al azar, ademas se crea otro para indicar
                //la posicion de la respuesta correcta.
                Log.d(TAG, "No se alcanzo ni el puntaje ni el LIMITE");
                Random c = new Random();
                int posicion = c.nextInt(3) + 1;
                Vector<Integer> opciones;
                opciones = creaOpciones();
                correcto = opciones.get(0);
                int op2 = opciones.get(1);
                int op3 = opciones.get(2);

                //Seteamos la proxima imagen y las opciones siguientes.
                imagenCosa.setImageDrawable(imagenesCosas[correcto]);
                if (posicion == 1) {
                    opcion1.setText(palabrasCosas[correcto]);
                    opcion2.setText(palabrasCosas[op2]);
                    opcion3.setText(palabrasCosas[op3]);
                } else if (posicion == 2) {
                    opcion1.setText(palabrasCosas[op2]);
                    opcion2.setText(palabrasCosas[correcto]);
                    opcion3.setText(palabrasCosas[op3]);
                } else {
                    opcion1.setText(palabrasCosas[op2]);
                    opcion2.setText(palabrasCosas[op3]);
                    opcion3.setText(palabrasCosas[correcto]);
                }
                /**
                 * Cambiamos la transparencia de los simbolos de resultado, de este modo no seran visibles en la proxima
                 * muestra de opciones.
                 */
                simboloResultado1.setImageResource(android.R.color.transparent);
                simboloResultado2.setImageResource(android.R.color.transparent);
                simboloResultado3.setImageResource(android.R.color.transparent);

                //llamamos al flip para ver las opciones.
                flipJuego.setDisplayedChild(0);
                opcion1.setClickable(true);
                opcion2.setClickable(true);
                opcion3.setClickable(true);
                hOpciones.postDelayed(aOpciones, 1500);
            }
        }
    };

    private Runnable aDesbloqueo = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnable aDesbloqueo");
            System.gc();
            hVideo.postDelayed(aVideo, 1000);
        }
    };


    //Pequeña funcion para ver si existe alguna igualdad entre los numeros introducidos.
    public boolean comparaOpciones(int a, int b, int c) {
        return a != b && a != c && b != c;
    }

    public void PreparaPantalla() {
        Log.d(TAG, "ejecutando PrepararPantall()");
        miDB = new DataBase(context);
        /***
         * Creacion del vector de opciones, se llama a la funcion crearOpciones() para que lo llene,
         * luego se asigna los valores del vector a las opciones.
         */
        Vector<Integer> opciones;
        opciones = creaOpciones();
        correcto = opciones.get(0);
        op2 = opciones.get(1);
        op3 = opciones.get(2);
        /***
         * System.out.println("!!!!!!!!!!!!!!" + correcto + "," + op2 + "," + op3 + "!!!!!!!!!!!!!!!!!!!!!"); Descomentar para ver opciones!!!!
         * Obtencion del arreglo de palabras desde el archivo xml.
         * Otencion de la lista de imagenes a usar, insertadas dentro de un arreglo para su manipulacion
         */
        palabrasCosas = getResources().getStringArray(R.array.cosasArray);
        llenarImagenes();

        //Setup de los elementos de la pantalla, views de texto con las opciones, puntaje.
        opcion1 = (TextView) findViewById(R.id.opcion1);
        opcion2 = (TextView) findViewById(R.id.opcion2);
        opcion3 = (TextView) findViewById(R.id.opcion3);
        puntajeP = (TextView) findViewById(R.id.puntaje);
        puntajeP.setText(" =       " + puntaje);
        opcion1.setText(palabrasCosas[correcto]);
        opcion2.setText(palabrasCosas[op3]);
        opcion3.setText(palabrasCosas[op2]);

        sonidoId = R.raw.sonido_estrellas;
        sonidoEstrella = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sonidoEstrellaId = sonidoEstrella.load(this, sonidoId, 1);

        //Setup de las imagenes a mostrar durante la actividad, parte del cuerpo, opcion correcta o incorrecta.
        imagenCosa = (ImageView) findViewById(R.id.imagenCosasJuego);
        imagenCosa.setImageDrawable(imagenesCosas[correcto]);
        simboloResultado1 = (ImageView) findViewById(R.id.resOpcion);
        simboloResultado2 = (ImageView) findViewById(R.id.resOpcion2);
        simboloResultado3 = (ImageView) findViewById(R.id.resOpcion3);
        simboloResultado1.setAlpha(0.5f);
        simboloResultado2.setAlpha(0.5f);
        simboloResultado3.setAlpha(0.5f);
        estrella = (ImageView) findViewById(R.id.star);
        animacionEstrellas = (ImageView) findViewById(R.id.animacionEstrella);
        Ion.with(animacionEstrellas).load("android.resource://com.example.meriadok.aprendealeer2/" + R.drawable.animation_stars);

        flipJuego = (ViewFlipper) findViewById(R.id.juegoCosas);

        //Despues de 3s de mostrar la imagen, se llama a travez del hander de opciones al runnable para mostrar las opciones
        //Este runnable (aOpciones) lo primero que hace es cambiar el viewFlipper, luego compara la opcion escojida
        //(de acuerdo) a lo que clickeamos, con la opcion correcta seteada al crearse la actividad.
        hOpciones.postDelayed(aOpciones, 1500);
    }

    public Vector<Integer> creaOpciones() {
        int cont = 0;
        Vector<Integer> opciones = new Vector<>(3);
        Random c = new Random();
        int correcto = c.nextInt(19);
        int op2 = c.nextInt(19);
        int op3 = c.nextInt(19);
        //Ciclo que asegura que las variables para las opciones no sean iguales.
        Log.d(TAG, "!!!!!!!!!!!!!!" + correcto + "," + op2 + "," + op3 + "!!!!!!!!!!!!!!!!!!!!!");
        while (!comparaOpciones(correcto, op2, op3)) {
            Log.d(TAG, "!!!!!!!!!!!!!!!Entrando a comparar opciones por!!!!!!!!!!!!!!!!!!" + " = " + cont);
            Log.d(TAG, "!!!!!!!!!!!!!!" + correcto + "," + op2 + "," + op3 + "!!!!!!!!!!!!!!!!!!!!!");
            correcto = c.nextInt(19);
            op2 = c.nextInt(19);
            op3 = c.nextInt(19);
            Log.d(TAG, "!!!!!!!!!!!!!!" + correcto + "," + op2 + "," + op3 + "!!!!!!!!!!!!!!!!!!!!!");
            cont++;
        }
        opciones.addElement(correcto);
        opciones.addElement(op2);
        opciones.addElement(op3);
        Log.d(TAG, "!!!!!!!!!!!!!!!Saliendo de comparar opciones!!!!!!!!!!!!!");

        return opciones;
    }

    public void llenarImagenes() {
        Log.d(TAG, "ejecutando llenarImagenes()");
        imagenesCosas = new Drawable[]{
                getResources().getDrawable(R.drawable.cama),
                getResources().getDrawable(R.drawable.silla),
                getResources().getDrawable(R.drawable.inodoro),
                getResources().getDrawable(R.drawable.escoba),
                getResources().getDrawable(R.drawable.televisor),
                getResources().getDrawable(R.drawable.perro),
                getResources().getDrawable(R.drawable.gato),
                getResources().getDrawable(R.drawable.mesa),
                getResources().getDrawable(R.drawable.pared),
                getResources().getDrawable(R.drawable.reloj),
                getResources().getDrawable(R.drawable.puerta),
                getResources().getDrawable(R.drawable.refrigerador),
                getResources().getDrawable(R.drawable.alfombra),
                getResources().getDrawable(R.drawable.ventana),
                getResources().getDrawable(R.drawable.plato),
                getResources().getDrawable(R.drawable.pijama),
                getResources().getDrawable(R.drawable.cuchara),
                getResources().getDrawable(R.drawable.zapato),
                getResources().getDrawable(R.drawable.taza),
                getResources().getDrawable(R.drawable.pelota)
        };
    }

    public void finActividad() {
        miDB = new DataBase(this);
        SesionManager sesionManager = new SesionManager(context, miDB);
        Toast.makeText(ModuloCosasGame.this, "Sesión Completada ", Toast.LENGTH_LONG).show();
        crearAlarmasParaNotificaciones();
        sesionManager.aumentarSesiones();
        sesionManager.mostrarAlerta(alumno);
    }

    private Runnable aVideo = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Reproduciendo video");
            setContentView(R.layout.video);
            video = (VideoView) findViewById(R.id.videoVictoria);
            String urlpath = "android.resource://" + getPackageName() + "/" + R.raw.estrellas;
            video.setVideoURI(Uri.parse(urlpath));
            video.start();
            video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    finish();
                }
            });
        }
    };

    /***
     * Creamos objetos calendarios con la fecha en la que se termino
     * la actividad, a esa fecha le agregamos el tiempo acordado con el cliente.
     * seteamos las alarmas a esas fechas y le asignamos un tipo con el fin
     * de cambiar el texto de la notificacion.
     */
    public void crearAlarmasParaNotificaciones() {
        Log.d(TAG, "ejecutando crearAlarmasParaNotificaciones()");
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Calendar c = Calendar.getInstance();
        String hora = df.format(c.getTime());
        miDB.insertFechaUltimaActividad(hora, "Cosas", alumno);
        AlarmManager alarmManager = new AlarmManager(context);
        alarmManager.setearAlarmas(scheduleClient1,
                scheduleClient2,
                scheduleClient3,
                scheduleClient4, alumno.getRut() + " " + alumno.getNombre());
    }

    public void llenarDatosAlumno() {
        SesionManager sesionManager = new SesionManager(context, miDB);
        Alumno al = new Alumno();
        al.setRut(sesionManager.getRut());
        alumno = miDB.getDatosAlumno(al);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modulo_cosas_game, menu);
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
    public void onBackPressed() {
        Log.d(TAG, "override onBackPressed");
        super.onBackPressed();
        hDesbloqueo.removeCallbacksAndMessages(null);
        hImagen.removeCallbacksAndMessages(null);
        hOpciones.removeCallbacksAndMessages(null);
        kill_activity();
    }

    @Override
    protected void onStop() {
        // When our activity is stopped ensure we also stop the connection to the service
        // this stops us leaking our activity into the system *bad*
        if (scheduleClient1 != null) {
            scheduleClient1.doUnbindService();
            scheduleClient2.doUnbindService();
            scheduleClient3.doUnbindService();
            scheduleClient4.doUnbindService();
        }
        super.onStop();
    }

    private void kill_activity() {
        finish();
    }
}
