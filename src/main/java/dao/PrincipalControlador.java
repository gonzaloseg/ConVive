package dao;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
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
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class PrincipalControlador implements Initializable {
    
	//Botones-labels izquierda home (navegabilidad)
	@FXML private Label lbMiPerfil; //lleva a ventana MiPerfil
    @FXML private Label lbMiComunidad; //lleva a ventana MiComunidad
    @FXML private Label lbListaEventos; //lleva a ventana ListaEventos
    @FXML private Label lbCerrarSesion;
	
    //Calendario
    @FXML private Button btnMesAnterior; //boton retroceder mes (calendario)
    @FXML private Button btnMesSiguiente; //boton adelantar mes (calendario)
    @FXML private Label month; //mes (calendario)
    @FXML private Label year; //año (calendario)
    @FXML private FlowPane calendar;  // Contenedor de los días del mes
    private LocalDate currentDate = LocalDate.now();  // Fecha actual
     
    //Proximas actividades (del mes) 
    @FXML  private VBox actividadVBox;
    private VBox vboxActividades; 
    @FXML private Button btnFiltros; //boton para filtrar actividades
    
    
    @FXML private Button btnNuevaActividad; //boton que lleva a ventana CreacionActividad
    @FXML private Label labelNuevaActividad;
    @FXML private Stage primaryStage;

    private static final String SQL_OBTENER_ACTIVIDADES = "SELECT * FROM actividad WHERE MONTH(fecha) = MONTH(CURRENT_DATE()) AND YEAR(fecha) = YEAR(CURRENT_DATE()) ORDER BY fecha DESC";
    
    // Mapa para almacenar actividades por día (usando int como clave para el día del mes)
    private Map<Integer, List<Actividades>> actividadesPorDia = new HashMap<>(); //calendario
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
        cargarDatos();
        
        //Si eres menor no puedes añadir activades, por tanto, no te aparecerá el botón 
        if (UsuarioGlobal.getInstacne().getTabla().equals("menor")) {
        	btnNuevaActividad.setVisible(false);
        	labelNuevaActividad.setVisible(false);
        }
    }
    
    
    
    
    
/* MÉTODOS DE NAVEGACIÓN ENTRE VISTAS */
    
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
            currentStage.close(); //cerrar ventana actual 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    @FXML
    private void abrirMiComunidad() {  
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaMiComunidad.fxml"));
            AnchorPane root = loader.load();
            
            //Obtener instancia del controlador y pasarle los datos (MI COMUNIDAD)
            MiComunidadControlador controller = loader.getController();
            controller.rellenarMiVivienda(UsuarioGlobal.getInstacne().getDniGlobal(), UsuarioGlobal.getInstacne().getTabla());

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

    
    
    @FXML
    private void cerrarSesion() { 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaInicioSesion.fxml"));
            AnchorPane root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Inicio de sesión - ConVive");
            stage.centerOnScreen();
            stage.show();

            Stage currentStage = (Stage) lbCerrarSesion.getScene().getWindow();
            currentStage.close(); //cerrar la ventana 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    // Método para abrir la ventana "Nueva Actividad" 
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
            currentStage.close(); //cerrar ventana actual 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    
       
/* MÉTODOS PARA MANEJAR EL CALENDARIO */
    
    // Método para ir al mes anterior
    @FXML
    private void mesAnterior(ActionEvent event) {
        currentDate = currentDate.minusMonths(1);
        actualizarCalendario();
    }
    // Método para ir al mes siguiente
    @FXML
    private void mesSiguiente(ActionEvent event) {
        currentDate = currentDate.plusMonths(1);
        actualizarCalendario();
    }
    
    
    
    private List<Actividades> obtenerActividadesDesdeBaseDeDatos() {
        List<Actividades> actividades = new ArrayList<>();
        String query = "SELECT * FROM actividad"; 

        try (Connection connection = Conexion.dameConexion("convive");
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Actividades actividad = new Actividades(
                    resultSet.getInt("id"),
                    resultSet.getString("nombre"),
                    resultSet.getString("descripcion"),
                    resultSet.getDate("fecha").toLocalDate(),
                    resultSet.getTime("hora") != null ? resultSet.getTime("hora").toLocalTime() : null,
                    resultSet.getString("lugar"),
                    resultSet.getInt("edad_min"),
                    resultSet.getInt("edad_max"),
                    resultSet.getInt("creador")
                );
                actividades.add(actividad);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return actividades;
    }

    private void cargarActividades() {
        todasLasActividades = obtenerActividadesDesdeBaseDeDatos();
        actividadesPorDia.clear();

        for (Actividades actividad : todasLasActividades) {
            int dia = actividad.getFecha().getDayOfMonth();
            actividadesPorDia.computeIfAbsent(dia, k -> new ArrayList<>()).add(actividad);
        }

        actualizarCalendario();
    }
    
    
    
    // Método para obtener las actividades de un día específico
    private List<Actividades> obtenerActividadesPorDia(int dia) {
        List<Actividades> actividadesDelMes = new ArrayList<>();
        for (Actividades actividad : todasLasActividades) {
            if (actividad.getFecha().getDayOfMonth() == dia && actividad.getFecha().getMonth() == currentDate.getMonth()) {
                actividadesDelMes.add(actividad);
            }
        }
        return actividadesDelMes;
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

            // Establecer border-radius de 18px
            dayButton.setStyle("-fx-background-color: white; -fx-border-radius: 18px; -fx-background-radius: 18px;");

            // Comprobar si hay actividades programadas para este día
            List<Actividades> actividadesDelDia = obtenerActividadesPorDia(day);
            if (!actividadesDelDia.isEmpty()) {
                // Si hay actividades, poner el botón en verde
                dayButton.setStyle("-fx-background-color: #83C5BE; -fx-text-fill: white; -fx-border-radius: 18px; -fx-background-radius: 18px;");
            }

            // Agregar acción al botón de cada día (para cuando se hace clic)
            dayButton.setOnAction(event -> handleDayClick(event));

            // Añadir el botón del día al FlowPane
            calendar.getChildren().add(dayButton);
        }
    }


    
    // Método para manejar el clic en un día específico
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
            mensaje.append("- ").append(actividad.getNombre()).append(" a las ").append(actividad.getHora()).append("\n")
                   .append("   Edad permitida: ").append(actividad.getEdadMin()).append(" - ").append(actividad.getEdadMax()).append(" años\n");
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Actividades del día");
        alert.setHeaderText("Día " + day + " - Actividades");
        alert.setContentText(mensaje.toString());
        alert.initOwner(primaryStage);
        alert.showAndWait();
    }


    
    public void mostrarActividades(List<Actividades> actividades) {
        vboxActividades.getChildren().clear(); // Limpiar el contenido actual
        
        for (Actividades actividad : actividades) {
            // Contenedor horizontal para cada actividad
            VBox actividadBox = new VBox(10); // Cambia VBox por HBox para mostrar horizontalmente
            actividadBox.setStyle("-fx-background-color: #e0f7fa; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #00796b; -fx-border-radius: 10;");

            // Añadir los elementos de actividad
            Label titulo = new Label(actividad.getNombre());
            Label hora = new Label("Hora: " + actividad.getHora().toString());
            Label edades = new Label("Edad permitida: " + actividad.getEdadMin() + " - " + actividad.getEdadMax() + " años");

            // Añadir los elementos a HBox
            actividadBox.getChildren().addAll(titulo, hora, edades);

            // Añadir HBox de la actividad al VBox principal
            vboxActividades.getChildren().add(actividadBox);
        }
    }

  

    
    
/* PROXIMAS ACTIVIDADES DEL MES */
    
    //cargar los datos de cada actividad
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
    
    
    
    //Método para saber el número de vecinos apuntados a una actividad
    private int vecinosApuntados (int idAct) {
    	int contadorApuntados = 0;
    	String sql = "SELECT id FROM apuntados WHERE id_actividad = ?";
    	try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive")){
    		PreparedStatement pst = conn.prepareStatement(sql);
    		pst.setInt(1, idAct);
    		ResultSet rs = pst.executeQuery();
    		
    		while (rs.next()) {
    			int id = rs.getInt("id");
    			contadorApuntados ++;
    		}
			
		} catch (Exception e) {
			e.printStackTrace(); e.getMessage();
		}
    	
    	return contadorApuntados;
    }

    
    
    //Crear un contenedor para cada actividad
    private VBox crearContainerActividad(Actividades actividad, String dniGlobal) {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: #FFFFFF;");

        // Etiquetas de la actividad
        Label nombreLabel = new Label(actividad.getNombre());
        Label descripcionLabel = new Label(actividad.getDescripcion());
        System.out.println(actividad.getNombre());
        Label fechaLabel = new Label("Fecha: " + actividad.getFecha());
        Label horaLabel = new Label("Hora: " + actividad.getHora());
        Label edadesLabel;
        if (actividad.getEdadMin() == 0 && actividad.getEdadMax() == 99) {
        	 edadesLabel = new Label("Para todas las edades");
        }else {
        	 edadesLabel = new Label("Edades: " + actividad.getEdadMin() + " - " + actividad.getEdadMax());
        }
        
        Label apuntadosLabel = new Label("Número de apuntados: " + vecinosApuntados(actividad.getId()));
        System.out.println(vecinosApuntados(actividad.getId())); //comprobar que los cuenta bien 

        // Aplicar estilos
        nombreLabel.setFont(Font.font(18));
        descripcionLabel.setFont(Font.font(14));
        fechaLabel.setFont(Font.font(14));
        horaLabel.setFont(Font.font(14));
        edadesLabel.setFont(Font.font(14));
        apuntadosLabel.setFont(Font.font(14));

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
            int edadUsuario = obtenerEdadUsuario(idUsuario); // Asume que tienes un método para obtener la edad del usuario

            // Verificar si la edad está dentro del rango
            if (edadUsuario >= actividad.getEdadMin() && edadUsuario <= actividad.getEdadMax()) {
                // Si la edad está dentro del rango, mostrar el botón "Apuntarse" o "Desapuntarse"
                if (estaApuntado) {
                    apuntarButton.setText("Desapuntarse");
                    apuntarButton.setOnAction(event -> desapuntarse(finalIdUsuario, actividad.getId()));
                } else {
                    apuntarButton.setText("Apuntarse");
                    apuntarButton.setOnAction(event -> apuntarse(finalIdUsuario, actividad.getId()));
                }
            } else {
                // Si la edad no está dentro del rango, deshabilitar el botón
                apuntarButton.setText("No puedes apuntarte");
                apuntarButton.setDisable(true); // Deshabilitar el botón

                // Puedes agregar un mensaje adicional si lo deseas
                Label mensajeRestriccionEdad = new Label("Tu edad no cumple con los requisitos para apuntarte a esta actividad.");
                container.getChildren().add(mensajeRestriccionEdad);
            }
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
    
    
    
    //Apuntarse a una actividad
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

    
    
    //Borrarse de una actividad
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
                System.err.println("Error al borrarse de la actividad.");
            }

        } catch (SQLException e) {
            System.err.println("Error al borrarse de la actividad: " + e.getMessage());
        }
    }

	
    
    //Eliminar una actividad creada por ti
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
    
    
    
    //Devolver la actividad para editarla
    private Actividades obtenerActividad(int actividadId) {
        Actividades actividad = null;

        String sql = "SELECT * FROM actividad WHERE id = ?";

        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, actividadId);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                LocalDate fecha = rs.getDate("fecha").toLocalDate();
                String horaString = rs.getString("hora");
                LocalTime hora = null;

                // Parse the String into LocalTime
                if (horaString != null && !horaString.isEmpty()) {
                    hora = LocalTime.parse(horaString);
                }

                String lugar = rs.getString("lugar");
                int edadMin = rs.getInt("edad_min");
                int edadMax = rs.getInt("edad_max");
                int creador = rs.getInt("creador");

                actividad = new Actividades(actividadId, nombre, descripcion, fecha, hora, lugar, edadMin, edadMax, creador);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return actividad;
    }
    
    
    
    //Editar actividad   
    private void editarActividad(int actividadId) {
        System.out.println("Editar actividad con ID: " + actividadId);
        try {
            // Cargar la vista FXML para editar la actividad
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaEditarActividad.fxml"));
            AnchorPane root = loader.load();

            // Obtener el controlador de la vista cargada
            EditarActividadControlador controlador = loader.getController();

            // Recuperar la actividad desde la base de datos
            Actividades actividad = obtenerActividad(actividadId);

            // Verifica si la actividad existe antes de pasarla al controlador
            if (actividad != null) {
                // Pasa la actividad al controlador
                controlador.setActividad(actividad);
            } else {
                // Si no se encuentra la actividad, mostrar un mensaje o manejar el error
                System.out.println("No se pudo cargar la actividad.");
                return;
            }

            // Mostrar la nueva ventana
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Editar Actividad");
            stage.show();

           

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private int obtenerEdadUsuario(int idUsuario) {
        int edad = -1;  // Valor por defecto si no se encuentra la edad

        // Consulta SQL para obtener la fecha de nacimiento del usuario
        String sql = "SELECT fecha_nacimiento FROM adulto WHERE id = ? UNION SELECT fecha_nacimiento FROM menor WHERE id = ?";

        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idUsuario);
            pst.setInt(2, idUsuario);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                // Obtener la fecha de nacimiento
                Date fechaNacimiento = rs.getDate("fecha_nacimiento");
                
                // Calcular la edad
                LocalDate hoy = LocalDate.now();
                LocalDate fechaNacimientoLocal = fechaNacimiento.toLocalDate();
                edad = Period.between(fechaNacimientoLocal, hoy).getYears();  // Calcula la edad en años
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener la edad del usuario: " + e.getMessage());
        }

        return edad;  // Retorna la edad calculada o -1 si hubo algún error
    }

}
