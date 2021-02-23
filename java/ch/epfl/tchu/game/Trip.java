package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public final class Trip {

	private final Station from;
	private final Station to;
	private int points;



	public Trip(Station from, Station to, int points) {

		Preconditions.checkArgument(points > 0);

		this.from = Objects.requireNonNull(from);
		this.to = Objects.requireNonNull(to);
		this.points = points;

	}

	public static final List<Trip> all(List<Station> from, List<Station> to, int points) {

		List<Trip> tripPossibilities = new ArrayList<>();

		for (Station departure : from) {
			for (Station arrival : to) {
				tripPossibilities.add(new Trip(departure, arrival, points));
			}
		}

		return tripPossibilities;
	}

	public Station from() {
		
		return from;	
	}
	
	public Station to() {
		
		return to;
	}
	
	public int points() {

		return points;
	}
	
	public int points(StationConnectivity connectivity) {

		return connectivity.connected(from, to) ? points : -points;
	}

  }
