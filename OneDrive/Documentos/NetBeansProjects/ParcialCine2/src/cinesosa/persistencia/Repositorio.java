/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cinesosa.persistencia;

import java.util.List;
import java.util.Optional;

public interface Repositorio<T, ID> {

    void guardar(T entidad) throws ExcepcionPersistencia;

    Optional<T> buscarPorId(ID id) throws ExcepcionPersistencia;

    List<T> listarTodos() throws ExcepcionPersistencia;

    void actualizar(T entidad) throws ExcepcionPersistencia;

    boolean existePorId(ID id) throws ExcepcionPersistencia;
}
