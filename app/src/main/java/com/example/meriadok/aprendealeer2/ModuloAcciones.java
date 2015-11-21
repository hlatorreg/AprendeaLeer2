package com.example.meriadok.aprendealeer2;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;


public class ModuloAcciones extends AppCompatActivity {

    ViewFlipper flip;
    TextView text;
    ImageView imagenAcciones;
    ImageView animacion;
    SoundPool sonidoAcciones;
    AnimationDrawable tapAnimation;
    Button jugar;
    Button repetir;
    int[] sonidoAccionesID;
    int contadorPalabras;
    int contadorPantallas;
    private static final String TAG = ModuloAcciones.class.getSimpleName();
    private static final int TOTAL_PALABRAS = 9;

    //Arreglos de las palabras a usar y las ID's de los archivos de audio, luego se a�adiran las ID's de las imagenes
    String[] palabrasAcciones;
    private Drawable[] imagenesAcciones;
    int[] soundIds;

    //Handler para ejejuctar el runnable que modifica las imagenes, texto y sonido
    private Handler mHandler = new Handler();
    private Handler nHandler = new Handler();
    //Handlers para el cambio automatico de vistas durante la primera pasada
    private Handler aHandler = new Handler();
    private Handler bHandler = new Handler();
    //Contexto para usar en el runnable
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_modulo_acciones);
        context = this; // -> una referencia el contexto
        PrepararPantalla();
        PrimeraPasada();

        jugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagenAcciones.setImageDrawable(null);
                sonidoAcciones.release();
                System.gc();
                finish();
                startActivity(new Intent(ModuloAcciones.this, ModuloAccionesGame.class));
            }
        });

        repetir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrepararPantalla();
                Repetir();
            }
        });
    }

    private void PrepararPantalla() {
        Log.d(TAG, "ejecutando PrepararPantalla()");
        contadorPalabras = 0; // -> contador de palabras
        contadorPantallas = 0; // -> contador pantallas para salir de la rotacion

        //Setup sonidos, imagen, palabras y animaciones
        jugar = (Button) findViewById(R.id.irJuegoAcciones);
        repetir = (Button) findViewById(R.id.irAtras);
        palabrasAcciones = getResources().getStringArray(R.array.accionesArray);
        llenarIdSonidos();
        llenarIdSonidosAcciones();
        llenarImagenes();

        text = (TextView) findViewById(R.id.palabraAcciones);
        text.setText(palabrasAcciones[contadorPalabras]);

        imagenAcciones = (ImageView) findViewById(R.id.imagenAcciones);
        imagenAcciones.setImageDrawable(imagenesAcciones[contadorPalabras]);

        animacion = (ImageView) findViewById(R.id.tapAnimationAcciones);
        animacion.setBackgroundResource(R.drawable.tap_animation);

        tapAnimation = (AnimationDrawable) animacion.getBackground();
        tapAnimation.start();
        //Fin setup recursos

        //Setup del flipper de palabras
        flip = (ViewFlipper) findViewById(R.id.flipperAcciones);
    }

    private void PrimeraPasada() {
        Log.d(TAG, "ejecutando void PrimeraPasada()");
        sonidoAcciones.play(sonidoAccionesID[contadorPalabras], 1, 1, 1, 0, 1); //Se toca el sonido
        aHandler.postDelayed(actualizaPantallaP, 1500);    //Se llama al handler para hacer los cambios en pantalla (cambiar a la imagen y modificar el proximo text e image view)
    }

    private void Repetir() {
        Log.d(TAG, "ejecutando void Repetir()");
        flip.setDisplayedChild(0);
        flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flip.setClickable(false);
                sonidoAcciones.play(sonidoAccionesID[contadorPalabras], 1, 1, 1, 0, 1); //Se toca el sonido
                mHandler.postDelayed(actualizaPantalla, 1500);    //Se llama al handler para hacer los cambios en pantalla (cambiar a la imagen y modificar el proximo text e image view)

            }
        });
    }

    private Runnable actualizaPantallaP = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnable actualizarPantallaP");
            flip.setDisplayedChild(1);
            sonidoAcciones.play(sonidoAccionesID[contadorPalabras], 1, 1, 1, 0, 1);
            bHandler.postDelayed(nextPantallaP, 1500);
        }
    };

    private Runnable nextPantallaP = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnable nextPantallaP");
            if (contadorPantallas == TOTAL_PALABRAS) {
                Log.d(TAG, "contadorPantallas == TOTAL_PALABRAS, " + contadorPantallas + " == " + TOTAL_PALABRAS);
                flip.setDisplayedChild(2);
            } else {
                Log.d(TAG, "contadorPantallas != TOTAL_PALABRAS, " + contadorPantallas + " == " + TOTAL_PALABRAS);
                flip.setDisplayedChild(0);
                contadorPalabras++;
                if (contadorPalabras == TOTAL_PALABRAS) {
                    Log.d(TAG, "contadorPalabras == TOTAL_PALABRAS, " + contadorPalabras + " == " + TOTAL_PALABRAS);
                    flip.setDisplayedChild(2);
                } else {
                    Log.d(TAG, "contadorPalabras != TOTAL_PALABRAS, " + contadorPalabras + " == " + TOTAL_PALABRAS);
                    text.setText(palabrasAcciones[contadorPalabras]);
                    imagenAcciones.setImageDrawable(imagenesAcciones[contadorPalabras]);
                    contadorPantallas++;
                    PrimeraPasada();
                }
            }
        }
    };

    /**
     * En orden, se cambia el flipper (texto e imagen)
     * se toca nuevamente el sonido de la parte del cuerpo
     * se hacen los cambios necesarios para acceder a la proxima palabra e imagen
     * si llegamos a la ultima palabra/imagen, reseteamos el contador para comenzar denuevo
     * finalmente llamamos a otro handler (runnable nextPantalla)
     */
    private Runnable actualizaPantalla = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnableActualizarPantalla()");
            flip.showNext();
            sonidoAcciones.play(sonidoAccionesID[contadorPalabras], 1, 1, 1, 0, 1);
            nHandler.postDelayed(nextPantalla, 1500);
        }
    };

    /**
     * Este runnable tiene el fin de cambiar el flipper
     * esto se hace con un retraso de dos segundos.
     * El fin de esta accion es crear una sensacion de
     * peso a cada pantalla, ademas de dejarnos nuevamente en una
     * pantalla donde tenemos una palabra.
     */
    private Runnable nextPantalla = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnable nextPantalla()");
            if (contadorPantallas == TOTAL_PALABRAS) {
                Log.d(TAG, "contadorPantallas == TOTAL_PALABRAS, " + contadorPantallas + " == " + TOTAL_PALABRAS);
                flip.setDisplayedChild(2);
                flip.setClickable(false);
            } else {
                Log.d(TAG, "contadosPantallas != TOTAL_PALABRAS");
                flip.setDisplayedChild(0);
                contadorPalabras++;
                if (contadorPalabras == TOTAL_PALABRAS) {
                    Log.d(TAG, "contadorPalabras == TOTAL_PALABRAS, " + contadorPalabras + " == " + TOTAL_PALABRAS);
                    flip.setDisplayedChild(2);
                    flip.setClickable(false);
                } else {
                    Log.d(TAG, "contadorPalabras != TOTAL_PALABRAS");
                    text.setText(palabrasAcciones[contadorPalabras]);
                    imagenAcciones.setImageDrawable(imagenesAcciones[contadorPalabras]);
                    flip.setClickable(true);
                    contadorPantallas++;
                }
            }
        }
    };

    public void llenarImagenes() {
        imagenesAcciones = new Drawable[]{
                getResources().getDrawable(R.drawable.beber),
                getResources().getDrawable(R.drawable.comer),
                getResources().getDrawable(R.drawable.correr),
                getResources().getDrawable(R.drawable.dormir),
                getResources().getDrawable(R.drawable.leer),
                getResources().getDrawable(R.drawable.llorar),
                getResources().getDrawable(R.drawable.reir),
                getResources().getDrawable(R.drawable.saltar),
                getResources().getDrawable(R.drawable.sentarse)
        };
    }

    public void llenarIdSonidos() {
        soundIds = new int[]{
                R.raw.ma_beber,
                R.raw.ma_comer,
                R.raw.ma_correr,
                R.raw.ma_dormir,
                R.raw.ma_leer,
                R.raw.ma_llorar,
                R.raw.ma_reir,
                R.raw.ma_saltar,
                R.raw.ma_sentarse
        };
    }

    public void llenarIdSonidosAcciones() {
        sonidoAcciones = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sonidoAccionesID = new int[]{
                sonidoAcciones.load(this, R.raw.ma_beber, 1),
                sonidoAcciones.load(this, R.raw.ma_comer, 1),
                sonidoAcciones.load(this, R.raw.ma_correr, 1),
                sonidoAcciones.load(this, R.raw.ma_dormir, 1),
                sonidoAcciones.load(this, R.raw.ma_leer, 1),
                sonidoAcciones.load(this, R.raw.ma_llorar, 1),
                sonidoAcciones.load(this, R.raw.ma_reir, 1),
                sonidoAcciones.load(this, R.raw.ma_saltar, 1),
                sonidoAcciones.load(this, R.raw.ma_sentarse, 1)
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modulo_acciones, menu);
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
        Log.d(TAG, "onBackPressed() Modulo Acciones");
        super.onBackPressed();
        sonidoAcciones.release();
        mHandler.removeCallbacksAndMessages(null);
        nHandler.removeCallbacksAndMessages(null);
        aHandler.removeCallbacksAndMessages(null);
        bHandler.removeCallbacksAndMessages(null);
        System.gc();
        finish();
    }


}
