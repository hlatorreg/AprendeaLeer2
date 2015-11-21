package com.example.meriadok.aprendealeer2;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class ModuloLibro extends AppCompatActivity {

    String libro;
    String[] frasesLibro;
    String[] palabrasLibro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulo_libro);
        libro = getResources().getString(R.string.libro);
        separarLibro();
        for (String aFrasesLibro : frasesLibro) {
            System.out.println(aFrasesLibro);
        }
        separarFrases();

        for (String aPalabrasLibro : palabrasLibro) {
            System.out.println(aPalabrasLibro);
        }

    }

    public void separarLibro() {
        frasesLibro = libro.split("/");
    }

    public void separarFrases() {
        String[] aux;
        int posTotal = 0;
        int posFrase;
        for (String aFrasesLibro : frasesLibro) {
            posFrase = 0;
            aux = aFrasesLibro.split(" ");
            while (posFrase < aux.length) {
                palabrasLibro[posTotal] = aux[posFrase];
                posFrase++;
                posTotal++;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modulo_libro, menu);
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
