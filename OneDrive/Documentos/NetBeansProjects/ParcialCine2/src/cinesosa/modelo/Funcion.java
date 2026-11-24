/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.modelo;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public final class Funcion implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private final Pelicula pelicula;
    private final Sala sala;
    private final LocalDateTime fechaHora;
    private final double precioBase;

    public Funcion(
            int id,
            Pelicula pelicula,
            Sala sala,
            LocalDateTime fechaHora,
            double precioBase
    ) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor que cero.");
        }
        if (pelicula == null) {
            throw new IllegalArgumentException("La función debe tener una película.");
        }
        if (sala == null) {
            throw new IllegalArgumentException("La función debe tener una sala.");
        }
        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha y hora son obligatorias.");
        }
        if (precioBase <= 0 || Double.isNaN(precioBase)
                || Double.isInfinite(precioBase)) {
            throw new IllegalArgumentException(
                    "El precio debe ser un número mayor que cero."
            );
        }

        this.id = id;
        this.pelicula = pelicula;
        this.sala = sala;
        this.fechaHora = fechaHora;
        this.precioBase = precioBase;
    }

    public int getId() {
        return id;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public Sala getSala() {
        return sala;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public LocalDateTime calcularFechaHoraFin() {
        return fechaHora.plusMinutes(pelicula.getDuracionMinutos());
    }

    public boolean seSuperponeCon(Funcion otra) {
        if (otra == null || !sala.equals(otra.sala)) {
            return false;
        }

        return fechaHora.isBefore(otra.calcularFechaHoraFin())
                && otra.fechaHora.isBefore(calcularFechaHoraFin());
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (objeto == null || getClass() != objeto.getClass()) {
            return false;
        }
        Funcion otra = (Funcion) objeto;
        return id == otra.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
