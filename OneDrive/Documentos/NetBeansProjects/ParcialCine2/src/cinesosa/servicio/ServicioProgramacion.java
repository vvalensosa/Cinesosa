/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.servicio;

import cinesosa.modelo.Funcion;
import cinesosa.modelo.Pelicula;
import cinesosa.modelo.Sala;
import cinesosa.persistencia.EstadoCine;
import cinesosa.persistencia.ExcepcionPersistencia;
import cinesosa.persistencia.PersistenciaCine;
import cinesosa.persistencia.Repositorio;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class ServicioProgramacion {
    private final Repositorio<Pelicula, Integer> repositorioPeliculas;
    private final Repositorio<Sala, Integer> repositorioSalas;
    private final Repositorio<Funcion, Integer> repositorioFunciones;
    private final PersistenciaCine persistencia;
    private final EstadoCine estado;
    public ServicioProgramacion(
            Repositorio<Pelicula, Integer> repositorioPeliculas,
            Repositorio<Sala, Integer> repositorioSalas,
            Repositorio<Funcion, Integer> repositorioFunciones,
            PersistenciaCine persistencia,
            EstadoCine estado
    ) {
        this.repositorioPeliculas = repositorioPeliculas;
        this.repositorioSalas = repositorioSalas;
        this.repositorioFunciones = repositorioFunciones;
        this.persistencia = persistencia;
        this.estado = estado;
    }
    public Funcion programarFuncion(
            int peliculaId,
            int salaId,
            LocalDateTime fechaHora,
            double precioBase
    ) throws ExcepcionPersistencia {
        Pelicula pelicula = repositorioPeliculas
                .buscarPorId(peliculaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la película seleccionada."
                ));
        Sala sala = repositorioSalas
                .buscarPorId(salaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la sala seleccionada."
                ));
        if (!pelicula.isActiva()) {
            throw new IllegalArgumentException(
                    "No se puede programar una película desactivada."
            );
        }
        if (fechaHora == null || !fechaHora.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "La función debe tener una fecha y hora futuras."
            );
        }
        Funcion nuevaFuncion = new Funcion(
                obtenerSiguienteId(),
                pelicula,
                sala,
                fechaHora,
                precioBase
        );
        for (Funcion existente : repositorioFunciones.listarTodos()) {
            if (existente.seSuperponeCon(nuevaFuncion)) {
                throw new IllegalArgumentException(
                        "La sala ya tiene una función en ese horario."
                );
            }
        }
        repositorioFunciones.guardar(nuevaFuncion);
        try {
            persistencia.guardar(estado);
        } catch (ExcepcionPersistencia ex) {
            estado.getFunciones().remove(nuevaFuncion);
            throw ex;
        }
        return nuevaFuncion;
    }
    public List<Funcion> listarFunciones() throws ExcepcionPersistencia {
        List<Funcion> resultado = new ArrayList<>(
                repositorioFunciones.listarTodos()
        );
        Collections.sort(
                resultado,
                Comparator.comparing(Funcion::getFechaHora)
        );
        return resultado;
    }
    public List<Funcion> buscarPorPelicula(int peliculaId)
            throws ExcepcionPersistencia {
        List<Funcion> resultado = new ArrayList<>();
        for (Funcion funcion : repositorioFunciones.listarTodos()) {
            if (funcion.getPelicula().getId() == peliculaId) {
                resultado.add(funcion);
            }
        }
        return resultado;
    }
    public List<Funcion> buscarPorFecha(LocalDate fecha)
            throws ExcepcionPersistencia {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }
        List<Funcion> resultado = new ArrayList<>();
        for (Funcion funcion : repositorioFunciones.listarTodos()) {
            if (funcion.getFechaHora().toLocalDate().equals(fecha)) {
                resultado.add(funcion);
            }
        }
        return resultado;
    }
    private int obtenerSiguienteId() throws ExcepcionPersistencia {
        int mayorId = 0;
        for (Funcion funcion : repositorioFunciones.listarTodos()) {
            if (funcion.getId() > mayorId) {
                mayorId = funcion.getId();
            }
        }
        return mayorId + 1;
    }
}