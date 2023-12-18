import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class TMDbApi {

    private static final String API_KEY = "6ccde7bb4510cf5a87e4345171cc3a70";
    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";

    public static String getMoviesByGenre(String genre) throws IOException, InterruptedException {
        // Genera un número aleatorio entre 1 y 100 para el parámetro de la página
        int randomPage = new Random().nextInt(100) + 1;

        String url = BASE_URL + "?api_key=" + API_KEY + "&with_genres=" + genre + "&sort_by=popularity.desc&page=" + randomPage;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}