package com.example.meriadok.aprendealeer2;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Created by MeriaDoK on 29-10-2015.
 * Maneja el servicio de notificaciones, el cliente accede a este servicio.
 */
public class NotifyService extends Service {
    /**
     * Class for clients to access
     */
    public class ServiceBinder extends Binder {
        NotifyService getService() {
            return NotifyService.this;
        }
    }

    private DataBase miDB;
    // Unique id to identify the notification.
    private static int NOTIFICATION = (int) System.currentTimeMillis();
    // Name of an intent extra we can use to identify if this service was started to create a notification
    public static String INTENT_NOTIFY = "com.blundell.tut.service.INTENT_NOTIFY";
    // The system notification manager
    private NotificationManager mNM;
    public static int tipoNotificacion;
    public static String newline = System.getProperty("line.separator");

    @Override
    public void onCreate() {
        Log.i("NotifyService", "onCreate()");
        Log.i("NotifiService:", "" + NOTIFICATION);
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        // If this service was started by out AlarmTask intent then we want to show our notification
        if (intent.getBooleanExtra(INTENT_NOTIFY, false)) {
            showNotification(intent.getIntExtra("tipoNotificacion", 0), intent.getStringExtra("nombreAlumno"));
        }
        // We don't care if this service is stopped as we have already delivered our notification
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients
    private final IBinder mBinder = new ServiceBinder();

    /**
     * Creates a notification and shows it in the OS drag-down status bar
     */
    private void showNotification(int tipoNotificacion, String nombre) {
        // This is the 'title' of the notification
        CharSequence title = "Atención: ";
        // This is the icon to use on the notification
        int icon = R.drawable.ic_dialog_alert;
        // This is the scrolling text of the notification
        CharSequence text;
        //Objeto para comunicacion con bd y setear el alumno
        miDB = new DataBase(this);
        SharedPreferences sharedPreferences = this.getSharedPreferences("datosAlumno", Context.MODE_PRIVATE);
        Alumno alumno = new Alumno();
        alumno.setRut(sharedPreferences.getString("rut", "null"));
        Alumno al;
        al = miDB.getDatosAlumno(alumno);
        //metodo para extraer la ultima actividad
        String actividad = miDB.extraerUltimaActividad(al);
        miDB.close();
        //TODO ver el tema de los emails.
        switch (tipoNotificacion) {
            case 1:
                text = nombre + " lleva 1 Dia sin realizar la actividad " + actividad + ", favor resumir lo antes posible.";
                break;
            case 2:
                text = nombre + " lleva 8 Dias sin realizar la actividad " + actividad + ", favor resumir lo antes posible.";
                break;
            case 3:
                text = nombre + " lleva 16 Dias sin realizar la actividad " + actividad + ", favor resumir lo antes posible.";
                break;
            case 4:
                text = nombre + " lleva 24 Dias sin realizar la actividad " + actividad + ", favor resumir lo antes posible.";
                emailNotificacion(nombre, miDB, actividad);
                break;
            default:
                text = "Realizar ultima actividad.";
                break;
        }


        // What time to show on the notification
        long time = System.currentTimeMillis();

        Notification notification = new Notification(icon, text, time);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, title, text, contentIntent);

        // Clear the notification when it is pressed
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Send the notification to the system.
        mNM.notify(NOTIFICATION, notification);

        // Stop the service when we are finished
        stopSelf();
    }

    public void emailNotificacion(String alumno, DataBase dataBase, String actividad){
        Mail mail = new Mail("titok23@gmail.com", "meriadoksink230023");
        String[] toArr = {dataBase.extraerEmail()};
        mail.setTo(toArr);
        mail.setFrom("aprendealeer@aal.cl");
        mail.setSubject("Notificación Aprende a Leer");
        mail.setBody("Estimado usuario," + newline + " " + alumno + " no a realizado la actividad " + actividad + " en mas de 24 dias, se recomienza empezar lo antes posible desde el comienzo."
         + newline + newline + newline + "Este es un correo automatizado, favor no responder." + newline + "Para mas información consulte a hector.latorre23@gmail.com");
        try {
            mail.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
