/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.persistencia;

import cinesosa.modelo.Entrada;
import cinesosa.modelo.Reserva;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
public final class ExportadorReservasCSV {
    private final Path archivoPrincipal;
    public ExportadorReservasCSV(Path archivoPrincipal) {
        if (archivoPrincipal == null) {
            throw new IllegalArgumentException(
                    "La ruta del archivo principal es obligatoria."
            );
        }
        this.archivoPrincipal = archivoPrincipal.toAbsolutePath().normalize();
    }
    public void exportar(EstadoCine estado, Path destino)
            throws ExcepcionPersistencia {
        if (estado == null || destino == null) {
            throw new IllegalArgumentException(
                    "El estado y la ruta de exportación son obligatorios."
            );
        }
        Path rutaDestino = destino.toAbsolutePath().normalize();
        if (rutaDestino.equals(archivoPrincipal)) {
            throw new IllegalArgumentException(
                    "El CSV no puede reemplazar el archivo principal del cine."
            );
        }
        Path archivoTemporal = null;
        try {
            Path carpeta = rutaDestino.getParent();
            Files.createDirectories(carpeta);
            String prefijo = rutaDestino.getFileName().toString();
            while (prefijo.length() < 3) {
                prefijo += "_";
            }
            archivoTemporal = Files.createTempFile(carpeta, prefijo, ".tmp");
            try (BufferedWriter escritor = Files.newBufferedWriter(
                    archivoTemporal,
                    StandardCharsets.UTF_8)) {
                escribirFila(
                        escritor,
                        "codigo_reserva",
                        "estado",
                        "fecha_reserva",
                        "identificacion_cliente",
                        "nombre_cliente",
                        "pelicula",
                        "fecha_funcion",
                        "sala",
                        "fila",
                        "numero_butaca",
                        "tipo_entrada",
                        "precio_entrada",
                        "importe_total"
                );
                for (Reserva reserva : estado.getReservas()) {
                    for (Entrada entrada : reserva.getEntradas()) {
                        escribirFila(
                                escritor,
                                reserva.getCodigo(),
                                reserva.getEstado().name(),
                                reserva.getFechaReserva().toString(),
                                reserva.getCliente().getIdentificacion(),
                                reserva.getCliente().getNombre(),
                                reserva.getFuncion().getPelicula().getTitulo(),
                                reserva.getFuncion().getFechaHora().toString(),
                                reserva.getFuncion().getSala().getNombre(),
                                entrada.getButaca().getFila(),
                                String.valueOf(entrada.getButaca().getNumero()),
                                entrada.getTipo().getNombre(),
                                String.valueOf(entrada.getPrecioCobrado()),
                                String.valueOf(reserva.getImporteTotal())
                        );
                    }
                }
            }
            Files.move(
                    archivoTemporal,
                    rutaDestino,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );
            archivoTemporal = null;
        } catch (AtomicMoveNotSupportedException ex) {
            throw new ExcepcionPersistencia(
                    "El sistema de archivos no permite completar la exportación de forma segura.",
                    ex
            );
        } catch (IOException | SecurityException ex) {
            throw new ExcepcionPersistencia(
                    "No se pudieron exportar las reservas.",
                    ex
            );
        } finally {
            if (archivoTemporal != null) {
                try {
                    Files.deleteIfExists(archivoTemporal);
                } catch (IOException ignored) {
                    // El archivo temporal no contiene los datos principales.
                }
            }
        }
    }
    private void escribirFila(BufferedWriter escritor, String... campos)
            throws IOException {

        for (int indice = 0; indice < campos.length; indice++) {
            if (indice > 0) {
                escritor.write(',');
            }
            escritor.write('"');
            escritor.write(campos[indice].replace("\"", "\"\""));
            escritor.write('"');
        }
        escritor.newLine();
    }
}
