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


public class ModuloCuerpo extends AppCompatActivity {

    ViewFlipper flip;
    TextView text;
    ImageView imagenCuerpo;
    ImageView animacion;
    SoundPool sonidoCuerpo;
    AnimationDrawable tapAnimation;
    Button jugar;
    Button repetir;
    int[] sonidoCuerpoID;
    int contadorPalabras;
    int contadorPantallas;
    private static final String TAG = ModuloCuerpo.class.getSimpleName();
    private static final int TOTAL_PALABRAS = 2;

    //Arreglos de las palabras a usar y las ID's de los archivos de audio, luego se a�adiran las ID's de las imagenes
    String[] palabrasCuerpo;
    private Drawable[] imagenesCuerpo;
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
        setContentView(R.layout.activity_modulo_cuerpo);
        context = this; // -> una referencia el contexto
        PrepararPantalla();
        PrimeraPasada();

        jugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagenCuerpo.setImageDrawable(null);
                sonidoCuerpo.release();
                System.gc();
                finish();
                startActivity(new Intent(ModuloCuerpo.this, moduloCuerpoGame.class));
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
        jugar = (Button) findViewById(R.id.irJuego);
        repetir = (Button) findViewById(R.id.irAtras);
        palabrasCuerpo = getResources().getStringArray(R.array.cuerpoArray);
        llenarIdSonidos();
        llenarIdSonidosCuerpo();
        llenarImagenes();

        text = (TextView) findViewById(R.id.palabraCuerpo);
        text.setText(palabrasCuerpo[contadorPalabras]);

        imagenCuerpo = (ImageView) findViewById(R.id.imagenCuerpo);
        imagenCuerpo.setImageDrawable(imagenesCuerpo[contadorPalabras]);

        animacion = (ImageView) findViewById(R.id.tapAnimation);
        animacion.setBackgroundResource(R.drawable.tap_animation);

        tapAnimation = (AnimationDrawable) animacion.getBackground();
        tapAnimation.start();
        //Fin setup recursos

        //Setup del flipper de palabras
        flip = (ViewFlipper) findViewById(R.id.flipperCuerpo);
    }

    private void PrimeraPasada() {
        Log.d(TAG, "ejecutando void PrimeraPasada()");
        sonidoCuerpo.play(sonidoCuerpoID[contadorPalabras], 1, 1, 1, 0, 1); //Se toca el sonido
        aHandler.postDelayed(actualizaPantallaP, 1500);    //Se llama al handler para hacer los cambios en pantalla (cambiar a la imagen y modificar el proximo text e image view)
    }

    private void Repetir() {
        Log.d(TAG, "ejecutando void Repetir()");
        flip.setDisplayedChild(0);
        flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flip.setClickable(false);
                sonidoCuerpo.play(sonidoCuerpoID[contadorPalabras], 1, 1, 1, 0, 1); //Se toca el sonido
                mHandler.postDelayed(actualizaPantalla, 1500);    //Se llama al handler para hacer los cambios en pantalla (cambiar a la imagen y modificar el proximo text e image view)

            }
        });
    }

    private Runnable actualizaPantallaP = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnable actualizarPantallaP");
            flip.setDisplayedChild(1);
            sonidoCuerpo.play(sonidoCuerpoID[contadorPalabras], 1, 1, 1, 0, 1);
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
                    text.setText(palabrasCuerpo[contadorPalabras]);
                    imagenCuerpo.setImageDrawable(imagenesCuerpo[contadorPalabras]);
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
            sonidoCuerpo.play(sonidoCuerpoID[contadorPalabras], 1, 1, 1, 0, 1);
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
                    text.setText(palabrasCuerpo[contadorPalabras]);
                    imagenCuerpo.setImageDrawable(imagenesCuerpo[contadorPalabras]);
                    flip.setClickable(true);
                    contadorPantallas++;
                }
            }
        }
    };

    public void llenarImagenes() {
        Log.d(TAG, "ejecutando llenarImagenes()");
        imagenesCuerpo = new Drawable[]{
                getResources().getDrawable(R.drawable.boca),
                getResources().getDrawable(R.drawable.cara),
                getResources().getDrawable(R.drawable.ceja),
                getResources().getDrawable(R.drawable.codo),
                getResources().getDrawable(R.drawable.cuello),
                getResources().getDrawable(R.drawable.dedo),
                getResources().getDrawable(R.drawable.espalda),
                getResources().getDrawable(R.drawable.mano),
                getResources().getDrawable(R.drawable.nariz),
                getResources().getDrawable(R.drawable.ojos),
                getResources().getDrawable(R.drawable.oreja),
                getResources().getDrawable(R.drawable.pelo),
                getResources().getDrawable(R.drawable.pie),
                getResources().getDrawable(R.drawable.rodilla)
        };
    }

    public void llenarIdSonidos() {
        Log.d(TAG, "ejecutando llenarIdSonidos()");
        soundIds = new int[]{
                R.raw.mc_boca,
                R.raw.mc_cara,
                R.raw.mc_ceja,
                R.raw.mc_codo,
                R.raw.mc_cuello,
                R.raw.mc_dedo,
                R.raw.mc_espalda,
                R.raw.mc_mano,
                R.raw.mc_nariz,
                R.raw.mc_ojos,
                R.raw.mc_oreja,
                R.raw.mc_pelo,
                R.raw.mc_pie,
                R.raw.mc_rodilla
        };
    }

    public void llenarIdSonidosCuerpo() {
        Log.d(TAG, "ejecutando llenarIdSonidosCuerpo()");
        sonidoCuerpo = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sonidoCuerpoID = new int[]{
                sonidoCuerpo.load(this, R.raw.mc_boca, 1),
                sonidoCuerpo.load(this, R.raw.mc_cara, 1),
                sonidoCuerpo.load(this, R.raw.mc_ceja, 1),
                sonidoCuerpo.load(this, R.raw.mc_codo, 1),
                sonidoCuerpo.load(this, R.raw.mc_cuello, 1),
                sonidoCuerpo.load(this, R.raw.mc_dedo, 1),
                sonidoCuerpo.load(this, R.raw.mc_espalda, 1),
                sonidoCuerpo.load(this, R.raw.mc_mano, 1),
                sonidoCuerpo.load(this, R.raw.mc_nariz, 1),
                sonidoCuerpo.load(this, R.raw.mc_ojos, 1),
                sonidoCuerpo.load(this, R.raw.mc_oreja, 1),
                sonidoCuerpo.load(this, R.raw.mc_pelo, 1),
                sonidoCuerpo.load(this, R.raw.mc_pie, 1),
                sonidoCuerpo.load(this, R.raw.mc_rodilla, 1)
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modulo_cuerpo, menu);
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
        sonidoCuerpo.release(); //limpiar buffer de sonidos
        mHandler.removeCallbacksAndMessages(null);
        nHandler.removeCallbacksAndMessages(null);
        aHandler.removeCallbacksAndMessages(null);
        bHandler.removeCallbacksAndMessages(null);
        System.gc(); //GarbageColector, limpiar memoria
        kill_activity(); //destruir actividad
    }

    private void kill_activity() {
        this.finish();
    }

}
