/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.modelo;
import java.io.Serializable;

public final class Entrada implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Butaca butaca;
    private final TipoEnntrada tipo;
    private final double precioCobrado;

    public Entrada(Butaca butaca, TipoEnntrada tipo, double precioBase) {
        if (butaca == null) {
            throw new IllegalArgumentException("La entrada debe tener una butaca.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("La entrada debe tener un tipo.");
        }
        if (precioBase <= 0 || Double.isNaN(precioBase)
                || Double.isInfinite(precioBase)) {
            throw new IllegalArgumentException("El precio base no es válido.");
        }

        double precioCalculado = tipo.calcularPrecio(precioBase);

        if (precioCalculado <= 0 || Double.isNaN(precioCalculado)
                || Double.isInfinite(precioCalculado)) {
            throw new IllegalArgumentException(
                    "El precio calculado no es válido."
            );
        }

        this.butaca = butaca;
        this.tipo = tipo;
        this.precioCobrado = Math.round(precioCalculado * 100.0) / 100.0;
    }

    public Butaca getButaca() {
        return butaca;
    }

    public TipoEnntrada getTipo() {
        return tipo;
    }

    public double getPrecioCobrado() {
        return precioCobrado;
    }
}
