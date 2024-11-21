package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import BaseDeDatos.Conexion;
import dto.Actividades;

public class ListaEventosControlador implements Initializable {
    @FXML
    private Button botonVolver;
    @FXML
    private VBox actividadVBox; // Contenedor principal donde se agregan las actividades

    private ObservableList<Actividades> listaActividades;
    private int idUsuarioActual; // ID del usuario actual, pasado al controlador

    // Consultas SQL como constantes
    private static final String SQL_OBTENER_ACTIVIDADES = "SELECT * FROM actividad";
    private static final String SQL_OBTENER_USUARIO_POR_DNI = "SELECT id FROM usuarios WHERE dni = ?";
    private static final String SQL_OBTENER_APUNTADOS = "SELECT COUNT(*) FROM apuntados WHERE id_actividad = ?";
    private static final String SQL_BORRAR_USUARIO_DE_ACTIVIDAD = "DELETE FROM apuntados WHERE id_actividad = ? AND id_adulto = ?";
    private static final String SQL_USUARIO_REGISTRADO = "SELECT COUNT(*) FROM apuntados WHERE id_actividad = ? AND id_adulto = ?";
    private static final String SQL_APUNTAR_USUARIO = "INSERT INTO apuntados (id_adulto, id_actividad) VALUES (?, ?)";
    private static final String SQL_ELIMINAR_ACTIVIDAD = "DELETE FROM actividad WHERE id = ?";

    public ListaEventosControlador() {
        listaActividades = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos(); // Cargar actividades desde la base de datos
    }

    public void setUsuarioActual(int idUsuario) {
        this.idUsuarioActual = idUsuario;
    }

    public void inicializarUsuarioActual(String dniGlobal) {
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(SQL_OBTENER_USUARIO_POR_DNI)) {

            stmt.setString(1, dniGlobal);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    this.idUsuarioActual = rs.getInt("id");
                    System.out.println("Usuario actual inicializado con ID: " + idUsuarioActual);
                } else {
                    System.err.println("No se encontró un usuario con el DNI proporcionado.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al inicializar usuario actual: " + e.getMessage());
        }
    }

    public void cargarDatos() {
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(SQL_OBTENER_ACTIVIDADES);
             ResultSet rs = stmt.executeQuery()) {

            actividadVBox.getChildren().clear(); // Limpiar las actividades previas

            while (rs.next()) {
                Actividades actividad = new Actividades(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getDate("fecha").toLocalDate(),
                    rs.getTime("hora").toLocalTime(),
                    rs.getString("lugar"),
                    rs.getInt("edad_min"),
                    rs.getInt("edad_max"),
                    rs.getInt("creador")
                );

                VBox actividadBox = crearContainerActividad(actividad);
                actividadVBox.getChildren().add(actividadBox);
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar datos de actividades: " + e.getMessage());
        }
    }

    private VBox crearContainerActividad(Actividades actividad) {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #22504e; -fx-border-width: 2px;");

        // Etiquetas de la actividad
        Label nombreLabel = new Label("Nombre: " + actividad.getNombre());
        Label descripcionLabel = new Label("Descripción: " + actividad.getDescripcion());
        Label fechaLabel = new Label("Fecha: " + actividad.getFecha());
        Label horaLabel = new Label("Hora: " + actividad.getHora());
        Label edadesLabel = new Label("Edades: " + actividad.getEdadMin() + " - " + actividad.getEdadMax());
        Label apuntadosLabel = new Label("Número de apuntados: " + getApuntados(actividad.getId()));

        // Aplicar estilos
        nombreLabel.setFont(Font.font(18));
        descripcionLabel.setFont(Font.font(14));
        fechaLabel.setFont(Font.font(14));
        horaLabel.setFont(Font.font(14));
        edadesLabel.setFont(Font.font(14));
        apuntadosLabel.setFont(Font.font(14));

        // Verificar si el usuario es el creador
        if (actividad.getCreador() == idUsuarioActual) {
            container.getChildren().addAll(crearBotonEliminar(actividad, container), crearBotonModificar());
        } else {
            container.getChildren().add(crearBotonApuntame(actividad, apuntadosLabel));
        }

        container.getChildren().addAll(nombreLabel, descripcionLabel, fechaLabel, horaLabel, edadesLabel, apuntadosLabel);
        return container;
    }

    private Button crearBotonEliminar(Actividades actividad, VBox container) {
        Button deleteButton = new Button("Eliminar");
        deleteButton.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: #FFFFFF;");
        deleteButton.setOnAction(event -> eliminarActividad(actividad.getId(), container));
        return deleteButton;
    }

    private Button crearBotonModificar() {
        Button modifyButton = new Button("Modificar");
        modifyButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #FFFFFF;");
        return modifyButton;
    }

    Button crearBotonApuntame(Actividades actividad, Label apuntadosLabel) {
        Button botonApuntame = new Button();
        boolean estaApuntadoInicial = estaElUsuarioRegistrado(actividad.getId(), idUsuarioActual);

        // Usamos un array para almacenar el estado mutable
        boolean[] estaApuntado = { estaApuntadoInicial };

        actualizarBotonApuntame(botonApuntame, estaApuntado[0]);

        botonApuntame.setOnAction(event -> {
            if (estaApuntado[0]) {
                borrarUsuarioDeActividad(actividad.getId(), idUsuarioActual);
            } else {
                apuntarUsuarioActividad(actividad.getId(), idUsuarioActual);
            }
            estaApuntado[0] = !estaApuntado[0];
            actualizarBotonApuntame(botonApuntame, estaApuntado[0]);
            apuntadosLabel.setText("Número de apuntados: " + getApuntados(actividad.getId()));
        });

        return botonApuntame;
    }


    private void actualizarBotonApuntame(Button boton, boolean estaApuntado) {
        if (estaApuntado) {
            boton.setText("Elimíname");
            boton.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: #FFFFFF;");
        } else {
            boton.setText("Apúntame");
            boton.setStyle("-fx-background-color: #006D77; -fx-text-fill: #FFFFFF;");
        }
    }

    private int getApuntados(int actividadId) {
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(SQL_OBTENER_APUNTADOS)) {

            stmt.setInt(1, actividadId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener apuntados: " + e.getMessage());
        }
        return 0;
    }

    private void borrarUsuarioDeActividad(int actividadId, int idUsuarioActual) {
        ejecutarUpdate(SQL_BORRAR_USUARIO_DE_ACTIVIDAD, actividadId, idUsuarioActual);
    }

    private boolean estaElUsuarioRegistrado(int actividadId, int idUsuarioActual) {
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(SQL_USUARIO_REGISTRADO)) {

            stmt.setInt(1, actividadId);
            stmt.setInt(2, idUsuarioActual);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar registro del usuario: " + e.getMessage());
        }
        return false;
    }

    private void apuntarUsuarioActividad(int actividadId, int idUsuarioActual) {
        ejecutarUpdate(SQL_APUNTAR_USUARIO, idUsuarioActual, actividadId);
    }

    private void eliminarActividad(int actividadId, VBox container) {
        ejecutarUpdate(SQL_ELIMINAR_ACTIVIDAD, actividadId);
        actividadVBox.getChildren().remove(container); // Eliminar el contenedor visualmente
    }

    private void ejecutarUpdate(String sql, Object... params) {
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al ejecutar la actualización: " + e.getMessage());
        }
    }

    @FXML
    void volver(ActionEvent event) { 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaPrincipal.fxml"));
            AnchorPane root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Principal - ConVive");
            stage.show();

            Stage currentStage = (Stage) botonVolver.getScene().getWindow();
            currentStage.close(); //cerrar la ventana mi perfil

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}