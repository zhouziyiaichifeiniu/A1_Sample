import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MovieAnalyzer {
    static Stream<Movie> movies;
    static String path;
    public static Stream<Movie> readMovies() throws IOException {
        // certificate, meta_score, gross might be null
        return Files.lines(Paths.get(path), StandardCharsets.UTF_8)
                .filter(s->s.startsWith("\""))
                .map(l -> l.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"))
                .filter(a->a.length>0)
                .map(a -> new Movie(a[0], a[1], Integer.parseInt(a[2]), a[3], a[4], a[5], Float.parseFloat(a[6]), a[7], a[8].length()>0?Integer.parseInt(a[8]):0, a[9], a[10], a[11], a[12], a[13], Integer.parseInt(a[14]),a.length<16?0L: Long.parseLong(a[15].substring(1,a[15].length()-1).replace(",",""))));
    }

    public MovieAnalyzer(String dataset_path) throws IOException {
        path = dataset_path;
        }

    public Map<Integer, Integer> getMovieCountByYear() throws IOException {
        movies = readMovies();
        Map<Integer, Integer> movie =movies.sorted((o1, o2) -> o1.getReleased_Year() - o2.getReleased_Year()).collect(Collectors.groupingBy(Movie::getReleased_Year, Collectors.reducing(0, e -> 1, Integer::sum)));
        return movie;
    }

    public Map<String, Integer> getMovieCountByGenre() throws IOException {
        movies = readMovies();
        Map<String, Integer> movie = movies.sorted((o1, o2) -> o1.getGenre().compareTo(o2.getGenre())).collect(Collectors.groupingBy(Movie::getGenre, Collectors.reducing(0, e -> 1, Integer::sum)));
        return movie;
    }

    public Map<List<String>, Integer> getCoStarCount() throws IOException {
        movies = readMovies();
        Map<List<String>, Integer> movie = new HashMap<>();
        return movie;
    }

    public List<String> getTopMovies(int top_k, String by) throws IOException {
        movies = readMovies();
        List<String> movie = new ArrayList<>();
        return movie;
    }

    public List<String> getTopStars(int top_k, String by) throws IOException {
        movies = readMovies();
        List<String> movie = new ArrayList<>();
        return movie;
    }

    public List<String> searchMovies(String genre, float min_rating, int max_runtime) throws IOException {
        movies = readMovies();
        List<String> movie = new ArrayList<>();
        return movie;
    }

    public static class Movie {
        private String Nothing;
        private String Series_Title;
        private Integer Released_Year;
        private String Certificate;
        private String Runtime;
        private String Genre;
        private Float IMDB_Rating;
        private String Overview;
        private Integer Meta_score;
        private String Director;
        private String star1;
        private String star2;
        private String star3;
        private String star4;
        private Integer Noofvotes;
        private Long Gross;

        public Movie(String nothing, String series_Title, Integer released_Year, String certificate, String runtime, String genre, Float IMDB_Rating, String overview, Integer meta_score, String director, String star1, String star2, String star3, String star4, Integer noofvotes, Long gross) {
            Nothing = nothing;
            Series_Title = series_Title;
            Released_Year = released_Year;
            Certificate = certificate;
            Runtime = runtime;
            Genre = genre;
            this.IMDB_Rating = IMDB_Rating;
            Overview = overview;
            Meta_score = meta_score;
            Director = director;
            this.star1 = star1;
            this.star2 = star2;
            this.star3 = star3;
            this.star4 = star4;
            Noofvotes = noofvotes;
            Gross = gross;
        }

        public String getNothing() {
            return Nothing;
        }

        public String getSeries_Title() {
            return Series_Title;
        }

        public Integer getReleased_Year() {
            return Released_Year;
        }

        public String getCertificate() {
            return Certificate;
        }

        public String getRuntime() {
            return Runtime;
        }

        public String getGenre() {
            return Genre;
        }

        public Float getIMDB_Rating() {
            return IMDB_Rating;
        }

        public String getOverview() {
            return Overview;
        }

        public Integer getMeta_score() {
            return Meta_score;
        }

        public String getDirector() {
            return Director;
        }

        public String getStar1() {
            return star1;
        }

        public String getStar2() {
            return star2;
        }

        public String getStar3() {
            return star3;
        }

        public String getStar4() {
            return star4;
        }

        public Integer getNoofvotes() {
            return Noofvotes;
        }

        public Long getGross() {
            return Gross;
        }
    }
}
