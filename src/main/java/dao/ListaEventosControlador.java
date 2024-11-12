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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import BaseDeDatos.Conexion;
import dto.Actividades;

public class ListaEventosControlador implements Initializable {
    @FXML
    private Button botonVolver;
    @FXML
    private VBox actividadVBox; // Contenedor principal donde se agregan las actividades
    
    private ObservableList<Actividades> listaActividades;
    private int currentUserId; // Id del usuario actual, se debe establecer según el contexto de la aplicación

    public ListaEventosControlador() {
        listaActividades = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();  // Cargar actividades desde la base de datos
    }

    public void cargarDatos() {
        String sql = "SELECT * FROM actividad";

        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(sql);
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

                VBox actividadBox = crearContainerActividad(actividad, currentUserId);
                actividadVBox.getChildren().add(actividadBox);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void volver(ActionEvent event) { //BOTON VOLVER AL HOME
    	
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
    
    private VBox crearContainerActividad(Actividades actividad, int currentUserId) {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #22504e; -fx-border-width: 2px;");

        Label nombreLabel = new Label("Nombre: " + actividad.getNombre());
        Label descripcionLabel = new Label("Descripción: " + actividad.getDescripcion());
        Label fechaLabel = new Label("Fecha: " + actividad.getFecha().toString());
        Label horaLabel = new Label("Hora: " + actividad.getHora().toString());
        Label edadesLabel = new Label("Edades: " + actividad.getEdadMin() + " - " + actividad.getEdadMax());
        
        // Inicializar el label de apuntados con el número de usuarios apuntados
        Label apuntadosLabel = new Label("Número de apuntados: " + getApuntados(actividad.getId()));
        apuntadosLabel.setFont(Font.font(14));

        nombreLabel.setFont(Font.font(18));
        descripcionLabel.setFont(Font.font(14));
        fechaLabel.setFont(Font.font(14));
        horaLabel.setFont(Font.font(14));
        edadesLabel.setFont(Font.font(14));

        // Verificar si el usuario es el creador de la actividad
        if (actividad.getCreador() == currentUserId) {
            // Botón Eliminar
            Button deleteButton = new Button("Eliminar");
            deleteButton.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: #FFFFFF; -fx-font-size: 16;");
            deleteButton.setOnAction(event -> eliminarActividad(actividad.getId(), container));

            // Botón Modificar (sin funcionalidad por ahora)
            Button modifyButton = new Button("Modificar");
            modifyButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #FFFFFF; -fx-font-size: 16;");

            container.getChildren().addAll(deleteButton, modifyButton);
        } else {
            // Mostrar botón "Apuntarme"/"Elimíname"
            Button botonApuntame = new Button();
            boolean estaApuntado = estaElUsuarioRegistrado(actividad.getId(), currentUserId);

            if (estaApuntado) {
                botonApuntame.setText("Elimíname");
                botonApuntame.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: #FFFFFF; -fx-font-size: 16;");
            } else {
                botonApuntame.setText("Apúntame");
                botonApuntame.setStyle("-fx-background-color: #006D77; -fx-text-fill: #FFFFFF; -fx-font-size: 16;");
            }

            // BOTONES APUNTAME/ELIMINAME -> sin funcionalidad 
            /* botonApuntame.setOnAction(event -> {
                if (estaApuntado) {
                    borrarUsuarioDeActividad(actividad.getId(), currentUserId);
                    botonApuntame.setText("Apúntame");
                    botonApuntame.setStyle("-fx-background-color: #006D77; -fx-text-fill: #FFFFFF; -fx-font-size: 16;");
                } else {
                    apuntarUsuarioActividad(actividad.getId(), currentUserId);
                    botonApuntame.setText("Elimíname");
                    botonApuntame.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: #FFFFFF; -fx-font-size: 16;");
                }
                // Actualizar el número de apuntados
                apuntadosLabel.setText("Número de apuntados: " + getApuntados(actividad.getId())); 
            });*/

            container.getChildren().add(botonApuntame);
        }

        // Añadir todos los elementos al container
        container.getChildren().addAll(nombreLabel, descripcionLabel, fechaLabel, horaLabel, edadesLabel, apuntadosLabel);
        return container;
    }
    
    private void borrarUsuarioDeActividad(int actividadId, int userId) {
        String sql = "DELETE FROM apuntados WHERE id_actividad = ? AND id_adulto = ?";
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, actividadId);
            stmt.setInt(2, userId);  // El id del usuario

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Usuario eliminado de la actividad.");
            } else {
                System.out.println("Hubo un problema al intentar eliminar al usuario.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean estaElUsuarioRegistrado(int actividadId, int userId) {
        String sql = "SELECT COUNT(*) FROM apuntados WHERE id_actividad = ? AND id_adulto = ?";
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, actividadId);
            stmt.setInt(2, userId);  // Asumimos que el id_adulto es igual a userId

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;  // Si el conteo es mayor que 0, significa que el usuario está apuntado
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Si no se encuentra registro, el usuario no está apuntado
    }

    private void apuntarUsuarioActividad(int actividadId, int userId) {
        // Inscribir al adulto directamente
        String sql = "INSERT INTO apuntados (id_adulto, id_actividad) VALUES (?, ?)";
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);  // El adulto
            stmt.setInt(2, actividadId);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Usuario inscrito correctamente.");
            } else {
                System.out.println("Hubo un problema al intentar inscribir al usuario.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void eliminarActividad(int actividadId, VBox actividadBox) {
        String sql = "DELETE FROM actividad WHERE id = ?";
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, actividadId);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                actividadVBox.getChildren().remove(actividadBox);
                System.out.println("Actividad eliminada.");
            } else {
                System.out.println("Hubo un problema al intentar eliminar la actividad.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getApuntados(int actividadId) {
        String sql = "SELECT COUNT(*) FROM apuntados WHERE id_actividad = ?";
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, actividadId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);  // Retorna el número de usuarios apuntados
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;  // Si no hay registros, devuelve 0
    }
}
