/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.persistencia;
public class ExcepcionPersistencia extends Exception {
    private static final long serialVersionUID = 1L;
    public ExcepcionPersistencia(String mensaje) {
        super(mensaje);
    }
    public ExcepcionPersistencia(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
