/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.servicio;

import cinesosa.modelo.Pelicula;
import cinesosa.modelo.Sala;
import cinesosa.persistencia.EstadoCine;
import cinesosa.persistencia.ExcepcionPersistencia;
import cinesosa.persistencia.PersistenciaCine;
import cinesosa.persistencia.Repositorio;
import java.util.List;
public final class ServicioCatalogo {
    private final Repositorio<Pelicula, Integer> repositorioPeliculas;
    private final Repositorio<Sala, Integer> repositorioSalas;
    private final PersistenciaCine persistencia;
    private final EstadoCine estado;
    public ServicioCatalogo(
            Repositorio<Pelicula, Integer> repositorioPeliculas,
            Repositorio<Sala, Integer> repositorioSalas,
            PersistenciaCine persistencia,
            EstadoCine estado
    ) {
        this.repositorioPeliculas = repositorioPeliculas;
        this.repositorioSalas = repositorioSalas;
        this.persistencia = persistencia;
        this.estado = estado;
    }
    public Pelicula registrarPelicula(String titulo, int duracionMinutos)
            throws ExcepcionPersistencia {
        Pelicula pelicula = new Pelicula(
                siguienteIdPelicula(),
                titulo,
                duracionMinutos
        );
        repositorioPeliculas.guardar(pelicula);
        try {
            persistencia.guardar(estado);
        } catch (ExcepcionPersistencia ex) {
            estado.getPeliculas().remove(pelicula);
            throw ex;
        }
        return pelicula;
    }
    public void modificarPelicula(
            int id,
            String nuevoTitulo,
            int nuevaDuracion
    ) throws ExcepcionPersistencia {
        Pelicula pelicula = repositorioPeliculas
                .buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la película seleccionada."
                ));
        String tituloAnterior = pelicula.getTitulo();
        int duracionAnterior = pelicula.getDuracionMinutos();

        pelicula.modificarDatos(nuevoTitulo, nuevaDuracion);

        try {
            persistencia.guardar(estado);
        } catch (ExcepcionPersistencia ex) {
            pelicula.modificarDatos(tituloAnterior, duracionAnterior);
            throw ex;
        }
    }
    public void desactivarPelicula(int id) throws ExcepcionPersistencia {
        Pelicula pelicula = repositorioPeliculas
                .buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la película seleccionada."
                ));
        if (!pelicula.isActiva()) {
            throw new IllegalArgumentException(
                    "La película ya está desactivada."
            );
        }
        pelicula.desactivar();
        try {
            persistencia.guardar(estado);
        } catch (ExcepcionPersistencia ex) {
            pelicula.activar();
            throw ex;
        }
    }
    public List<Pelicula> listarPeliculas() throws ExcepcionPersistencia {
        return repositorioPeliculas.listarTodos();
    }
    public Sala registrarSala(
            String nombre,
            int cantidadFilas,
            int butacasPorFila
    ) throws ExcepcionPersistencia {
        Sala sala = new Sala(
                siguienteIdSala(),
                nombre,
                cantidadFilas,
                butacasPorFila
        );
        repositorioSalas.guardar(sala);
        try {
            persistencia.guardar(estado);
        } catch (ExcepcionPersistencia ex) {
            estado.getSalas().remove(sala);
            throw ex;
        }
        return sala;
    }
    public List<Sala> listarSalas() throws ExcepcionPersistencia {
        return repositorioSalas.listarTodos();
    }
    private int siguienteIdPelicula() throws ExcepcionPersistencia {
        int mayorId = 0;
        for (Pelicula pelicula : repositorioPeliculas.listarTodos()) {
            if (pelicula.getId() > mayorId) {
                mayorId = pelicula.getId();
            }
        }
        return mayorId + 1;
    }
    private int siguienteIdSala() throws ExcepcionPersistencia {
        int mayorId = 0;
        for (Sala sala : repositorioSalas.listarTodos()) {
            if (sala.getId() > mayorId) {
                mayorId = sala.getId();
            }
        }
        return mayorId + 1;
    }
}
