package ch.epfl.tchu.game;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public final class Trip {
	  private final Station from;
	  private final Station to;
	  private int points;
	  
	  

	  public Trip(Station from, Station to, int points) { 
		  
	    this.from = Objects.requireNonNull(from);
	    this.to = Objects.requireNonNull(to);
	    this.points = points;
	    
	  }
	
	public static final List<Trip> all(List<Station> from, List<Station> to, int points) {
		
		
		
		
	}

	public Station from() {
		
		return from;	
	}
	
	public Station to() {
		
		return to;
	}
	
	public int points() {
		if(points >= 0) {
			
			return points;
			 
		}
		else {
			
			throw new IllegalArgumentException();
		}
	
	}
	
	public int points(StationConnectivity connectivity) {
		
		return 0;
			
	}
	
	
  }
