/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.persistencia;
import cinesosa.modelo.Cliente;
import cinesosa.modelo.Funcion;
import cinesosa.modelo.Pelicula;
import cinesosa.modelo.Reserva;
import cinesosa.modelo.Sala;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public final class EstadoCine implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Pelicula> peliculas;
    private final List<Sala> salas;
    private final List<Funcion> funciones;
    private final List<Cliente> clientes;
    private final List<Reserva> reservas;
    public EstadoCine() {
        this.peliculas = new ArrayList<>();
        this.salas = new ArrayList<>();
        this.funciones = new ArrayList<>();
        this.clientes = new ArrayList<>();
        this.reservas = new ArrayList<>();
    }
    public List<Pelicula> getPeliculas() {
        return peliculas;
    }
    public List<Sala> getSalas() {
        return salas;
    }
    public List<Funcion> getFunciones() {
        return funciones;
    }
    public List<Cliente> getClientes() {
        return clientes;
    }
    public List<Reserva> getReservas() {
        return reservas;
    }
}
