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


public class ModuloCosas extends AppCompatActivity {

    ViewFlipper flip;
    TextView text;
    ImageView imagenCosa;
    ImageView animacion;
    SoundPool sonidoCosas;
    AnimationDrawable tapAnimation;
    Button jugar;
    Button repetir;
    int[] sonidoCosasID;
    int contadorPalabras;
    int contadorPantallas;
    private static final String TAG = ModuloCosas.class.getSimpleName();
    //TODO cambiar a 20 para funcionamiento estandar
    private static final int TOTAL_PALABRAS = 2;

    //Arreglos de las palabras a usar y las ID's de los archivos de audio, luego se a�adiran las ID's de las imagenes
    String[] palabrasCosas;
    private Drawable[] imagenesCosas;
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
        setContentView(R.layout.activity_modulo_cosas);
        context = this; // -> una referencia el contexto
        PrepararPantalla();
        PrimeraPasada();

        jugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagenCosa.setImageDrawable(null);
                sonidoCosas.release();
                System.gc();
                finish();
                startActivity(new Intent(ModuloCosas.this, ModuloCosasGame.class));
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
        jugar = (Button) findViewById(R.id.irJuegoCosas);
        repetir = (Button) findViewById(R.id.irAtras);
        palabrasCosas = getResources().getStringArray(R.array.cosasArray);
        llenarIdSonidos();
        llenarIdSonidosCosas();
        llenarImagenes();

        text = (TextView) findViewById(R.id.palabraCosas);
        text.setText(palabrasCosas[contadorPalabras]);

        imagenCosa = (ImageView) findViewById(R.id.imagenCosa);
        imagenCosa.setImageDrawable(imagenesCosas[contadorPalabras]);

        animacion = (ImageView) findViewById(R.id.tapAnimationCosas);
        animacion.setBackgroundResource(R.drawable.tap_animation);

        tapAnimation = (AnimationDrawable) animacion.getBackground();
        tapAnimation.start();
        //Fin setup recursos

        //Setup del flipper de palabras
        flip = (ViewFlipper) findViewById(R.id.flipperCosas);
    }

    private void PrimeraPasada() {
        Log.d(TAG, "ejecutando void PrimeraPasada()");
        sonidoCosas.play(sonidoCosasID[contadorPalabras], 1, 1, 1, 0, 1); //Se toca el sonido
        aHandler.postDelayed(actualizaPantallaP, 1500);    //Se llama al handler para hacer los cambios en pantalla (cambiar a la imagen y modificar el proximo text e image view)
    }

    private void Repetir() {
        Log.d(TAG, "ejecutando void Repetir()");
        flip.setDisplayedChild(0);
        flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flip.setClickable(false);
                sonidoCosas.play(sonidoCosasID[contadorPalabras], 1, 1, 1, 0, 1); //Se toca el sonido
                mHandler.postDelayed(actualizaPantalla, 1500);    //Se llama al handler para hacer los cambios en pantalla (cambiar a la imagen y modificar el proximo text e image view)

            }
        });
    }

    private Runnable actualizaPantallaP = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnable actualizarPantallaP");
            flip.setDisplayedChild(1);
            sonidoCosas.play(sonidoCosasID[contadorPalabras], 1, 1, 1, 0, 1);
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
                    text.setText(palabrasCosas[contadorPalabras]);
                    imagenCosa.setImageDrawable(imagenesCosas[contadorPalabras]);
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
            sonidoCosas.play(sonidoCosasID[contadorPalabras], 1, 1, 1, 0, 1);
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
                    text.setText(palabrasCosas[contadorPalabras]);
                    imagenCosa.setImageDrawable(imagenesCosas[contadorPalabras]);
                    flip.setClickable(true);
                    contadorPantallas++;
                }
            }
        }
    };

    public void llenarImagenes() {
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

    public void llenarIdSonidos() {
        soundIds = new int[]{
                R.raw.mco_cama,
                R.raw.mco_silla,
                R.raw.mco_bano,
                R.raw.mco_escoba,
                R.raw.mco_televisor,
                R.raw.mco_perro,
                R.raw.mco_gato,
                R.raw.mco_mesa,
                R.raw.mco_pared,
                R.raw.mco_reloj,
                R.raw.mco_puerta,
                R.raw.mco_refrigerador,
                R.raw.mco_alfombra,
                R.raw.mco_ventana,
                R.raw.mco_plato,
                R.raw.mco_pijama,
                R.raw.mco_cuchara,
                R.raw.mco_zapato,
                R.raw.mco_taza,
                R.raw.mco_pelota
        };
    }

    public void llenarIdSonidosCosas() {
        sonidoCosas = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sonidoCosasID = new int[]{
                sonidoCosas.load(this, R.raw.mco_cama, 1),
                sonidoCosas.load(this, R.raw.mco_silla, 1),
                sonidoCosas.load(this, R.raw.mco_bano, 1),
                sonidoCosas.load(this, R.raw.mco_escoba, 1),
                sonidoCosas.load(this, R.raw.mco_televisor, 1),
                sonidoCosas.load(this, R.raw.mco_perro, 1),
                sonidoCosas.load(this, R.raw.mco_gato, 1),
                sonidoCosas.load(this, R.raw.mco_mesa, 1),
                sonidoCosas.load(this, R.raw.mco_pared, 1),
                sonidoCosas.load(this, R.raw.mco_reloj, 1),
                sonidoCosas.load(this, R.raw.mco_puerta, 1),
                sonidoCosas.load(this, R.raw.mco_refrigerador, 1),
                sonidoCosas.load(this, R.raw.mco_alfombra, 1),
                sonidoCosas.load(this, R.raw.mco_ventana, 1),
                sonidoCosas.load(this, R.raw.mco_plato, 1),
                sonidoCosas.load(this, R.raw.mco_pijama, 1),
                sonidoCosas.load(this, R.raw.mco_cuchara, 1),
                sonidoCosas.load(this, R.raw.mco_zapato, 1),
                sonidoCosas.load(this, R.raw.mco_taza, 1),
                sonidoCosas.load(this, R.raw.mco_pelota, 1)
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modulo_cosas, menu);
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
        Log.d(TAG, "override onBackPressed()");
        super.onBackPressed();
        sonidoCosas.release();
        mHandler.removeCallbacksAndMessages(null);
        nHandler.removeCallbacksAndMessages(null);
        aHandler.removeCallbacksAndMessages(null);
        bHandler.removeCallbacksAndMessages(null);
        System.gc();
        kill_activity();
    }

    private void kill_activity() {
        finish();
    }
}
