/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.persistencia;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
public final class PersistenciaCine {
    private final Path archivo;
    public PersistenciaCine(Path archivo) {
        if (archivo == null) {
            throw new IllegalArgumentException("La ruta del archivo es obligatoria.");
        }
        this.archivo = archivo;
    }
    public EstadoCine cargar() throws ExcepcionPersistencia {
        if (Files.notExists(archivo)) {
            return new EstadoCine();
        }
        if (!Files.exists(archivo)) {
            throw new ExcepcionPersistencia(
                    "No se pudo comprobar el archivo de datos."
            );
        }
        try (ObjectInputStream entrada = new ObjectInputStream(
                Files.newInputStream(archivo))) {
            Object objeto = entrada.readObject();
            if (!(objeto instanceof EstadoCine)) {
                throw new ExcepcionPersistencia(
                        "El archivo no contiene datos válidos de Cine Sosa."
                );
            }
            return (EstadoCine) objeto;
        } catch (IOException | ClassNotFoundException ex) {
            throw new ExcepcionPersistencia(
                    "No se pudieron leer los datos del cine.",
                    ex
            );
        }
    }
    public void guardar(EstadoCine estado) throws ExcepcionPersistencia {
        if (estado == null) {
            throw new IllegalArgumentException("El estado del cine es obligatorio.");
        }
        Path archivoTemporal = null;
        try {
            Path carpeta = archivo.toAbsolutePath().getParent();
            Files.createDirectories(carpeta);
            String prefijo = archivo.getFileName().toString();
            while (prefijo.length() < 3) {
                prefijo += "_";
            }
            archivoTemporal = Files.createTempFile(
                    carpeta,
                    prefijo,
                    ".tmp"
            );
            try (ObjectOutputStream salida = new ObjectOutputStream(
                    Files.newOutputStream(
                            archivoTemporal,
                            StandardOpenOption.WRITE
                    ))) {
                salida.writeObject(estado);
            }
            Files.move(
                    archivoTemporal,
                    archivo,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );
            archivoTemporal = null;
        } catch (AtomicMoveNotSupportedException ex) {
            throw new ExcepcionPersistencia(
                    "El sistema de archivos no permite guardar los datos de forma segura.",
                    ex
            );
        } catch (IOException | SecurityException ex) {
            throw new ExcepcionPersistencia(
                    "No se pudieron guardar los datos del cine.",
                    ex
            );
        } finally {
            if (archivoTemporal != null) {
                try {
                    Files.deleteIfExists(archivoTemporal);
                } catch (IOException ignored) {
                    // El archivo temporal no contiene el estado principal del cine.
                }
            }
        }
    }
}
