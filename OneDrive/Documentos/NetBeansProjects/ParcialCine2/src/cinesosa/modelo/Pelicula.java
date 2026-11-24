/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.modelo;
import java.io.Serializable;
import java.util.Objects;

public final class Pelicula implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private String titulo;
    private int duracionMinutos;
    private boolean activa;

    public Pelicula(int id, String titulo, int duracionMinutos) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor que cero.");
        }

        this.id = id;
        this.titulo = validarTitulo(titulo);
        this.duracionMinutos = validarDuracion(duracionMinutos);
        this.activa = true;}
    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public boolean isActiva() {
        return activa;
    }

    public void modificarDatos(String titulo, int duracionMinutos) {
        String nuevoTitulo = validarTitulo(titulo);
        int nuevaDuracion = validarDuracion(duracionMinutos);

        this.titulo = nuevoTitulo;
        this.duracionMinutos = nuevaDuracion;
    }

    public void desactivar() {
        this.activa = false;
    }

    private static String validarTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título es obligatorio.");
        }
        return titulo.trim();
    }

    private static int validarDuracion(int duracionMinutos) {
        if (duracionMinutos <= 0) {
            throw new IllegalArgumentException(
                    "La duración debe ser mayor que cero."
            );
        }
        return duracionMinutos;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (objeto == null || getClass() != objeto.getClass()) {
            return false;
        }
        Pelicula otra = (Pelicula) objeto;
        return id == otra.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return titulo + " (" + duracionMinutos + " min)"
                + (activa ? "" : " [inactiva]");
    }

public void activar() {
    this.activa = true;
}}