package dao;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import dto.Actividades;
import BaseDeDatos.Conexion;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class EditarActividadControlador implements Initializable {

    @FXML private Button botonGuardar;
    @FXML private Button botonVolver;
    
    @FXML private DatePicker dateFechaActividad;
    @FXML private TextArea txtDescripcionActividad;
    @FXML private Spinner<Integer> spinnerEdadMin;
    @FXML private Spinner<Integer> spinnerEdadMax;
    @FXML private TextField txtHoraActividad;
    @FXML private TextField txtLugarActividad;
    @FXML private TextField txtNombreActividad;
    
    @FXML private Label labelErrorNombre;
    @FXML private Label labelErrorHora;
    @FXML private Label labelErrorEdad;

    private Actividades actividad;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicialización de los spinners
        spinnerEdadMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99, 0));
        spinnerEdadMax.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99, 0));
        
        // Listener de validaciones
        txtHoraActividad.setOnKeyReleased(event -> validarHora());
        spinnerEdadMax.setOnKeyReleased(event -> validarEdad());
        spinnerEdadMin.setOnKeyReleased(event -> validarEdad());
    }

    @FXML
    private void guardarCambios(ActionEvent event) {
        // Comprobar que la actividad no es null
        if (actividad == null) {
            alertaError("La actividad no está inicializada.");
            return;
        }
        
        // Obtener los valores de los campos del formulario
        String nombre = txtNombreActividad.getText();
        LocalDate fecha = dateFechaActividad.getValue();
        String hora = txtHoraActividad.getText();
        String descripcion = txtDescripcionActividad.getText();
        int edadMin = spinnerEdadMin.getValue();
        int edadMax = spinnerEdadMax.getValue();
        String lugar = txtLugarActividad.getText();

        // Validar campos vacíos
        if (nombre.isEmpty() || fecha == null || hora.isEmpty() || descripcion.isEmpty() || lugar.isEmpty()) {
            alertaError("Se deben rellenar todos los campos");
            return;
        }
        
        // Validar que el nombre contiene solo letras
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            alertaError("Solo pueden aparecer letras en el nombre");
            return;
        }

        // Validar el formato de la hora
        LocalTime horaActividad = null;
        try {
            horaActividad = LocalTime.parse(hora);
        } catch (Exception e) {
            alertaError("La hora no tiene un formato válido (hh:mm)");
            return;
        }

        // Comprobar que la edad mínima no sea mayor que la edad máxima
        if (edadMin > edadMax) {
            alertaError("La edad mínima no puede ser mayor que la edad máxima.");
            return;
        }

        // Actualización de la actividad en la base de datos
        try (Connection conn = Conexion.dameConexion("convive")) {
            String sql = "UPDATE actividad SET nombre = ?, descripcion = ?, fecha = ?, hora = ?, lugar = ?, edad_min = ?, edad_max = ? WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, nombre);
            pst.setString(2, descripcion);
            pst.setDate(3, java.sql.Date.valueOf(fecha));
            pst.setTime(4, java.sql.Time.valueOf(horaActividad));
            pst.setString(5, lugar);
            pst.setInt(6, edadMin);
            pst.setInt(7, edadMax);
            pst.setInt(8, actividad.getId());

            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas > 0) {
                alertaInformacion("Actividad editada correctamente");
                
                // Cerrar la ventana actual (VistaEditarActividad)
                Stage currentStage = (Stage) botonGuardar.getScene().getWindow();
                currentStage.close();

            } else {
                alertaError("Ocurrió un error al editar la actividad, inténtelo de nuevo más tarde");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            alertaError("Error en la base de datos. Por favor, inténtelo de nuevo.");
        }
    }


    // Método para recibir la actividad y asignarla al controlador
    public void setActividad(Actividades actividad) {
        this.actividad = actividad;
        prellenarCampos();
    }

    // Método para prellenar los campos de la vista con la información de la actividad
    private void prellenarCampos() {
        if (actividad != null) {
            txtNombreActividad.setText(actividad.getNombre());
            dateFechaActividad.setValue(actividad.getFecha());
            txtHoraActividad.setText(actividad.getHora().toString());
            txtDescripcionActividad.setText(actividad.getDescripcion());
            spinnerEdadMin.getValueFactory().setValue(actividad.getEdadMin());
            spinnerEdadMax.getValueFactory().setValue(actividad.getEdadMax());
            txtLugarActividad.setText(actividad.getLugar());
        } else {
            alertaError("No se ha encontrado la actividad.");
        }
    }

    // Métodos de validación
    private void validarHora() {
        String hora = txtHoraActividad.getText();
        try {
            LocalTime.parse(hora);
            txtHoraActividad.setStyle("-fx-border-color: green;");
            labelErrorHora.setText("");
        } catch (Exception e) {
            txtHoraActividad.setStyle("-fx-border-color: red;");
            labelErrorHora.setText("Formato de hora incorrecto");
        }
    }
    
    private void validarEdad() {
        int edadMin = spinnerEdadMin.getValue();
        int edadMax = spinnerEdadMax.getValue();
        
        if (edadMin > edadMax) {
            spinnerEdadMax.setStyle("-fx-border-color: red;");
            spinnerEdadMin.setStyle("-fx-border-color: red;");
            labelErrorEdad.setText("La edad mínima no puede ser mayor que la máxima");
        } else {
            spinnerEdadMax.setStyle("-fx-border-color: green;");
            spinnerEdadMin.setStyle("-fx-border-color: green;");
            labelErrorEdad.setText("");
        }
    }

    private void alertaError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void alertaInformacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
    private String vistaPrevia;

    public void setVistaPrevia(String vistaPrevia) {
        this.vistaPrevia = vistaPrevia;
    }

    
    
    // Método para volver a la ventana de la que venga
    @FXML
    void volver(ActionEvent event) {
        try {
            String vistaDestino = vistaPrevia.equals("vistaMiPerfil") ? "/vista/VistaMiPerfil.fxml" : "/vista/VistaListaEventos.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(vistaDestino));
            AnchorPane root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("ConVive");
            stage.show();

            // Cerrar la ventana actual
            Stage currentStage = (Stage) botonVolver.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    String ventana (String ventana ) {
    	return ventana; 
    }
    
}

