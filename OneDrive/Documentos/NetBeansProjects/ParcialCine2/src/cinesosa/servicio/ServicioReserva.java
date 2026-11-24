/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.servicio;
import cinesosa.modelo.Butaca;
import cinesosa.modelo.Cliente;
import cinesosa.modelo.Entrada;
import cinesosa.modelo.EstadoReserva;
import cinesosa.modelo.Funcion;
import cinesosa.modelo.Reserva;
import cinesosa.modelo.TipoEnntrada;
import cinesosa.persistencia.EstadoCine;
import cinesosa.persistencia.ExcepcionPersistencia;
import cinesosa.persistencia.PersistenciaCine;
import cinesosa.persistencia.Repositorio;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Collections;
public final class ServicioReserva {
    private final Repositorio<Cliente, String> repositorioClientes;
    private final Repositorio<Funcion, Integer> repositorioFunciones;
    private final Repositorio<Reserva, String> repositorioReservas;
    private final PersistenciaCine persistencia;
    private final EstadoCine estado;
public ServicioReserva(
        Repositorio<Cliente, String> repositorioClientes,
        Repositorio<Funcion, Integer> repositorioFunciones,
        Repositorio<Reserva, String> repositorioReservas,
        PersistenciaCine persistencia,
        EstadoCine estado
) {
    this.repositorioClientes = repositorioClientes;
    this.repositorioFunciones = repositorioFunciones;
    this.repositorioReservas = repositorioReservas;
    this.persistencia = persistencia;
    this.estado = estado;
}
    public Reserva confirmarReserva(
            Cliente datosCliente,
            int funcionId,
            Map<Butaca, TipoEnntrada> tipoPorButaca
    ) throws ExcepcionPersistencia {
        if (datosCliente == null) {
            throw new IllegalArgumentException("Los datos del cliente son obligatorios.");
        }
        if (tipoPorButaca == null || tipoPorButaca.isEmpty()) {
            throw new IllegalArgumentException(
                    "Seleccioná al menos una butaca."
            );
        }
        Funcion funcion = repositorioFunciones
                .buscarPorId(funcionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la función seleccionada."
                ));
        if (!funcion.getFechaHora().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "No se puede reservar una función que ya comenzó."
            );
        }
        List<Entrada> entradas = new ArrayList<>();
        for (Map.Entry<Butaca, TipoEnntrada> seleccion : tipoPorButaca.entrySet()) {
            Butaca butaca = seleccion.getKey();
            TipoEnntrada tipo = seleccion.getValue();
            if (butaca == null || tipo == null) {
                throw new IllegalArgumentException(
                        "Cada butaca debe tener un tipo de entrada."
                );
            }
            if (!funcion.getSala().contieneButaca(butaca)) {
                throw new IllegalArgumentException(
                        "La butaca seleccionada no pertenece a esta sala."
                );
            }
            if (estaOcupada(funcion, butaca)) {
                throw new IllegalArgumentException(
                        "La butaca " + butaca + " ya está ocupada."
                );
            }
            entradas.add(new Entrada(
                    butaca,
                    tipo,
                    funcion.getPrecioBase()
            ));
        }
        Cliente cliente = repositorioClientes
                .buscarPorId(datosCliente.getIdentificacion())
                .orElse(datosCliente);
        boolean clienteNuevo = !repositorioClientes
                .existePorId(cliente.getIdentificacion());
        String codigo = "RSV-" + UUID.randomUUID().toString();
        Reserva reserva = new Reserva(
                codigo,
                cliente,
                funcion,
                LocalDateTime.now(),
                entradas
        );
        try {
            if (clienteNuevo) {
                repositorioClientes.guardar(cliente);
            }
            repositorioReservas.guardar(reserva);
            persistencia.guardar(estado);
        } catch (ExcepcionPersistencia ex) {
            estado.getReservas().remove(reserva);
            if (clienteNuevo) {
                estado.getClientes().remove(cliente);
            }
            throw ex;
        }
        return reserva;
    }
    private boolean estaOcupada(Funcion funcion, Butaca butaca) throws ExcepcionPersistencia {
        for (Reserva reserva : repositorioReservas.listarTodos()) {
            if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
                continue;
            }
            if (!reserva.getFuncion().equals(funcion)) {
                continue;
            }
            for (Entrada entrada : reserva.getEntradas()) {
                if (entrada.getButaca().equals(butaca)) {
                    return true;
                }
            }
        }
        return false;
    }
public void cancelarReserva(String codigo)
        throws ExcepcionPersistencia {
    Reserva reserva = repositorioReservas
            .buscarPorId(codigo)
            .orElseThrow(() -> new IllegalArgumentException(
                    "No existe una reserva con ese código."
            ));

    if (reserva.getEstado() == EstadoReserva.CANCELADA) {
        throw new IllegalArgumentException(
                "La reserva ya está cancelada."
        );
    }
    EstadoCine estadoCancelado = crearCopiaConReservaCancelada(reserva);
    // Si el guardado falla, la reserva actual sigue confirmada.
    persistencia.guardar(estadoCancelado);
    // Actualizamos el estado en memoria solo después de guardar.
    reserva.cancelar();
}
private EstadoCine crearCopiaConReservaCancelada(Reserva reservaObjetivo) {
    EstadoCine copia = new EstadoCine();
    copia.getPeliculas().addAll(estado.getPeliculas());
    copia.getSalas().addAll(estado.getSalas());
    copia.getFunciones().addAll(estado.getFunciones());
    copia.getClientes().addAll(estado.getClientes());
    for (Reserva reserva : estado.getReservas()) {
        if (reserva.equals(reservaObjetivo)) {
            Reserva reservaCancelada = new Reserva(
                    reserva.getCodigo(),
                    reserva.getCliente(),
                    reserva.getFuncion(),
                    reserva.getFechaReserva(),
                    reserva.getEntradas()
            );
            reservaCancelada.cancelar();
            copia.getReservas().add(reservaCancelada);
        } else {
            copia.getReservas().add(reserva);
        }
    }
    return copia;
}
public List<Butaca> listarButacasOcupadas(int funcionId)
        throws ExcepcionPersistencia {

    Funcion funcion = repositorioFunciones
            .buscarPorId(funcionId)
            .orElseThrow(() -> new IllegalArgumentException(
                    "No existe la función seleccionada."
            ));

    List<Butaca> ocupadas = new ArrayList<>();

    for (Reserva reserva : repositorioReservas.listarTodos()) {
        if (reserva.getEstado() != EstadoReserva.CONFIRMADA
                || !reserva.getFuncion().equals(funcion)) {
            continue;
        }

        for (Entrada entrada : reserva.getEntradas()) {
            if (!ocupadas.contains(entrada.getButaca())) {
                ocupadas.add(entrada.getButaca());
            }
        }
    }

    return Collections.unmodifiableList(ocupadas);
}
public Reserva buscarPorCodigo(String codigo)
        throws ExcepcionPersistencia {

    if (codigo == null || codigo.trim().isEmpty()) {
        throw new IllegalArgumentException(
                "Ingresá un código de reserva."
        );
    }

    return repositorioReservas
            .buscarPorId(codigo.trim())
            .orElseThrow(() -> new IllegalArgumentException(
                    "No se encontró una reserva con ese código."
            ));
}

public List<Reserva> buscarPorIdentificacion(String identificacion)
        throws ExcepcionPersistencia {

    if (identificacion == null || identificacion.trim().isEmpty()) {
        throw new IllegalArgumentException(
                "Ingresá la identificación del cliente."
        );
    }

    List<Reserva> resultado = new ArrayList<>();

    for (Reserva reserva : repositorioReservas.listarTodos()) {
        if (reserva.getCliente()
                .getIdentificacion()
                .equalsIgnoreCase(identificacion.trim())) {
            resultado.add(reserva);
        }
    }

    return resultado;
}}