package com.example.meriadok.aprendealeer2;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewFlipper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ModuloPadres extends AppCompatActivity {

    private TextView botonPapa;
    private TextView botonMama;
    private ImageView imagenPapa;
    private ImageView imagenMama;
    private ImageView resOpcionPapa;
    private ImageView resOpcionMama;
    private ViewFlipper viewFlipper;
    private Button botonJugar;
    private Button botonRepetir;
    private int papaId;
    private int mamaId;
    private int repeticiones;
    private int repeticionesJuego = 0;
    private int puntaje;
    private final int TIEMPO_ESPERA = 2000;
    private final int CANTIDAD_REPETICIONES = 2;
    private final int CANTIDAD_REPETICIONES_JUEGO = 1;
    private final int PUNTAJE_MAXIMO = 2;
    private SoundPool sonidoMama;
    private SoundPool sonidoPapa;
    private Handler HMostrarM = new Handler();
    private Handler HMostrarP = new Handler();
    private Handler cargaSonidos = new Handler();
    private static final String TAG = ModuloPadres.class.getSimpleName();
    private Context context;
    private SesionManager sesionManager;
    private DataBase miDB;
    private String correcto;
    private String dia;
    private Alumno alumno;
    private boolean cargado = false;
    private VideoView video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulo_padres);
        context = this;
        miDB = new DataBase(context);
        sesionManager = new SesionManager(context, miDB);
        Alumno al = new Alumno();
        al.setRut(sesionManager.getRut());
        alumno = miDB.getDatosAlumno(al);
        //SharedPreferences sharedPreferences = this.getSharedPreferences("vecesDia", Context.MODE_PRIVATE);
        //SharedPreferences sharedPreferencesLogros = this.getSharedPreferences("logros", Context.MODE_PRIVATE);
        //TODO Importante, cambiar tipo de dia a impar para presentación, dia par con el fin de probar el juego en si
        //dia = sharedPreferences.getString("tipo", "impar");
        dia = alumno.getTipo_dia();
        puntaje = 0;
        Log.d(TAG, " Tipo dia " + dia);
        prepararPantalla();
        cargaSonidos.postDelayed(comenzar, 1500);
    }

    /***
     * Metodo que prepara los elementos que seran visibles en la pantalla
     */
    private void prepararPantalla() {
        repeticiones = 0;
        botonPapa = (TextView) findViewById(R.id.opcionPapa);
        botonMama = (TextView) findViewById(R.id.opcionMama);
        imagenPapa = (ImageView) findViewById(R.id.imagenPapa);
        imagenMama = (ImageView) findViewById(R.id.imagenMama);
        resOpcionMama = (ImageView) findViewById(R.id.resOpcionMama);
        resOpcionPapa = (ImageView) findViewById(R.id.resOpcionPapa);
        resOpcionMama.setAlpha(0.5f);
        resOpcionPapa.setAlpha(0.5f);
        sonidoPapa = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        papaId = sonidoPapa.load(context, R.raw.papaaudio, 1);
        sonidoMama = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mamaId = sonidoMama.load(context, R.raw.mamaaudio, 1);
        botonJugar = (Button) findViewById(R.id.irJuegoPadres);
        botonRepetir = (Button) findViewById(R.id.repetirPadres);
        viewFlipper = (ViewFlipper) findViewById(R.id.flipperPadres);
        correcto = "";

        sonidoPapa.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                cargado = true;
            }
        });

        botonJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comenzarJuego();
            }
        });

        botonRepetir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partePapa();
            }
        });

        botonPapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botonMama.setClickable(false);
                botonPapa.setClickable(false);
                if (correcto.equals("papa")) {
                    resOpcionPapa.setImageResource(R.drawable.m2_success);
                    ++puntaje;
                } else {
                    resOpcionPapa.setImageResource(R.drawable.m2_fail);
                }
                correcto = "papa";
                if (repeticionesJuego == CANTIDAD_REPETICIONES_JUEGO) {
                    finActividadDiaPar();
                } else {
                    HMostrarP.postDelayed(aImagenPapaJuego, TIEMPO_ESPERA);
                }
            }
        });

        botonMama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botonMama.setClickable(false);
                botonPapa.setClickable(false);
                if (correcto.equals("mama")) {
                    resOpcionMama.setImageResource(R.drawable.m2_success);
                    ++puntaje;
                } else {
                    resOpcionMama.setImageResource(R.drawable.m2_fail);
                }
                correcto = "papa";
                if (repeticionesJuego == CANTIDAD_REPETICIONES_JUEGO) {
                    finActividadDiaPar();
                } else {
                    HMostrarP.postDelayed(aImagenPapaJuego, TIEMPO_ESPERA);
                }
            }
        });


    }

    /***
     * Runnable para asegurarnos que los sonidos hayan cargado,
     * toma 1,5 segundos en ejecutarse luego de presionar el boton
     * para ir al modulo padres.
     */
    private Runnable comenzar = new Runnable() {
        @Override
        public void run() {
            partePapa();
        }
    };

    private void partePapa() {
        Log.d(TAG, "partePapa(), comenzando sesion.");
        viewFlipper.setDisplayedChild(0);
        sonidoPapa.play(papaId, 1, 1, 1, 0, 1);
        HMostrarP.postDelayed(aImagenPapa, TIEMPO_ESPERA);
    }

    private Runnable aImagenPapa = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnable aImagenPapa");
            viewFlipper.setDisplayedChild(1);
            sonidoPapa.play(papaId, 1, 1, 1, 0, 1);
            HMostrarM.postDelayed(aTextoMama, TIEMPO_ESPERA);
        }
    };

    private Runnable aTextoMama = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnable aTextoMama");
            viewFlipper.setDisplayedChild(2);
            sonidoMama.play(mamaId, 1, 1, 1, 0, 1);
            HMostrarM.postDelayed(aImagenMama, TIEMPO_ESPERA);
        }
    };

    private Runnable aImagenMama = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnable aImagenMama");
            viewFlipper.setDisplayedChild(3);
            sonidoMama.play(mamaId, 1, 1, 1, 0, 1);
            repeticiones++;
            HMostrarP.postDelayed(aTextoPapa, TIEMPO_ESPERA);
        }
    };

    private Runnable aTextoPapa = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnable aTextoPapa");
            if (repeticiones == CANTIDAD_REPETICIONES) {
                Log.d(TAG, "repeticiones (" + repeticiones + ") == (" + CANTIDAD_REPETICIONES + ") CANTIDAD_REPETICIONES");
                if (dia.equals("par")) {
                    Log.d(TAG, "Dia = Par");
                    viewFlipper.setDisplayedChild(4);
                } else {
                    Log.d(TAG, "Dia = Impar");
                    finActividadDiaImpar();
                }
            } else {
                Log.d(TAG, "repeticiones (" + repeticiones + ") != (" + CANTIDAD_REPETICIONES + ") CANTIDAD_REPETICIONES");
                partePapa();
            }
        }
    };

    private void comenzarJuego() {
        Log.d(TAG, "ejecutando comenzarJuego()");
        viewFlipper.setDisplayedChild(3);
        //sesionManager.aumentarSesiones();
        transparentarImagenes();
        sonidoMama.play(mamaId, 1, 1, 1, 0, 1);
        correcto = "mama";
        HMostrarM.postDelayed(aOpcionesJuego, TIEMPO_ESPERA);
    }

    private Runnable aOpcionesJuego = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnable aOpcionesJuego");
            viewFlipper.setDisplayedChild(5);
        }
    };

    private Runnable aImagenPapaJuego = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnable aImagenPapaJuego");
            botonMama.setClickable(true);
            botonPapa.setClickable(true);
            ++repeticionesJuego;
            sonidoPapa.play(papaId, 1, 1, 1, 0, 1);
            transparentarImagenes();
            viewFlipper.setDisplayedChild(1);
            HMostrarP.postDelayed(aOpcionesJuego, TIEMPO_ESPERA);
        }
    };

    public void finActividadDiaImpar() {
        Log.d(TAG, "ejecutando finActividadDiaImpar");
        Log.d(TAG, " Numero de sesiones: " + sesionManager.valorSesiones());
        miDB.insertFechaUltimaActividad(getFecha(), "Padres", alumno);
        sesionManager.aumentarSesiones();
        miDB.sumarSesiones(alumno);
        Log.d("finActividad()", " : " + sesionManager.valorSesiones());
        if (sesionManager.valorSesiones() == 5) {
            Log.d("finActividadImpar()", " Se alcanzo el maximo de sesiones");
            //TODO comentado para ver comportamiento con el cambio a la BD
            //SharedPreferences.Editor editor = getSharedPreferences("vecesDia", Context.MODE_PRIVATE).edit();
            //editor.putString("tipo", "par");
            //editor.apply();
            miDB.cambiarTipoDia(alumno);
        }
        sesionManager.mostrarAlerta();
    }

    //TODO terminar este modulo, verificar que la comparacion de valorSesiones se realize en de forma adecuada en el tipo de dia.
    public void finActividadDiaPar() {
        Log.d(TAG, "runnable aOpcionesJuego");
        Log.d(TAG, " : " + sesionManager.valorSesiones());

        if (puntaje == PUNTAJE_MAXIMO) {
            Log.d(TAG, "Puntaje maximo alcanzado, desbloqueando modulo cuerpo");
            miDB.modificarDesbloqueo(alumno, "cuerpo");
            Toast.makeText(this, "Desbloqueado Modulo Cuerpo", Toast.LENGTH_LONG).show();
            viewFlipper.setDisplayedChild(6);
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
            //finish();
        } else {
            if (sesionManager.valorSesiones() == 5) {
                Log.d(TAG, "Se alcanzo el maximo de sesiones");
                //TODO comentado para ver comportamiento con el cambio a la BD
                //SharedPreferences.Editor editor = getSharedPreferences("vecesDia", Context.MODE_PRIVATE).edit();
                //editor.putString("tipo", "impar");
                //editor.apply();
                miDB.cambiarTipoDia(alumno);
                sesionManager.mostrarAlerta();
            } else {
                sesionManager.aumentarSesiones();
                miDB.sumarSesiones(alumno);
                //TODO metodo funciona incorrectamente, siempre resetea las sesiones ya que el metodo mismoDia() llama a extraerFechaUltimaAct.. y su query es incorrecta.
                sesionManager.mostrarAlerta();
            }
        }
    }

    public String getFecha() {
        Log.d(TAG, "ejecutando getFecha()");
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Calendar c = Calendar.getInstance();
        return df.format(c.getTime());
    }

    private void transparentarImagenes() {
        resOpcionMama.setImageResource(android.R.color.transparent);
        resOpcionPapa.setImageResource(android.R.color.transparent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modulo_padres, menu);
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
        super.onBackPressed();
        Log.d(TAG, "override onBackPressed");
        finish();
        HMostrarP.removeCallbacksAndMessages(null);
        HMostrarM.removeCallbacksAndMessages(null);
        cargaSonidos.removeCallbacksAndMessages(null);
        sonidoMama.release();
        sonidoPapa.release();
        System.gc();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "override a onResume()");
        prepararPantalla();
    }
}
