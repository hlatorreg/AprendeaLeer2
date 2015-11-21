package com.example.meriadok.aprendealeer2;

import android.util.Log;

/**
 * Created by MeriaDoK on 17-11-2015.
 * Clase con metodos de utilidad numerica, actualmente
 * contiene la validacion de los ruts.
 */
public class Utilidades {

    private static final String TAG = Utilidades.class.getSimpleName();

    public static boolean validarRut(String rut) {

        boolean validacion = false;
        try {
            rut = rut.toUpperCase();
            rut = rut.replace(".", "");
            rut = rut.replace("-", "");
            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

            char dv = rut.charAt(rut.length() - 1);

            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (dv == (char) (s != 0 ? s + 47 : 75)) {
                validacion = true;
            }

        } catch (java.lang.NumberFormatException e) {
            Log.d(TAG, "java.lang.NumberFormatException");
        } catch (Exception e) {
            Log.d(TAG, "Excepcion no prevista");
        }
        return validacion;
    }

}
