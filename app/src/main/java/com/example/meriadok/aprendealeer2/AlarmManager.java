package com.example.meriadok.aprendealeer2;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

/**
 * Created by MeriaDoK on 03-11-2015.
 * Clase para setear las alarmas.
 */
public class AlarmManager {

    private Context context;
    private DataBase dataBase;

    public AlarmManager(Context context) {
        this.context = context;
    }

    public void setearAlarmas(ScheduleClient scheduleClient1,
                              ScheduleClient scheduleClient2, ScheduleClient scheduleClient3,
                              ScheduleClient scheduleClient4, String alumno) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("fechas", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        Calendar aux = Calendar.getInstance();


        System.out.println("Fechas antiguas para alarmas");
        System.out.println(sharedPreferences.getLong("fecha1", -1));
        System.out.println(sharedPreferences.getLong("fecha2", -1));
        System.out.println(sharedPreferences.getLong("fecha3", -1));
        System.out.println(sharedPreferences.getLong("fecha4", -1));

        System.out.println("Iniciando deleteAlarmForNotification con los siguientes objetos Calendar");
        aux.setTimeInMillis(sharedPreferences.getLong("fecha1", -1));
        scheduleClient1.deleteAlarmForNotification(aux, 1, alumno);
        System.out.println(aux.getTimeInMillis());
        aux.setTimeInMillis(sharedPreferences.getLong("fecha2", -1));
        scheduleClient2.deleteAlarmForNotification(aux, 2, alumno);
        System.out.println(aux.getTimeInMillis());
        aux.setTimeInMillis(sharedPreferences.getLong("fecha3", -1));
        scheduleClient3.deleteAlarmForNotification(aux, 3, alumno);
        System.out.println(aux.getTimeInMillis());
        aux.setTimeInMillis(sharedPreferences.getLong("fecha4", -1));
        scheduleClient4.deleteAlarmForNotification(aux, 4, alumno);
        System.out.println(aux.getTimeInMillis());

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        Calendar c3 = Calendar.getInstance();
        Calendar c4 = Calendar.getInstance();

        c1.set(Calendar.MINUTE, c1.get(Calendar.MINUTE) + 1);
        c2.set(Calendar.MINUTE, c2.get(Calendar.MINUTE) + 2);
        c3.set(Calendar.MINUTE, c3.get(Calendar.MINUTE) + 3);
        c4.set(Calendar.MINUTE, c4.get(Calendar.MINUTE) + 4);

        editor.putLong("fecha1", c1.getTimeInMillis());
        editor.putLong("fecha2", c2.getTimeInMillis());
        editor.putLong("fecha3", c3.getTimeInMillis());
        editor.putLong("fecha4", c4.getTimeInMillis());
        editor.apply();


        System.out.println("Nuevas fechas para alarmas");
        System.out.println(c1.getTimeInMillis());
        System.out.println(c2.getTimeInMillis());
        System.out.println(c3.getTimeInMillis());
        System.out.println(c4.getTimeInMillis());

        scheduleClient1.setAlarmForNotification(c1, 1, alumno);
        scheduleClient2.setAlarmForNotification(c2, 2, alumno);
        scheduleClient3.setAlarmForNotification(c3, 3, alumno);
        scheduleClient4.setAlarmForNotification(c4, 4, alumno);
    }

}
