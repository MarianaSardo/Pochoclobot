
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controlador {

    private Connection connection = null;

    public Controlador() {
        try {
            this.connection = DatabaseConnector.connect();
        } catch (SQLException ex) {
            System.out.println("no se pudo conectar a la base de datos");
        }
    }

   public void agregarUsuario(Usuario usuario) {
        // Verificar si el nombre de usuario ya existe en la base de datos
        if (usuarioYaExiste(usuario.getNombre())) {
            return;
        }

        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO Usuarios (nombre, password) VALUES (?, ?)")) {

            statement.setString(1, usuario.getNombre());

            // Hashear la contraseña antes de almacenarla
            String hashedPassword = hashPassword(usuario.getPassword());
            statement.setString(2, hashedPassword);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean usuarioYaExiste(String nombreUsuario) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM Usuarios WHERE nombre = ?")) {

            statement.setString(1, nombreUsuario);

            try (ResultSet resultSet = statement.executeQuery()) {
                // Si hay al menos un resultado, significa que el nombre de usuario ya existe
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Si no se encontraron coincidencias, el nombre de usuario no existe
        return false;
    }

    public int autenticarUsuario(String nombre, String password) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id_usuario, password FROM usuarios WHERE nombre = ?")) {
            statement.setString(1, nombre);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Obtén la contraseña almacenada en la base de datos
                    String hashedStoredPassword = resultSet.getString("password");

                    // Verifica si la contraseña ingresada coincide con la almacenada
                    if (hashedStoredPassword.equals(hashPassword(password))) {
                        // Si la autenticación es exitosa, devuelve el ID del usuario
                        return resultSet.getInt("id_usuario");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Si la autenticación falla, puedes devolver un valor especial, como -1, para indicar que no se encontró un usuario válido.
        return -1;
    }

    private String hashPassword(String password) {
        // Utiliza la función hashCode() para obtener un hash simple
        return String.valueOf(password.hashCode());
    }


    public int obtenerIdUsuarioPorNombre(String nombreUsuario) {
        int idUsuario = -1; // Valor predeterminado si no se encuentra el usuario
        try (Connection connection = DatabaseConnector.connect()) {
            String selectQuery = "SELECT id_usuario FROM usuarios WHERE nombre = ?";
            try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                statement.setString(1, nombreUsuario);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        idUsuario = resultSet.getInt("id_usuario");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idUsuario;
    }

    public int obtenerIdResponseDesdeIntent(String intent) {
        int idResponse = -1; // Valor predeterminado si no se encuentra la intención
        try (Connection connection = DatabaseConnector.connect()) {
            String selectQuery = "SELECT id_response FROM responses WHERE intent = ?";
            try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                statement.setString(1, intent);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        idResponse = resultSet.getInt("id_response");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idResponse;
    }

    public void guardarGeneroFavorito(int idUsuario, int idResponse) {
        // Verificar que el idResponse sea mayor o igual a 7
        if (idResponse < 7) {

            return;
        }

        try (Connection connection = DatabaseConnector.connect()) {
            String insertQuery = "INSERT INTO usuario_generosfav (id_usuario, id_response) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                statement.setInt(1, idUsuario);
                statement.setInt(2, idResponse);

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Double> obtenerPorcentajeGenerosFavoritos(int idUsuario) {
        try (Connection connection = DatabaseConnector.connect()) {
            String selectCountQuery = "SELECT COUNT(*) AS totalGeneros FROM usuario_generosfav WHERE id_usuario = ?";
            String selectPercentageQuery = "SELECT intent, COUNT(*) AS count from responses JOIN usuario_generosfav on responses.id_response = usuario_generosfav.id_response WHERE usuario_generosfav.id_usuario= ? GROUP by intent;";
            // "SELECT id_response, COUNT(*) AS count FROM usuario_generosfav WHERE id_usuario = ? GROUP BY id_response";

            try (PreparedStatement countStatement = connection.prepareStatement(selectCountQuery)) {
                countStatement.setInt(1, idUsuario);

                // Obtén la cuenta total de géneros
                int totalGeneros = 0;
                try (ResultSet countResultSet = countStatement.executeQuery()) {
                    if (countResultSet.next()) {
                        totalGeneros = countResultSet.getInt("totalGeneros");
                    }
                }

                // Verifica que haya géneros antes de continuar
                if (totalGeneros > 0) {
                    // Prepara la declaración para obtener el porcentaje
                    try (PreparedStatement percentageStatement = connection.prepareStatement(selectPercentageQuery)) {
                        percentageStatement.setInt(1, idUsuario);

                        Map<String, Double> porcentajes = new HashMap<>();

                        // Obtén los porcentajes
                        try (ResultSet percentageResultSet = percentageStatement.executeQuery()) {
                            while (percentageResultSet.next()) {
                                String intent = percentageResultSet.getString("intent");
                                int count = percentageResultSet.getInt("count");

                                double porcentaje = (count * 100.0) / totalGeneros;

                                // Crear un objeto DecimalFormat con el formato deseado
                                DecimalFormat df = new DecimalFormat("#.##"); // Dos decimales

                                    // Redondear y formatear el porcentaje
                                String porcentajeFormateado = df.format(porcentaje);
                                porcentajeFormateado = porcentajeFormateado.replace(",", "."); // Reemplaza la coma con un punto

                                    // Convertir el porcentaje formateado de nuevo a double
                                double porcentajeRedondeado = Double.parseDouble(porcentajeFormateado);

                                porcentajes.put(intent, porcentajeRedondeado);
                            }
                        }

                        return porcentajes;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Si hay un error, devuelve un mapa vacío
        return new HashMap<>();
    }

    public List<String> obtenerGenerosFavoritosUsuario(int idUsuario) {
        List<String> generosFavoritos = new ArrayList<>();

        try (Connection connection = DatabaseConnector.connect(); PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT r.intent FROM usuario_generosfav ug "
                + "INNER JOIN responses r ON ug.id_response = r.id_response "
                + "WHERE ug.id_usuario = ?"
        )) {

            // Establecer el parámetro de idUsuario en la consulta preparada
            preparedStatement.setInt(1, idUsuario);

            // Ejecutar la consulta
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Recorrer los resultados y agregar los géneros a la lista
                while (resultSet.next()) {
                    String genero = resultSet.getString("intent");
                    generosFavoritos.add(genero);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Manejar la excepción apropiadamente en tu aplicación
        }

        return generosFavoritos;
    }

    // Método para cerrar la conexión cuando sea necesario
    public void cerrarConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
