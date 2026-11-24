/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.modelo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
public final class Sala implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String nombre;
    private final int cantidadFilas;
    private final int butacasPorFila;
    private final List<Butaca> butacas;
    public Sala(int id, String nombre, int cantidadFilas, int butacasPorFila) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor que cero.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la sala es obligatorio.");
        }
        if (cantidadFilas <= 0 || cantidadFilas > 26) {
            throw new IllegalArgumentException(
                    "La sala debe tener entre 1 y 26 filas."
            );
        }
        if (butacasPorFila <= 0) {
            throw new IllegalArgumentException(
                    "Debe haber al menos una butaca por fila."
            );
        }
        this.id = id;
        this.nombre = nombre.trim();
        this.cantidadFilas = cantidadFilas;
        this.butacasPorFila = butacasPorFila;
        this.butacas = generarButacas(cantidadFilas, butacasPorFila);
    }
    private List<Butaca> generarButacas(int filas, int cantidadPorFila) {
        List<Butaca> resultado = new ArrayList<>();
        for (int indiceFila = 0; indiceFila < filas; indiceFila++) {
            String letraFila = String.valueOf((char) ('A' + indiceFila));
            for (int numero = 1; numero <= cantidadPorFila; numero++) {
                resultado.add(new Butaca(letraFila, numero));
            }
        }
        return resultado;
    }
    public int getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public int getCantidadFilas() {
        return cantidadFilas;
    }
    public int getButacasPorFila() {
        return butacasPorFila;
    }
    public List<Butaca> getButacas() {
        return Collections.unmodifiableList(butacas);
    }
    public boolean contieneButaca(Butaca butaca) {
        return butacas.contains(butaca);
    }
    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (objeto == null || getClass() != objeto.getClass()) {
            return false;
        }
        Sala otra = (Sala) objeto;
        return id == otra.id;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    @Override
    public String toString() {
        return nombre;
    }
}
