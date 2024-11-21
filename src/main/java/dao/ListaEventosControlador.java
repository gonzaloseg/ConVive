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

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import BaseDeDatos.Conexion;
import dto.Actividades;
import dto.UsuarioGlobal;

public class ListaEventosControlador implements Initializable {
    @FXML private Button botonVolver;
    @FXML private VBox actividadVBox;
    private static final String SQL_OBTENER_ACTIVIDADES = "SELECT * FROM actividad ORDER BY fecha DESC";

    /*__________________________________________________________*/

    public void initialize(URL location, ResourceBundle resources) {
    	cargarDatos();
    }
    
    /* CARGA LOS DATOS DE CADA ACTIVIDAD */
    public void cargarDatos() {
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(SQL_OBTENER_ACTIVIDADES);
             ResultSet rs = stmt.executeQuery()) {
        	String dniGlobal = UsuarioGlobal.getInstacne().getDniGlobal();
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

                VBox actividadBox = crearContainerActividad(actividad, dniGlobal);
                actividadVBox.getChildren().add(actividadBox);
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar datos de actividades: " + e.getMessage());
        }
    }

    /* CREAR LOS CONTENEDORES PARA LAS ACTIVIDADES */
    private VBox crearContainerActividad(Actividades actividad, String dniGlobal) {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: #FFFFFF;");

        // Etiquetas de la actividad
        Label nombreLabel = new Label(actividad.getNombre());
        Label descripcionLabel = new Label(actividad.getDescripcion());
        Label fechaLabel = new Label("Fecha: " + actividad.getFecha());
        Label horaLabel = new Label("Hora: " + actividad.getHora());
        Label edadesLabel = new Label("Edades: " + actividad.getEdadMin() + " - " + actividad.getEdadMax());
        //Label apuntadosLabel = new Label("Número de apuntados: " + getApuntados(actividad.getId()));

        // Aplicar estilos
        nombreLabel.setFont(Font.font(18));
        descripcionLabel.setFont(Font.font(14));
        fechaLabel.setFont(Font.font(14));
        horaLabel.setFont(Font.font(14));
        edadesLabel.setFont(Font.font(14));
        //apuntadosLabel.setFont(Font.font(14));

        // Obtener el ID del usuario autenticado
        int idUsuario = -1;
        String sqlObtenerId = "SELECT id FROM adulto WHERE dni = ? UNION SELECT id FROM menor WHERE dni = ?";
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement pst = conn.prepareStatement(sqlObtenerId)) {
            pst.setString(1, dniGlobal);
            pst.setString(2, dniGlobal);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                idUsuario = rs.getInt("id");
            }
            //System.out.println(idUsuario);
        } catch (SQLException e) {
            System.err.println("Error al obtener ID del usuario: " + e.getMessage());
        }

        // Verificar si el usuario está inscrito
        boolean estaApuntado = false;
        if (idUsuario != -1) {
            String sqlVerificarApuntado = "SELECT * FROM apuntados WHERE id_adulto = ? AND id_actividad = ?";
            try (Connection conn = Conexion.dameConexion("convive");
                 PreparedStatement pst = conn.prepareStatement(sqlVerificarApuntado)) {
                pst.setInt(1, idUsuario);
                pst.setInt(2, actividad.getId());

                ResultSet rs = pst.executeQuery();
                estaApuntado = rs.next();
            } catch (SQLException e) {
                System.err.println("Error al verificar inscripción: " + e.getMessage());
            }
        }

        // Crear botones
        Button apuntarButton = new Button();
        if (idUsuario == actividad.getCreador()) {
            // Si el usuario es el creador de la actividad, mostrar los botones "Eliminar" y "Editar"
            Button eliminarButton = new Button("Eliminar actividad");
            eliminarButton.setOnAction(event -> eliminarActividad(actividad.getId()));

            Button editarButton = new Button("Editar actividad");
            editarButton.setOnAction(event -> editarActividad(actividad.getId()));

            // Agregar los botones al contenedor
            container.getChildren().addAll(nombreLabel, descripcionLabel, fechaLabel, horaLabel, edadesLabel, eliminarButton, editarButton);
        } else {
            // Si no es el creador, mostrar el botón de "Apuntarse" o "Desapuntarse"
            int finalIdUsuario = idUsuario; // Declarar una nueva variable final

            if (estaApuntado) {
                apuntarButton.setText("Desapuntarse");
                apuntarButton.setOnAction(event -> desapuntarse(finalIdUsuario, actividad.getId()));
            } else {
                apuntarButton.setText("Apuntarse");
                apuntarButton.setOnAction(event -> apuntarse(finalIdUsuario, actividad.getId()));
            }
            container.getChildren().addAll(nombreLabel, descripcionLabel, fechaLabel, horaLabel, edadesLabel, apuntarButton);
        }
        return container;
    }
    
    /* APUNTARSE A UNA ACTIVIDAD */
    private void apuntarse(int idUsuario, int idActividad) {
        String sqlApuntarse = "INSERT INTO apuntados (id_adulto, id_actividad) VALUES (?, ?)";
        
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement pst = conn.prepareStatement(sqlApuntarse)) {
            
            // Verificar si el usuario ya está inscrito
            String sqlVerificar = "SELECT * FROM apuntados WHERE id_adulto = ? AND id_actividad = ?";
            try (PreparedStatement pstVerificar = conn.prepareStatement(sqlVerificar)) {
                pstVerificar.setInt(1, idUsuario);
                pstVerificar.setInt(2, idActividad);
                ResultSet rs = pstVerificar.executeQuery();
                
                // Si no está inscrito, insertar el registro
                if (!rs.next()) {
                    pst.setInt(1, idUsuario);
                    pst.setInt(2, idActividad);
                    int rowsAffected = pst.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Usuario apuntado a la actividad.");
                        cargarDatos(); // Recargar las actividades para actualizar el estado de inscripción
                    } else {
                        System.err.println("Error al apuntarse a la actividad.");
                    }
                } else {
                    System.out.println("El usuario ya está inscrito en esta actividad.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al apuntarse a la actividad: " + e.getMessage());
        }
    }

    /* DESAPUNTARSE DE UNA ACTIVIDAD*/
    private void desapuntarse(int idUsuario, int idActividad) {
        String sqlDesapuntarse = "DELETE FROM apuntados WHERE id_adulto = ? AND id_actividad = ?";
        
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement pst = conn.prepareStatement(sqlDesapuntarse)) {
            
            pst.setInt(1, idUsuario);
            pst.setInt(2, idActividad);
            int rowsAffected = pst.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Usuario desapuntado de la actividad.");
                cargarDatos(); // Recargar las actividades para actualizar el estado de inscripción
            } else {
                System.err.println("Error al desapuntarse de la actividad.");
            }

        } catch (SQLException e) {
            System.err.println("Error al desapuntarse de la actividad: " + e.getMessage());
        }
    }

	
    /* ELIMINAR ACTIVIDAD */
    private void eliminarActividad(int actividadId) {
        String sqlEliminar = "DELETE FROM actividad WHERE id = ?";
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement pst = conn.prepareStatement(sqlEliminar)) {
            pst.setInt(1, actividadId);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Actividad eliminada con éxito.");
                cargarDatos();
            } else {
                System.err.println("No se pudo eliminar la actividad.");
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar actividad: " + e.getMessage());
        }
    }

    /* EDITAR ACTIVIDAD */    
    private void editarActividad(int actividadId) {
        System.out.println("Editar actividad con ID: " + actividadId + ". PENDIENTE DE CREAR VISTA");
        // TODO AÑADIR VENTANA PARA EDITARLA
    }
    
    
    
    
    
	/* BOTÓN PARA VOLVER A LA PÁGINA PRINCIPAL*/
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
            currentStage.close(); //cerrar la ventana

        } catch (Exception e) {
            e.printStackTrace();
        }
    } 

}