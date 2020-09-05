/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.t3d.T3DRessource.Type;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Very basic implementation of unreal package
 * 
 * @author XtremeXp
 */
public class UPackage {

	/**
	 * UT game this package comes from
	 */
	private UTGames.UTGame game;

	/**
	 * Name of package
	 */
	String name;

	/**
	 * File of package
	 */
	File file;

	/**
	 * Package ressources (textures, staticmeshes, ...)
	 */
	Set<UPackageRessource> ressources = new HashSet<>();

	boolean exported;

	/**
	 * Type of package (level, sound, textures, ...) TODO remove some package
	 * may not contain only one type of ressource (e.g: map packages)
	 */
	public Type type;

	/**
	 * 
	 * @param name
	 *            Package Name
	 * @param type
	 *            Type of package (sounds, textures, ...)
	 * @param game
	 *            UT game this package belong to
	 * @param uRessource
	 */
	public UPackage(String name, Type type, UTGames.UTGame game, UPackageRessource uRessource) {
		this.name = name;
		this.type = type;
		this.game = game;

		if (uRessource != null) {
			ressources.add(uRessource);
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * Gets the associated file with this package.
	 * 
	 * @param mapConverter
	 *            Map converter
	 * @return
	 */
	public File getFileContainer(final MapConverter mapConverter) {

		File gamePath = mapConverter.getUserConfig().getGameConfigByGame(mapConverter.getInputGame()).getPath();

		if (this.file != null) {
			return this.file;
		}

		// refactor this
		if (type == Type.LEVEL) {
			this.file = new File(name);
		} else {

			if (mapConverter.isFrom(UnrealEngine.UE1, UnrealEngine.UE2)) {
				this.file = new File(gamePath.getAbsolutePath() + File.separator + getFileFolder() + File.separator + getName() + getFileExtension());

				// Temp hack sometimes textures are embedded not only in .utx
				// files but .u files
				if (!this.file.exists()) {
					this.file = new File(gamePath.getAbsolutePath() + File.separator + "System" + File.separator + getName() + ".u");
				}

				// might be map itself
				if (!this.file.exists() && !mapConverter.getInMap().getName().endsWith(".t3d")) {
					this.file = mapConverter.getInMap();
				}
			} else if (mapConverter.isFrom(UnrealEngine.UE3)) {
				return mapConverter.getUe3PackageFileFromName(getName());
			}
		}

		return this.file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void addRessource(UPackageRessource ressource) {
		ressources.add(ressource);
	}

	public UPackageRessource findRessource(String fullName){
		return findRessource(fullName, true);
	}

	/**
	 * Returns ressource package by full name
	 * 
	 * @param fullName
	 *            Full ressource name (e.g: "AmbAncient.Looping.Stower51")
	 * @return ressource with same full name
	 */
	public UPackageRessource findRessource(String fullName, boolean perfectMatchOnly) {

		String[] s = fullName.split("\\.");
		String fullNameWithoutGroup = null;
		String group = null;

		if (s.length == 3) {
			fullNameWithoutGroup = s[0] + "." + s[2];
			group = s[1];
		}
		
		// sometimes not same case
		// e.g: Ambmodern package name in .t3d file
		// and AmbModern with extractors
		fullName = fullName.toLowerCase();

		for (UPackageRessource packageRessource : ressources) {

			// matching "pakname.groupname.name"
			if (fullName.equals(packageRessource.getFullName().toLowerCase()) || fullName.equalsIgnoreCase(packageRessource.getFullNameWithoutGroup())) {
				return packageRessource;
			}

			// matching "pakname_groupname_name"
			else if (fullName.equalsIgnoreCase(packageRessource.getFullNameWithoutDots())) {
				return packageRessource;
			}

			// matching "groupname_name"
			else if (fullName.equalsIgnoreCase(packageRessource.getGroupAndNameWithoutDots())) {
				return packageRessource;
			}

			// Package ressource was created without group info
			// since we have this info now, update the ressource and return it
			// matching "pakname.name"
			else if (fullNameWithoutGroup != null && packageRessource.getFullNameWithoutGroup().equalsIgnoreCase(fullNameWithoutGroup)) {

				if (group != null) {
					packageRessource.group = s[2];
				}

				return packageRessource;
			} 
			// try matching very close resource names
			// e.g: A_Movers_Movers_Elevator01_LoopCue used in a InterpActor
			// but exported resource name (.wav) is
			// A_Movers_Movers_Elevator01_Loop
			else if (!perfectMatchOnly && s.length >= 2) {
				// same package
				if (s[0].equalsIgnoreCase(packageRessource.getUnrealPackage().getName())) {
					// e.g: A_Movers_Movers_Elevator01_LoopCue
					final String pakResName = packageRessource.getName().toLowerCase();

					// e.g: A_Movers_Movers_Elevator01_Loop
					final String theName = s[s.length - 1].toLowerCase();

					if (pakResName.length() > 6 && theName.length() > 6 && pakResName.contains(theName)) {
						packageRessource.name = s[s.length - 1];
						return packageRessource;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Get ressources used by the package. The ressource list is built on
	 * extracting ressource packages with unreal package extractor
	 * 
	 * @return List of ressources of the package
	 */
	public Set<UPackageRessource> getRessources() {
		return ressources;
	}

	/**
	 * Return path where unreal packages are stored depending on type of
	 * ressource
	 * 
	 * @return Relative folder from UT path where the unreal package file should
	 *         be
	 */
	private String getFileFolder() {

		if (null != type)
			switch (type) {
			case MUSIC:
				return "Music";
			case SOUND:
				return "Sounds";
			case TEXTURE:
				return "Textures";
			case STATICMESH:
				return "StaticMeshes";
			case LEVEL:
				return "Maps";
			case SCRIPT:
				return "System";
			default:
			}

		return null;
	}

	/**
	 * Return relative path
	 * 
	 * @return
	 */
	private String getFileExtension() {

		if (null != type)
			switch (type) {
			case MUSIC:
				if (game.engine == UnrealEngine.UE1) {
					return ".umx";
				} else if (game.engine == UnrealEngine.UE2) {
					return ".ogg";
				}
			case SOUND:
				return ".uax";
			case TEXTURE:
				return ".utx";
			case STATICMESH:
				return ".usx";
			case SCRIPT:
				return ".u";
			case LEVEL:
				return ".unr";
			default:
			}

		return null;
	}

	public boolean isExported() {
		return exported;
	}

	public void setExported(boolean exported) {
		this.exported = exported;
	}

	public boolean isMapPackage(String mapName) {
		return name != null && name.equals(mapName);
	}

	@Override
	public String toString() {
		return getName();
	}

}
