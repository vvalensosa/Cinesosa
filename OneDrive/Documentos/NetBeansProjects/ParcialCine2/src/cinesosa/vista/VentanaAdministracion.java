/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.vista;

import cinesosa.modelo.Funcion;
import cinesosa.modelo.Pelicula;
import cinesosa.modelo.Sala;
import cinesosa.persistencia.ExcepcionPersistencia;
import cinesosa.servicio.SistemaCine;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public final class VentanaAdministracion {
    private final SistemaCine sistema;
    private final Stage ventana = new Stage();
    private final TableView<Pelicula> tablaPeliculas = new TableView<>();
    private final TextField campoTitulo = new TextField();
    private final TextField campoDuracion = new TextField();
    private final TableView<Sala> tablaSalas = new TableView<>();
    private final TextField campoNombreSala = new TextField();
    private final TextField campoFilas = new TextField();
    private final TextField campoButacasPorFila = new TextField();
    private final TableView<Funcion> tablaFunciones = new TableView<>();
    private final ComboBox<Pelicula> selectorPelicula = new ComboBox<>();
    private final ComboBox<Sala> selectorSala = new ComboBox<>();
    private final DatePicker selectorFecha = new DatePicker();
    private final TextField campoHora = new TextField();
    private final TextField campoPrecio = new TextField();
    public VentanaAdministracion(SistemaCine sistema) {
        this.sistema = sistema;
        configurarVentana();
    }
    private void configurarVentana() {
        TabPane pestañas = new TabPane();
        Tab pestañaPeliculas = new Tab(
                "Películas",
                crearPestañaPeliculas()
        );
        pestañaPeliculas.setClosable(false);
        Tab pestañaSalas = new Tab(
                "Salas",
                crearPestañaSalas()
        );
        pestañaSalas.setClosable(false);
        Tab pestañaFunciones = new Tab(
                "Funciones",
                crearPestañaFunciones()
        );
        pestañaFunciones.setClosable(false);
        pestañas.getTabs().addAll(
                pestañaPeliculas,
                pestañaSalas,
                pestañaFunciones
        );
        cargarPeliculas();
        cargarSalas();
        cargarFunciones();
        ventana.setTitle("Administración - Cine Sosa");
        ventana.setScene(new Scene(pestañas, 900, 620));
    }
    private VBox crearPestañaPeliculas() {
        configurarTablaPeliculas();
        campoTitulo.setPromptText("Título");
        campoDuracion.setPromptText("Duración en minutos");
        Button botonRegistrar = new Button("Registrar");
        botonRegistrar.setOnAction(accion -> registrarPelicula());
        Button botonModificar = new Button("Modificar seleccionada");
        botonModificar.setOnAction(accion -> modificarPelicula());
        Button botonDesactivar = new Button("Desactivar seleccionada");
        botonDesactivar.setOnAction(accion -> desactivarPelicula());
        tablaPeliculas.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionada) -> {
                    if (seleccionada != null) {
                        campoTitulo.setText(seleccionada.getTitulo());
                        campoDuracion.setText(
                                String.valueOf(
                                        seleccionada.getDuracionMinutos()
                                )
                        );
                    }
                });
        HBox formulario = new HBox(
                8,
                campoTitulo,
                campoDuracion,
                botonRegistrar
        );
        HBox acciones = new HBox(
                8,
                botonModificar,
                botonDesactivar
        );
        VBox contenido = new VBox(
                12,
                new Label("Administrar películas"),
                tablaPeliculas,
                formulario,
                acciones
        );
        contenido.setPadding(new Insets(16));
        return contenido;
    }
    private void configurarTablaPeliculas() {
        TableColumn<Pelicula, String> columnaId =
                new TableColumn<>("ID");
        columnaId.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        String.valueOf(datos.getValue().getId())
                )
        );
        TableColumn<Pelicula, String> columnaTitulo =
                new TableColumn<>("Título");
        columnaTitulo.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(datos.getValue().getTitulo())
        );
        TableColumn<Pelicula, String> columnaDuracion =
                new TableColumn<>("Duración");
        columnaDuracion.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getDuracionMinutos() + " min"
                )
        );
        TableColumn<Pelicula, String> columnaActiva =
                new TableColumn<>("Activa");
        columnaActiva.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().isActiva() ? "Sí" : "No"
                )
        );
        tablaPeliculas.getColumns().addAll(
                columnaId,
                columnaTitulo,
                columnaDuracion,
                columnaActiva
        );
        tablaPeliculas.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
    }
    private void registrarPelicula() {
        try {
            int duracion = Integer.parseInt(campoDuracion.getText().trim());
            sistema.getServicioCatalogo().registrarPelicula(
                    campoTitulo.getText(),
                    duracion
            );
            campoTitulo.clear();
            campoDuracion.clear();
            cargarPeliculas();
        } catch (NumberFormatException ex) {
            mostrarError("La duración debe ser un número entero.");
        } catch (IllegalArgumentException | ExcepcionPersistencia ex) {
            mostrarError(ex.getMessage());
        }
    }
    private void modificarPelicula() {
        Pelicula seleccionada =
                tablaPeliculas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Seleccioná una película de la tabla.");
            return;
        }
        try {
            int duracion = Integer.parseInt(campoDuracion.getText().trim());
            sistema.getServicioCatalogo().modificarPelicula(
                    seleccionada.getId(),
                    campoTitulo.getText(),
                    duracion
            );
            cargarPeliculas();
        } catch (NumberFormatException ex) {
            mostrarError("La duración debe ser un número entero.");
        } catch (IllegalArgumentException | ExcepcionPersistencia ex) {
            mostrarError(ex.getMessage());
        }
    }
    private void desactivarPelicula() {
        Pelicula seleccionada =
                tablaPeliculas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Seleccioná una película de la tabla.");
            return;
        }
        try {
            sistema.getServicioCatalogo()
                    .desactivarPelicula(seleccionada.getId());
            cargarPeliculas();
        } catch (IllegalArgumentException | ExcepcionPersistencia ex) {
            mostrarError(ex.getMessage());
        }
    }
    private void cargarPeliculas() {
        try {
            tablaPeliculas.setItems(
                    FXCollections.observableArrayList(
                            sistema.getServicioCatalogo().listarPeliculas()
                    )
            );
            actualizarSelectorPeliculas();
        } catch (ExcepcionPersistencia ex) {
            mostrarError(ex.getMessage());
        }
    }
    private VBox crearPestañaSalas() {
        configurarTablaSalas();
        campoNombreSala.setPromptText("Nombre de sala");
        campoFilas.setPromptText("Cantidad de filas");
        campoButacasPorFila.setPromptText("Butacas por fila");
        Button botonRegistrar = new Button("Registrar sala");
        botonRegistrar.setOnAction(accion -> registrarSala());
        HBox formulario = new HBox(
                8,
                campoNombreSala,
                campoFilas,
                campoButacasPorFila,
                botonRegistrar
        );
        VBox contenido = new VBox(
                12,
                new Label("Administrar salas"),
                tablaSalas,
                formulario
        );
        contenido.setPadding(new Insets(16));
        return contenido;
    }
    private void configurarTablaSalas() {
        TableColumn<Sala, String> columnaId =
                new TableColumn<>("ID");
        columnaId.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        String.valueOf(datos.getValue().getId())
                )
        );
        TableColumn<Sala, String> columnaNombre =
                new TableColumn<>("Sala");
        columnaNombre.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(datos.getValue().getNombre())
        );
        TableColumn<Sala, String> columnaDistribucion =
                new TableColumn<>("Distribución");
        columnaDistribucion.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getCantidadFilas()
                        + " filas × "
                        + datos.getValue().getButacasPorFila()
                        + " butacas"
                )
        );
        tablaSalas.getColumns().addAll(
                columnaId,
                columnaNombre,
                columnaDistribucion
        );
        tablaSalas.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
    }
    private void registrarSala() {
        try {
            int filas = Integer.parseInt(campoFilas.getText().trim());
            int butacas = Integer.parseInt(
                    campoButacasPorFila.getText().trim()
            );
            sistema.getServicioCatalogo().registrarSala(
                    campoNombreSala.getText(),
                    filas,
                    butacas
            );
            campoNombreSala.clear();
            campoFilas.clear();
            campoButacasPorFila.clear();
            cargarSalas();
        } catch (NumberFormatException ex) {
            mostrarError("Filas y butacas deben ser números enteros.");
        } catch (IllegalArgumentException | ExcepcionPersistencia ex) {
            mostrarError(ex.getMessage());
        }
    }
    private void cargarSalas() {
        try {
            tablaSalas.setItems(
                    FXCollections.observableArrayList(
                            sistema.getServicioCatalogo().listarSalas()
                    )
            );
            actualizarSelectorSalas();
        } catch (ExcepcionPersistencia ex) {
            mostrarError(ex.getMessage());
        }
    }
    private VBox crearPestañaFunciones() {
        configurarTablaFunciones();
        campoHora.setPromptText("Hora (HH:mm)");
        campoPrecio.setPromptText("Precio base");
        Button botonProgramar = new Button("Programar función");
        botonProgramar.setOnAction(accion -> programarFuncion());
        HBox formulario = new HBox(
                8,
                selectorPelicula,
                selectorSala,
                selectorFecha,
                campoHora,
                campoPrecio,
                botonProgramar
        );
        VBox contenido = new VBox(
                12,
                new Label("Administrar funciones"),
                tablaFunciones,
                formulario
        );
        contenido.setPadding(new Insets(16));
        return contenido;
    }
    private void configurarTablaFunciones() {
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
                new TableColumn<>("Precio");
        columnaPrecio.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        "$ " + datos.getValue().getPrecioBase()
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
    private void programarFuncion() {
        try {
            Pelicula pelicula = selectorPelicula.getValue();
            Sala sala = selectorSala.getValue();
            if (pelicula == null || sala == null
                    || selectorFecha.getValue() == null) {
                mostrarError(
                        "Seleccioná una película, una sala y una fecha."
                );
                return;
            }
            LocalTime hora = LocalTime.parse(campoHora.getText().trim());
            double precio = Double.parseDouble(campoPrecio.getText().trim());
            sistema.getServicioProgramacion().programarFuncion(
                    pelicula.getId(),
                    sala.getId(),
                    LocalDateTime.of(selectorFecha.getValue(), hora),
                    precio
            );
            campoHora.clear();
            campoPrecio.clear();
            cargarFunciones();
        } catch (NumberFormatException ex) {
            mostrarError("El precio debe ser un número válido.");
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        } catch (ExcepcionPersistencia ex) {
            mostrarError(ex.getMessage());
        } catch (RuntimeException ex) {
            mostrarError(
                    "Revisá la fecha y la hora. Usá el formato HH:mm."
            );
        }
    }
    private void cargarFunciones() {
        try {
            tablaFunciones.setItems(
                    FXCollections.observableArrayList(
                            sistema.getServicioProgramacion()
                                    .listarFunciones()
                    )
            );
        } catch (ExcepcionPersistencia ex) {
            mostrarError(ex.getMessage());
        }
    }
    private void actualizarSelectorPeliculas()
            throws ExcepcionPersistencia {
        List<Pelicula> activas = new ArrayList<>();
        for (Pelicula pelicula
                : sistema.getServicioCatalogo().listarPeliculas()) {
            if (pelicula.isActiva()) {
                activas.add(pelicula);
            }
        }
        selectorPelicula.setItems(
                FXCollections.observableArrayList(activas)
        );
    }
    private void actualizarSelectorSalas()
            throws ExcepcionPersistencia {

        selectorSala.setItems(
                FXCollections.observableArrayList(
                        sistema.getServicioCatalogo().listarSalas()
                )
        );
    }
    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Administración");
        alerta.setHeaderText("No se pudo completar la operación");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    public void mostrar() {
        ventana.showAndWait();
    }
}