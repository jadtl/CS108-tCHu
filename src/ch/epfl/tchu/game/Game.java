package ch.epfl.tchu.game;

import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.Preconditions;

public final class Game {

	public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
	
		Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);
		for(PlayerId playerId : PlayerId.ALL)
		{	
			players.get(playerId).initPlayers(playerId, playerNames);

		}
		
	    .recieveInfo(willPlayFirst());

	}
	
	
}
