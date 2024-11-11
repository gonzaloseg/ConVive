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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
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
    private TableView<Actividades> tablaActividades;
    @FXML
    private TableColumn<Actividades, String> columnaNombre;
    @FXML
    private TableColumn<Actividades, String> columnaDescripcion;
    @FXML
    private TableColumn<Actividades, String> columnaFecha;
    @FXML
    private TableColumn<Actividades, String> columnaHora;
    @FXML
    private TableColumn<Actividades, String> columnaLugar;
    @FXML
    private TableColumn<Actividades, Integer> columnaEdadMin;
    @FXML
    private TableColumn<Actividades, Integer> columnaEdadMax;
    @FXML
    private TableColumn<Actividades, Integer> columnaCreador;
    
   
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();  // Llamamos a cargarDatos() para cargar las actividades desde la base de datos
    }
    private ObservableList<Actividades> listaActividades;

    public ListaEventosControlador() {
        listaActividades = FXCollections.observableArrayList();
    }

    // Método que se llama para cargar los datos de la base de datos en la tabla
    public void cargarDatos() {
        String sql = "SELECT * FROM actividad";  // Suponiendo que la tabla en la base de datos se llama "actividad"

        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            listaActividades.clear();  // Limpiar la lista antes de llenarla nuevamente

            while (rs.next()) {
                // Crear un objeto Actividades con los datos de la base de datos
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
                listaActividades.add(actividad);
            }

            // Establecer los valores en las columnas de la tabla
            columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            columnaDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
            columnaFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
            columnaHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
            columnaLugar.setCellValueFactory(new PropertyValueFactory<>("lugar"));
            columnaEdadMin.setCellValueFactory(new PropertyValueFactory<>("edadMin"));
            columnaEdadMax.setCellValueFactory(new PropertyValueFactory<>("edadMax"));
            columnaCreador.setCellValueFactory(new PropertyValueFactory<>("creador"));

            // Asignar la lista de actividades a la tabla
            tablaActividades.setItems(listaActividades);

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
