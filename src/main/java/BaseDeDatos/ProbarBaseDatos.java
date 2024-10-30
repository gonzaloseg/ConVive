package BaseDeDatos;

import java.sql.Connection;
import java.sql.SQLException;

public class ProbarBaseDatos {
	public static void main(String[] args) {
        try {
            Connection connection = Conexion.getConnection();

            if (connection != null) {
                System.out.println("Conexión a la base de datos establecida con éxito.");
                connection.close();
            } else {
                System.out.println("No se pudo establecer la conexión a la base de datos.");
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }
}

