/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.modelo;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class Reserva implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String codigo;
    private final Cliente cliente;
    private final Funcion funcion;
    private final LocalDateTime fechaReserva;
    private final List<Entrada> entradas;
    private final double importeTotal;
    private EstadoReserva estado;

    public Reserva(
            String codigo,
            Cliente cliente,
            Funcion funcion,
            LocalDateTime fechaReserva,
            List<Entrada> entradas
    ) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de reserva es obligatorio.");
        }
        if (cliente == null) {
            throw new IllegalArgumentException("La reserva debe tener un cliente.");
        }
        if (funcion == null) {
            throw new IllegalArgumentException("La reserva debe tener una función.");
        }
        if (fechaReserva == null) {
            throw new IllegalArgumentException("La fecha de reserva es obligatoria.");
        }
        if (entradas == null || entradas.isEmpty()) {
            throw new IllegalArgumentException(
                    "La reserva debe incluir al menos una entrada."
            );
        }

        List<Entrada> copiaEntradas = new ArrayList<>(entradas);
        Set<Butaca> butacasReservadas = new HashSet<>();
        double total = 0.0;

        for (Entrada entrada : copiaEntradas) {
            if (entrada == null) {
                throw new IllegalArgumentException(
                        "La reserva no puede contener entradas vacías."
                );
            }
            if (!funcion.getSala().contieneButaca(entrada.getButaca())) {
                throw new IllegalArgumentException(
                        "Una butaca no pertenece a la sala de la función."
                );
            }
            if (!butacasReservadas.add(entrada.getButaca())) {
                throw new IllegalArgumentException(
                        "No se puede incluir dos veces la misma butaca."
                );
            }

            total += entrada.getPrecioCobrado();
        }

        this.codigo = codigo.trim();
        this.cliente = cliente;
        this.funcion = funcion;
        this.fechaReserva = fechaReserva;
        this.entradas = copiaEntradas;
        this.importeTotal = Math.round(total * 100.0) / 100.0;
        this.estado = EstadoReserva.CONFIRMADA;
    }

    public String getCodigo() {
        return codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public List<Entrada> getEntradas() {
        return Collections.unmodifiableList(entradas);
    }

    public double getImporteTotal() {
        return importeTotal;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void cancelar() {
        if (estado == EstadoReserva.CANCELADA) {
            throw new IllegalStateException("La reserva ya está cancelada.");
        }
        estado = EstadoReserva.CANCELADA;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (objeto == null || getClass() != objeto.getClass()) {
            return false;
        }
        Reserva otra = (Reserva) objeto;
        return codigo.equals(otra.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
