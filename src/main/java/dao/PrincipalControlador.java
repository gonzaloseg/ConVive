package dao;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import BaseDeDatos.Conexion;
import dto.Actividades;
import dto.UsuarioGlobal;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    private VBox vboxActividades; 
    
    // Mapa para almacenar actividades por día (usando int como clave para el día del mes)
    private Map<Integer, List<Actividades>> actividadesPorDia = new HashMap<>();
    private List<Actividades> todasLasActividades = new ArrayList<>(); // Lista de actividades cargadas
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        // Convertir los labels en botones
        lbMiPerfil.setOnMouseClicked(event -> abrirMiPerfil());
        lbMiComunidad.setOnMouseClicked(event -> abrirMiComunidad());
        lbListaEventos.setOnMouseClicked(event -> abrirListaEventos());
        lbCerrarSesion.setOnMouseClicked(event -> cerrarSesion());
        
        // Actualizamos el calendario con el mes y año actuales
        todasLasActividades = obtenerActividadesDesdeBaseDeDatos();
        cargarActividades();
        actualizarCalendario();
        
    }
    
    @FXML
    private void abrirMiPerfil() { 
        try {
            // Cargar la ventana del perfil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaMiPerfil.fxml"));
            AnchorPane root = loader.load();

            // Obtener la instancia del controlador y pasar los datos (MI PERFIL)
            MiPerfilControlador controller = loader.getController();
            controller.rellenarPerfil(UsuarioGlobal.getInstacne().getDniGlobal(), UsuarioGlobal.getInstacne().getTabla());
            
            
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
    
    private void cargarActividades() {
    	String SQL_OBTENER_ACTIVIDADES = "SELECT id, nombre, descripcion, fecha, hora, lugar, edad_min, edad_max, creador FROM actividad";

        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(SQL_OBTENER_ACTIVIDADES);
             ResultSet rs = stmt.executeQuery()) {

            // Limpiar la lista de actividades previamente cargadas
            todasLasActividades.clear();
            actividadesPorDia.clear();

            while (rs.next()) {
                // Crear un objeto Actividades basado en los datos del ResultSet
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

                // Agregar la actividad a la lista general
                todasLasActividades.add(actividad);

                // Organizar actividades por día
                int dia = actividad.getFecha().getDayOfMonth();
                actividadesPorDia.computeIfAbsent(dia, k -> new ArrayList<>()).add(actividad);
            }

            // Actualizar el calendario para reflejar las actividades cargadas
            actualizarCalendario();

        } catch (Exception e) {
            e.printStackTrace();
            // Puedes mostrar una alerta en caso de error si es necesario
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al cargar actividades");
            alert.setHeaderText("Ocurrió un problema al intentar cargar las actividades.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


    
    // Método para obtener las actividades de un día específico
    private List<Actividades> obtenerActividadesPorDia(int dia) {
        return actividadesPorDia.getOrDefault(dia, new ArrayList<>());
    }
    
    private List<Actividades> obtenerActividadesDesdeBaseDeDatos() {
        List<Actividades> actividades = new ArrayList<>();
        
        String query = "SELECT * FROM actividad"; // Asumiendo que la tabla en la base de datos se llama 'actividades'
        
        try (Connection connection = Conexion.dameConexion("convive");
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
             
            // Iterar por cada fila del resultado
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                String descripcion = resultSet.getString("descripcion");
                LocalDate fecha = resultSet.getDate("fecha").toLocalDate();
                LocalTime hora = resultSet.getTime("hora") != null ? resultSet.getTime("hora").toLocalTime() : null;
                String lugar = resultSet.getString("lugar");
                int edadMin = resultSet.getInt("edad_min");
                int edadMax = resultSet.getInt("edad_max");
                int creador = resultSet.getInt("creador");
                
                // Crear una instancia de Actividades y agregarla a la lista
                Actividades actividad = new Actividades(id, nombre, descripcion, fecha, hora, lugar, edadMin, edadMax, creador);
                actividades.add(actividad);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return actividades;
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
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        int dayOfWeekIndex = firstDayOfMonth.getDayOfWeek().getValue(); // 1 para Lunes, 7 para Domingo

        // Insertar botones vacíos hasta el primer día del mes
        for (int i = 1; i < dayOfWeekIndex; i++) {
            Button emptyButton = new Button();
            emptyButton.setPrefSize(40, 40);
            emptyButton.setDisable(true);  // Botón deshabilitado
            emptyButton.setStyle("-fx-background-color: transparent;"); // Color transparente
            calendar.getChildren().add(emptyButton);
        }

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
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Sin actividades");
            alert.setHeaderText(null);
            alert.setContentText("No hay actividades programadas para este día.");
            alert.initOwner(primaryStage);
            alert.showAndWait();
            return;
        }

        StringBuilder mensaje = new StringBuilder("Actividades programadas para el día " + day + ":\n");
        for (Actividades actividad : actividadesDelDia) {
            mensaje.append("- ").append(actividad.getNombre()).append(": ").append(actividad.getDescripcion()).append("\n");
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Actividades del día");
        alert.setHeaderText("Día " + day + " - Actividades");
        alert.setContentText(mensaje.toString());
        alert.initOwner(primaryStage);
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
    
    public void mostrarActividades(List<Actividades> actividades) {
        vboxActividades.getChildren().clear(); // Limpiar el contenido actual
        
        for (Actividades actividad : actividades) {
            // Contenedor horizontal para cada actividad
            VBox actividadBox = new VBox(10); // Cambia VBox por HBox para mostrar horizontalmente
            actividadBox.setStyle("-fx-background-color: #e0f7fa; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #00796b; -fx-border-radius: 10;");

            // Añadir los elementos de actividad
            Label titulo = new Label(actividad.getNombre());
            Label descripcion = new Label("Descripción: " + actividad.getDescripcion());
            Label fecha = new Label("Fecha: " + actividad.getFecha());
            Label hora = new Label("Hora: " + actividad.getHora());
            Label lugar = new Label("Lugar: " + actividad.getLugar());
            Label edades = new Label("Edad permitida: " + actividad.getEdadMin() + " - " + actividad.getEdadMax() + " años");

            // Botón "Ver más"
            Button verMas = new Button("Ver más");
            verMas.setOnAction(event -> verDetallesActividad(actividad));

            // Añadir los elementos a HBox
            actividadBox.getChildren().addAll(titulo, descripcion, fecha, hora, lugar, edades, verMas);

            // Añadir HBox de la actividad al VBox principal
            vboxActividades.getChildren().add(actividadBox);
        }
    }


    // Método para manejar el botón "Ver más"
    private void verDetallesActividad(Actividades actividad) {
        // Aquí puedes mostrar una ventana con los detalles de la actividad seleccionada.
        // Puedes usar una nueva ventana o mostrar una alerta con información detallada.
        System.out.println("Detalles de la actividad seleccionada: " + actividad);
    }

}
