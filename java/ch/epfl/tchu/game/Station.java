package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;


public final class Station {
	
	int id;
	String name;
	

	public Station(int id, String name) {
		
		if(id >= 0) {
			
			this.id = id();
			this.name = name().toString();		
		}
		else {
		
			throw new IllegalArgumentException();
	     
		}
	}
	
      public int id() {
		
    	  return id;
 	
		}
          
      public String name() {
    	  
		return name;
		
	}	
	
}

