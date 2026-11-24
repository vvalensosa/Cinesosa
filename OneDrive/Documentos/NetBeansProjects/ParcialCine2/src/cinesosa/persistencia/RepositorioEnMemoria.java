/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.persistencia;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
public class RepositorioEnMemoria<T, ID> implements Repositorio<T, ID> {
    private final List<T> entidades;
    private final Function<T, ID> obtenerId;
    public RepositorioEnMemoria(
            List<T> entidades,
            Function<T, ID> obtenerId
    ) {
        if (entidades == null) {
            throw new IllegalArgumentException("La lista de entidades es obligatoria.");
        }
        if (obtenerId == null) {
            throw new IllegalArgumentException(
                    "La función para obtener el ID es obligatoria."
            );
        }
        this.entidades = entidades;
        this.obtenerId = obtenerId;
    }
    @Override
    public void guardar(T entidad) throws ExcepcionPersistencia {
        if (entidad == null) {
            throw new ExcepcionPersistencia(
                    "No se puede guardar una entidad vacía."
            );
        }
        ID id = obtenerId.apply(entidad);
        if (id == null) {
            throw new ExcepcionPersistencia(
                    "La entidad debe tener un ID."
            );
        }
        if (existePorId(id)) {
            throw new ExcepcionPersistencia(
                    "Ya existe una entidad con ese ID."
            );
        }
        entidades.add(entidad);
    }
    @Override
    public Optional<T> buscarPorId(ID id) throws ExcepcionPersistencia {
        if (id == null) {
            return Optional.empty();
        }
        for (T entidad : entidades) {
            if (Objects.equals(obtenerId.apply(entidad), id)) {
                return Optional.of(entidad);
            }
        }
        return Optional.empty();
    }
    @Override
    public List<T> listarTodos() {
        return Collections.unmodifiableList(new ArrayList<>(entidades));
    }
    @Override
    public void actualizar(T entidad) throws ExcepcionPersistencia {
        if (entidad == null) {
            throw new ExcepcionPersistencia(
                    "No se puede actualizar una entidad vacía."
            );
        }
        ID id = obtenerId.apply(entidad);
        for (int indice = 0; indice < entidades.size(); indice++) {
            T existente = entidades.get(indice);
            if (Objects.equals(obtenerId.apply(existente), id)) {
                entidades.set(indice, entidad);
                return;
            }
        }
        throw new ExcepcionPersistencia(
                "No existe una entidad con ese ID para actualizar."
        );
    }
    @Override
    public boolean existePorId(ID id) {
        if (id == null) {
            return false;
        }
        for (T entidad : entidades) {
            if (Objects.equals(obtenerId.apply(entidad), id)) {
                return true;
            }
        }
        return false;
    }
}