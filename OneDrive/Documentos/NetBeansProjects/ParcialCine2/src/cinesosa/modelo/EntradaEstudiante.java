/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.modelo;

public final class EntradaEstudiante implements TipoEnntrada {

    private static final long serialVersionUID = 1L;

    private static final double PORCENTAJE_DESCUENTO = 0.25;

    @Override
    public double calcularPrecio(double precioBase) {
        double descuento = precioBase * PORCENTAJE_DESCUENTO;
        return precioBase - descuento;
    }

    @Override
    public String getNombre() {
        return "Estudiante";
    }
}
