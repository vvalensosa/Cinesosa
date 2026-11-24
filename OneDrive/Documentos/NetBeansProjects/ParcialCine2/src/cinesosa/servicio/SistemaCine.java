/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.servicio;
import cinesosa.modelo.Cliente;
import cinesosa.modelo.Funcion;
import cinesosa.modelo.Pelicula;
import cinesosa.modelo.Reserva;
import cinesosa.modelo.Sala;
import cinesosa.persistencia.EstadoCine;
import cinesosa.persistencia.ExcepcionPersistencia;
import cinesosa.persistencia.ExportadorReservasCSV;
import cinesosa.persistencia.PersistenciaCine;
import cinesosa.persistencia.Repositorio;
import cinesosa.persistencia.RepositorioEnMemoria;
import java.nio.file.Path;
import java.nio.file.Paths;
import cinesosa.persistencia.DatosDemostracion;
public final class SistemaCine {
    private final EstadoCine estado;
    private final PersistenciaCine persistencia;
    private final ExportadorReservasCSV exportadorCSV;
    private final ServicioCatalogo servicioCatalogo;
    private final ServicioProgramacion servicioProgramacion;
    private final ServicioReserva servicioReserva;
    public SistemaCine() throws ExcepcionPersistencia {
        Path archivoDatos = Paths.get("datos", "cine-sosa.dat");
        this.persistencia = new PersistenciaCine(archivoDatos);
        this.exportadorCSV = new ExportadorReservasCSV(archivoDatos);
        this.estado = persistencia.cargar();
        Repositorio<Pelicula, Integer> peliculas =
                new RepositorioEnMemoria<>(
                        estado.getPeliculas(),
                        Pelicula::getId
                );
        Repositorio<Sala, Integer> salas =
                new RepositorioEnMemoria<>(
                        estado.getSalas(),
                        Sala::getId
                );
        Repositorio<Funcion, Integer> funciones =
                new RepositorioEnMemoria<>(
                        estado.getFunciones(),
                        Funcion::getId
                );
        Repositorio<Cliente, String> clientes =
                new RepositorioEnMemoria<>(
                        estado.getClientes(),
                        Cliente::getIdentificacion
                );
        Repositorio<Reserva, String> reservas =
                new RepositorioEnMemoria<>(
                        estado.getReservas(),
                        Reserva::getCodigo
                );
        this.servicioCatalogo = new ServicioCatalogo(
                peliculas,
                salas,
                persistencia,
                estado
        );
        this.servicioProgramacion = new ServicioProgramacion(
                peliculas,
                salas,
                funciones,
                persistencia,
                estado
        );
        this.servicioReserva = new ServicioReserva(
                clientes,
                funciones,
                reservas,
                persistencia,
                estado
        );
        DatosDemostracion.cargarSiEstaVacio(estado, persistencia);
    }
    public ServicioCatalogo getServicioCatalogo() {
        return servicioCatalogo;
    }
    public ServicioProgramacion getServicioProgramacion() {
        return servicioProgramacion;
    }
    public ServicioReserva getServicioReserva() {
        return servicioReserva;
    }
    public void exportarReservasCSV(Path destino)
            throws ExcepcionPersistencia {
        exportadorCSV.exportar(estado, destino);
    }
}