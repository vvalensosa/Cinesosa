/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.modelo;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

public final class Butaca implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String fila;
    private final int numero;
    public Butaca(String fila, int numero) {
        if (fila == null || fila.trim().isEmpty()) {
            throw new IllegalArgumentException("La fila es obligatoria.");
        }
        if (numero <= 0) {
            throw new IllegalArgumentException(
                    "El número de butaca debe ser mayor que cero."
            );
        }
        this.fila = fila.trim().toUpperCase(Locale.ROOT);
        this.numero = numero;
    }
    public String getFila() {
        return fila;
    }
    public int getNumero() {
        return numero;
    }
    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (objeto == null || getClass() != objeto.getClass()) {
            return false;
        }
        Butaca otra = (Butaca) objeto;
        return numero == otra.numero && fila.equals(otra.fila);
    }
    @Override
    public int hashCode() {
        return Objects.hash(fila, numero);
    }
    @Override
    public String toString() {
        return fila + numero;
    }
}
