package com.example.meriadok.aprendealeer2;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MeriaDoK on 20-10-2015.
 * Clase que ayuda al manejo de la base de datos.
 * Crea, extrae, busca, updatea.
 */

public class DataBase extends SQLiteOpenHelper {

    public static final String NOMBRE_DB = "dbaal.db";
    public static final String NOMBRE_TABLA_TUTOR = "tabla_tutor";
    public static final String TUTOR_COL_1 = "EMAIL";
    public static final String NOMBRE_TABLA_ALUMNO = "tabla_alumno";
    public static final String ALUMNO_COL_1 = "RUT";
    public static final String ALUMNO_COL_2 = "NOMBRE";
    public static final String ALUMNO_COL_3 = "D_CUERPO";
    public static final String ALUMNO_COL_4 = "D_COSAS";
    public static final String ALUMNO_COL_5 = "D_ACCIONES";
    public static final String ALUMNO_COL_6 = "NUMERO_SESIONES";
    public static final String ALUMNO_COL_7 = "TIPO_DIA";
    public static final int DEFAULT = 0;
    public static final String NOMBRE_TABLA_SESION = "tabla_sesion";
    public static final String SESION_COL_1 = "RUT";
    public static final String SESION_COL_2 = "ACTIVIDAD";
    public static final String SESION_COL_3 = "FECHA";
    public static final String NOMBRE_TABLA_LOGROS = "tabla_logros";
    public static final String LOGROS_COL_1 = "RUT_ALUMNO";
    public static final String LOGROS_COL_2 = "P_PADRES";
    public static final String LOGROS_COL_3 = "P_CUERPO";
    public static final String LOGROS_COL_4 = "P_COSAS";
    public static final String LOGROS_COL_5 = "P_ACCIONES";
    private static final String TAG = DataBase.class.getSimpleName();

    /**
     * Constructor de la clase
     *
     * @param context Contexto de la actividad
     */
    public DataBase(Context context) {
        super(context, NOMBRE_DB, null, 1);
        //SQLiteDatabase db = this.getWritableDatabase();
    }

    /**
     * Override de metodo onCreate
     *
     * @param sqLiteDatabase objeto para manejar una base de datos sqlite
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + NOMBRE_TABLA_TUTOR + " (" + TUTOR_COL_1 + " TEXT PRIMARY KEY)");
        sqLiteDatabase.execSQL("create table " + NOMBRE_TABLA_ALUMNO + "(" + ALUMNO_COL_1 +
                " TEXT PRIMARY KEY, " + ALUMNO_COL_2 + " TEXT, " + ALUMNO_COL_3 + " INTEGER, " + ALUMNO_COL_4 + " INTEGER, " +
                ALUMNO_COL_5 + " INTEGER, " + ALUMNO_COL_6 + " INTEGER, " + ALUMNO_COL_7 + " TEXT)");
        sqLiteDatabase.execSQL("create table " + NOMBRE_TABLA_SESION + " (" + SESION_COL_1 + " TEXT PRIMARY KEY, " +
                SESION_COL_2 + " TEXT, " + SESION_COL_3 + " TEXT)");
        sqLiteDatabase.execSQL("create table " + NOMBRE_TABLA_LOGROS + " (" + LOGROS_COL_1 + " TEXT PRIMARY KEY, " +
                LOGROS_COL_2 + " INTEGER, " + LOGROS_COL_3 + " INTEGER, " + LOGROS_COL_4 + " INTEGER, " + LOGROS_COL_5 + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + NOMBRE_TABLA_ALUMNO);
        sqLiteDatabase.execSQL("drop table if exists " + NOMBRE_TABLA_TUTOR);
        sqLiteDatabase.execSQL("drop table if exists " + NOMBRE_TABLA_SESION);
        sqLiteDatabase.execSQL("drop table if exists " + NOMBRE_TABLA_LOGROS);
        onCreate(sqLiteDatabase);
    }

    /**
     * Metodo para insertar un nuevo email a la base de datos
     *
     * @param mail String que contiene el email
     * @return booleano que indica si se inserto con exito el mail
     */
    public boolean insertMail(String mail) {
        SQLiteDatabase db = this.getWritableDatabase(); //objeto para escribir a la base de datos sqlite
        ContentValues contentValues = new ContentValues(); //objeto que maneja los contenidos a ingresar
        contentValues.put(TUTOR_COL_1, mail); //ingresamos el nombre de la columna y el valor que ingresaremos en esa columna
        long resultado = db.insert(NOMBRE_TABLA_TUTOR, null, contentValues);
        db.close();
        return resultado != -1;
    }

    /**
     * Metodo para insertar la fecha en que se realizo la ultima actividad, junto con la ultima actividad
     *
     * @param fecha     string con la fecha en formato "dd-MM-yyyy HH:mm"
     * @param actividad string con el nombre de la ultima actividad realizada
     */
    public void insertFechaUltimaActividad(String fecha, String actividad, Alumno alumno) {
        SQLiteDatabase db = this.getWritableDatabase();
        /*
        ContentValues contentValues = new ContentValues();
        contentValues.put(SESION_COL_1, alumno.getRut());
        contentValues.put(SESION_COL_2, actividad);
        contentValues.put(SESION_COL_3, fecha);
        //vaciarTablaSesion();
        long resultado = db.insert(NOMBRE_TABLA_SESION, null, contentValues);
        db.close();
        return resultado != -1;
        */
        String query = "INSERT OR REPLACE INTO " + NOMBRE_TABLA_SESION + " (" + SESION_COL_1 + ", " + SESION_COL_2 + ", " + SESION_COL_3 + ") VALUES ('" + alumno.getRut() + "', '" + actividad + "', '" + fecha + "')";
        Log.d(TAG, query);
        db.execSQL(query);
        db.close();
    }

    /**
     * Metodo para sacar la ultima actividad que se realizo desde la base de datos
     *
     * @return ultima actividad realizada
     */
    public String extraerUltimaActividad(Alumno alumno) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select actividad from " + NOMBRE_TABLA_SESION + " WHERE " + SESION_COL_1 + "='" + alumno.getRut() + "'";
        try {
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            String resultado = cursor.getString(0);
            cursor.close();
            db.close();
            Log.d(TAG, "La ultima actividad del alumno=" + alumno.getRut() + " es " + resultado);
            return resultado;
        } catch (Exception e) {
            Log.d(TAG, "No existe actividad anterior");
            return "No existe actividad anterior";
        }
    }

    public String extraerFechaUltimaActividad(Alumno alumno) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select fecha from " + NOMBRE_TABLA_SESION + " where " + SESION_COL_1 + "='" + alumno.getRut() + "'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            String resultado = cursor.getString(0);
            cursor.close();
            db.close();
            return resultado;
        } else {
            return "default_date";
        }
    }

    public void vaciarTablaSesion() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NOMBRE_TABLA_SESION, null, null);
        Log.d(TAG, "Tabla sesion reseteada");
    }

    public boolean insertarAlumno(Alumno alumno) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        ContentValues contentValuesP = new ContentValues();
        contentValues.put(ALUMNO_COL_1, alumno.getRut());
        contentValues.put(ALUMNO_COL_2, alumno.getNombre());
        contentValues.put(ALUMNO_COL_3, DEFAULT);
        contentValues.put(ALUMNO_COL_4, DEFAULT);
        contentValues.put(ALUMNO_COL_5, DEFAULT);
        contentValues.put(ALUMNO_COL_6, DEFAULT);
        contentValues.put(ALUMNO_COL_7, "par"); //TODO cambiar a impar para funcionamento por defecto
        contentValuesP.put(LOGROS_COL_1, alumno.getRut());
        contentValuesP.put(LOGROS_COL_2, DEFAULT);
        contentValuesP.put(LOGROS_COL_3, DEFAULT);
        contentValuesP.put(LOGROS_COL_4, DEFAULT);
        contentValuesP.put(LOGROS_COL_5, DEFAULT);
        long resultado = db.insert(NOMBRE_TABLA_ALUMNO, null, contentValues);
        long resultadoP = db.insert(NOMBRE_TABLA_LOGROS, null, contentValuesP);
        db.close();
        Log.d(TAG, "Insertando alumno en la BD");
        return (resultado != -1) && (resultadoP != -1);
    }

    public int modificarDesbloqueo(Alumno alumno, String tipo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String nombre_columna;
        switch (tipo) {
            case "cuerpo":
                nombre_columna = ALUMNO_COL_3;
                break;
            case "cosas":
                nombre_columna = ALUMNO_COL_4;
                break;
            case "acciones":
                nombre_columna = ALUMNO_COL_5;
                break;
            default:
                return -1;
        }
        String query = "UPDATE " + NOMBRE_TABLA_ALUMNO + " SET  " + nombre_columna + "=1 WHERE " + ALUMNO_COL_1 + "='" + alumno.getRut() + "'";
        Log.d(TAG, query);
        db.execSQL(query);
        db.close();
        return 1;
    }

    public boolean existeAlumno(Alumno alumno) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + ALUMNO_COL_2 + " FROM " + NOMBRE_TABLA_ALUMNO + " WHERE " + ALUMNO_COL_1 + "='" + alumno.getRut() + "'";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        String nombre = cursor.getString(0);
        cursor.close();
        db.close();

        return nombre.equals(alumno.getNombre());
    }


    public Alumno getDatosAlumno(Alumno alumno) {
        Log.d(TAG, "Comenzando a sacar datos del alumno desde la BD");
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + ALUMNO_COL_2 + ", " + ALUMNO_COL_3 + ", " + ALUMNO_COL_4 + ", " + ALUMNO_COL_5 + ", " + ALUMNO_COL_6 + ", " + ALUMNO_COL_7 + " FROM " + NOMBRE_TABLA_ALUMNO + " WHERE " + ALUMNO_COL_1 + "='" + alumno.getRut() + "'";
        Log.d(TAG, query);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        String nombre = cursor.getString(0);
        int d_cuerpo = cursor.getInt(1);
        int d_cosas = cursor.getInt(2);
        int d_acciones = cursor.getInt(3);
        int num_sesiones = cursor.getInt(4);
        String tipo_dia = cursor.getString(5);
        cursor.close();
        db.close();

        Alumno al = new Alumno();
        al.setRut(alumno.getRut());
        al.setNombre(nombre);
        al.setD_cuerpo(d_cuerpo);
        al.setD_cosas(d_cosas);
        al.setD_acciones(d_acciones);
        al.setNum_sesiones(num_sesiones);
        al.setTipo_dia(tipo_dia);

        Log.d(TAG, "Terminando de sacar los datos del alumno desde la BD");
        return al;
    }

    public List<Alumno> listaAlumnos() {
        Log.d(TAG, "Sacando la lista de alumnos");
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + ALUMNO_COL_1 + " , " + ALUMNO_COL_2 + " FROM " + NOMBRE_TABLA_ALUMNO;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        List<Alumno> lista = new ArrayList<>();
        do {
            Alumno al = new Alumno();
            al.setRut(cursor.getString(0));
            al.setNombre(cursor.getString(1));
            lista.add(al);
        } while (cursor.moveToNext());
        db.close();
        cursor.close();
        return lista;
    }

    public void resetSesiones(Alumno alumno) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + NOMBRE_TABLA_ALUMNO + " SET " + ALUMNO_COL_6 + "=0 " + "WHERE " + ALUMNO_COL_1 + "='" + alumno.getRut() + "'";
        Log.d(TAG, query);
        db.execSQL(query);
        Log.d(TAG, "Sesiones de " + alumno.getRut() + " reseteadas");
        db.close();
    }

    public void sumarSesiones(Alumno alumno) {
        SQLiteDatabase db = this.getWritableDatabase();
        int sesiones = alumno.getNum_sesiones() + 1;
        String query = "UPDATE " + NOMBRE_TABLA_ALUMNO + " SET " + ALUMNO_COL_6 + "=" + sesiones + " WHERE " + ALUMNO_COL_1 + "='" + alumno.getRut() + "'";
        Log.d(TAG, query);
        db.execSQL(query);
        Log.d(TAG, "Sesiones de " + alumno.getRut() + " aumentadas");
        db.close();
    }

    public void cambiarTipoDia(Alumno alumno) {
        SQLiteDatabase db = this.getWritableDatabase();
        String tipo;
        if (alumno.getTipo_dia().equals("impar"))
            tipo = "par";
        else
            tipo = "impar";

        String query = "UPDATE " + NOMBRE_TABLA_ALUMNO + " SET " + ALUMNO_COL_7 + "='" + tipo + "' WHERE " + ALUMNO_COL_1 + "='" + alumno.getRut() + "'";
        Log.d(TAG, query);
        db.execSQL(query);
        Log.d(TAG, "Tipo de dia cambiado a " + tipo);
        db.close();
    }

    public String extraerEmail(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select " + TUTOR_COL_1 + " from " + NOMBRE_TABLA_TUTOR;
        Cursor cursor= db.rawQuery(query, null);
        cursor.moveToFirst();
        String email = cursor.getString(0);
        db.close();
        return email;
    }

    public void cambiarEmail(String email){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "update " + NOMBRE_TABLA_TUTOR + " SET " + TUTOR_COL_1 + "='" + email + "'";
        Log.d(TAG, query);
        db.execSQL(query);
        Log.d(TAG, "Email de tutor cambiado a " + email);
        db.close();
    }

    public boolean existeEmail(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from " + NOMBRE_TABLA_TUTOR;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

}
