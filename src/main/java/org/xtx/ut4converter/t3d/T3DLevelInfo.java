/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.ucore.UPackageRessource;
import org.xtx.ut4converter.ucore.UnrealEngine;

import javax.vecmath.Vector3d;

/**
 *
 * @author XtremeXp
 */
public class T3DLevelInfo extends T3DActor {

	/**
	 * Creator of map
	 */
	private String author;

	/**
	 * Title of map
	 */
	private String title;

	/**
	 * UE1: "IdealPlayerCount="4 - 8 "" UE2: split prop with
	 * "IdealPlayerCountMin" and "IdealPlayerCountMax" UE3: ?
	 */
	private String idealPlayerCount;


	/**
	 * UE1: "Song=Music'Foregone.Foregone'" UE2: "Song="KR-Chemical-Burn"" UE3:
	 * ?
	 */
	private UPackageRessource music;

	private Double killZ;

	public T3DLevelInfo(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
	}

	@Override
	public boolean analyseT3DData(String line) {

		if (line.startsWith("Author=")) {
			author = T3DUtils.getString(line);
		} else if (line.startsWith("Title=")) {
			title = T3DUtils.getString(line);
		}

		else if (line.startsWith("IdealPlayerCount=")) {
			idealPlayerCount = T3DUtils.getString(line);
		}

		else if (line.startsWith("Song=")) {

			if (mapConverter.isFrom(UnrealEngine.UE1)) {
				music = mapConverter.getUPackageRessource(line.split("'")[1], T3DRessource.Type.MUSIC);
			} else if (mapConverter.isFrom(UnrealEngine.UE2)) {
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

		note.setText(text);

		children.add(note);

		if (music != null && mapConverter.getConversionSettings().isConvertMusic()) {
			music.export(UTPackageExtractor.getExtractor(mapConverter, music));
		}
	}

	@Override
	public void scale(double newScale) {

		if (killZ != null) {
			killZ *= newScale;
		}

	}

	@Override
	public String toT3d() {

		/*
		 * writeBeginActor();
		 * 
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
		 */

		return "";
	}
}
