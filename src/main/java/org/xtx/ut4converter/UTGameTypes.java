/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter;

import org.xtx.ut4converter.UTGames.UTGame;

/**
 * 
 * @author XtremeXp
 */
public class UTGameTypes {

	/**
     *
     */
	public enum GameType {

		/**
         *
         */
		ASSAULT("AS", "Assault", true),

		/**
         *
         */
		CTF("CTF", "Capture the flag", true),

		/**
         *
         */
		DM("DM", "Death match", false),

		/**
         *
         */
		DUEL("DUEL", "Death match", false),

		/**
         *
         */
		TDM("TDM", "Team deathmatch", true),

		/**
        *
        */
		DOM("DOM", "Domination", true),

		/**
        *
        */
		BR("BR", "Bombing run", true);

		final String prefix;
		final String name;

		/**
         *
         */
		public final boolean isTeamBased;

		GameType(String prefix, String name, boolean isTeamBased) {
			this.prefix = prefix;
			this.isTeamBased = isTeamBased;
			this.name = name;
		}
	}


	/**
	 * Guess if map is a team based gametype from map name.
	 * E.g: CTF-Face works with team based gametype, DM-Deck16 not
	 *
	 * @param mapName Map name (e.g: "CTF-Face")
	 * @return <code>true</code> if map name is linked with team based gametype else <code>false</code>
	 */
	public static boolean isTeamBasedFromMapName(String mapName) {

		if (mapName == null) {
			return false;
		}

		if (mapName.contains("-")) {
			String prefix = mapName.split("-")[0];
			GameType gameType = getGameType(prefix);

			if (gameType != null) {
				return gameType.isTeamBased;
			}
		}

		return false;
	}

	public static boolean isUt99Assault(MapConverter mc) {
		return mc.getInputGame() == UTGame.UT99 && getGameType(mc.getInMap().getName().split("-")[0]) == GameType.ASSAULT;
	}

	public static GameType getGameType(String prefix) {

		if (prefix == null) {
			return null;
		}

		prefix = prefix.toUpperCase();

		return switch (prefix) {
			case "DM" -> GameType.DM;
			case "AS" -> GameType.ASSAULT;
			case "CTF" -> GameType.CTF;
			case "DOM" -> GameType.DOM;
			case "BR" -> GameType.BR;
			default -> null;
		};
	}

}
