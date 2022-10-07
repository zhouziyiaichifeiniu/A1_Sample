import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static java.util.Comparator.naturalOrder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MovieAnalyzer {
  static Stream<Movie> movies;
  static String path;

  public Stream<Movie> readMovies() throws IOException {
    return Files.lines(Paths.get(path), StandardCharsets.UTF_8)
            .filter(s -> s.startsWith("\""))
            .map(l -> l.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"))
            .filter(a -> a.length > 0)
            .map(a -> new Movie(a[0], a[1].startsWith("\"") ? a[1].substring(1, a[1].length() - 1) : a[1], Integer.parseInt(a[2]), a[3], a[4], a[5].startsWith("\"") ? a[5].substring(1, a[5].length() - 1) : a[5], Float.parseFloat(a[6]), a[7].startsWith("\"") ? a[7].substring(1, a[7].length() - 1) : a[7], a[8].length() > 0 ? Integer.parseInt(a[8]) : 0, a[9], a[10], a[11], a[12], a[13], Integer.parseInt(a[14]), a.length < 16 ? 0L : Long.parseLong(a[15].substring(1, a[15].length() - 1).replace(",", ""))));
  }


  public MovieAnalyzer(String dataset_path) {
    path = dataset_path;
  }

  public Map<Integer, Integer> getMovieCountByYear() throws IOException {
    movies = readMovies();
    Map<Integer, Integer> movie = movies.sorted((comparingInt(Movie::getReleased_Year)))
            .collect(Collectors.groupingBy(Movie::getReleased_Year, Collectors.reducing(0, e -> 1, Integer::sum)));
    TreeMap<Integer, Integer> ans = new TreeMap<>((o1, o2) -> o2 - o1);
    for (Map.Entry<Integer, Integer> entry : movie.entrySet()) {
      ans.put(entry.getKey(), entry.getValue());
    }
    return ans;
  }

  /**
   * @return This method returns a <genre, count> map
   * @throws IOException
   * @author ZhouZiyi
   */
  public Map<String, Integer> getMovieCountByGenre() throws IOException {
    movies = readMovies();
    List<Movie> list1 = movies.collect(Collectors.toList());
    Map<String, Integer> map = new HashMap<>();
    for (int i = 0; i < list1.size(); i++) {
      String[] strings = list1.get(i).getGenre().split(", ");
      for (int j = 0; j < strings.length; j++) {
        if (map.get(strings[j]) == null) {
          map.put(strings[j], 1);
        } else {
          map.put(strings[j], map.get(strings[j]) + 1);
        }
      }
    }
    List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
    Collections.sort(list, ((o1, o2) -> o1.getKey().compareTo(o2.getKey())));
    Collections.sort(list, (o1, o2) -> o2.getValue() - o1.getValue());
    LinkedHashMap<String, Integer> ans = new LinkedHashMap<>();
    for (Map.Entry<String, Integer> e :
            list) {
      ans.put(e.getKey(), e.getValue());
    }

    return ans;
  }

  /**
   * @return This method returns a <[star1, star2], count> map
   * @throws IOException
   * @author ZhouZiyi
   */
  public Map<List<String>, Integer> getCoStarCount() throws IOException {
    movies = readMovies();
    List<Movie> list = movies.collect(Collectors.toList());
    List<List<String>> stars = new ArrayList<>();
    for (Movie value : list) {
      List<String> temp = new ArrayList<>();
      String star1 = value.getStar1();
      String star2 = value.getStar2();
      String star3 = value.getStar3();
      String star4 = value.getStar4();
      temp.add(star1);
      temp.add(star2);
      temp.sort(naturalOrder());
      stars.add(temp);
      temp = new ArrayList<>();
      temp.add(star1);
      temp.add(star3);
      temp.sort(naturalOrder());
      stars.add(temp);
      temp = new ArrayList<>();
      temp.add(star1);
      temp.add(star4);
      temp.sort(naturalOrder());
      stars.add(temp);
      temp = new ArrayList<>();
      temp.add(star2);
      temp.add(star3);
      temp.sort(naturalOrder());
      stars.add(temp);
      temp = new ArrayList<>();
      temp.add(star4);
      temp.add(star2);
      temp.sort(naturalOrder());
      stars.add(temp);
      temp = new ArrayList<>();
      temp.add(star3);
      temp.add(star4);
      temp.sort(naturalOrder());
      stars.add(temp);
    }
    Map<List<String>, Integer> movie = new HashMap<>();
    for (int i = 0; i < stars.size(); i++) {
      if (movie.get(stars.get(i)) == null) {
        movie.put(stars.get(i), 1);
      } else {
        movie.put(stars.get(i), movie.get(stars.get(i)) + 1);
      }
    }
    return movie;
  }

  /**
   * @return This method returns the top K movies
   * @throws IOException
   * @author ZhouZiyi
   */
  public List<String> getTopMovies(int top_k, String by) throws IOException {
    movies = readMovies();
    List<String> movie = movies.sorted((comparing(Movie::getSeries_Title)))
            .sorted(((o1, o2) -> by.equals("runtime") ? Integer.parseInt(o2.getRuntime().split(" ")[0]) - Integer.parseInt(o1.getRuntime().split(" ")[0]) : o2.getOverview().length() - o1.getOverview().length()))
            .limit(top_k)
            .map(Movie::getSeries_Title)
            .collect(Collectors.toList());
    return movie;
  }

  /**
   * @return This method returns the top K stars
   * @throws IOException
   * @author ZhouZiyi
   */
  public List<String> getTopStars(int top_k, String by) throws IOException {
    movies = readMovies();
    List<Movie> list = movies.collect(Collectors.toList());
    Map<String, Double> rating = new HashMap<>();
    Map<String, Long> gross = new HashMap<>();
    Map<String, Integer> count = new HashMap<>();
    if (by.equals("rating")) {
      for (Movie movie : list) {

        if (rating.get(movie.getStar1()) == null) {
          count.put(movie.getStar1(), 1);
          rating.put(movie.getStar1(), (double) movie.getIMDB_Rating());
        } else {
          count.put(movie.getStar1(), count.get(movie.getStar1()) + 1);
          rating.put(movie.getStar1(), rating.get(movie.getStar1()) + (double) movie.getIMDB_Rating());
        }
        if (rating.get(movie.getStar2()) == null) {
          count.put(movie.getStar2(), 1);
          rating.put(movie.getStar2(), (double) movie.getIMDB_Rating());
        } else {
          count.put(movie.getStar2(), count.get(movie.getStar2()) + 1);
          rating.put(movie.getStar2(), rating.get(movie.getStar2()) + (double) movie.getIMDB_Rating());
        }
        if (rating.get(movie.getStar3()) == null) {
          count.put(movie.getStar3(), 1);
          rating.put(movie.getStar3(), (double) movie.getIMDB_Rating());
        } else {
          count.put(movie.getStar3(), count.get(movie.getStar3()) + 1);
          rating.put(movie.getStar3(), rating.get(movie.getStar3()) + (double) movie.getIMDB_Rating());
        }
        if (rating.get(movie.getStar4()) == null) {
          count.put(movie.getStar4(), 1);
          rating.put(movie.getStar4(), (double) movie.getIMDB_Rating());
        } else {
          count.put(movie.getStar4(), count.get(movie.getStar4()) + 1);
          rating.put(movie.getStar4(), rating.get(movie.getStar4()) + (double) movie.getIMDB_Rating());
        }
      }
      List<Map.Entry<String, Double>> list1 = new ArrayList<>(rating.entrySet());
      for (Map.Entry<String, Double> stringDoubleEntry : list1) {
        stringDoubleEntry.setValue(stringDoubleEntry.getValue() / count.get(stringDoubleEntry.getKey()));
      }
      list1.sort((Map.Entry.comparingByKey()));
      list1.sort((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()));
      List<String> ans = new ArrayList<>();
      for (int i = 0; i < top_k; i++) {
        ans.add(list1.get(i).getKey());
      }
      return ans;
    } else {
      for (Movie movie : list) {
        if (movie.getGross() == 0) {
          continue;
        }
        if (gross.get(movie.getStar1()) == null) {
          count.put(movie.getStar1(), 1);
          gross.put(movie.getStar1(), movie.getGross());
        } else {
          count.put(movie.getStar1(), count.get(movie.getStar1()) + 1);
          gross.put(movie.getStar1(), movie.getGross() + gross.get(movie.getStar1()));
        }
        if (gross.get(movie.getStar2()) == null) {
          count.put(movie.getStar2(), 1);
          gross.put(movie.getStar2(), movie.getGross());
        } else {
          count.put(movie.getStar2(), count.get(movie.getStar2()) + 1);
          gross.put(movie.getStar2(), movie.getGross() + gross.get(movie.getStar2()));
        }
        if (gross.get(movie.getStar3()) == null) {
          count.put(movie.getStar3(), 1);
          gross.put(movie.getStar3(), movie.getGross());
        } else {
          count.put(movie.getStar3(), count.get(movie.getStar3()) + 1);
          gross.put(movie.getStar3(), movie.getGross() + gross.get(movie.getStar3()));
        }
        if (gross.get(movie.getStar4()) == null) {
          count.put(movie.getStar4(), 1);
          gross.put(movie.getStar4(), movie.getGross());
        } else {
          count.put(movie.getStar4(), count.get(movie.getStar4()) + 1);
          gross.put(movie.getStar4(), movie.getGross() + gross.get(movie.getStar4()));
        }
      }
      List<Map.Entry<String, Long>> list1 = new ArrayList<>(gross.entrySet());
      for (int i = 0; i < list1.size(); i++) {
        list1.get(i).setValue(list1.get(i).getValue() / count.get(list1.get(i).getKey()));
      }
      list1.sort((comparing(Map.Entry::getKey)));
      list1.sort((o1, o2) -> Long.compare(o2.getValue(), o1.getValue()));
      List<String> ans = new ArrayList<>();
      for (int i = 0; i < top_k; i++) {
        ans.add(list1.get(i).getKey());
      }
      return ans;
    }
  }

  /**
   * @return This method searches movies based on three criterion
   * @throws IOException
   * @author ZhouZiyi
   */
  public List<String> searchMovies(String genre, float min_rating, int max_runtime) throws IOException {
    movies = readMovies();
    List<String> movie = movies.filter(l -> l.getGenre().contains(genre) && (double) l.getIMDB_Rating() >= (double) min_rating && Integer.parseInt(l.getRuntime().split(" ")[0]) <= max_runtime)
            .sorted(comparing(Movie::getSeries_Title))
            .map(Movie::getSeries_Title)
            .collect(Collectors.toList());
    return movie;
  }

  public class Movie {
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
