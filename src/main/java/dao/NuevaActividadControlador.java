package dao;

import dto.Actividades;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import BaseDeDatos.Conexion;

public class NuevaActividadControlador {

    // FXML elements
    @FXML
    private TextField txtNombreActividad;
    @FXML
    private DatePicker dateFechaActividad;
    @FXML
    private TextField txtHoraActividad;
    @FXML
    private TextArea txtDescripcionActividad;
    @FXML
    private TextField txtEdadMin;   // Cambiado de txtEdadMinActividad a txtEdadMin
    @FXML
    private TextField txtEdadMax;   // Cambiado de txtEdadMaxActividad a txtEdadMax
    @FXML
    private TextField txtLugarActividad;
    @FXML
    private Button btnAniadirActividad;
    @FXML
    private Button botonVolver;

    // Método para manejar el evento de añadir actividad
    @FXML
    public void anadirActividad() {
        String nombre = txtNombreActividad.getText();
        LocalDate fecha = dateFechaActividad.getValue();
        String hora = txtHoraActividad.getText();
        String descripcion = txtDescripcionActividad.getText();
        String edadMinStr = txtEdadMin.getText();  // Leemos el campo de edad mínima
        String edadMaxStr = txtEdadMax.getText();  // Leemos el campo de edad máxima
        String lugar = txtLugarActividad.getText();

        // Validaciones
        if (nombre.isEmpty() || fecha == null || hora.isEmpty() || descripcion.isEmpty() || edadMinStr.isEmpty() || edadMaxStr.isEmpty() || lugar.isEmpty()) {
            mostrarError("Por favor, completa todos los campos.");
            return;
        }

        // Validar el formato de la hora
        LocalTime horaActividad = null;
        try {
            horaActividad = LocalTime.parse(hora);
        } catch (Exception e) {
            mostrarError("La hora no tiene un formato válido (hh:mm).");
            return;
        }

        // Validar los valores de las edades (convertir los textos a enteros)
        int edadMin = 0, edadMax = 0;
        try {
            edadMin = Integer.parseInt(edadMinStr);
            edadMax = Integer.parseInt(edadMaxStr);
        } catch (NumberFormatException e) {
            mostrarError("Las edades deben ser números válidos.");
            return;
        }

        // Comprobar que la edad mínima no sea mayor que la edad máxima
        if (edadMin > edadMax) {
            mostrarError("La edad mínima no puede ser mayor que la edad máxima.");
            return;
        }

        // Convertimos la fecha y la hora en ZonedDateTime
        ZonedDateTime fechaHoraActividad = ZonedDateTime.of(fecha, horaActividad, ZoneId.systemDefault());

        // Crear la actividad (asumimos que el creador es un ID fijo por ahora, puedes cambiarlo según tu lógica)
        Actividades actividad = new Actividades(nombre, descripcion, fechaHoraActividad, lugar, edadMin, edadMax, 1);  // 1 es el ID del creador

        // Guardar la actividad en la base de datos
        agregarActividadBD(actividad);

        // Confirmación de éxito
        mostrarExito("Actividad añadida correctamente.");
    }

    @FXML
    public void volver() { // BOTÓN VOLVER AL HOME
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaPrincipal.fxml"));
            AnchorPane root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Principal - ConVive");
            stage.show();

            Stage currentStage = (Stage) botonVolver.getScene().getWindow();
            currentStage.close(); // Cerrar la ventana actual
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Métodos auxiliares para mostrar mensajes de error y éxito
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Método para agregar la actividad a la base de datos
    private void agregarActividadBD(Actividades actividad) {
        String sql = "INSERT INTO actividad (nombre, descripcion, fecha, hora, lugar, edad_min, edad_max, creador) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Seteamos los parámetros en la consulta SQL
            stmt.setString(1, actividad.getNombre());
            stmt.setString(2, actividad.getDescripcion());
            stmt.setDate(3, java.sql.Date.valueOf(actividad.getFechaHora().toLocalDate()));  // Convertimos a java.sql.Date
            stmt.setTime(4, java.sql.Time.valueOf(actividad.getFechaHora().toLocalTime()));   // Convertimos a java.sql.Time
            stmt.setString(5, actividad.getLugar());
            stmt.setInt(6, actividad.getEdadMin());
            stmt.setInt(7, actividad.getEdadMax());
            stmt.setInt(8, actividad.getCreador());  // ID del creador

            // Ejecutamos la consulta
            stmt.executeUpdate();
            System.out.println("Actividad añadida a la base de datos: " + actividad);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarError("Ocurrió un error al añadir la actividad en la base de datos.");
        }
    }
}
