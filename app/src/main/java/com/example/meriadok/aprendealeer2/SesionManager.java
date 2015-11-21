package com.example.meriadok.aprendealeer2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by MeriaDoK on 05-11-2015.
 * Maneja las variables sesion.
 */
public class SesionManager extends Activity {
    private Context context;
    private DataBase dataBase;
    private static final String TAG = SesionManager.class.getSimpleName();

    public SesionManager(Context context, DataBase dataBase) {
        this.context = context;
        this.dataBase = dataBase;
    }


    public int aumentarSesiones() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("vecesDia", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int nuevaCantidad = sharedPreferences.getInt("cantidad", 0) + 1;
        editor.putInt("cantidad", nuevaCantidad);
        editor.apply();
        return nuevaCantidad;
    }

    public void resetValorSesiones() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("vecesDia", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        dataBase = new DataBase(context);
        editor.putInt("cantidad", 0);
        editor.apply();
        Alumno al = new Alumno();
        al.setRut(getRut());
        dataBase.resetSesiones(al);
    }

    public void setValorSesiones(Alumno alumno) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("vecesDia", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("cantidad", alumno.getNum_sesiones());
        editor.apply();
    }

    public int valorSesiones() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("vecesDia", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("cantidad", 0);
    }


    public boolean mismoDia() {
        dataBase = new DataBase(context);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String fecha = simpleDateFormat.format(calendar.getTime());
        String ultimaFecha = dataBase.extraerFechaUltimaActividad();
        ultimaFecha = ultimaFecha.substring(0, 10);
        Log.d(TAG, ultimaFecha + " " + fecha);
        return fecha.equals(ultimaFecha);
    }

    public void mostrarAlerta() {
        System.out.println("Comenzando mostrarAlerta()");
        final Activity activity = (Activity) context;
        if ((valorSesiones() <= 5)) {
            if (!mismoDia())
                resetValorSesiones();
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Recordatorio");
            alertDialog.setMessage("Le restan " + (5 - valorSesiones()) + " sesiones este dia.");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    activity.finish();
                    //activity.startActivity(new Intent(context, GameMenu.class));
                }
            });
            Log.d(TAG, "Mostrando alerta");
            alertDialog.show();
        }
    }

    public String getRut() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("datosAlumno", Context.MODE_PRIVATE);
        return sharedPreferences.getString("rut", "");
    }

    public String getNombre() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("datosAlumno", Context.MODE_PRIVATE);
        return sharedPreferences.getString("nombre", "");
    }

    public void setDatosAlumno(Alumno alumno) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("datosAlumno", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("rut", alumno.getRut());
        editor.putString("nombre", alumno.getNombre());
        editor.apply();
        Log.d(TAG, "Datos del alumno cambiados por SesionManager, Nuevos datos RUT=" + alumno.getRut() + ", NOMBRE=" + alumno.getNombre());
    }

    public void asignarAsharedPref(Alumno alumno) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("logros", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("vecesDia", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        boolean desCu = false;
        boolean desCo = false;
        boolean desAc = false;

        if (alumno.getD_cuerpo() != 0)
            desCu = true;
        if (alumno.getD_cosas() != 0)
            desCo = true;
        if (alumno.getD_acciones() != 0)
            desAc = true;

        editor.putBoolean("desbloquearCuerpo", desCu);
        editor.putBoolean("desbloquearCosas", desCo);
        editor.putBoolean("desbloquearAcciones", desAc);
        editor1.putInt("cantidad", alumno.getNum_sesiones());
        editor1.putString("tipo", alumno.getTipo_dia());
        Log.d(TAG, "CUERPO=" + alumno.getD_cuerpo() + ", COSAS=" + alumno.getD_cosas() + ", ACCIONES=" + alumno.getD_acciones() + ", NUM_SESIONES=" + alumno.getNum_sesiones() + ", TIPO_DIA=" + alumno.getTipo_dia());
        editor.apply();
        editor1.apply();
    }

    public boolean completaSesiones() {
        return (valorSesiones() == 5);
    }

    public void cambioDia() {
        if (mismoDia())
            resetValorSesiones();
    }

}



