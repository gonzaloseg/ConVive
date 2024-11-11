package dao;

import dto.Actividades;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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

public class ListaEventosControlador implements Initializable {
	@FXML
	private Button botonVolver;
    @FXML
    private VBox actividadVBox; // Contenedor principal donde se agregan las actividades
    
   
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();  // Llamamos a cargarDatos() para cargar las actividades desde la base de datos
    }
    private ObservableList<Actividades> listaActividades;

    public ListaEventosControlador() {
        listaActividades = FXCollections.observableArrayList();
    }

    public void cargarDatos() {
        String sql = "SELECT * FROM actividad";  // Suponiendo que la tabla en la base de datos se llama "actividad"

        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            actividadVBox.getChildren().clear(); // Limpiar las actividades previas

            while (rs.next()) {
                // Obtener los datos de la actividad de la base de datos
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                String fecha = rs.getDate("fecha").toString();
                String hora = rs.getTime("hora").toString();
                String lugar = rs.getString("lugar");
                int edadMin = rs.getInt("edad_min");
                int edadMax = rs.getInt("edad_max");

                // Crear un VBox para cada actividad
                VBox actividadBox = new VBox(5);
                actividadBox.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 10; -fx-border-radius: 10px; -fx-background-radius: 10px;");
                
                // Crear etiquetas para cada campo de la actividad
                Label lblNombre = new Label("Nombre: " + nombre);
                lblNombre.setFont(new Font(18));
                Label lblDescripcion = new Label("Descripción: " + descripcion);
                Label lblFechaHora = new Label("Fecha: " + fecha + " | Hora: " + hora);
                Label lblLugar = new Label("Lugar: " + lugar);
                Label lblEdad = new Label("Edades: " + edadMin + " - " + edadMax);
                
                // Agregar todas las etiquetas al VBox de la actividad
                actividadBox.getChildren().addAll(lblNombre, lblDescripcion, lblFechaHora, lblLugar, lblEdad);

                // Agregar el VBox de la actividad al VBox principal en el ScrollPane
                actividadVBox.getChildren().add(actividadBox);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Volver a ventana principal
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
