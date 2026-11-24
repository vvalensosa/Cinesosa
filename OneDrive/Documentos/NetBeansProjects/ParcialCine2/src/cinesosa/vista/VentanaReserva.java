/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cinesosa.vista;

import cinesosa.modelo.Butaca;
import cinesosa.modelo.Cliente;
import cinesosa.modelo.Entrada;
import cinesosa.modelo.EntradaEstudiante;
import cinesosa.modelo.EntradaGeneral;
import cinesosa.modelo.Funcion;
import cinesosa.modelo.Reserva;
import cinesosa.modelo.TipoEnntrada;
import cinesosa.persistencia.ExcepcionPersistencia;
import cinesosa.servicio.SistemaCine;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public final class VentanaReserva {
    private final SistemaCine sistema;
    private final Funcion funcion;
    private final Stage ventana;
    private final Set<Butaca> seleccionadas = new LinkedHashSet<>();
    private final Map<Butaca, TipoEnntrada> tiposPorButaca =
            new LinkedHashMap<>();
    private final ListView<Butaca> listaSeleccionadas = new ListView<>();
    private final ComboBox<String> selectorTipo = new ComboBox<>();
    private final TextField campoIdentificacion = new TextField();
    private final TextField campoNombre = new TextField();
    private final TextField campoTelefono = new TextField();
    public VentanaReserva(SistemaCine sistema, Funcion funcion)
            throws ExcepcionPersistencia {
        this.sistema = sistema;
        this.funcion = funcion;
        this.ventana = new Stage();

        configurarVentana();
    }
    private void configurarVentana() throws ExcepcionPersistencia {
        List<Butaca> ocupadas =
                sistema.getServicioReserva()
                        .listarButacasOcupadas(funcion.getId());
        Label encabezado = new Label(
                funcion.getPelicula().getTitulo()
                        + " — "
                        + funcion.getSala().getNombre()
                        + " — "
                        + funcion.getFechaHora().format(
                                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                        )
        );
        encabezado.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");
        GridPane mapa = crearMapaButacas(ocupadas);
        ScrollPane desplazamiento = new ScrollPane(mapa);
        desplazamiento.setFitToWidth(true);
        desplazamiento.setPrefViewportHeight(260);
        Label leyenda = new Label(
                "Verde: disponible     Azul: seleccionada     Gris: ocupada"
        );
        VBox panelMapa = new VBox(10, leyenda, desplazamiento);
        panelMapa.setPrefWidth(500);
        listaSeleccionadas.setPrefHeight(130);
        listaSeleccionadas.setPlaceholder(
                new Label("Todavía no seleccionaste butacas.")
        );
        selectorTipo.getItems().addAll("General", "Estudiante");
        selectorTipo.setPromptText("Tipo de entrada");
        selectorTipo.setDisable(true);
        listaSeleccionadas.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, actual) -> {
                    boolean haySeleccion = actual != null;
                    selectorTipo.setDisable(!haySeleccion);
                    if (haySeleccion) {
                        TipoEnntrada tipo = tiposPorButaca.get(actual);
                        selectorTipo.setValue(tipo.getNombre());
                    } else {
                        selectorTipo.setValue(null);
                    }
                });
        selectorTipo.setOnAction(event -> asignarTipoSeleccionado());
        VBox panelSeleccion = new VBox(
                8,
                new Label("Butacas seleccionadas"),
                listaSeleccionadas,
                new Label("Tipo para la butaca seleccionada"),
                selectorTipo
        );
        panelSeleccion.setPrefWidth(260);
        HBox seleccion = new HBox(18, panelMapa, panelSeleccion);
        campoIdentificacion.setPromptText("Identificación");
        campoNombre.setPromptText("Nombre y apellido");
        campoTelefono.setPromptText("Teléfono (opcional)");
        VBox datosCliente = new VBox(
                8,
                new Label("Datos del cliente"),
                campoIdentificacion,
                campoNombre,
                campoTelefono
        );
        Button botonCancelar = new Button("Cerrar");
        botonCancelar.setOnAction(event -> ventana.close());
        Button botonConfirmar = new Button("Confirmar reserva");
        botonConfirmar.setOnAction(event -> confirmarReserva());
        HBox botones = new HBox(10, botonCancelar, botonConfirmar);
        botones.setAlignment(Pos.CENTER_RIGHT);
        VBox contenido = new VBox(
                16,
                encabezado,
                seleccion,
                datosCliente,
                botones
        );
        contenido.setPadding(new Insets(20));
        BorderPane raiz = new BorderPane(contenido);
        ventana.setTitle("Reservar butacas");
        ventana.setScene(new Scene(raiz, 820, 680));
        ventana.setMinWidth(720);
        ventana.setMinHeight(580);
    }
    private GridPane crearMapaButacas(List<Butaca> ocupadas) {
        GridPane mapa = new GridPane();
        mapa.setHgap(6);
        mapa.setVgap(6);
        mapa.setPadding(new Insets(8));
        mapa.add(new Label("Fila"), 0, 0);
        for (int numero = 1;
                numero <= funcion.getSala().getButacasPorFila();
                numero++) {
            mapa.add(new Label(String.valueOf(numero)), numero, 0);
        }
        for (int indiceFila = 0;
                indiceFila < funcion.getSala().getCantidadFilas();
                indiceFila++) {
            String fila = String.valueOf((char) ('A' + indiceFila));
            mapa.add(new Label(fila), 0, indiceFila + 1);
            for (int numero = 1;
                    numero <= funcion.getSala().getButacasPorFila();
                    numero++) {
                Butaca butaca = new Butaca(fila, numero);
                ToggleButton boton = new ToggleButton(
                        String.valueOf(numero)
                );
                boton.setPrefSize(42, 34);
                if (ocupadas.contains(butaca)) {
                    boton.setDisable(true);
                    boton.setStyle("-fx-background-color: #bdbdbd;");
                } else {
                    boton.setStyle("-fx-background-color: #c8e6c9;");
                    boton.setOnAction(event ->
                            cambiarSeleccion(butaca, boton)
                    );
                }
                mapa.add(boton, numero, indiceFila + 1);
            }
        }
        return mapa;
    }
    private void cambiarSeleccion(Butaca butaca, ToggleButton boton) {
        if (boton.isSelected()) {
            seleccionadas.add(butaca);
            tiposPorButaca.put(butaca, new EntradaGeneral());
            boton.setStyle("-fx-background-color: #90caf9;");
        } else {
            seleccionadas.remove(butaca);
            tiposPorButaca.remove(butaca);
            boton.setStyle("-fx-background-color: #c8e6c9;");
        }
        listaSeleccionadas.getItems().setAll(
                new ArrayList<>(seleccionadas)
        );
        if (boton.isSelected()) {
            listaSeleccionadas.getSelectionModel().select(butaca);
        }
    }
    private void asignarTipoSeleccionado() {
        Butaca butaca =
                listaSeleccionadas.getSelectionModel().getSelectedItem();
        String tipoElegido = selectorTipo.getValue();
        if (butaca == null || tipoElegido == null) {
            return;
        }
        if ("Estudiante".equals(tipoElegido)) {
            tiposPorButaca.put(butaca, new EntradaEstudiante());
        } else {
            tiposPorButaca.put(butaca, new EntradaGeneral());
        }
    }
    private void confirmarReserva() {
        try {
            Cliente cliente = new Cliente(
                    campoIdentificacion.getText(),
                    campoNombre.getText(),
                    campoTelefono.getText()
            );
            Reserva reserva = sistema.getServicioReserva().confirmarReserva(
                    cliente,
                    funcion.getId(),
                    new LinkedHashMap<>(tiposPorButaca)
            );
            mostrarComprobante(reserva);
            ventana.close();
        } 
        catch (IllegalArgumentException | ExcepcionPersistencia ex) {
            mostrarError(ex.getMessage());
        }
    }
    private void mostrarComprobante(Reserva reserva) {
        NumberFormat formatoPrecio =
                NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
        StringBuilder detalle = new StringBuilder();
        detalle.append("Código: ").append(reserva.getCodigo()).append("\n");
        detalle.append("Cliente: ")
                .append(reserva.getCliente().getNombre()).append("\n");
        detalle.append("Película: ")
                .append(reserva.getFuncion().getPelicula().getTitulo()).append("\n");
        detalle.append("Butacas:\n");
        for (Entrada entrada : reserva.getEntradas()) {
            detalle.append("  ")
                    .append(entrada.getButaca())
                    .append(" — ")
                    .append(entrada.getTipo().getNombre())
                    .append(": ")
                    .append(formatoPrecio.format(entrada.getPrecioCobrado()))
                    .append("\n");
        }
        detalle.append("Total: ")
                .append(formatoPrecio.format(reserva.getImporteTotal()));
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Reserva confirmada");
        alerta.setHeaderText("Comprobante de Cine Sosa");
        alerta.setContentText(detalle.toString());
        alerta.showAndWait();
    }
    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("No se pudo confirmar la reserva");
        alerta.setHeaderText("Revisá los datos");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    public void mostrar() {
        ventana.showAndWait();
    }
}
