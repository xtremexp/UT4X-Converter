/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter;

import org.xtx.ut4converter.t3d.T3DRessource;
import org.xtx.ut4converter.tools.SystemUtil;

import java.io.File;

/**
 * Class defining all UT Games
 * 
 * @author XtremeXp
 */
public class UTGames {


	public static final String T3D_LEVEL_NAME_UE3 = "PersistentLevel.t3d";

	/**
	 * Default internal UT4 Editor folder where converted map stuff should by
	 * copied to
	 */
	public static final String UE4_FOLDER_MAP = "/Game/RestrictedAssets/Maps/WIP";

	/**
	 * List all unreal engines
	 */
	public enum UnrealEngine {

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
		 * @return Name of unreal engine
		 */
		@Override
		public String toString() {
			return name;
		}
	}

	/**
	 * List all UT Games
	 */
	public enum UTGame {

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
		 * Unreal Engine 4.23+ game
		 */
		UE4Game("UE4 Game", "UE4Game", UnrealEngine.UE4, "umap"),

		/**
		 * Unreal 1
		 */
		U1("Unreal 1", "U1", UnrealEngine.UE1, "unr"),

		/**
		 * Unreal 2
		 */
		U2("Unreal 2", "U2", UnrealEngine.UE2, "un2"),

		/**
		 * UDK
		 */
		UDK("Unreal Development Kit", "UDK", UnrealEngine.UE3, "udk"),

		/**
         * Deus Ex (UNTESTED)
         */
		DEUSEX("Deux Ex", "DE", UnrealEngine.UE2, "un2"),
		/**
		 * Duke Nukem Forever
		 */
		DNF("Duke Nukem Forever", "DNF", UnrealEngine.UE1, "dnf");

		/**
		 * Generic name of UT game
		 */
		public final String name;

		/**
		 * Short name of the game (e.g: UT99)
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
		 * @return Name of game
		 */
		@Override
		public String toString() {
			return name;
		}
	}

	
	/**
	 * Returns default folder for textures.
	 * @param basePath Unreal game root path (e.g: C:\Program Files (x86)\Steam\steamapps\common\Unreal Tournament)
	 * @param utgame UT Game
	 * @return Default folder for texture (only for UE1/UE2 ut games)
	 */
	public static File getTexturesFolder(final File basePath, UTGames.UTGame utgame) {
		if (utgame.engine.version <= UnrealEngine.UE2.version) {
			return new File(basePath + File.separator + "Textures");
		} else {
			return null;
		}
	}
	
	public static File getSystemFolder(final File basePath, UTGames.UTGame utgame) {
		if (utgame.engine.version <= UnrealEngine.UE2.version) {
			return new File(basePath + File.separator + "System");
		} else {
			return null;
		}
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

		else if (utgame == UTGame.UDK) {
			return new File(basePath + File.separator + "UDKGame" + File.separator + "Content" + File.separator + "Maps");
		}

		else if (utgame == UTGame.UT4) {
			return new File(basePath + File.separator + "UnrealTournament" + File.separator + "Content" + File.separator + "RestrictedAssets" + File.separator + "Maps" + File.separator + "WIP");
		}

		else {
			return basePath;
		}
	}

	/**
	 * 
	 * @param basePath Game path from config
	 * @param utgame UT Game
	 * @return Folder where binaries files of UT game are.
	 */
	public static File getBinariesFolder(final File basePath, final UTGames.UTGame utgame) {

		if (utgame.engine.version <= UnrealEngine.UE2.version) {
			return new File(basePath + File.separator + "System");
		}

		// not really a specific "Maps" folder but most of them are in parent
		// folder cookedpc
		else if (utgame == UTGame.UT3) {
			return new File(basePath + File.separator + "Binaries");
		}
		else if (utgame == UTGame.UDK) {
			if(SystemUtil.is32BitOS()){
				return new File(basePath + File.separator + "Binaries" + File.separator + "Win32");
			} else {
				return new File(basePath + File.separator + "Binaries" + File.separator + "Win64");
			}

		}

		else {
			return basePath;
		}
	}

	/**
	 * UE1/UE2 only
	 * Returns file extension of this package depending on its type
	 *
	 * @return Extension of resource
	 */
	public static String getPackageFileExtensionByGameAndType(final UTGame game, final T3DRessource.Type type) {

		if (null != type)
			switch (type) {
				case MUSIC:
					if (game == UTGames.UTGame.DNF) {
						return ".mp3";
					} else {
						if (game.engine == UnrealEngine.UE1) {
							return ".umx";
						} else if (game.engine == UnrealEngine.UE2) {
							return ".ogg";
						}
					}
				case SOUND:
					if (game == UTGames.UTGame.DNF) {
						return ".dfx";
					} else {
						return ".uax";
					}
				case TEXTURE:
					if (game == UTGames.UTGame.DNF) {
						return ".dtx";
					} else {
						return ".utx";
					}
				case STATICMESH:
					return ".usx";
				case SCRIPT:
					return ".u";
				case LEVEL:
					return "." + game.mapExtension;
				default:
			}

		return null;
	}

	/**
	 * Return path where unreal packages are stored depending on type of
	 * ressource
	 *
	 * @return Relative folder from UT path where the unreal package file should
	 *         be
	 */
	public static String getPackageBaseFolderByResourceType(T3DRessource.Type type){
		if (null != type)
			switch (type) {
				case MUSIC -> {
					return "Music";
				}
				case SOUND -> {
					return "Sounds";
				}
				case TEXTURE -> {
					return "Textures";
				}
				case STATICMESH -> {
					return "StaticMeshes";
				}
				case LEVEL -> {
					return "Maps";
				}
				case SCRIPT -> {
					return "System";
				}
				default -> {
				}
			}

		return null;
	}
}
