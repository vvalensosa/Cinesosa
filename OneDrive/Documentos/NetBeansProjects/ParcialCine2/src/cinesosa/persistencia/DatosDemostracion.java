/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.persistencia;

import cinesosa.modelo.Butaca;
import cinesosa.modelo.Cliente;
import cinesosa.modelo.Entrada;
import cinesosa.modelo.EntradaEstudiante;
import cinesosa.modelo.EntradaGeneral;
import cinesosa.modelo.Funcion;
import cinesosa.modelo.Pelicula;
import cinesosa.modelo.Reserva;
import cinesosa.modelo.Sala;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

public final class DatosDemostracion {
    private DatosDemostracion() {
    }
    public static void cargarSiEstaVacio(
            EstadoCine estado,
            PersistenciaCine persistencia
    ) throws ExcepcionPersistencia {
        if (!estaVacio(estado)) {
            return;
        }
        Pelicula pelicula1 = new Pelicula(1, "La aventura espacial", 120);
        Pelicula pelicula2 = new Pelicula(2, "Misterio en la noche", 100);
        Pelicula pelicula3 = new Pelicula(3, "Viaje al futuro", 110);
        Sala sala1 = new Sala(1, "Sala 1", 8, 10);
        Sala sala2 = new Sala(2, "Sala 2", 6, 8);
        LocalDate manana = LocalDate.now().plusDays(1);
        Funcion funcion1 = new Funcion(
                1, pelicula1, sala1,
                LocalDateTime.of(manana, LocalTime.of(14, 0)), 5000
        );
        Funcion funcion2 = new Funcion(
                2, pelicula2, sala1,
                LocalDateTime.of(manana, LocalTime.of(17, 0)), 4500
        );
        Funcion funcion3 = new Funcion(
                3, pelicula3, sala1,
                LocalDateTime.of(manana, LocalTime.of(20, 0)), 5000
        );
        Funcion funcion4 = new Funcion(
                4, pelicula2, sala2,
                LocalDateTime.of(manana, LocalTime.of(14, 30)), 4500
        );
        Funcion funcion5 = new Funcion(
                5, pelicula3, sala2,
                LocalDateTime.of(manana, LocalTime.of(17, 30)), 5000
        );
        Funcion funcion6 = new Funcion(
                6, pelicula1, sala2,
                LocalDateTime.of(manana, LocalTime.of(20, 30)), 5000
        );
        Cliente cliente1 = new Cliente("12345678", "Cliente de prueba 1", "");
        Cliente cliente2 = new Cliente("87654321", "Cliente de prueba 2", "");
        Entrada entrada1 = new Entrada(
                new Butaca("A", 1), new EntradaGeneral(), funcion1.getPrecioBase()
        );
        Entrada entrada2 = new Entrada(
                new Butaca("A", 2), new EntradaEstudiante(), funcion1.getPrecioBase()
        );
        Reserva reserva1 = new Reserva(
                "RSV-DEMO-001",
                cliente1,
                funcion1,
                LocalDateTime.now(),
                Arrays.asList(entrada1, entrada2)
        );
        Entrada entrada3 = new Entrada(
                new Butaca("B", 3), new EntradaEstudiante(), funcion4.getPrecioBase()
        );
        Reserva reserva2 = new Reserva(
                "RSV-DEMO-002",
                cliente2,
                funcion4,
                LocalDateTime.now(),
                Arrays.asList(entrada3)
        );
        estado.getPeliculas().addAll(Arrays.asList(
                pelicula1, pelicula2, pelicula3
        ));
        estado.getSalas().addAll(Arrays.asList(sala1, sala2));
        estado.getFunciones().addAll(Arrays.asList(
                funcion1, funcion2, funcion3,
                funcion4, funcion5, funcion6
        ));
        estado.getClientes().addAll(Arrays.asList(cliente1, cliente2));
        estado.getReservas().addAll(Arrays.asList(reserva1, reserva2));

        persistencia.guardar(estado);
    }
    private static boolean estaVacio(EstadoCine estado) {
        return estado.getPeliculas().isEmpty()
                && estado.getSalas().isEmpty()
                && estado.getFunciones().isEmpty()
                && estado.getClientes().isEmpty()
                && estado.getReservas().isEmpty();
    }
}
