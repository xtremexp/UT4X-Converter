/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import javax.vecmath.Vector3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGameTypes;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.ucore.UPackageRessource;

/**
 *
 * @author XtremeXp
 */
public class T3DLevelInfo extends T3DActor {

	/**
	 * Creator of map
	 */
	String author;

	/**
	 * Title of map
	 */
	String title;

	/**
	 * UE1: "IdealPlayerCount="4 - 8 "" UE2: split prop with
	 * "IdealPlayerCountMin" and "IdealPlayerCountMax" UE3: ?
	 */
	String idealPlayerCount;

	/**
	 * Default game mode. Overrides the one
	 */
	String defaultGameMode;

	/**
	 * UE1: "Song=Music'Foregone.Foregone'" UE2: "Song="KR-Chemical-Burn"" UE3:
	 * ?
	 */
	UPackageRessource music;

	Double killZ;

	public T3DLevelInfo(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
	}

	@Override
	public boolean analyseT3DData(String line) {

		if (line.startsWith("Author=")) {
			author = line.split("\\=")[0];
		} else if (line.startsWith("Title=")) {
			title = line.split("\\=")[0];
		}

		else if (line.startsWith("IdealPlayerCount=")) {
			title = line.split("\\=")[0];
		}

		else if (line.startsWith("Song=")) {

			if (mapConverter.isFrom(UTGames.UnrealEngine.UE1)) {
				music = mapConverter.getUPackageRessource(line.split("\\'")[1], T3DRessource.Type.MUSIC);
			} else if (mapConverter.isFrom(UTGames.UnrealEngine.UE2)) {
				music = mapConverter.getUPackageRessource(line.split("\"")[1], T3DRessource.Type.MUSIC);
			}
		}

		else {
			return super.analyseT3DData(line);
		}

		return false;
	}

	@Override
	public void convert() {

		T3DNote note = new T3DNote(mapConverter);
		note.location = new Vector3d(0, 0, 0);
		note.drawScale = 4d;
		note.name = this.name + "_Note";

		String text = "";

		if (author != null) {
			text += "Author: " + author;
		}

		if (title != null) {
			text += " Title: " + title;
		}

		if (idealPlayerCount != null) {
			text += "IdealPlayerCount: " + idealPlayerCount;
		}

		if (UTGameTypes.isUt99Assault(mapConverter)) {
			defaultGameMode = UTGameTypes.UT4_ASSAULT_CLASS;
		}

		note.text = text;

		children.add(note);

		if (music != null && mapConverter.convertMusic()) {
			music.export(UTPackageExtractor.getExtractor(mapConverter, music));
		}
	}

	@Override
	public void scale(Double newScale) {

		if (killZ != null) {
			killZ *= newScale;
		}

	}

	@Override
	public String toString() {

		return ""; // disabled because UE4 not handling WorldInfo on import ...

		/**
		 * sbf.append(IDT).append(
		 * "Begin Actor Class=UTWorldSettings Name=UTWorldSettings\n");
		 * 
		 * if(killZ != null){
		 * sbf.append(IDT).append("\tKillZ=").append(killZ).append("\n"); }
		 * 
		 * if(defaultGameMode != null){ // =BlueprintGeneratedClass
		 * '/Game/Blueprints/UTASGameMode.UTASGameMode_C'
		 * sbf.append(IDT).append(
		 * "\tDefaultGameMode=").append(defaultGameMode).append("\n"); }
		 * 
		 * writeEndActor();
		 * 
		 * return sbf.toString();
		 */
	}
}
