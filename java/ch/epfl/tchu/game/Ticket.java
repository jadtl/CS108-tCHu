package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.*;

public final class Ticket implements Comparable<Ticket> {

    private List<Trip> trips;
    private final String text;

    public Ticket(List<Trip> trips) {

        Collection<String> fromStations = new TreeSet<>();
        for (Trip trip : trips) {
            fromStations.add(trip.from().name());
        }
        Preconditions.checkArgument((trips.size() != 0) && (fromStations.size() == 1));


        this.trips = trips;
        this.text = computeText(this.trips);

    }

    public Ticket(Station from, Station to, int points) {

        this(new ArrayList<>(List.of(new Trip(from, to, points))));
    }

    private static String computeText(List<Trip> trips) {
        Map map = new HashMap();
        String text = trips.get(0).from().name() + " - ";
        Collection<String> arrivalNoDuplicates = new TreeSet<>();

        for (Trip trip : trips)
            arrivalNoDuplicates.add(trip.to().name());

        for (String arrivalStation : arrivalNoDuplicates) {
            for (Trip trip : trips) {
                if (trip.to().name().equals(arrivalStation))
                    map.put(arrivalStation, trip.points());
            }
        }

        if (arrivalNoDuplicates.size() == 1) {
            text += arrivalNoDuplicates.toArray()[0] +
                    " (" + trips.get(0).points() + ")" ;
        }
        else {
            text += "{";
            for (String arrivalStation : arrivalNoDuplicates) {
                    text += arrivalStation +
                            " (" + map.get(arrivalStation) + "), ";
            }
            text = text.substring(0, text.length() - 2) + "}";
        }

        return text;
    }

    public String text() {

        return text;
    }

    public int points(StationConnectivity connectivity) {

        int points = 0;
        boolean noConnectivity = true;
        for (Trip trip : trips) {
            if (connectivity.connected(trip.from(), trip.to()))
                noConnectivity = false;
        }
        if (noConnectivity) {
            for (Trip trip : trips) {
                if (points == 0) points = trip.points();
                points = Math.min(points, trip.points());
            }
        }
        else {
            for (Trip trip : trips) {
                if (connectivity.connected(trip.from(), trip.to()))
                    points = Math.max(points, trip.points());
            }
        }

        return (noConnectivity) ? -points : points;
    }

    @Override
    public int compareTo(Ticket that) {

        return this.text().compareTo(that.text());
    }

    @Override
    public String toString() {

        return this.text();
    }

}
