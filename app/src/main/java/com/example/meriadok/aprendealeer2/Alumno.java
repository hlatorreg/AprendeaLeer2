package com.example.meriadok.aprendealeer2;

/**
 * Created by MeriaDoK on 15-11-2015.
 * Clase para contener los datos del objeto alumno
 */
public class Alumno {
    private String rut;
    private String nombre;
    private int d_cuerpo;
    private int d_cosas;
    private int d_acciones;
    private int num_sesiones;
    private String tipo_dia;

    public Alumno() {

    }

    public String getTipo_dia() {
        return tipo_dia;
    }

    public void setTipo_dia(String tipo_dia) {
        this.tipo_dia = tipo_dia;
    }

    public int getNum_sesiones() {
        return num_sesiones;
    }

    public void setNum_sesiones(int num_sesiones) {
        this.num_sesiones = num_sesiones;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getD_cuerpo() {
        return d_cuerpo;
    }

    public void setD_cuerpo(int d_cuerpo) {
        this.d_cuerpo = d_cuerpo;
    }

    public int getD_cosas() {
        return d_cosas;
    }

    public void setD_cosas(int d_cosas) {
        this.d_cosas = d_cosas;
    }

    public int getD_acciones() {
        return d_acciones;
    }

    public void setD_acciones(int d_acciones) {
        this.d_acciones = d_acciones;
    }
}
