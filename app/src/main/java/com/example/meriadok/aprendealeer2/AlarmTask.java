package com.example.meriadok.aprendealeer2;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Created by MeriaDoK on 29-10-2015.
 * Manda la el intent al servicio, contiene la ID que es el rut + el tiempo en ms en el cual se
 * creo la alarma.
 */
public class AlarmTask implements Runnable {
    // The date selected for the alarm
    private Calendar date;
    // The android system alarm manager
    private AlarmManager am;
    // Your context to retrieve the alarm manager from
    private Context context;
    private int tipoNotificacion;
    private int id;
    private static final String TAG = AlarmTask.class.getSimpleName();
    private String alumno;


    //Hola
    public AlarmTask(Context context, Calendar date, int tipo, String alumno) {
        Log.d(TAG, "Constructor AlarmTask");
        this.context = context;
        this.am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.date = date;
        this.tipoNotificacion = tipo;
        this.id = (int) date.getTimeInMillis();
        this.alumno = alumno;
        Log.d(TAG, "Fin Constructor AlarmTask");
    }


    @Override
    public void run() {
        Log.d(TAG, "Comenzando metodo run()");
        // Request to start are service when the alarm date is upon us
        // We don't start an activity as we just want to pop up a notification into the system bar not a full activity
        Intent intent = new Intent(context, NotifyService.class);
        intent.putExtra(NotifyService.INTENT_NOTIFY, true);
        intent.putExtra("tipoNotificacion", tipoNotificacion);
        //Experimento para los ID, para diferenciar alumnos
        String[] partes = alumno.split(" ");
        String rut = partes[0];
        String nombre = partes[1];
        String[] partesRut = rut.split("-");
        String partesNumericaRut = partesRut[0];
        int rutAnumero = Integer.parseInt(partesNumericaRut);
        Log.d(TAG, "Parte numerica de rut es " + rutAnumero);
        id = id + rutAnumero;
        Log.d(TAG, "La ID de la alarma es " + id);
        intent.putExtra("nombreAlumno", nombre);
        //La ID separa los intent, por lo que si se quiere mas notificaciones se le debe entregar un ID distinto a los demas intent
        PendingIntent pendingIntent = PendingIntent.getService(context, id, intent, 0);

        // Sets an alarm - note this alarm will be lost if the phone is turned off and on again
        am.set(AlarmManager.RTC, date.getTimeInMillis(), pendingIntent);
    }


    public void borrar() {
        Log.d(TAG, "Comenzando metodo borrar()");
        Intent intent = new Intent(context, NotifyService.class);
        //Experimento para los ID, para diferenciar alumnos
        String[] partes = alumno.split(" ");
        String rut = partes[0];
        String nombre = partes[1];
        String[] partesRut = rut.split("-");
        String partesNumericaRut = partesRut[0];
        int rutAnumero = Integer.parseInt(partesNumericaRut);
        Log.d(TAG, "Parte numerica de rut es " + rutAnumero);
        id = id + rutAnumero;
        Log.d(TAG, "La id a borrar es " + id);
        PendingIntent pendingIntent = PendingIntent.getService(context, id, intent, 0);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
        Log.d(TAG, "Alarma borrada??????");
    }

}
