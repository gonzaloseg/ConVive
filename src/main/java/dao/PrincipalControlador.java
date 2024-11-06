package dao;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class PrincipalControlador implements Initializable{
	@FXML
    private Button botonVolver;
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Inicializamos las acciones de las etiquetas
        lbMiPerfil.setOnMouseClicked(event -> abrirMiPerfil());
        lbCerrarSesion.setOnMouseClicked(event -> cerrarSesion());
        lbMiComunidad.setOnMouseClicked(event -> abrirMiComunidad());
        lbListaEventos.setOnMouseClicked(event -> abrirListaEventos());
        
        // Actualizamos el calendario con el mes y año actuales
        actualizarCalendario();
	}
	
	@FXML
    void volver() { //BOTON VOLVER 
		Stage escenario = (Stage) this.botonVolver.getScene().getWindow();
		escenario.close();
    }
	
	// Método para cerrar sesión (cierra ConVive)
	private void cerrarSesion() {
        // Cerrar la ventana principal
        Stage escenario = (Stage) lbCerrarSesion.getScene().getWindow();
        escenario.close();
        
	}

    // Método para abrir la ventana "Mi perfil" y cerrar la ventana actual
    @FXML
	private void abrirMiPerfil() {
        try {
            // Cargar la nueva ventana ("VistaMiPerfil.fxml")
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaMiPerfil.fxml"));
            AnchorPane root = loader.load();

            // Crear una nueva escena con la vista "Mi perfil - ConVive"
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Mi Perfil - ConVive");

            // Mostrar la nueva ventana
            stage.show();

            // Cerrar la ventana actual
            Stage currentStage = (Stage) lbMiPerfil.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Método para abrir la ventana "Mi comunidad" y cerrar la ventana actual
    @FXML
    private void abrirMiComunidad() {
        try {
            // Cargar la nueva ventana ("VistaMiComunidad.fxml")
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaMiComunidad.fxml"));
            AnchorPane root = loader.load();

            // Crear una nueva escena con la vista "Mi comunidad - ConVive"
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Mi Comunidad - ConVive");

            // Mostrar la nueva ventana
            stage.show();

            // Cerrar la ventana actual
            Stage currentStage = (Stage) lbMiComunidad.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Método para abrir la ventana "Lista de Eventos" y cerrar la ventana actual
    @FXML
    private void abrirListaEventos() {
        try {
            // Cargar la nueva ventana ("VistaListaEventos.fxml")
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaListaEventos.fxml"));
            AnchorPane root = loader.load();

            // Crear una nueva escena con la vista "Lista de Eventos - ConVive"
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Lista de Eventos - ConVive");

            // Mostrar la nueva ventana
            stage.show();

            // Cerrar la ventana actual
            Stage currentStage = (Stage) lbListaEventos.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Método para abrir la ventana "Nueva Actividad" y cerrar la ventana actual
    @FXML
    private void abrirCrearActividad(ActionEvent event) {
        try {
            // Cargar la nueva ventana ("VistaNuevaActividad.fxml")
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaNuevaActividad.fxml"));
            AnchorPane root = loader.load();

            // Crear una nueva escena con la vista "Nueva Actividad - ConVive"
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Nueva Actividad - ConVive");

            // Mostrar la nueva ventana
            stage.show();

            // Cerrar la ventana actual
            Stage currentStage = (Stage) btnNuevaActividad.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Método para actualizar el calendario (con días del mes actual)
    private void actualizarCalendario() {
    	// Crear el formateador para obtener solo el mes en inglés
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");
        // Crear el formateador para obtener solo el año
        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");

        // Obtener el mes y el año de la fecha actual
        String monthText = currentDate.format(monthFormatter);
        String yearText = currentDate.format(yearFormatter);
        
        // Mostrar el mes y el año en los Labels correspondientes
        month.setText(monthText.toUpperCase());  // Muestra el mes en el label month 
        year.setText(yearText);    // Muestra el año en el label year

        // Limpiar el FlowPane (el contenedor de los días)
        calendar.getChildren().clear();

        // Obtener la cantidad de días en el mes y el primer día de la semana
        int daysInMonth = currentDate.getMonth().length(currentDate.isLeapYear());
        int firstDayOfWeek = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), 1).getDayOfWeek().getValue();

        // Añadir espacios vacíos al principio del mes para alinear los días correctamente
        for (int i = 0; i < firstDayOfWeek - 1; i++) {
            calendar.getChildren().add(new Label(""));  // Espacios vacíos
        }
        
        // Crear botones para cada día del mes
        for (int day = 1; day <= daysInMonth; day++) {
            Button dayButton = new Button(String.valueOf(day));
            dayButton.setPrefSize(40, 40);
            dayButton.setStyle("-fx-background-color: lightgray;");

            // Agregar acción al botón de cada día (para cuando se hace clic)
            dayButton.setOnAction(event -> handleDayClick(event));

            // Añadir el botón del día al FlowPane
            calendar.getChildren().add(dayButton);
        }
    }
    
    // Método para manejar el clic en un día específico
    @FXML
    private void handleDayClick(ActionEvent event) {
        Button dayButton = (Button) event.getSource();  // Obtener el botón que se ha clickeado
        String dayText = dayButton.getText();  // Obtener el texto del botón (número del día)

        System.out.println("Has seleccionado el día: " + dayText);
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
