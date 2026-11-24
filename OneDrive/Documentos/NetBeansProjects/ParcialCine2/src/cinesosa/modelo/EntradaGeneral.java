/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.modelo;
public final class EntradaGeneral implements TipoEnntrada {

    private static final long serialVersionUID = 1L;

    @Override
    public double calcularPrecio(double precioBase) {
        return precioBase;
    }

    @Override
    public String getNombre() {
        return "General";
    }
}
