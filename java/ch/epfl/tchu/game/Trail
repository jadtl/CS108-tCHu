package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

public class Trail {

	private final List<Route> routes;
	private final int length;

	private Trail(List<Route> routes) {

		this.routes = routes;
		int length = 0;
		for (Route route : routes) {

			length += route.length();

		}
		
		this.length = length;

	}

	private static final Trail longest(List<Route> routes) {

		List<Trail> longestTrails = new ArrayList<>();
		
		for (Route route : routes) {
			
			longestTrails.add(new Trail(List.of(route)));
		}

		return longestTrails.get(0);
		
		
	}

	public int length() {

		return length;
	}

	public Station station1() {

		return routes.get(0).station1();
	}

	public Station station2() {

		return routes.get(routes.size() - 1).station2();
	}

	@Override

	public String toString() {

		return (station1() + " - " + station2() + " (" + length() + ")");
	}

}
