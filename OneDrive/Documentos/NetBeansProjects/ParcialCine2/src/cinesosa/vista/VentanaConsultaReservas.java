/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.vista;

import cinesosa.modelo.Entrada;
import cinesosa.modelo.Reserva;
import cinesosa.persistencia.ExcepcionPersistencia;
import cinesosa.servicio.SistemaCine;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.beans.property.ReadOnlyStringWrapper;
public final class VentanaConsultaReservas {
    private final SistemaCine sistema;
    private final Stage ventana = new Stage();
    private final TextField campoCodigo = new TextField();
    private final TextField campoIdentificacion = new TextField();
    private final TableView<Reserva> tabla = new TableView<>();
    private final TextArea detalle = new TextArea();
    private final Button botonCancelar = new Button("Cancelar reserva");
    public VentanaConsultaReservas(SistemaCine sistema) {
        this.sistema = sistema;
        configurarVentana();
    }
    private void configurarVentana() {
        campoCodigo.setPromptText("Código de reserva");
        campoIdentificacion.setPromptText("Identificación del cliente");
        Button botonBuscarCodigo = new Button("Buscar código");
        botonBuscarCodigo.setOnAction(accion -> buscarPorCodigo());
        Button botonBuscarCliente = new Button("Buscar cliente");
        botonBuscarCliente.setOnAction(accion -> buscarPorIdentificacion());
        HBox busqueda = new HBox(
                8,
                campoCodigo,
                botonBuscarCodigo,
                campoIdentificacion,
                botonBuscarCliente
        );
        configurarTabla();
        tabla.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionada) -> {
                    mostrarDetalle(seleccionada);
                    botonCancelar.setDisable(
                            seleccionada == null
                                    || seleccionada.getEstado().name()
                                            .equals("CANCELADA")
                    );
                });
        detalle.setEditable(false);
        detalle.setPrefRowCount(6);
        detalle.setWrapText(true);
        botonCancelar.setDisable(true);
        botonCancelar.setOnAction(accion -> cancelarSeleccionada());
        VBox contenido = new VBox(
                12,
                new Label("Consultar reservas"),
                busqueda,
                tabla,
                new Label("Detalle"),
                detalle,
                botonCancelar
        );
        contenido.setPadding(new Insets(18));
        ventana.setTitle("Consulta de reservas - Cine Sosa");
        ventana.setScene(new Scene(contenido, 900, 620));
    }
    private void configurarTabla() {
        TableColumn<Reserva, String> columnaCodigo =
                new TableColumn<>("Código");
        columnaCodigo.setCellValueFactory(
                new PropertyValueFactory<>("codigo")
        );
        TableColumn<Reserva, String> columnaEstado =
                new TableColumn<>("Estado");
        columnaEstado.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getEstado().name()
                )
        );
        TableColumn<Reserva, String> columnaCliente =
                new TableColumn<>("Cliente");
        columnaCliente.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getCliente().getNombre()
                )
        );
        TableColumn<Reserva, String> columnaPelicula =
                new TableColumn<>("Película");
        columnaPelicula.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue()
                                .getFuncion()
                                .getPelicula()
                                .getTitulo()
                )
        );
        tabla.getColumns().addAll(
                columnaCodigo,
                columnaEstado,
                columnaCliente,
                columnaPelicula
        );
        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
    }
    private void buscarPorCodigo() {
        try {
            Reserva reserva = sistema.getServicioReserva()
                    .buscarPorCodigo(campoCodigo.getText());
            tabla.setItems(
                    FXCollections.observableArrayList(reserva)
            );
        } catch (IllegalArgumentException | ExcepcionPersistencia ex) {
            mostrarError(ex.getMessage());
        }
    }
    private void buscarPorIdentificacion() {
        try {
            List<Reserva> reservas = sistema.getServicioReserva()
                    .buscarPorIdentificacion(campoIdentificacion.getText());
            tabla.setItems(
                    FXCollections.observableArrayList(reservas)
            );
            if (reservas.isEmpty()) {
                mostrarAviso("No hay reservas para esa identificación.");
            }
        } catch (IllegalArgumentException | ExcepcionPersistencia ex) {
            mostrarError(ex.getMessage());
        }
    }
    private void mostrarDetalle(Reserva reserva) {
        if (reserva == null) {
            detalle.clear();
            return;
        }
        NumberFormat moneda =
                NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
        StringBuilder texto = new StringBuilder();
        texto.append("Código: ").append(reserva.getCodigo()).append("\n");
        texto.append("Estado: ").append(reserva.getEstado()).append("\n");
        texto.append("Cliente: ")
                .append(reserva.getCliente().getNombre())
                .append(" — ")
                .append(reserva.getCliente().getIdentificacion())
                .append("\n");
        texto.append("Función: ")
                .append(reserva.getFuncion().getPelicula().getTitulo())
                .append(" — ")
                .append(reserva.getFuncion().getFechaHora().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                ))
                .append("\n");
        texto.append("Butacas:\n");
        for (Entrada entrada : reserva.getEntradas()) {
            texto.append("  ")
                    .append(entrada.getButaca())
                    .append(" — ")
                    .append(entrada.getTipo().getNombre())
                    .append(": ")
                    .append(moneda.format(entrada.getPrecioCobrado()))
                    .append("\n");
        }

        texto.append("Total: ").append(moneda.format(reserva.getImporteTotal()));
        detalle.setText(texto.toString());
    }
    private void cancelarSeleccionada() {
        Reserva seleccionada = tabla.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            return;
        }
        try {
            sistema.getServicioReserva()
                    .cancelarReserva(seleccionada.getCodigo());

            mostrarAviso("La reserva fue cancelada.");
            if (!campoIdentificacion.getText().trim().isEmpty()) {
                buscarPorIdentificacion();
            } else {
                buscarPorCodigo();
            }
        } catch (IllegalArgumentException | ExcepcionPersistencia ex) {
            mostrarError(ex.getMessage());
        }
    }
    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText("No se pudo completar la operación");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    private void mostrarAviso(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Reservas");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    public void mostrar() {
        ventana.showAndWait();
    }
}
