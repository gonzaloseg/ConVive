package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MiComunidadControlador {
    @FXML
    private Button botonVolver;

    @FXML
    private ImageView img_volver;

    @FXML
    private BarChart<String, Number> barChart;

    public void initialize() {
        // Configura el evento de clic para img_volver
        img_volver.setOnMouseClicked(event -> volver(new ActionEvent()));

        // Configura los datos en la gráfica
        cargarDatosEnGrafica();
    }

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

            // Cerrar la ventana actual
            Stage currentStage = (Stage) botonVolver.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarDatosEnGrafica() {
        // Obtiene los datos de la base de datos
        int numeroDeAdultos = getNumeroDeAdultos();
        int numeroDeMenores = getNumeroDeMenores();

        // Crea una serie de datos para la gráfica
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Distribución por Edad");

        // Añade los datos a la serie
        series.getData().add(new XYChart.Data<>("Adultos", numeroDeAdultos));
        series.getData().add(new XYChart.Data<>("Menores", numeroDeMenores));

        // Añade la serie al gráfico de barras
        barChart.getData().add(series);
    }

    // Métodos de ejemplo para obtener los datos de la base de datos
    private int getNumeroDeAdultos() {
        String query = "SELECT COUNT(*) AS total FROM adulto";
        try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Retorna 0 si ocurre un error
    }

    // Consulta el número de menores en tiempo real desde la tabla 'menores'
    private int getNumeroDeMenores() {
        String query = "SELECT COUNT(*) AS total FROM menor";
        try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Retorna 0 si ocurre un error
    }
    
    // ... El resto de tu código controlador permanece igual.

}
