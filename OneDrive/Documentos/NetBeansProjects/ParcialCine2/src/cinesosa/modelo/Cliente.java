/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.modelo;
import java.io.Serializable;
import java.util.Objects;

public final class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String identificacion;
    private String nombre;
    private String telefono;

    public Cliente(String identificacion, String nombre, String telefono) {
        this.identificacion = validarIdentificacion(identificacion);
        this.nombre = validarNombre(nombre);
        this.telefono = normalizarTelefono(telefono);
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void modificarDatos(String nombre, String telefono) {
        String nuevoNombre = validarNombre(nombre);
        String nuevoTelefono = normalizarTelefono(telefono);

        this.nombre = nuevoNombre;
        this.telefono = nuevoTelefono;
    }

    private static String validarIdentificacion(String identificacion) {
        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La identificación del cliente es obligatoria."
            );
        }
        return identificacion.trim();
    }

    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre del cliente es obligatorio."
            );
        }
        return nombre.trim();
    }

    private static String normalizarTelefono(String telefono) {
        if (telefono == null) {
            return "";
        }
        return telefono.trim();
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (objeto == null || getClass() != objeto.getClass()) {
            return false;
        }
        Cliente otro = (Cliente) objeto;
        return identificacion.equals(otro.identificacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificacion);
    }

    @Override
    public String toString() {
        return nombre + " (" + identificacion + ")";
    }
}
