package dao;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import dto.Actividades;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class PrincipalControlador implements Initializable {
    
    @FXML
    private Button btnMesAnterior; //boton retroceder mes (calendario)
    @FXML
    private Button btnMesSiguiente; //boton adelantar mes (calendario)
    @FXML
    private Button btnFiltros; //boton para filtrar actividades
    @FXML
    private Button btnNuevaActividad; //boton que lleva a ventana CreacionActividad
    @FXML
    private Label lbMiPerfil; //lleva a ventana MiPerfil
    @FXML
    private Label lbMiComunidad; //lleva a ventana MiComunidad
    @FXML
    private Label lbListaEventos; //lleva a ventana ListaEventos
    @FXML
    private Label lbCerrarSesion;
    @FXML
    private Label month; //mes (calendario)
    @FXML
    private Label year; //año (calendario)
    @FXML
    private Stage primaryStage;
    @FXML
    private FlowPane calendar;  // Contenedor de los días del mes
    
    private LocalDate currentDate = LocalDate.now();  // Fecha actual
    
    // Mapa para almacenar actividades por día (usando int como clave para el día del mes)
    private Map<Integer, List<Actividades>> actividadesPorDia = new HashMap<>();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        // Convertir los labels en botones
        lbMiPerfil.setOnMouseClicked(event -> abrirMiPerfil());
        lbMiComunidad.setOnMouseClicked(event -> abrirMiComunidad());
        lbListaEventos.setOnMouseClicked(event -> abrirListaEventos());
        lbCerrarSesion.setOnMouseClicked(event -> cerrarSesion());
        
        // Actualizamos el calendario con el mes y año actuales
        actualizarCalendario();
    }
    
    @FXML
    private void abrirMiPerfil() { 
        try {
            // Cargar la ventana del perfil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaMiPerfil.fxml"));
            AnchorPane root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Mi Perfil - ConVive");
            stage.centerOnScreen();
            stage.show();

            Stage currentStage = (Stage) lbMiPerfil.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void abrirMiComunidad() {  
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaMiComunidad.fxml"));
            AnchorPane root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Mi Comunidad - ConVive");
            stage.centerOnScreen();
            stage.show();

            Stage currentStage = (Stage) lbMiComunidad.getScene().getWindow();
            currentStage.close(); // Cerrar la ventana actual
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirListaEventos() { 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaListaEventos.fxml"));
            AnchorPane root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Lista de Eventos - ConVive");
            stage.centerOnScreen();
            stage.show();

            Stage currentStage = (Stage) lbListaEventos.getScene().getWindow();
            currentStage.close(); //cerrar ventana actual
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cerrarSesion() { 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaInicioSesion.fxml"));
            AnchorPane root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Principal - ConVive");
            stage.centerOnScreen();
            stage.show();

            Stage currentStage = (Stage) lbCerrarSesion.getScene().getWindow();
            currentStage.close(); //cerrar la ventana 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Método para abrir la ventana "Nueva Actividad" y cerrar la ventana actual
    @FXML
    private void abrirCrearActividad(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaNuevaActividad.fxml"));
            AnchorPane root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Nueva Actividad - ConVive");
            stage.centerOnScreen();

            stage.show();

            Stage currentStage = (Stage) btnNuevaActividad.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para obtener las actividades de un día específico
    private List<Actividades> obtenerActividadesPorDia(int dia) {
        return actividadesPorDia.getOrDefault(dia, new ArrayList<>());
    }

    // Método para actualizar el calendario (con días del mes actual)
    private void actualizarCalendario() {
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");
        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");

        String monthText = currentDate.format(monthFormatter);
        String yearText = currentDate.format(yearFormatter);

        month.setText(monthText.toUpperCase());  // Muestra el mes en el label month 
        year.setText(yearText);    // Muestra el año en el label year

        calendar.getChildren().clear();  // Limpiar el FlowPane (contenedor de los días)

        int daysInMonth = currentDate.getMonth().length(currentDate.isLeapYear());

        // Crear botones para cada día del mes
        for (int day = 1; day <= daysInMonth; day++) {
            Button dayButton = new Button(String.valueOf(day));
            dayButton.setPrefSize(40, 40);

            // Comprobar si hay actividades programadas para este día
            List<Actividades> actividadesDelDia = obtenerActividadesPorDia(day);
            if (!actividadesDelDia.isEmpty()) {
                // Si hay actividades, poner el botón en verde
                dayButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
            } else {
                // Si no hay actividades, el botón es blanco
                dayButton.setStyle("-fx-background-color: white;");
            }

            // Agregar acción al botón de cada día (para cuando se hace clic)
            dayButton.setOnAction(event -> handleDayClick(event));

            // Añadir el botón del día al FlowPane
            calendar.getChildren().add(dayButton);
        }
    }


    // Método para manejar el clic en un día específico
    @FXML
    private void handleDayClick(ActionEvent event) {
    	Button dayButton = (Button) event.getSource();
        int day = Integer.parseInt(dayButton.getText());

        List<Actividades> actividadesDelDia = obtenerActividadesPorDia(day);
        if (actividadesDelDia.isEmpty()) {
            return;  // No mostrar ventana si no hay actividades
        }

        StringBuilder actividadesTexto = new StringBuilder("Actividades para el día " + day + ":\n");
        for (Actividades actividad : actividadesDelDia) {
            actividadesTexto.append("- ").append(actividad.getNombre()).append("\n");
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Actividades del día " + day);
        alert.setHeaderText(null);
        alert.setContentText(actividadesTexto.toString());
        alert.initOwner(primaryStage);  // Asegura que el diálogo sea modal respecto a la ventana principal
        alert.showAndWait();
    }

    // Método para ir al mes anterior
    @FXML
    private void mesAnterior() {
        currentDate = currentDate.minusMonths(1);
        actualizarCalendario();
    }

    // Método para ir al mes siguiente
    @FXML
    private void mesSiguiente() {
        currentDate = currentDate.plusMonths(1);
        actualizarCalendario();
    }
}
