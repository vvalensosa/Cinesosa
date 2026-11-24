/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.vista;

import cinesosa.modelo.Funcion;
import cinesosa.persistencia.ExcepcionPersistencia;
import cinesosa.servicio.SistemaCine;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;
import javafx.stage.FileChooser;
public class CineSosaApp extends Application {
    private final TableView<Funcion> tablaFunciones = new TableView<>();
    private final TextField filtroTitulo = new TextField();
    private final DatePicker filtroFecha = new DatePicker();
    private final Label mensajeEstado = new Label();
    private SistemaCine sistema;
    @Override
    public void start(Stage escenario) {
        try {
            sistema = new SistemaCine();
        } catch (ExcepcionPersistencia ex) {
            mostrarError("No se pudieron cargar los datos del cine.", ex);
            return;
        }
        configurarTabla();
        Label titulo = new Label("Cine Sosa");
        titulo.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
        Label subtitulo = new Label("Cartelera de funciones");
        filtroTitulo.setPromptText("Buscar película");
        filtroFecha.setPromptText("Filtrar por fecha");
        Button botonBuscar = new Button("Buscar");
        botonBuscar.setOnAction(
                accionBuscar -> aplicarFiltros()
        );
        Button botonLimpiar = new Button("Limpiar");
        botonLimpiar.setOnAction(accionLimpiar -> {
            filtroTitulo.clear();
            filtroFecha.setValue(null);
            cargarCartelera();
        });
        Button botonReservar = new Button("Reservar butacas");
        botonReservar.setOnAction(
                accionReserva -> abrirVentanaReserva()
        );
        Button botonConsultarReservas =
                new Button("Consultar reservas");
        botonConsultarReservas.setOnAction(accionConsulta -> abrirConsultaReservas());
        Button botonExportarCSV = new Button("Exportar reservas CSV");
        botonExportarCSV.setOnAction(accionExportar -> exportarReservas(escenario));
        Button botonAdministracion = new Button("Administración");
        botonAdministracion.setOnAction(accionAdmin -> abrirAdministracion());
        HBox filtros = new HBox(
                10,
                filtroTitulo,
                filtroFecha,
                botonBuscar,
                botonLimpiar
        );
        HBox accionesReservas = new HBox(
                10,
                botonReservar,
                botonConsultarReservas,
                botonExportarCSV,
                botonAdministracion
        );
        VBox contenido = new VBox(
                12,
                titulo,
                subtitulo,
                filtros,
                tablaFunciones,
                accionesReservas,
                mensajeEstado
        );
        contenido.setPadding(new Insets(24));
        cargarCartelera();
        Scene escena = new Scene(contenido, 900, 600);
        escenario.setTitle("Cine Sosa - Sistema de reservas");
        escenario.setMinWidth(760);
        escenario.setMinHeight(480);
        escenario.setScene(escena);
        escenario.show();
    }
    private void configurarTabla() {
        TableColumn<Funcion, String> columnaPelicula =
                new TableColumn<>("Película");
        columnaPelicula.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getPelicula().getTitulo()
                )
        );
        TableColumn<Funcion, String> columnaSala =
                new TableColumn<>("Sala");
        columnaSala.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getSala().getNombre()
                )
        );
        TableColumn<Funcion, String> columnaFecha =
                new TableColumn<>("Fecha y hora");
        columnaFecha.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getFechaHora().format(
                                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                        )
                )
        );
        TableColumn<Funcion, String> columnaPrecio =
                new TableColumn<>("Precio base");
        columnaPrecio.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        String.format(
                                new Locale("es", "AR"),
                                "$ %.2f",
                                datos.getValue().getPrecioBase()
                        )
                )
        );
        tablaFunciones.getColumns().addAll(
                columnaPelicula,
                columnaSala,
                columnaFecha,
                columnaPrecio
        );
        tablaFunciones.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
    }
    private void cargarCartelera() {
        try {
            List<Funcion> funciones =
                    sistema.getServicioProgramacion().listarFunciones();
            tablaFunciones.setItems(
                    FXCollections.observableArrayList(funciones)
            );
            mensajeEstado.setText(
                    "Funciones disponibles: " + funciones.size()
            );
        } catch (ExcepcionPersistencia ex) {
            mostrarError("No se pudo cargar la cartelera.", ex);
        }
    }
    private void aplicarFiltros() {
        try {
            List<Funcion> funciones =
                    sistema.getServicioProgramacion().listarFunciones();
            String texto = filtroTitulo.getText()
                    .trim()
                    .toLowerCase(Locale.ROOT);
            LocalDate fecha = filtroFecha.getValue();
            List<Funcion> resultado = new ArrayList<>();
            for (Funcion funcion : funciones) {
                boolean coincideTitulo = texto.isEmpty()
                        || funcion.getPelicula()
                                .getTitulo()
                                .toLowerCase(Locale.ROOT)
                                .contains(texto);
                boolean coincideFecha = fecha == null
                        || funcion.getFechaHora()
                                .toLocalDate()
                                .equals(fecha);
                if (coincideTitulo && coincideFecha) {
                    resultado.add(funcion);
                }
            }
            tablaFunciones.setItems(
                    FXCollections.observableArrayList(resultado)
            );
            mensajeEstado.setText(
                    "Funciones encontradas: " + resultado.size()
            );
        } catch (ExcepcionPersistencia ex) {
            mostrarError("No se pudo buscar en la cartelera.", ex);
        }
    }
    private void abrirVentanaReserva() {
        Funcion seleccionada =
                tablaFunciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Seleccioná una función");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "Elegí una función de la cartelera para reservar butacas."
            );
            alerta.showAndWait();
            return;
        }
        try {
            new VentanaReserva(sistema, seleccionada).mostrar();
        } catch (ExcepcionPersistencia ex) {
            mostrarError("No se pudo abrir la reserva.", ex);
        }
    }
    private void exportarReservas(Stage escenario) {
    FileChooser selector = new FileChooser();
    selector.setTitle("Exportar reservas");
    selector.setInitialFileName("reservas.csv");
    selector.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivo CSV", "*.csv")
    );
    File archivo = selector.showSaveDialog(escenario);
    if (archivo == null) {
        return;
    }
    if (!archivo.getName().toLowerCase(Locale.ROOT).endsWith(".csv")) {
        archivo = new File(
                archivo.getParentFile(),
                archivo.getName() + ".csv"
        );
    }
    try {
        sistema.exportarReservasCSV(archivo.toPath());
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Exportación completada");
        alerta.setHeaderText(null);
        alerta.setContentText("Las reservas se exportaron al archivo CSV.");
        alerta.showAndWait();
    } catch (ExcepcionPersistencia | IllegalArgumentException ex) {
        mostrarError("No se pudieron exportar las reservas.", ex);
    }
}
    private void abrirAdministracion() {
    new VentanaAdministracion(sistema).mostrar();
    }
    private void abrirConsultaReservas() {
        new VentanaConsultaReservas(sistema).mostrar();
    }
    private void mostrarError(String mensaje, Exception ex) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(mensaje);
        alerta.setContentText(ex.getMessage());
        alerta.showAndWait();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
