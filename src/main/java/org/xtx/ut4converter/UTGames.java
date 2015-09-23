/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter;

import java.io.File;

/**
 * Class defining all UT Games
 * 
 * @author XtremeXp
 */
public class UTGames {

	public static final String T3D_LEVEL_NAME_UE12 = "myLevel.t3d";

	public static final String T3D_LEVEL_NAME_UE3 = "PersistentLevel.t3d";

	/**
	 * Default internal UT4 Editor folder where converted map stuff should by
	 * copied to
	 */
	public static final String UE4_FOLDER_MAP = "/Game/RestrictedAssets/Maps/WIP";

	/**
	 * List all unreal engines
	 */
	public static enum UnrealEngine {

		/**
         *
         */
		NONE("None", 0), // Used for java objects sometimes

		/**
         *
         */
		UE1("Unreal Engine 1", 1),

		/**
         *
         */
		UE2("Unreal Engine 2", 2),

		/**
         *
         */
		UE3("Unreal Engine 3", 3),

		/**
         *
         */
		UE4("Unreal Engine 4", 4);

		private final String name;

		/**
         *
         */
		public final int version;

		UnrealEngine(String name, int version) {
			this.name = name;
			this.version = version;

		}

		/**
		 *
		 * @return
		 */
		@Override
		public String toString() {
			return name;
		}
	}

	/**
	 * List all UT Games
	 */
	public static enum UTGame {

		/**
		 * Undefined
		 */
		NONE("None", "None", UnrealEngine.NONE, "unr"),

		/**
		 * Unreal Tournament
		 */
		UT99("Unreal Tournament", "UT99", UnrealEngine.UE1, "unr"),

		/**
		 * Unreal Tournament 2003
		 */
		UT2003("Unreal Tournament 2003", "UT2003", UnrealEngine.UE2, "ut2"),

		/**
		 * Unreal Tournament 2004
		 */
		UT2004("Unreal Tournament 2004", "UT2004", UnrealEngine.UE2, "ut2"),

		/**
		 * Unreal Tournament 3
		 */
		UT3("Unreal Tournament 3", "UT3", UnrealEngine.UE3, "ut3"),

		/**
		 * Unreal Tournament 4
		 */
		UT4("Unreal Tournament 4", "UT4", UnrealEngine.UE4, "umap"),

		/**
		 * Unreal 1
		 */
		U1("Unreal 1", "U1", UnrealEngine.UE1, "unr"),

		/**
		 * Unreal 2
		 */
		U2("Unreal 2", "U2", UnrealEngine.UE2, "un2"),

		/**
         *
         */
		DEUSEX("Deux Ex", "DE", UnrealEngine.UE2, "un2");

		/**
		 * Generic name of UT game
		 */
		public final String name;

		/**
		 * Short name of UT name
		 */
		public final String shortName;

		/**
		 * Unreal Engine UT game is using
		 */
		public final UnrealEngine engine;

		/**
		 * Default extension for map files
		 */
		public final String mapExtension;

		/**
		 * 
		 * @param name
		 *            Generic name (e.g: Unreal Tournament 3)
		 * @param shortName
		 *            Short name (e.g: UT3)
		 * @param ueVersion
		 *            Unreal Engine version used
		 * @param mapExtension
		 *            Default extension for map files
		 */
		UTGame(String name, String shortName, UnrealEngine ueVersion, String mapExtension) {
			this.name = name;
			this.shortName = shortName;
			this.engine = ueVersion;
			this.mapExtension = mapExtension;
		}

		/**
		 *
		 * @return
		 */
		@Override
		public String toString() {

			return name;
		}
	}

	/**
	 *
	 * @param utGame
	 * @return
	 */
	public static boolean isUnrealEngine4(UTGames.UTGame utGame) {
		return utGame.engine == UnrealEngine.UE4;
	}

	/**
	 *
	 * @param utGame
	 * @return
	 */
	public static boolean isUnrealEngine3(UTGames.UTGame utGame) {
		return utGame.engine == UnrealEngine.UE3;
	}

	/**
	 *
	 * @param utGame
	 * @return
	 */
	public static boolean isUnrealEngine2(UTGames.UTGame utGame) {
		return utGame.engine == UnrealEngine.UE2;
	}

	/**
	 *
	 * @param utGame
	 * @return
	 */
	public static boolean isUnrealEngine1(UTGames.UTGame utGame) {
		return utGame.engine == UnrealEngine.UE1;
	}

	public static File getMapsFolder(File basePath, UTGames.UTGame utgame) {

		if (utgame.engine.version <= UnrealEngine.UE2.version) {
			return new File(basePath + File.separator + "Maps");
		}

		// not really a specific "Maps" folder but most of them are in parent
		// folder cookedpc
		else if (utgame == UTGame.UT3) {
			return new File(basePath + File.separator + "UTGame" + File.separator + "CookedPC");
		}

		else if (utgame == UTGame.UT4) {
			return new File(basePath + File.separator + "UnrealTournament" + File.separator + "Content" + File.separator + "Maps");
		}

		else {
			return basePath;
		}
	}

	/**
	 * 
	 * @param basePath
	 * @param utgame
	 * @return
	 */
	public static File getBinariesFolder(File basePath, UTGames.UTGame utgame) {

		if (utgame.engine.version <= UnrealEngine.UE2.version) {
			return new File(basePath + File.separator + "System");
		}

		// not really a specific "Maps" folder but most of them are in parent
		// folder cookedpc
		else if (utgame == UTGame.UT3) {
			return new File(basePath + File.separator + "Binaries");
		}

		else {
			return basePath;
		}
	}

}
