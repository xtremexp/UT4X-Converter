/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter;

import org.xtx.ut4converter.t3d.T3DRessource;
import org.xtx.ut4converter.ucore.UnrealEngine;
import org.xtx.ut4converter.ucore.UnrealGame;

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
	 * List all UT Games
	 */
	public enum UTGame {


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
		 * UDK
		 */
		UDK("Unreal Development Kit", "UDK", UnrealEngine.UE3, "udk"),


		/**
		 * Duke Nukem Forever
		 */
		DNF("Duke Nukem Forever (2001)", "DNF", UnrealEngine.UE1, "dnf");

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
	 * UE1/UE2 only
	 * Returns file extension of this package depending on its type
	 *
	 * @return Extension of resource
	 */
	public static String getPackageFileExtensionByGameAndType(final UnrealGame game, final T3DRessource.Type type) {

		if (null != type)
			switch (type) {
				case MUSIC -> {
					return game.getMusicExt();
				}
				case SOUND -> {
					return game.getSoundExt();
				}
				case TEXTURE -> {
					return game.getTexExt();
				}
				case STATICMESH -> {
					return "usx";
				}
				case SCRIPT -> {
					return "u";
				}
				case LEVEL -> {
					return game.getMapExt();
				}
				default -> {
				}
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
