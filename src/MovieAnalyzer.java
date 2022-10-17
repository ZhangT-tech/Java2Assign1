import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class MovieAnalyzer {

    public static class Movie {
        private String seriesTitle;
        private int releasedYear;
        private String certificate;
        private int runtime;
        private String genre;
        private float imdbRating;
        private String overview;
        private String metaData;
        private String director;
        private String star1;
        private String star2;
        private String star3;
        private String star4;
        private String noOfVotes;
        private double gross;

        public Movie(String seriesTitle, int releasedYear, String certificate,
                     int runtime, String genre, float imdbRating,
                     String overview, String metaData, String director,
                     String star1, String star2, String star3, String star4,
                     String noOfVotes, double gross) {
            this.seriesTitle = seriesTitle;
            this.releasedYear = releasedYear;
            this.certificate = certificate;
            this.runtime = runtime;
            this.genre = genre;
            this.imdbRating = imdbRating;
            this.overview = overview;
            this.metaData = metaData;
            this.director = director;
            this.star1 = star1;
            this.star2 = star2;
            this.star3 = star3;
            this.star4 = star4;
            this.noOfVotes = noOfVotes;
            this.gross = gross;
        }
        public int getReleasedYear() {
            return releasedYear;
        }

        public double getGross() {
            return gross;
        }

        public String getGenre() {
            return genre;
        }

        public int getRuntime() {
            return runtime;
        }

        public String getOverview() {
            return overview;
        }

        public String getSeriesTitle() {
            return seriesTitle;
        }

        public float getImdbRating() {
            return imdbRating;
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
    }

    public static List<Movie> lines = new ArrayList<>();
    public MovieAnalyzer(String dataset_path) throws IOException {
        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(dataset_path);
            sc = new Scanner(inputStream, StandardCharsets.UTF_8);
            int count = 0;
            while (sc.hasNextLine()) {
                count = count+1;
                String line = sc.nextLine();
                if (count == 1) {
                    System.out.println();
                    continue;
                }
                String[] content = line.trim().split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", 16);
                Movie movie = new Movie(content[1].replace("\"", ""), Integer.parseInt(content[2]),
                        content[3], Integer.parseInt(content[4].split("\\s")[0]), content[5], Float.parseFloat(content[6]), content[7].replace("\"", " ").trim(),
                        content[8], content[9],content[10], content[11],
                        content[12], content[13], content[14], Double.parseDouble(content[15].replace("\"", "").replace(",","").equals("") ? "0": content[15].replace("\"", "").replace(",","")));
                lines.add(movie);
            }
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }
    }
    public Map<Integer, Integer> getMovieCountByYear() {
        Map<Integer, Integer> movieCountByYear = new HashMap<>();
        movieCountByYear = lines.stream().collect(Collectors.groupingBy(Movie::getReleasedYear,  Collectors.summingInt(x -> 1)));
        return sortYear(movieCountByYear);
    }
    private static List<String> getGenreList() {
        List<String> genres = new ArrayList<>();
        for (Movie movie: lines) {
            String[] genre = movie.getGenre().trim().replace("\"", "").split(",\\s");
            for (String item: genre) {
                if (genres.contains(item))
                    continue;
                genres.add(item);
            }
        }
        return genres;
    }

    private static List<String> getActorList() {
        List<String> actors = new ArrayList<>();
        for (Movie movie: lines) {
            if (movie.getStar1() != null && !actors.contains(movie.getStar1())) {
                actors.add(movie.getStar1());
            }
            if (movie.getStar2() != null && !actors.contains(movie.getStar2())) {
                actors.add(movie.getStar2());
            }
            if (movie.getStar3() != null && !actors.contains(movie.getStar3())) {
                actors.add(movie.getStar3());
            }
            if (movie.getStar4() != null && !actors.contains(movie.getStar4())) {
                actors.add(movie.getStar4());
            }
        }
        return actors;
    }

    private static Map<Integer, Integer> sortYear (Map<Integer, Integer> map) {
        LinkedHashMap<Integer, Integer> linkedHashMap = new LinkedHashMap<>();
        Set<Map.Entry<Integer, Integer>> entrySet = map.entrySet();
        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(entrySet);
        list.sort(new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return o2.getKey() - o1.getKey();
            }
        });

        for (int i = 0; i < list.size(); i++) {
            linkedHashMap.put(list.get(i).getKey(), list.get(i).getValue());
        }

        return linkedHashMap;
    }

    private static Map<String, double[]> sortStarRating (Map<String, double[]> map) {
        LinkedHashMap<String, double[]> linkedHashMap = new LinkedHashMap<>();
        Set<Map.Entry<String, double[]>> entrySet = map.entrySet();
        List<Map.Entry<String, double[]>> list = new ArrayList<>(entrySet);
        list.sort(new Comparator<Map.Entry<String, double[]>>() {
            @Override
            public int compare(Map.Entry<String, double[]> o1, Map.Entry<String, double[]> o2) {
                if (o2.getValue()[0] - o1.getValue()[0] > 0)
                    return 1;
                if (o2.getValue()[0] - o1.getValue()[0] < 0)
                    return -1;
                if (o2.getValue()[0] - o1.getValue()[0] == 0) {
                    return o1.getKey().compareTo(o2.getKey());
                }
                return 0;
            }
        });

        for (int i = 0; i < list.size(); i++) {
            linkedHashMap.put(list.get(i).getKey(), list.get(i).getValue());
        }

        return linkedHashMap;
    }

    private static Map<String, double[]> sortStarGross (Map<String, double[]> map) {
        LinkedHashMap<String, double[]> linkedHashMap = new LinkedHashMap<>();
        Set<Map.Entry<String, double[]>> entrySet = map.entrySet();
        List<Map.Entry<String, double[]>> list = new ArrayList<>(entrySet);
        list.sort(new Comparator<Map.Entry<String, double[]>>() {
            @Override
            public int compare(Map.Entry<String, double[]> o1, Map.Entry<String, double[]> o2) {
                if (o2.getValue()[1] - o1.getValue()[1] > 0)
                    return 1;
                if (o2.getValue()[1] - o1.getValue()[1] < 0)
                    return -1;
                if (o2.getValue()[1] - o1.getValue()[1] == 0) {
                    return o1.getKey().compareTo(o2.getKey());
                }
                return 0;
            }
        });

        for (int i = 0; i < list.size(); i++) {
            linkedHashMap.put(list.get(i).getKey(), list.get(i).getValue());
        }

        return linkedHashMap;
    }

    public Map<String, Integer> getMovieCountByGenre() {
        Map<String, Integer> movieCountByGenre = new HashMap<>();

        List<String> genres = getGenreList();
        for (String genre: genres) {
            int count = 0;
            for (Movie movie : lines) {
                List<String> movieGenre = getMovieGenre(movie);
                if (movieGenre.contains(genre))
                    movieCountByGenre.put(genre, ++count);
            }
        }
        return sortGenre(movieCountByGenre);
    }

    private static List<String> getMovieGenre(Movie movie) {
        List<String> movieGenre = new ArrayList<>();
        String[] content = movie.getGenre().trim().replace("\"", "").split(",\\s");
        for (int i = 0; i < content.length; i++)
            movieGenre.add(content[i]);
        return movieGenre;
    }

    private static Map<String, Integer> sortGenre (Map<String, Integer> map) {
        LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<>();
        Set<Map.Entry<String, Integer>> entrySet = map.entrySet();
        List<Map.Entry<String, Integer>> list = new ArrayList<>(entrySet);
        list.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                if (o2.getValue() - o1.getValue() == 0)
                    return o1.getKey().compareTo(o2.getKey());
                return o2.getValue() - o1.getValue();
            }
        });

        for (int i = 0; i < list.size(); i++) {
            linkedHashMap.put(list.get(i).getKey(), list.get(i).getValue());
        }

        return linkedHashMap;
    }

    public Map<List<String>, Integer> getCoStarCount() {
        Map<List<String>, Integer> movieCountByCoStars = new HashMap<>();
        //List<String> stars = getActorList();
        List<List<String>> co_star = new ArrayList<>();
        Map<List<String>, Integer> count = new HashMap<>();
        for (Movie movie: lines) {
            List<String> movieStars = getMovieStars(movie);
            for (int i = 0; i < movieStars.size(); i++) {
                for (int j = i+1; j < movieStars.size(); j++) {
                    boolean isRepeatedName = false;
                    if (movieStars.get(i) == movieStars.get(j))
                        isRepeatedName = true;
                    int val = movieStars.get(i).compareTo(movieStars.get(j));
                    List<String> list = addList(val, movieStars.get(i), movieStars.get(j));
                    if (count.get(list) == null)
                        count.put(list, 1);
                    else if (count.get(list) != null) {
                        count.put(list, count.get(list)+1);
                    }
//                    else if (count.get(list) == 1 && !isRepeatedName)
//                        continue;
                    co_star.add(list);
                }
            }
        }

        movieCountByCoStars = count;

//        for (int i = 0; i < co_star.size(); i++) {
//            String star1 = co_star.get(i).get(0);
//            String star2 = co_star.get(i).get(1);
//            int num = count.get(co_star.get(i));
//            for (Movie movie: lines) {
//                List<String> movieStars = getMovieStars(movie);
//                if (movieStars.contains(star1)) {
//                    movieStars.remove(star1);
//                    if (movieStars.contains(star2)) {
//                        num = num + 1;
//                    }
//                }
//            }
//            movieCountByCoStars.put(co_star.get(i), --num);
//        }



        return  sortNum(movieCountByCoStars);
    }

    private Map<List<String>, Integer> sortNum(Map<List<String>, Integer> map) {
        LinkedHashMap<List<String>, Integer> linkedHashMap = new LinkedHashMap<>();
        Set<Map.Entry<List<String>, Integer>> entrySet = map.entrySet();
        List<Map.Entry<List<String>, Integer>> list = new ArrayList<>(entrySet);
        list.sort(new Comparator<Map.Entry<List<String>, Integer>>() {
            @Override
            public int compare(Map.Entry<List<String>, Integer> o1, Map.Entry<List<String>, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        for (int i = 0; i < list.size(); i++) {
            linkedHashMap.put(list.get(i).getKey(), list.get(i).getValue());
        }

        return linkedHashMap;
    }
    private static List<String> getMovieStars(Movie movie) {
        List<String> movieStars = new ArrayList<>();
        movieStars.add(movie.getStar1());
        movieStars.add(movie.getStar2());
        movieStars.add(movie.getStar3());
        movieStars.add(movie.getStar4());
        return movieStars;
    }

    private static List<String> addList(int val, String a1, String a2) {
        List<String> list = new ArrayList<>();
        if (val < 0) {
            list.add(a1);
            list.add(a2);
            return list;
        }
        list.add(a2);
        list.add(a1);
        return list;
    }

    public List<String> getTopMovies(int top_k, String by) {
        List<String> topMovies = new ArrayList<>();
        if (by.equals("runtime")) {
            lines.sort(new Comparator<Movie>() {
                @Override
                public int compare(Movie o1, Movie o2) {
                    if ( o2.getRuntime() == o1.getRuntime()) {
                        return o1.getSeriesTitle().compareTo(o2.getSeriesTitle());
                    }
                    return o2.getRuntime() - o1.getRuntime();
                }
            });
        }

        if (by.equals("overview")) {
            lines.sort(new Comparator<Movie>() {
                @Override
                public int compare(Movie o1, Movie o2) {
                    if (o2.getOverview().trim().length() == o1.getOverview().trim().length())
                        return o1.getSeriesTitle().compareTo(o2.getSeriesTitle());
                    return o2.getOverview().trim().length() - o1.getOverview().trim().length();
                }
            });
        }

        for (int i = 0; i < top_k; i++) {
            topMovies.add(lines.get(i).getSeriesTitle());
        }

        return topMovies;
    }

    public List<String> getTopStars(int top_k, String by) {
        List<String> topStars = new ArrayList<>();
        List<String> actors = getActorList();
        Map<String, double[]> average_value = new HashMap<>();
        Map<String, double[]> result = new HashMap<>();
        for (int i = 0; i < actors.size(); i++) {
            String actor = actors.get(i);
            double[] rating_gross = {0,0};
            int count_rating = 0;
            int count_gross = 0;
            for (Movie movie: lines) {
                if (movie.getStar1().equals(actor) || movie.getStar2().equals(actor)
                        || movie.getStar3().equals(actor) || movie.getStar4().equals(actor)) {
                    rating_gross[0] = rating_gross[0] + movie.getImdbRating();
                    if (movie.getGross() != 0) {
                        rating_gross[1] = rating_gross[1] + movie.getGross();
                        count_gross = count_gross + 1;
                    }
                    count_rating = count_rating + 1;
                }
            }
            rating_gross[0] = rating_gross[0]/count_rating;
            if (count_gross == 0)
                rating_gross[1] = 0;
            else rating_gross[1] = rating_gross[1]/count_gross;
            average_value.put(actor, rating_gross);
        }
        if (by.equals("rating")){
            result = sortStarRating(average_value);
            int count = 0;
            for (Map.Entry<String, double[]> entry: result.entrySet()) {
                topStars.add(entry.getKey());
                count = count+1;
                if (count == top_k) {
                    break;
                }
            }
        }
        if (by.equals("gross")){
            result = sortStarGross(average_value);
            int count = 0;
            for (Map.Entry<String, double[]> entry: result.entrySet()) {
                topStars.add(entry.getKey());
                count = count+1;
                if (count == top_k) {
                    break;
                }
            }
        }
        return topStars;
    }

    public List<String> searchMovies(String genre, float min_rating, int max_runtime) {
        List<String> movies = new ArrayList<>();
        for (Movie movie: lines) {
            if (movie.getGenre().contains(genre) && movie.getImdbRating() >= min_rating && movie.getRuntime() <= max_runtime) {
                movies.add(movie.getSeriesTitle());
            }
        }
        movies.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        return movies;
    }
}