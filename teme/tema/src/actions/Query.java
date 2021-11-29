package actions;

import actor.ActorsAwards;
import entertainment.Movie;
import entertainment.Pair;
import entertainment.Serial;
import entertainment.User;
import fileio.ActionInputData;
import fileio.ActorInputData;
import fileio.Input;
import fileio.UserInputData;

import java.util.*;
import java.util.stream.Collectors;

public final class Query {
    private Query() { };

    /**
     * Functie pentru filtrarea filmelor dupa filtre
     * Fac o copie a listei initiale de filme in carea adaug filmele ce indeplinesc filtrele
     * de an si de gen.
     * @param movies lista de filme din input
     * @param filters filtrele din input pentru an si gen
     * @return o copie a listei initiale care cuprinde doar elemente ce indeplinesc filtrele cerute
     */
    private static List<Movie> filterMovies(final List<Movie> movies,
                                            final List<List<String>> filters) {
        List<Movie> copyMovie = new ArrayList<>();
        int year = 0;
        if (filters.get(0).get(0) != null) {
            // transform anul din string in integer
            year = Integer.parseInt(filters.get(0).get(0));
        }
        // listele de filtre au un singur element, fiind de tip singleton in ActionInputData
        String genre = filters.get(1).get(0);
        for (Movie movie : movies) {
            if (year == 0) {
                if (genre == null) {
                    copyMovie.add(movie);
                } else {
                    if (movie.getMovie().getGenres().contains(genre)) {
                        copyMovie.add(movie);
                    }
                }
            } else if (genre == null) {
                if (movie.getMovie().getYear() == year) {
                    copyMovie.add(movie);
                }
            } else if (movie.getMovie().getYear() == year
                    && movie.getMovie().getGenres().contains(genre)) {
                copyMovie.add(movie);
            }
        }
        return copyMovie;
    }

    /**
     * Analog functiei de mai sus, dar filtrele se aplica pentru seriale
     * @param serials serialele din input
     * @param filters filtrele din input
     * @return copie a listei de seriale ce contine doare elemente ce indeplinesc
     *          filtrele de an si gen
     */
    private static List<Serial> filterSerial(final List<Serial> serials,
                                             final List<List<String>> filters) {
        List<Serial> copySerial = new ArrayList<>();
        int year = 0;
        if (filters.get(0).get(0) != null) {
            year = Integer.parseInt(filters.get(0).get(0));
        }

        String genre = filters.get(1).get(0);
        for (Serial serial : serials) {
            if (year == 0) {
                if (genre == null) {
                    copySerial.add(serial);
                } else {
                    if (serial.getSerial().getGenres().contains(genre)) {
                        copySerial.add(serial);
                    }
                }
            } else if (genre == null) {
                if (serial.getSerial().getYear() == year) {
                    copySerial.add(serial);
                }
            } else if (serial.getSerial().getYear() == year
                    && serial.getSerial().getGenres().contains(genre)) {
                copySerial.add(serial);
            }
        }
        return copySerial;
    }

    /**
     * Functie pentru sortarea crescatoare si alfabetica a unei mape
     * @param map mapa ce trebuie sortata crescator dupa valori apoi alfabetic dupa chei
     * @return lista sortata
     */
    public static List<Map.Entry<String, Integer>> sort(final Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> sortedList =
                new LinkedList<>(map.entrySet());

        sortedList.sort((o1, o2) -> {
            Integer view1 = o1.getValue();
            Integer view2 = o2.getValue();
            int x = view1.compareTo(view2);
            if (x != 0) {
                return x;
            }
            String name1 = o1.getKey();
            String name2 = o2.getKey();
            return name1.compareTo(name2);
        });
        return sortedList;
    }

    /**
     * Functie pentru crearea unui string cu primele n valori dintr-o lista sortata data
     * Daca numarul elementelor ce trebuie introduse este mai mare decat dimensiunea listei
     * initiale, se vor insera toate elementele din lista
     * @param sortedList lista din care se preiau elementele
     * @param n numarul de elemente
     * @return string-ul cu primele n elemente din lista
     */
    public static StringBuilder firstN(final List<Map.Entry<String, Integer>> sortedList,
                                       final int n) {
        StringBuilder list = new StringBuilder();
        if (n < sortedList.size()) {
            for (int i = 0; i < n - 1; i++) {
                list.append(sortedList.get(i).getKey());
                list.append(", ");
            }
            list.append(sortedList.get(n - 1).getKey());
        } else {
            for (int i = 0; i < sortedList.size() - 1; i++) {
                list.append(sortedList.get(i).getKey());
                list.append(", ");
            }
            list.append(sortedList.get(sortedList.size() - 1).getKey());
        }
        return list;
    }

    /**
     * Functie ce returneaza primii N actori sortați după media ratingurilor filmelor și a
     * serialelor în care au jucat
     * M-am folosit de o mapa ce are drept campuri atat numele actorului, cat si o pereche
     * alcatuita din numarul videoclipurilor in care joaca si suma ratingurilor acestora, pentru a
     * putea calcula ulterior ratingul final. Intai am initializat map-ul cu toti actorii si
     * valorile initiale din pereche 0.
     * Parcurgand distributiile atat ale filmelor, cat si ale serialelor, am adaugat fiecare rate
     * pentru fiecrae actor si am incrementat numarul de filme in care joaca. Apoi, pentru
     * fiecare actor, am calculat media ratingului si am sortat mapa in functie de aceste medii.
     * La sfarsit, am returnat lista cu primii n actori.
     * @param n numarul de actori ce trebuie afisati
     * @param action actiune
     * @param movies lista de filme din input
     * @param serials lista de seriale din input
     * @param input pentru preluarea listei de actori
     * @return lista ceruta
     */
    public static String average(final int n, final ActionInputData action,
                                     final List<Movie> movies, final List<Serial> serials,
                                     final Input input) {
        final StringBuilder list = new StringBuilder();
        double rate, newrate;

        Map<String, Pair<Integer, Double>> actorsRate = new HashMap<>();
        for (ActorInputData actor : input.getActors()) {
            actorsRate.put(actor.getName(), new Pair<>(0,  0.0));
        }
        for (Movie movie : movies) {
            if (movie.getRating() > 0) {
                for (String actor : movie.getMovie().getCast()) {
                    if (actorsRate.get(actor).getT1() == 0) {
                        actorsRate.get(actor).setT1(1);
                        actorsRate.get(actor).setT2(movie.getRating());
                    } else {
                        rate = actorsRate.get(actor).getT2();
                        newrate = rate + movie.getRating();
                        actorsRate.get(actor).setT1(actorsRate.get(actor).getT1() + 1);
                        actorsRate.get(actor).setT2(newrate);
                    }
                }
            }
        }
        for (Serial serial : serials) {
            if (serial.getRating() > 0) {
                for (String actor : serial.getSerial().getCast()) {
                    if (actorsRate.get(actor).getT1() == 0) {
                        actorsRate.get(actor).setT1(1);
                        actorsRate.get(actor).setT2(serial.getRating());
                    } else {
                        rate = actorsRate.get(actor).getT2();
                        newrate = rate + serial.getRating();
                        actorsRate.get(actor).setT1(actorsRate.get(actor).getT1() + 1);
                        actorsRate.get(actor).setT2(newrate);
                    }
                }
            }
        }

        for (ActorInputData actor : input.getActors()) {
            rate = actorsRate.get(actor.getName()).getT2();
            if (actorsRate.get(actor.getName()).getT1() != 0) {
                newrate = rate / (double) actorsRate.get(actor.getName()).getT1();
                actorsRate.get(actor.getName()).setT2(newrate);
            }
        }

        List<Map.Entry<String, Pair<Integer, Double>>> sortedList =
                new LinkedList<>(actorsRate.entrySet());

        sortedList.sort((o1, o2) -> {
            Double rate1 = o1.getValue().getT2();
            Double rate2 = o2.getValue().getT2();
            int x = rate1.compareTo(rate2);
            if (x != 0) {
                return x;
            }
            String name1 = o1.getKey();
            String name2 = o2.getKey();
            return name1.compareTo(name2);
        });
        sortedList = sortedList.stream().filter((x)
                -> x.getValue().getT1() != 0).collect(Collectors.toList());
            // elimin din lista elem cu rating egal 0
        if (!action.getSortType().equals("asc")) {
            Collections.reverse(sortedList);
        }

        if (n < sortedList.size()) {
            for (int i = 0; i < n - 1; i++) {
                list.append(sortedList.get(i).getKey());
                list.append(", ");
            }
            list.append(sortedList.get(n - 1).getKey());
        } else {
            for (int i = 0; i < sortedList.size() - 1; i++) {
                list.append(sortedList.get(i).getKey());
                list.append(", ");
            }
            list.append(sortedList.get(sortedList.size() - 1).getKey());
        }

        return "Query result: [" + list + "]";
    }

    /**
     * Functie ce returneaza toti actorii ce au primit premiile din inputul actiunii
     * Parcurg lista de actori si verific daca lista de premii a fiecaruia contine toate
     * premiile din input. In caz afirmativ, adaug numele actorului in mapa actorAwards, impreuna
     * cu numarul total de premii al acestuia. Sortez map-ul dupa numarul de premii, respectiv
     * alfabetic pentru valori egale.
     * @param action pentru ordinea sortarii
     * @param input pentru preluarea listei de actori
     * @param awards lista de premii din input
     * @return lista ceruta
     */
    public static String awards(final ActionInputData action, final Input input,
                                    final List<ActorsAwards> awards) {
        final StringBuilder list = new StringBuilder();
        Map<String, Integer> actorAwards = new HashMap<>();
        for (ActorInputData actor : input.getActors()) {
            boolean ok = true;
            int sum = 0;
            for (ActorsAwards award : awards) {
                if (!actor.getAwards().containsKey(award)) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                for (Integer award : actor.getAwards().values()) {
                    sum = sum + award;
                }
                actorAwards.put(actor.getName(), sum);
            }
        }
        if (actorAwards.isEmpty()) {
            return "Query result: []";
        }
        List<Map.Entry<String, Integer>> sortedList =
                sort(actorAwards);
        if (!action.getSortType().equals("asc")) {
            Collections.reverse(sortedList);
        }
        for (int i = 0; i < sortedList.size() - 1; i++) {
            list.append(sortedList.get(i).getKey());
            list.append(", ");
        }
        list.append(sortedList.get(sortedList.size() - 1).getKey());

        return "Query result: [" + list + "]";
    }

    /**
     * Functie ce returneaza toti actorii in descrierea carora se afla cuvintele mentionate in input
     * Delimitez descrierea fiecarui actor in cuvinte, pe care le introduc intr-un vector
     * de string-uri in care verific existenta tuturor cuvintelor din inputul actiunii.
     * Numele actorilor le adaug intr-o lista pe care o sortez alfabetic.
     * @param action pentru tipul sortarii
     * @param input pentru preluarea listei de actori
     * @param words cuvintele ce trebuie continute in descrierea actorului
     * @return lista cerute
     */
    public static String filterDescription(final ActionInputData action,
                                                final Input input,
                                                final List<String> words) {
        final StringBuilder list = new StringBuilder();
        List<String> actorsWords = new ArrayList<>();

        for (ActorInputData actor : input.getActors()) {
            int ct = 0;
            String[] descriptionWords =
                    actor.getCareerDescription().split("[.() ?!,/'\"@-]");

            for (String word : words) {
                for (String descriptionWord : descriptionWords) {
                    if (descriptionWord.equalsIgnoreCase(word)) {
                        ct++;
                        break;
                    }
                }
            }
            if (ct == words.size()) {
                actorsWords.add(actor.getName());
            }
        }

        if (actorsWords.isEmpty()) {
            return "Query result: []";
        }

        Collections.sort(actorsWords);
        if (!action.getSortType().equals("asc")) {
            Collections.reverse(actorsWords);
        }
        for (int i = 0; i < actorsWords.size() - 1; i++) {
            list.append(actorsWords.get(i));
            list.append(", ");
        }
        list.append(actorsWords.get(actorsWords.size() - 1));

        return "Query result: [" + list + "]";
    }

    /**
     * Functie ce returneaza primele n video-uri (filme sau seriale) sortate dupa rating
     * Lucrez cu listele de filme si seriale filtrate dupa filtrele din inputul actiunii.
     * In functie de tipul obiectului actiunii, adaug in mapa numele videoului si ratingul
     * acestuia doar pentru videoclipuri cu rating diferit de 0, mapa pe care o sortez ulterior
     * crescator dupa rating, respectiv alfabetic pentru ratinguri egale.
     * @param action pentru filtre si tipul obiectului actiunii (filme sau seriale)
     * @param movies lista de filme fara filtre aplicate
     * @param serials lista de seriale fara filtre aplicate
     * @return lista ceruta
     */
    public static String rating(final ActionInputData action,
                                    final List<Movie> movies, final List<Serial> serials) {
        List<Movie> copyMovies = filterMovies(movies, action.getFilters());
        List<Serial> copySerials = filterSerial(serials, action.getFilters());
        final StringBuilder list = new StringBuilder();
        Map<String, Double> ratings = new HashMap<>();
        if (action.getObjectType().equals("movies")) {
            for (Movie movie : copyMovies) {
                if (!ratings.containsKey(movie.getMovie().getTitle())) {
                    if (movie.getRating() != 0.0) {
                        ratings.put(movie.getMovie().getTitle(), movie.getRating());
                    }
                }
            }
        } else if (action.getObjectType().equals("shows")) {
            for (Serial serial : copySerials) {
                if (!ratings.containsKey(serial.getSerial().getTitle())) {
                    if (serial.getRating() != 0.0) {
                        ratings.put(serial.getSerial().getTitle(), serial.getRating());
                    }
                }
            }
        }
        if (ratings.isEmpty()) {
            return "Query result: []";
        }
        List<Map.Entry<String, Double>> sortedList =
                new LinkedList<>(ratings.entrySet());

        sortedList.sort((o1, o2) -> {
            Double rate1 = o1.getValue();
            Double rate2 = o2.getValue();
            int x = rate1.compareTo(rate2);
            if (x != 0) {
                return x;
            }
            String name1 = o1.getKey();
            String name2 = o2.getKey();
            return name1.compareTo(name2);
        });
        if (!action.getSortType().equals("asc")) {
            Collections.reverse(sortedList);
        }
        if (action.getNumber() > sortedList.size()) {
            for (int i = 0; i < sortedList.size() - 1; i++) {
                list.append(sortedList.get(i).getKey());
                list.append(", ");
            }
            list.append(sortedList.get(sortedList.size() - 1).getKey());
        } else {
            for (int i = 0; i < action.getNumber() - 1; i++) {
                list.append(sortedList.get(i).getKey());
                list.append(", ");
            }
            list.append(sortedList.get(action.getNumber() - 1).getKey());
        }
        return "Query result: [" + list + "]";
    }

    /**
     * Functie ce returneaza primele n videclipuri cele mai intalnite in listele de
     * favorite ale utilizatorilor
     * Lucrez cu listele de filme si seriale filtrate. Parcurg lista de favorite a fiecarui user
     * si, in functie de tipul obiectului actiunii, verific daca string-ul este numele unui
     * film sau al unui serial, il adaug in mapa daca nu exista, respectiv incrementez
     * valoarea sa daca deja exista cheia.
     * @param input pentru preluarea listei de utilizatori
     * @param action pentru filtre si tipul obiectului actiunii
     * @param movies lista de filme nefiltrata din input
     * @param serials lista de seriale nefiltrata din input
     * @return lista ceruta
     */
    public static String favorite(final Input input, final ActionInputData action,
                                      final List<Movie> movies,
                                      final List<Serial> serials) {
        List<Movie> copyMovies = filterMovies(movies, action.getFilters());
        List<Serial> copySerials = filterSerial(serials, action.getFilters());
        Map<String, Integer> favVideos = new HashMap<>();
        for (UserInputData user : input.getUsers()) {
            for (String video : user.getFavoriteMovies()) {
                if (action.getObjectType().equals("movies")) {
                    if (copyMovies.stream().anyMatch((x) ->
                            x.getMovie().getTitle().equals(video))) {
                        if (!favVideos.containsKey(video)) {
                            favVideos.put(video, 1);
                        } else {
                            int oldValue = favVideos.get(video);
                            int newValue = favVideos.get(video) + 1;
                            favVideos.replace(video, oldValue, newValue);
                        }
                    }
                } else if (action.getObjectType().equals("shows")) {
                    if (copySerials.stream().anyMatch((x) ->
                            x.getSerial().getTitle().equals(video))) {
                        if (!favVideos.containsKey(video)) {
                            favVideos.put(video, 1);
                        } else {
                            int oldValue = favVideos.get(video);
                            int newValue = favVideos.get(video) + 1;
                            favVideos.replace(video, oldValue, newValue);
                        }
                    }
                }
            }
        }
        if (favVideos.isEmpty()) {
            return "Query result: []";
        }
        List<Map.Entry<String, Integer>> sortedList =
                sort(favVideos);
        if (!action.getSortType().equals("asc")) {
            Collections.reverse(sortedList);
        }
        StringBuilder list = firstN(sortedList, action.getNumber());

        return "Query result: [" + list + "]";
    }

    /**
     * Functie ce returneaza primele n videoclipuri sortate dupa durata lor
     * Lucrand cu listele filtrate, in functie de tipul obiectului actiunii, adaug
     * filmul/serialul in mapa, alaturi de dimensiunea lui. Sortez mapa si returnez primele
     * n videoclipuri.
     * @param action pentru tipul obiectului actiunii si filtre
     * @param movies lista de filme nefiltrata din input
     * @param serials lista de seriale nefiltrata din input
     * @return lista ceruta
     */
    public static String longest(final ActionInputData action,
                                     final List<Movie> movies,
                                     final List<Serial> serials) {
        List<Movie> copyMovies = filterMovies(movies, action.getFilters());
        List<Serial> copySerials = filterSerial(serials, action.getFilters());
        Map<String, Integer> longestVideos = new HashMap<>();
        if (action.getObjectType().equals("movies")) {
            for (Movie movie : copyMovies) {
                if (!longestVideos.containsKey(movie.getMovie().getTitle())) {
                    longestVideos.put(movie.getMovie().getTitle(),
                            movie.getMovie().getDuration());
                }
            }
        } else if (action.getObjectType().equals("shows")) {
            for (Serial serial : copySerials) {
                if (!longestVideos.containsKey(serial.getSerial().getTitle())) {
                    longestVideos.put(serial.getSerial().getTitle(), serial.totalDuration());
                }
            }
        }

        if (longestVideos.isEmpty()) {
            return "Query result: []";
        }
        List<Map.Entry<String, Integer>> sortedList =
                sort(longestVideos);
        if (!action.getSortType().equals("asc")) {
            Collections.reverse(sortedList);
        }
        StringBuilder list = firstN(sortedList, action.getNumber());

        return "Query result: [" + list + "]";
    }

    /**
     * Functie ce returneaza primele n videoclipuri sortate dupa numarul de vizualizari
     * Lucrand cu listele de filme si seriale filtrate, parcurg istoricul fiecarui utilizator
     * si adaug, in functie de tipul obiectului actiunii, numele filmului/serialului in mapa,
     * alaturi de numarul de vizualizari. Daca titlul exista deja in mapa, actualizez valoarea
     * corespunzatoare acestuia, adaugand numarul de vizualizari ale userului prezent la vechea
     * valoare. Sortez mapa si returnez primele n videoclipuri.
     * @param input pentru preluarea listei de utilizatori
     * @param action pentru filtre si tipul oniectului actiunii
     * @param movies lista nefiltrata de filme
     * @param serials lista nefiltrata de seriale
     * @return lista ceruta
     */
    public static String mostViewed(final Input input,
                                        final ActionInputData action,
                                        final List<Movie> movies,
                                        final List<Serial> serials) {
        List<Movie> copyMovies = filterMovies(movies, action.getFilters());
        List<Serial> copySerials = filterSerial(serials, action.getFilters());

        Map<String, Integer> views = new HashMap<>();
        for (UserInputData user : input.getUsers()) {
            for (Map.Entry<String, Integer> video : user.getHistory().entrySet()) {
                if (action.getObjectType().equals("shows")) {
                    if (copySerials.stream().anyMatch((x)
                            -> x.getSerial().getTitle().equals(video.getKey()))) {
                        if (!views.containsKey(video.getKey())) {
                            views.put(video.getKey(), video.getValue());
                        } else {
                            int oldValue = views.get(video.getKey());
                            int newValue = views.get(video.getKey()) + video.getValue();
                            views.replace(video.getKey(), oldValue, newValue);
                        }
                    }
                } else if (action.getObjectType().equals("movies")) {
                    if (copyMovies.stream().anyMatch((x)
                            -> x.getMovie().getTitle().equals(video.getKey()))) {
                        if (!views.containsKey(video.getKey())) {
                            views.put(video.getKey(), video.getValue());
                        } else {
                            int oldValue = views.get(video.getKey());
                            int newValue = views.get(video.getKey()) + video.getValue();
                            views.replace(video.getKey(), oldValue, newValue);
                        }
                    }
                }
            }
        }
        if (views.isEmpty()) {
            return "Query result: []";
        }
        List<Map.Entry<String, Integer>> sortedList =
                sort(views);
        if (!action.getSortType().equals("asc")) {
            Collections.reverse(sortedList);
        }
        StringBuilder list = firstN(sortedList, action.getNumber());

        return "Query result: [" + list + "]";
    }

    /**
     * Functie ce returneaza primii n utilizatori dupa numarul de ratinguri date
     * In functia Rating din clasa Commands, am incrementat valoarea de ratinguri date de fiecare
     * utilizator la fiecare apelare a functiei fara erori. Acum ma folosesc de acest camp din
     * clasa User. Astfel, parcurg toti userii, adaugand numele acestora in mapa impreuna cu
     * numarul de ratinguri date, mapa pe care ulterior o sortez si returnez primele n valori.
     * @param action pentru preluarea numarului n
     * @param users lista de utilizatori din input
     * @return lista ceruta
     */
    public static String numberOfRatings(final ActionInputData action,
                                             final List<User> users) {
        Map<String, Integer> ratings = new HashMap<>();
        for (User user : users) {
            if (user.getNumberOfRatings() != 0) {
                ratings.put(user.getUser().getUsername(), user.getNumberOfRatings());
            }
        }
        List<Map.Entry<String, Integer>> sortedList =
                sort(ratings);
        if (!action.getSortType().equals("asc")) {
            Collections.reverse(sortedList);
        }
        StringBuilder list = firstN(sortedList, action.getNumber());

        return "Query result: [" + list + "]";
    }
}
