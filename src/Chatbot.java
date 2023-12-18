
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class Chatbot {

    private static final StanfordCoreNLP pipeline;

    private static final Map<String, List<String>> intenciones = new HashMap<>();

    static {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        pipeline = new StanfordCoreNLP(props);

        // Inicializar map de intenciones y palabras clave
        intenciones.put("saludo", Arrays.asList("hola", "saludo","hey"));
        intenciones.put("despedida", Arrays.asList("adios", "hasta pronto", "chau", "nos vemos"));
        intenciones.put("generos", Arrays.asList("ayuda", "ayudame", "genero", "generos"));
        intenciones.put("agradecimiento", Arrays.asList("gracias", "agradecido", "agradecida","agradezco"));
        intenciones.put("Action", Arrays.asList("accion"));
        intenciones.put("Adventure", Arrays.asList("aventura"));
        intenciones.put("Comedy", Arrays.asList("comedia","comica","comicas","comico","comicos"));
        intenciones.put("Crime", Arrays.asList("crimen", "policial"));
        intenciones.put("Documentary", Arrays.asList("documental"));
        intenciones.put("Drama", Arrays.asList("drama","dramatica","dramatico"));
        intenciones.put("Family", Arrays.asList("familia", "familiar"));
        intenciones.put("Fantasy", Arrays.asList("fantasia", "fantasioso", "fantasiosa"));
        intenciones.put("History", Arrays.asList("historia", "historica", "historico"));
        intenciones.put("Horror", Arrays.asList("horror", "terror"));
        intenciones.put("Music", Arrays.asList("musica", "musical"));
        intenciones.put("Mystery", Arrays.asList("misterio", "suspenso"));
        intenciones.put("Romance", Arrays.asList("romance", "romantico", "romantica"));
        intenciones.put("Sci-Fi", Arrays.asList("sci-fi", "ciiencia ficcion"));
        intenciones.put("Sport", Arrays.asList("deporte","deportiva","deportivo"));
        intenciones.put("Thriller", Arrays.asList("thriller"));
        intenciones.put("War", Arrays.asList("guerra", "belica"));
        intenciones.put("Western", Arrays.asList("western", "vaqueros","lejano oeste","cowboys"));

    }
    
    public Chatbot(StanfordCoreNLP pipeline) {
    }

    public static String determinarIntencion(String input) {
        CoreDocument document = new CoreDocument(input);
        pipeline.annotate(document);

        StringBuilder lemmatizedText = new StringBuilder();
        for (CoreLabel token : document.tokens()) {
            lemmatizedText.append(token.lemma()).append(" ");
        }

        String lemmatizedInput = lemmatizedText.toString().trim().toLowerCase();

        for (Map.Entry<String, List<String>> entry : intenciones.entrySet()) {
            String intent = entry.getKey();
            List<String> palabrasClave = entry.getValue();
            if (contienePalabrasClave(lemmatizedInput, palabrasClave)) {
                return intent;
            }
        }

        return "otro";
    }

    private static boolean contienePalabrasClave(String texto, List<String> palabrasClave) {
        for (String palabra : palabrasClave) {
            if (texto.contains(palabra)) {
                return true;
            }
        }
        return false;
    }

    public static String getResponseFromDatabase(String intent) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnector.connect();
            String query = "SELECT response FROM responses WHERE intent = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, intent);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String baseResponse = resultSet.getString("response");

                if (!intent.equals("saludo") && !intent.equals("despedida") && !intent.equals("agradecimiento") && !intent.equals("generos")) {
                    String genre = determineGenreFromIntent(intent);
                    String moviesResponse = getMoviesResponseFromApi(genre);
                    String formattedMoviesResponse = formatMoviesResponse(moviesResponse, 5);

                    return baseResponse + "\n" + formattedMoviesResponse;
                } else {
                    return baseResponse;
                }
            }
        } catch (SQLException | IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnector.close(connection, preparedStatement, resultSet);
        }

        return "Lo siento, mis respuestas son limitadas.";
    }

    private static String formatMoviesResponse(String moviesResponse, int numMoviesToShow) {
        //Crear un StringBuilder para construir la respuesta formateada
        StringBuilder formattedResponse = new StringBuilder();

        try {
            // Convertir la cadena JSON a un objeto JSONObject
            JSONObject jsonResponse = new JSONObject(moviesResponse);

            // Obtener el arreglo de resultados
            JSONArray resultsArray = jsonResponse.getJSONArray("results");

            // Limitar la cantidad de películas a mostrar
            int numMovies = Math.min(numMoviesToShow, resultsArray.length());

            // Iterar a través de las primeras 5 películas en el arreglo
            for (int i = 0; i < numMovies; i++) {
                // Obtener el objeto de película actual
                JSONObject movie = resultsArray.getJSONObject(i);

                // Extraer información relevante de la película
                String title = movie.getString("title");
                String overview = movie.getString("overview");
                String releaseDate = movie.getString("release_date");

                // Definir etiquetas personalizadas
                String titleLabel = "• Título: ";
                String overviewLabel = "• Resumen: ";
                String releaseDateLabel = "• Fecha de estreno: ";

                // Construir una cadena formateada con las etiquetas personalizadas
                String formattedMovie = String.format("%s%s%n%s%s%n%s%s%n%n",
                        titleLabel, title, overviewLabel, overview, releaseDateLabel, releaseDate);

                // Agregar la información de la película al resultado formateado
                formattedResponse.append(formattedMovie);

            }

            // Agregar un espacio adicional después de la lista de películas
            formattedResponse.append("\n");
        } catch (Exception e) {
            e.printStackTrace(); // Manejar cualquier excepción que pueda ocurrir durante el análisis
        }

        // Devolver la respuesta formateada como una cadena
        return formattedResponse.toString();
    }

    private static String determineGenreFromIntent(String intent) {
        switch (intent) {
            case "Action":
                return "28"; // Código de género de Acción en TMDb
            case "Adventure":
                return "12"; // Código de género de Aventura en TMDb
            case "Comedy":
                return "35"; // Código de género de Comedia en TMDb
            case "Crime":
                return "80"; // Código de género de Crimen en TMDb
            case "Documentary":
                return "99"; // Código de género de Documental en TMDb
            case "Drama":
                return "18"; // Código de género de Drama en TMDb
            case "Family":
                return "10751"; // Código de género de Familia en TMDb
            case "Fantasy":
                return "14"; // Código de género de Fantasía en TMDb
            case "History":
                return "36"; // Código de género de Historia en TMDb
            case "Horror":
                return "27"; // Código de género de Terror en TMDb
            case "Music":
                return "10402"; // Código de género de Música en TMDb
            case "Mystery":
                return "9648"; // Código de género de Misterio en TMDb
            case "Romance":
                return "10749"; // Código de género de Romance en TMDb
            case "Sci-Fi":
                return "878"; // Código de género de Ciencia Ficción en TMDb
            case "Sport":
                return "10770"; // Código de género de Deporte en TMDb
            case "Thriller":
                return "53"; // Código de género de Thriller en TMDb
            case "War":
                return "10752"; // Código de género de Guerra en TMDb
            case "Western":
                return "37"; // Código de género de Western en TMDb
            default:
                // Si no se encuentra un género correspondiente, devuelve un valor predeterminado o maneja de otra manera
                return "28"; // Código de género de Acción en TMDb (como valor predeterminado)
        }
    }

    private static String getMoviesResponseFromApi(String genre) throws IOException, InterruptedException {
        return TMDbApi.getMoviesByGenre(genre);
    }


}
